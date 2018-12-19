package com.example.root.robot_pen_sdk;

import android.Manifest;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.osastudio.apps.BaseActivity;

import cn.robotpen.pen.IRemoteRobotService;
import cn.robotpen.pen.RobotPenService;
import cn.robotpen.pen.RobotPenServiceImpl;
import cn.robotpen.pen.callback.RemoteCallback;

/**
 * Created by XChen on 2017/3/1.
 * email:man0fchina@foxmail.com
 */

public class PenBaseConnectActivity extends BaseActivity implements ServiceConnection {
    protected IRemoteRobotService robotService;
    protected RemoteCallback penServiceCallback;
    private RobotPenService robotPenService;

    public IRemoteRobotService getRobotService(){
        return robotService;
    }

    final IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (ActivityCompat.checkSelfPermission(PenBaseConnectActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(PenBaseConnectActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PenBaseConnectActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                return;
            }
            if(robotPenService != null){
                robotPenService.bindRobotPenService(
                        PenBaseConnectActivity.this, PenBaseConnectActivity.this);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntent().putExtra("BaseActivityName", "BaseConnectActivity");
        penServiceCallback = initPenServiceCallback();
        robotPenService =  new RobotPenServiceImpl(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }
        if(robotPenService != null) {
            robotPenService.bindRobotPenService(this, this);
        }
    }

    protected RemoteCallback initPenServiceCallback(){
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
            public void onStateChanged(int i, String s) {

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

            }

            @Override
            public void onUpdateFirmwareProgress(int progress, int total, String info) {
                
            }

        };
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        robotService = IRemoteRobotService.Stub.asInterface(service);
        try {
            robotService.registCallback(penServiceCallback);
            service.linkToDeath(deathRecipient, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
            onServiceConnectError(e.getMessage());
        }
    }

    /**
     * 无法连接到笔服务
     *
     * @param msg
     */
    public void onServiceConnectError(String msg) {

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (robotService != null) {
            try {
                byte model = robotService.getCurrentMode();
                //检查当前模式
                if (model == 0x0A) {
                    //退出同步模式
                    robotService.exitSyncMode();
                } else if (model == 0x06) {
                    //退出OTA模式
                    robotService.exitOTA();
                }
                //取消回调
                robotService.unRegistCallback(penServiceCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                robotService.asBinder().unlinkToDeath(deathRecipient, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            robotPenService.unBindRobotPenService(this, this);
        }
    }
}