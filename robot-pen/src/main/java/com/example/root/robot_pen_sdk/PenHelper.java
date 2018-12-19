package com.example.root.robot_pen_sdk;
import cn.robotpen.model.DevicePoint;

/**
 * Created by XChen on 2017/3/2.
 * email:man0fchina@foxmail.com
 */

public class PenHelper {
    private static OnPointChangeListener onPointChangeListener = null;
    private static boolean receivePenPointChange = true;

    public static void setOnPointChangeListener(OnPointChangeListener listener){
        onPointChangeListener = listener;
        if (listener != null){
            receivePenPointChange = true;
        }
    }

    public static OnPointChangeListener getOnPointChangeListener(){
        return  onPointChangeListener;
    }

    public static void setReceivePenPointChange(boolean value){
        receivePenPointChange = value;
    }

    public static boolean canReceivePenPointChange(){
        return receivePenPointChange && (onPointChangeListener != null);
    }

    public interface OnPointChangeListener{
        void onPointChange(DevicePoint point);
    }
}
