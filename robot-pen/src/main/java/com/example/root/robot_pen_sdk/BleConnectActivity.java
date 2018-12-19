package com.example.root.robot_pen_sdk;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.robotpen.model.entity.DeviceEntity;
import cn.robotpen.model.symbol.DeviceType;
import cn.robotpen.pen.callback.RemoteCallback;
import cn.robotpen.pen.model.RemoteState;
import cn.robotpen.pen.model.RobotDevice;
import cn.robotpen.pen.scan.RobotScanCallback;

public class BleConnectActivity extends PenBaseConnectActivity
        implements View.OnClickListener, PenAdapter.OnContentBtClickListener {

    private final UUID SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static final String SP_LAST_PAIRED = "last_paired_device";//上次配对的信息
    public static final String SP_PAIRED_DEVICE = "sp_paird";//记录配对信息
    public static final String SP_PAIRED_KEY = "address";//地址关键字
    public static final String FIREWARE_FILE_HOST = "http://dl.robotpen.cn/fw/";//固件升级地址
    public static final int SUCCESS = 0;
    public static final int ERRORCODE = 1;
    public static final int FAILURE = 2;
    public static final int UPDATESUCCESS = 3;
    public static final int UPDATEFAILURE = 4;

    public static final int DEVICE_NAME_SUFFIX_LENGTH = 6;

    private static final int REQUEST_CODE_LOCATION_SETTINGS = 1;

    private SharedPreferences lastSp;
    private SharedPreferences pairedSp;

    private ImageView imgBack;//返回按钮
    private ListView listview;//扫描的蓝牙设备列表
    private Button updateFirmwareButton;
    private Button scanQrcodeBtn; //扫码连设备按钮

    private PenAdapter penAdapter;//蓝牙设备adapter
    private BluetoothAdapter bluetoothAdapter;
    private ProgressDialog progressDialog;
    private RobotDevice robotDevice;//连接上的设备
    private String newVersion; //从网络获取的最新版本号
    private String connectingAddress = "";
    private String deviceNumber; //待连接设备名称后识别码
    private boolean isFinish; //成功连接设备之后是否结束当前页面
    
    private Map<String, DeviceVo> deviceVoMap = new HashMap<>();

    public static void start(Activity activity, String deviceNumber) {
        activity.startActivity(new Intent(activity, BleConnectActivity.class)
                .putExtra("deviceNumber", deviceNumber));
    }

    public static void start(Activity activity, boolean isFinish) {
        activity.startActivity(new Intent(activity, BleConnectActivity.class)
                .putExtra("isFinish", isFinish));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_connect);

        imgBack = (ImageView) findViewById(R.id.back_iv);
        listview = (ListView) findViewById(R.id.list_view);
        updateFirmwareButton = (Button) findViewById(R.id.update_firmware_bt);
        scanQrcodeBtn = (Button) findViewById(R.id.scan_qrcode_bt);

        imgBack.setOnClickListener(this);
        updateFirmwareButton.setOnClickListener(this);
        scanQrcodeBtn.setOnClickListener(this);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        lastSp = this.getSharedPreferences(SP_LAST_PAIRED, MODE_PRIVATE);
        pairedSp = this.getSharedPreferences(SP_PAIRED_DEVICE, MODE_PRIVATE);

        penAdapter = new PenAdapter(BleConnectActivity.this, this);
        listview.setAdapter(penAdapter);

        registerBoradcastReceiver();//注册蓝牙设备断开监听

        getIntentData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        getIntentData();
        scanDevice();
    }

    private void getIntentData() {
        if (getIntent() != null) {
            isFinish = getIntent().getBooleanExtra("isFinish", false);
            deviceNumber = getIntent().getStringExtra("deviceNumber");
            if (!TextUtils.isEmpty(deviceNumber)) {
                int length = deviceNumber.length();
                if (deviceNumber.contains("-")) {
                    int index = deviceNumber.indexOf("-");
                    if (index < length) {
                        deviceNumber = deviceNumber.substring(0, index);
                        length = index;
                    }
                }
                // 取手写板上条形码后6位与蓝牙设备名称后缀进行匹配
                if (length > DEVICE_NAME_SUFFIX_LENGTH) {
                    deviceNumber = deviceNumber.substring(length - DEVICE_NAME_SUFFIX_LENGTH, length);
                }
            }
        }
    }

    //注册蓝牙设备断开广播接收器
    private void registerBoradcastReceiver() {
        IntentFilter stateChangeFilter = new IntentFilter(
                BluetoothDevice.ACTION_ACL_DISCONNECTED);
        stateChangeFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(stateChangeReceiver, stateChangeFilter);
    }

    //蓝牙设备断开广播接收器
    private BroadcastReceiver stateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && !connectingAddress.isEmpty()) {
                    scanDevice();
                }
            }else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        //Log.d("aaa", "STATE_OFF 手机蓝牙关闭");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        //Log.d("aaa", "STATE_TURNING_OFF 手机蓝牙正在关闭");
                        stopScan();
                        deviceVoMap.clear();
                        penAdapter.clearItems();
                        penAdapter.notifyDataSetChanged();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        //Log.d("aaa", "STATE_ON 手机蓝牙开启");
                        scanDevice();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        //Log.d("aaa", "STATE_TURNING_ON 手机蓝牙正在开启");
                        break;
                    default:
                        break;
                }
            }
        }
    };

    //从列表中点击"连接"按钮
    @Override
    public void OnClickConnect(DeviceVo device) {
        stopScan();//停止搜索
        try {
            String address = device.getAddress();
            if (robotService.getConnectedDevice() == null) {
                robotService.connectDevice(address);//通过监听获取连接状态
                showProgress(getResources().getString(R.string.connecting));
            } else {
                Toast.makeText(BleConnectActivity.this,
                        getResources().getString(R.string.disconnect_device_please), Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击列表中“断开”按钮
     */
    @Override
    public void OnClickDisconnect(DeviceVo deviceEntity) {
        try {
            robotService.disconnectDevice();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 笔服务回调函数
     * 各种连接状态回调
     */
    @Override
    protected RemoteCallback initPenServiceCallback() {
        return new RemoteCallback(this) {
            @Override
            public void onPageInfo(int i, int i1) throws RemoteException {

            }

            @Override
            public void onSupportPenPressureCheck(boolean b) throws RemoteException {

            }

            @Override
            public void checkPenPressusering() throws RemoteException {

            }

            @Override
            public void checkPenPressureFinish(byte[] bytes) throws RemoteException {

            }

            @Override
            public void onRequestModuleVersion(byte[] bytes) throws RemoteException {

            }

            @Override
            public void onRemoteUpdateModuleProgress(int i, int i1, String s) throws RemoteException {

            }

            @Override
            public void onRemoteUpdateModuleFinished() throws RemoteException {

            }

            @Override
            public void onPageNumberAndCategory(int i, int i1) throws RemoteException {

            }

            @Override
            public void onPageNumberOnly(long l) throws RemoteException {

            }

            @Override
            public void onSetSyncPassWordWithOldPassWord(int i) throws RemoteException {

            }

            @Override
            public void onOpneReportedData(int i) throws RemoteException {

            }

            @Override
            public void onCloseReportedData(int i) throws RemoteException {

            }

            @Override
            public void onCleanDeviceDataWithType(int i) throws RemoteException {

            }

            @Override
            public void onStartSyncNoteWithPassWord(int i) throws RemoteException {

            }

            @Override
            public void onSleeptimeCallBack(int i) throws RemoteException {

            }

            @Override
            public void onStateChanged(int i, String address) {
                switch (i) {
                    case RemoteState.STATE_CONNECTED://已连接
                        break;
                    case RemoteState.STATE_CONNECTING://连接中
                        break;
                    case RemoteState.STATE_DISCONNECTED: //设备断开
                        penAdapter.setConnectAddress("");
                        penAdapter.notifyDataSetChanged();
                        break;
                    case RemoteState.STATE_DEVICE_INFO: //设备连接成功状态
                        deviceNumber = null; // 连接成功后置空，避免再次自动连接
                        closeProgress();
                        try {
                            RobotDevice robotDevice = robotService.getConnectedDevice();
                            if (null != robotDevice) {
                                BleConnectActivity.this.robotDevice = robotDevice;
                                if (robotDevice.getDeviceType() > 0) {//针对固件bug进行解决 STATE_DEVICE_INFO 返回两次首次无设备信息第二次会上报设备信息
                                    if (robotDevice.getDeviceType() == DeviceType.P1.getValue()) { //如果连接上的是usb设备
                                        Toast.makeText(BleConnectActivity.this,
                                                getResources().getString(R.string.disconnect_usb_device_please),
                                                Toast.LENGTH_SHORT).show();
                                    } else {//如果连接的是蓝牙设备
                                        saveConnectInfo(robotDevice, robotDevice.getName(), robotDevice.getAddress());
                                        connectingAddress = robotDevice.getAddress();
                                        penAdapter.setConnectAddress(connectingAddress);
                                        penAdapter.notifyDataSetChanged();

                                        if (isFinish) {
                                            finish();
                                        }
                                        //如果有离线笔记则同步离线笔记
                                        /*checkStorageNoteNum(robotDevice);
                                        if(robotDevice.getOfflineNoteNum()>0){
                                            deviceSync.setVisibility(View.VISIBLE);
                                        }else
                                            deviceSync.setVisibility(View.GONE);
                                        */
                                        //进行版本升级
                                        checkDeviceVersion(robotDevice);
                                    }
                                }
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    case RemoteState.STATE_ENTER_SYNC_MODE_SUCCESS://笔记同步成功
                        closeProgress();
                        Toast.makeText(BleConnectActivity.this,
                                getResources().getString(R.string.disconnect_usb_device_please),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case RemoteState.STATE_ERROR:
                        closeProgress();
                        OnClickDisconnect(null);
                        break;

                }

            }

            @Override
            public void onOffLineNoteHeadReceived(String s) {

            }

            @Override
            public void onSyncProgress(String s, int i, int i1) {

            }

            @Override
            public void onOffLineNoteSyncFinished(String s, byte[] bytes) {

            }

            @Override
            public void onPenServiceError(String s) {

            }

            @Override
            public void onPenPositionChanged(int i, int i1, int i2, int i3, byte b) {

            }

            @Override
            public void onRobotKeyEvent(int i) {

            }

            @Override
            public void onUpdateFirmwareFinished() {
                updateFirmwareButton.setVisibility(View.GONE);
                closeProgress();
                Toast.makeText(BleConnectActivity.this, "固件升级完毕", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUpdateFirmwareProgress(int progress, int total, String info) {
                
            }

        };
    }

    /**
     * 当有扫描蓝牙结果时的回调
     */
    RobotScanCallback robotScanCallback = new RobotScanCallback() {
        @Override
        public void onResult(BluetoothDevice bluetoothDevice, int i, boolean b) {
            DeviceEntity device = new DeviceEntity(bluetoothDevice);
//            if (!isInList(device.getAddress())) {
            if (!deviceVoMap.containsKey(device.getAddress())) {
                DeviceVo deviceVo = new DeviceVo(device);
                penAdapter.addItem(new DeviceVo(device));
                penAdapter.notifyDataSetChanged();

                deviceVoMap.put(device.getAddress(), deviceVo);
                connectDevice(device);
            }
        }

        @Override
        public void onFailed(int i) {
        }
    };


    // 手写板上条形码字符串后6位与蓝牙设备名称下划线后字符串匹配，匹配成功发起连接
    private void connectDevice(DeviceEntity deviceEntity) {
        String deviceNameSuffix = getDeviceNameSuffix(deviceEntity);
        if (!TextUtils.isEmpty(deviceNumber) && !TextUtils.isEmpty(deviceNameSuffix)
                && deviceNumber.equals(deviceNameSuffix)) {
//            stopScan();
            try {
                if (robotService.getConnectedDevice() == null) {
                    // 通过监听获取连接状态
                    robotService.connectDevice(deviceEntity.getAddress());
                } else {
                    Toast.makeText(BleConnectActivity.this,
                            getResources().getString(R.string.disconnect_device_please),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private String getDeviceNameSuffix(DeviceEntity deviceEntity) {
        if (deviceEntity != null && !TextUtils.isEmpty(deviceEntity.getName())) {
            String deviceName = deviceEntity.getName();
            if (deviceName.contains("_")) {
                int beginIndex = deviceName.lastIndexOf("_");
                return deviceName.substring(beginIndex + 1, deviceName
                        .length());
            }
        }
        return null;
    }

    private void addConnectedDevice(){
        try {
            if (robotService != null) {
                RobotDevice robotDevice = robotService.getConnectedDevice(); //获取目前连接的设备
                if (robotDevice != null) {//已连接设备
                    if (!deviceVoMap.containsKey(robotDevice.getAddress())) {
                        DeviceVo deviceVo = new DeviceVo(robotDevice.getAddress(), robotDevice
                                .getName());
                        penAdapter.addItem(deviceVo);
                        penAdapter.setConnectAddress(robotDevice.getAddress());
                        penAdapter.notifyDataSetChanged();

                        deviceVoMap.put(robotDevice.getAddress(), deviceVo);
                    }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //判断设备是否在当前设备列表中
    private boolean isInList(String address) {
        for (int i = 0; i < penAdapter.getCount(); i++) {
            if (penAdapter.getItem(i).getAddress().equals(address)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.back_iv) {
            finish();
        } else if (id == R.id.update_firmware_bt) {
            try {
                RobotDevice connectDevice = robotService.getConnectedDevice();
                if (connectDevice != null) {
                    updateDevice(connectDevice);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.scan_qrcode_bt) {
            scanForConnectDevice();
        }
    }

    private void scanForConnectDevice() {
        try {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(),
                    "com.galaxyschool.app.wawaschool.CaptureActivity");
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            
        }
    }

    private void scanDevice() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this,
                    getResources().getString(R.string.your_device_not_support_bt),
                    Toast.LENGTH_SHORT).show();
            finish();
        } else if (!bluetoothAdapter.isEnabled()) {//蓝牙未开启
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.startActivityForResult(enableBtIntent, 0xb);
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            penAdapter.clearItems();
            penAdapter.notifyDataSetChanged();
            deviceVoMap.clear();
            startScan();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0xb && resultCode == Activity.RESULT_OK){
            checkLocationService();
        } else if (requestCode == REQUEST_CODE_LOCATION_SETTINGS) {
            checkLocationService();
        }
    }

    private void checkLocationService() {
        if (isLocationEnable(BleConnectActivity.this)) {
            scanDevice();
        } else {
            //定位服务没打开，给提示退出页面
            Toast.makeText(BleConnectActivity.this, R.string.pls_open_location_service,
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 服务连接成功后需要实现
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        scanDevice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScan();
        unregisterReceiver(stateChangeReceiver);
    }

    /**
     * 开始扫描Ble设备--带过滤
     */
    public void startScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isLocationEnable(BleConnectActivity.this)) {
                setLocationService();
                return;
            }
        }
        Object callback = robotScanCallback.getScanCallback();
        if (callback == null) {
            return;
        }
        addConnectedDevice();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            List<ScanFilter> filters = new ArrayList<>();
            ScanFilter filter = new ScanFilter.Builder()
                    .setServiceUuid(new ParcelUuid(SERVICE_UUID))
                    .build();
            filters.add(filter);
            bluetoothAdapter.getBluetoothLeScanner()
                    .startScan(filters, settings, (ScanCallback) callback);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bluetoothAdapter.startLeScan(
                    null,//new UUID[]{SERVICE_UUID},
                    (BluetoothAdapter.LeScanCallback) callback);
        }
    }

    /**
     * 停止扫描Ble设备
     */
    public void stopScan() {
        Object callback = robotScanCallback.getScanCallback();
        if (callback == null) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bluetoothAdapter.getBluetoothLeScanner().stopScan((ScanCallback) callback);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                bluetoothAdapter.stopLeScan((BluetoothAdapter.LeScanCallback) callback);
            }
        }catch (Exception ex){

        }
    }

    /**
     * 保存设备的连接信息
     *
     * @param device
     * @param name
     * @param addr
     */
    private void saveConnectInfo(RobotDevice device, String name, String addr) {
        SharedPreferences.Editor edit = lastSp.edit().clear();
        if (!TextUtils.isEmpty(addr)) {
            pairedSp.edit()
                    .putString(SP_PAIRED_KEY, addr)
                    .apply();
            edit.putString(String.valueOf(device.getDeviceType()), addr);
        }
        edit.apply();
    }

    /**--------------
     * 笔迹同步部分
     -----------------*/
    /**
     * 检查存储笔记数
     */
    private void checkStorageNoteNum(RobotDevice device) {
        int num = device.getOfflineNoteNum();
        if (num > 0) {
            AlertDialog.Builder alert = new AlertDialog.Builder(BleConnectActivity.this);
            alert.setTitle("提示");
            alert.setMessage("共有" + num + "条数据可以同步！");
            alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        robotService.startSyncOffLineNote();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            });
            alert.setNegativeButton("取消", null);
            alert.show();
        }
    }
    /**--------------
     * 设备升级部分
     -----------------*/
    /**
     * 固件升级的相关回调
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    if (robotDevice != null) {
                        String device_firmwareVer = robotDevice.getFirmwareVerStr();
                        String newVersion = msg.obj.toString();
                        if (device_firmwareVer.compareTo(newVersion) > 0) { //存在新版
                            updateFirmwareButton.setVisibility(View.VISIBLE);
                            BleConnectActivity.this.newVersion = newVersion;
                        } else {
                            updateFirmwareButton.setVisibility(View.GONE);
                        }
                    }
                    break;
                case ERRORCODE:
                case FAILURE:
                    /*Toast.makeText(BleConnectActivity.this,
                            getResources().getString(R.string.connect_network_failed),
                            Toast.LENGTH_SHORT)
                            .show();*/
                    break;
                case UPDATESUCCESS:
                    if (robotService != null) {
                        byte[] newFirmwareVer = (byte[]) msg.obj;
                        try {
                            robotService.startUpdateFirmware(newVersion, newFirmwareVer);
                            //升级结果可以通过RemoteCallback 进行展示
                            //此时注意观察设备为紫灯常亮，直到设备升级完毕将自动进行重启
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case UPDATEFAILURE:
                    Toast.makeText(BleConnectActivity.this,
                            getResources().getString(R.string.update_failed),
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    /**
     * 检查设备固件版本
     */
    private void checkDeviceVersion(RobotDevice device) {
        final String otaFileName = device.getName() + "_svrupdate.txt";
        new Thread() {
            public void run() {
                int code;
                try {
                    URL url = new URL(FIREWARE_FILE_HOST + otaFileName);
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setRequestMethod("GET");//使用GET方法获取
                    conn.setConnectTimeout(5000);
                    code = conn.getResponseCode();
                    if (code == 200) {
                        InputStream is = conn.getInputStream();
                        String result = readMyInputStream(is);
                        Message msg = new Message();
                        msg.obj = result;
                        msg.what = SUCCESS;
                        mHandler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.what = ERRORCODE;
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = FAILURE;
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 升级固件版本
     */
    private void updateDevice(RobotDevice device) {
        final String path = generatorFirmwareUrl(device, newVersion);
        showProgress(getResources().getString(R.string.updating));
        new Thread() {
            public void run() {
                try {
                    URL url = new URL(path);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoOutput(false);
                    urlConnection.setConnectTimeout(10 * 1000);
                    urlConnection.setReadTimeout(10 * 1000);
                    urlConnection.setRequestProperty("Connection", "Keep-Alive");
                    urlConnection.setRequestProperty("Charset", "UTF-8");
                    urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
                    urlConnection.connect();
                    InputStream in = urlConnection.getInputStream();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    int bytetotal = urlConnection.getContentLength();
                    int bytesum = 0;
                    int byteread;
                    byte[] buffer = new byte[1024];
                    while ((byteread = in.read(buffer)) != -1) {
                        bytesum += byteread;
                        outputStream.write(buffer, 0, byteread);
                    }
                    outputStream.flush();
                    outputStream.close();
                    in.close();
                    byte[] result = outputStream.toByteArray();
                    outputStream.close();
                    Message msg = new Message();
                    msg.obj = result;
                    msg.what = UPDATESUCCESS;
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = UPDATEFAILURE;
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 生成固件升级url
     *
     * @param device
     * @return
     */
    private String generatorFirmwareUrl(RobotDevice device, String lastFirmwareVer) {
        return FIREWARE_FILE_HOST + device.getName() + "_" + lastFirmwareVer + ".bin";
    }

    /**
     * Stream转String
     *
     * @param is
     * @return
     */
    public String readMyInputStream(InputStream is) {
        byte[] result;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            is.close();
            baos.close();
            result = baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            String errorStr =  getResources().getString(R.string.get_data_failed);
            return errorStr;
        }
        return new String(result);
    }

    /**
     * 显示ProgressDialog
     **/
    private void showProgress(String flag) {
        progressDialog = ProgressDialog.show(this, "", flag + "……", true);
    }

    /**
     * 释放progressDialog
     **/
    private void closeProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /**
     * Location service if enable
     *
     * @param context
     * @return location is enable if return true, otherwise disable.
     */
    public static final boolean isLocationEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean networkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (networkProvider || gpsProvider) return true;
        return false;
    }

    private void setLocationService() {
        Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        this.startActivityForResult(locationIntent, REQUEST_CODE_LOCATION_SETTINGS);
    }
}
