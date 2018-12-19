package com.example.root.robot_pen_sdk;

import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import java.lang.ref.WeakReference;

import cn.robotpen.pen.callback.OnUiCallback;
import cn.robotpen.pen.callback.RobotPenActivity;
import cn.robotpen.pen.pool.RunnableMessage;

/**
 * Created by XChen on 2017/3/1.
 * email:man0fchina@foxmail.com
 */

public class PenServiceCallback extends cn.robotpen.pen.IRemoteRobotServiceCallback.Stub implements IBinder.DeathRecipient {
    private WeakReference<OnUiCallback> a;
    private Handler b;

    public PenServiceCallback(OnUiCallback uiCallback) {
        this.a = new WeakReference(uiCallback);
        this.b = new Handler();
    }

    public void onRemoteStateChanged(final int state, final String addr) throws RemoteException {
        try {
            this.b.post(new Runnable() {
                public void run() {
                    ((OnUiCallback)PenServiceCallback.this.a.get()).onStateChanged(state, addr);
                }
            });
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public void onRemoteOffLineNoteHeadReceived(final String json) throws RemoteException {
        try {
            this.b.post(new Runnable() {
                public void run() {
                    ((OnUiCallback)PenServiceCallback.this.a.get()).onOffLineNoteHeadReceived(json);
                }
            });
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public void onRemoteSyncProgress(final String key, final int total, final int progress) throws RemoteException {
        try {
            this.b.post(new Runnable() {
                public void run() {
                    ((OnUiCallback)PenServiceCallback.this.a.get()).onSyncProgress(key, total, progress);
                }
            });
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    public void onRemoteOffLineNoteSyncFinished(final String json, final byte[] data) throws RemoteException {
        try {
            this.b.post(new Runnable() {
                public void run() {
                    ((OnUiCallback)PenServiceCallback.this.a.get()).onOffLineNoteSyncFinished(json, data);
                }
            });
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public void onRemotePenServiceError(final String msg) throws RemoteException {
        try {
            this.b.post(new Runnable() {
                public void run() {
                    ((OnUiCallback)PenServiceCallback.this.a.get()).onPenServiceError(msg);
                }
            });
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public void onRemotePenPositionChanged(int deviceType, int x, int y, int presure, byte state) throws RemoteException {
        if(this.a.get() != null) {
            RunnableMessage.obtain(deviceType, x, y, presure, state, (OnUiCallback)this.a.get()).sendToTarget();
        }

    }

    public void onRemoteRobotKeyEvent(final int e) throws RemoteException {
        try {
            this.b.post(new Runnable() {
                public void run() {
                    ((OnUiCallback)PenServiceCallback.this.a.get()).onRobotKeyEvent(e);
                }
            });
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    @Override
    public void onPageInfo(int i, int i1) throws RemoteException {
        
    }

    @Override
    public void onRemoteUpdateFirmwareProgress(int i, int i1, String s) throws RemoteException {

    }

    public void onRemoteUpdateFirmwareProgress(final int progress, final int total) throws RemoteException {
        try {
            this.b.post(new Runnable() {
                public void run() {
                    ((OnUiCallback)PenServiceCallback.this.a.get()).onUpdateFirmwareProgress
                            (progress, total, "");
                }
            });
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public void onRemoteUpdateFirmwareFinished() throws RemoteException {
        try {
            this.b.post(new Runnable() {
                public void run() {
                    ((OnUiCallback)PenServiceCallback.this.a.get()).onUpdateFirmwareFinished();
                }
            });
        } catch (Exception var2) {
            var2.printStackTrace();
        }

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

    public void binderDied() {
        try {
            ((RobotPenActivity)this.a.get()).bindRobotPenService();
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }
}