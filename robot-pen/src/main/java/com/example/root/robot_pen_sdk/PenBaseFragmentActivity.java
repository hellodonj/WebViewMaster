package com.example.root.robot_pen_sdk;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.codingmaster.slib.S;
import com.osastudio.apps.BaseFragmentActivity;

import cn.robotpen.model.DevicePoint;
import cn.robotpen.pen.IRemoteRobotService;
import cn.robotpen.pen.RobotPenService;
import cn.robotpen.pen.RobotPenServiceImpl;
import cn.robotpen.pen.callback.OnUiCallback;

/**
 * Created by XChen on 2016/9/18.
 * email:man0fchina@foxmail.com
 */
public class PenBaseFragmentActivity extends BaseFragmentActivity implements ServiceConnection, OnUiCallback {
    protected IRemoteRobotService robotService;
    private RobotPenService robotPenService;
    private PenServiceCallback penServiceCallback;
    private final String[] requiredPermissons = new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};

    public IRemoteRobotService getRobotService(){
        return robotService;
    }
    private boolean usePen = true;
    private boolean isShow = false;
    public PenBaseFragmentActivity() {
    }

    public void setUsePen(boolean usePen){
        this.usePen = usePen;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usePen = getIntent().getBooleanExtra("USE_PEN", false);
        if (usePen) {
            this.penServiceCallback = new PenServiceCallback(this);
            this.robotPenService = new RobotPenServiceImpl(this);
            if (ActivityCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") == 0 && ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                this.bindRobotPenService();
            } else {
                ActivityCompat.requestPermissions(this, this.requiredPermissons, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0 && grantResults.length > 1 && grantResults[0] == 0 && grantResults[1] == 0) {
            this.bindRobotPenService();
        } else {
            Toast.makeText(this, "Require SD read/write permisson!", Toast.LENGTH_LONG).show();
        }

    }

    public void bindRobotPenService() {
        this.robotPenService.bindRobotPenService(this, this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.robotService = IRemoteRobotService.Stub.asInterface(service);

        try {
            this.robotService.registCallback(this.penServiceCallback);
            service.linkToDeath(this.penServiceCallback, 0);
        } catch (RemoteException var4) {
            var4.printStackTrace();
            this.onServiceConnectError(var4.getMessage());
        }

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }

    public void onServiceConnectError(String msg) {
        S.i(new Object[]{msg});
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (usePen) {
            if (this.robotService != null) {
                try {
                    byte var1 = this.robotService.getCurrentMode();
                    if (var1 == 10) {
                        this.robotService.exitSyncMode();
                    } else if (var1 == 6) {
                        this.robotService.exitOTA();
                    }

                    this.robotService.unRegistCallback(this.penServiceCallback);
                } catch (RemoteException var3) {
                    var3.printStackTrace();
                }

                try {
                    this.robotService.asBinder().unlinkToDeath(this.penServiceCallback, 0);
                } catch (Exception var2) {
                    var2.printStackTrace();
                }
            }

            this.robotPenService.unBindRobotPenService(this, this);
        }
    }

    @Override
    public void onPenPositionChanged(int deviceType, int x, int y, int presure, byte state) {
        if (usePen && isShow) {
            if (PenHelper.canReceivePenPointChange()) {
                DevicePoint point = DevicePoint.obtain(deviceType, x, y, presure, state);//将传入的数据转化为点数据
//                boolean isHorizontal = getIntent().getBooleanExtra("IS_HORIZONTAL", false);//获取横竖屏设置 默认为竖屏
//                point.setIsHorizontal(isHorizontal);
                PenHelper.getOnPointChangeListener().onPointChange(point);
            }
        }
    }

    @Override
    public void onOffLineNoteHeadReceived(String json) {
    }

    @Override
    public void onSyncProgress(String key, int total, int progress) {
    }

    @Override
    public void onOffLineNoteSyncFinished(String json, byte[] data) {
    }

    @Override
    public void onRobotKeyEvent(int e) {
    }

    @Override
    public void onPageInfo(int currentPage, int totalPage) {
        
    }

    @Override
    public void onPageNumberAndCategory(int pageNumber, int category) {

    }

    @Override
    public void onPageNumberOnly(int number) {

    }

    @Override
    public void onUpdateFirmwareFinished() {
    }

    @Override
    public void onUpdateFirmwareProgress(int progress, int total, String info) {

    }

    @Override
    public void onSupportPenPressureCheck(boolean flag) {

    }

    @Override
    public void onCheckPressureing() {

    }

    @Override
    public void onCheckPressurePen() {

    }

    @Override
    public void onCheckPressureFinish(int flag) {

    }

    @Override
    public void onUpdateModuleFinished() {

    }

    @Override
    public void onCheckModuleUpdate() {

    }

    @Override
    public void onCheckModuleUpdateFinish(byte[] data) {

    }

    @Override
    public void setSyncPassWordWithOldPassWord(String pwd, String n_pwd) {

    }

    @Override
    public void onSetSyncPassWordWithOldPasswordCallback(int code) {

    }

    @Override
    public void opneReportedData() {

    }

    @Override
    public void closeReportedData() {

    }

    @Override
    public void cleanDeviceDataWithType(int code) {

    }

    @Override
    public void startSyncNoteWithPassWord(String pwd) {

    }

    @Override
    public void opneReportedDataCallBack(int code) {

    }

    @Override
    public void closeReportedDataCallBack(int code) {

    }

    @Override
    public void cleanDeviceDataWithTypeCallBack(int code) {

    }

    @Override
    public void startSyncNoteWithPassWordCallBack(int code) {

    }

    @Override
    public void requetSleepTimeCallBack(int time) {

    }

    public void onUpdateFirmwareProgress(int progress, int total) {
    }

    @Override
    public void onPenServiceError(String s) {

    }

    @Override
    public void onStateChanged(int i, String s) {

    }

    @Override
    protected void onResume() {
        isShow = true;
        if (usePen) {
            bindRobotPenService();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        isShow = false;
        if (usePen) {
            PenHelper.setOnPointChangeListener(null);
        }
        super.onPause();
    }

    @Override
    protected void onStop(){
        isShow = false;
        super.onStop();
    }
}