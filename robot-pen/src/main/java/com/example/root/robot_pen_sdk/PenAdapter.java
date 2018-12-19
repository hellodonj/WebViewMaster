package com.example.root.robot_pen_sdk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


public class PenAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater inflater;
	private ArrayList<DeviceVo> mPenDevices;
	private OnContentBtClickListener listener;
	private String connectedAddress = "";

	public PenAdapter(Context context, OnContentBtClickListener listener) {
		this.mContext = context;
		this.listener = listener;
		inflater = LayoutInflater.from(context);
		mPenDevices = new ArrayList<DeviceVo>();
	}

	public void setConnectAddress(String address){
		this.connectedAddress = address;
	}

	@Override
	public int getCount() {
		if (mPenDevices == null){
			return 0;
		}else {
			return mPenDevices.size();
		}
	}

	@Override
	public DeviceVo getItem(int i) {
		return mPenDevices.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}
	
	public void addItem(DeviceVo item){
		if (mPenDevices == null){
			mPenDevices = new ArrayList<>();
		}
		for(int i = 0; i < mPenDevices.size(); i++){
			if (mPenDevices.get(i).getAddress().equals(item.getAddress())){
				return;
			}
		}
		mPenDevices.add(item);
	}
	
	/**
	 * 清除集合内容
	 */
	public void clearItems(){
		if (mPenDevices != null) {
			mPenDevices.clear();
		}
		mPenDevices = null;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		PageItem pageItem;
		if(convertView != null){
			pageItem = (PageItem)convertView.getTag();
		}else{
			convertView = inflater.inflate(R.layout.pen_adapter_item,null);
			pageItem = new PageItem(convertView);
			convertView.setTag(pageItem);
		}

		final DeviceVo deviceObject = getItem(position);
		
		pageItem.deviceName.setText(deviceObject.getName());
		pageItem.deviceAddress.setText(deviceObject.getAddress());
		if (deviceObject.getAddress().equals(connectedAddress)){
			pageItem.btConnect.setText(mContext.getResources().getString(R.string.disconnect));
			pageItem.btConnect.setBackground(
					mContext.getResources().getDrawable(R.drawable.pn_com_bt_gray_bg_selector));
			pageItem.btConnect.setTextColor(mContext.getResources().getColor(R.color.text_gray));
		}else{

			pageItem.btConnect.setText(mContext.getResources().getString(R.string.connect));
			pageItem.btConnect.setBackground(
					mContext.getResources().getDrawable(R.drawable.pn_com_bt_bg_selector));
			pageItem.btConnect.setTextColor(mContext.getResources().getColor(R.color.text_green));
		}
		pageItem.btConnect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (((Button)v).getText().toString().equals(
						mContext.getResources().getString(R.string.connect))){
					if (listener != null){
						listener.OnClickConnect(deviceObject);
						//已连接的设备断开后更新按钮状态（连接中...）
//						if (TextUtils.isEmpty(connectedAddress)) {
//							((Button) v).setText(mContext.getResources().getString(R.string.connecting));
//						}
					}
				}else if(((Button)v).getText().toString().equals(
						mContext.getResources().getString(R.string.disconnect))){
					if (listener != null){
						listener.OnClickDisconnect(deviceObject);
					}
				}
			}
		});
		
		return convertView;
	}
	
	private class PageItem{
		public TextView deviceName;
		public Button btConnect;
		public TextView deviceAddress;
		public PageItem(View view){
			deviceName = (TextView) view.findViewById(R.id.deviceName);
			deviceAddress = (TextView) view.findViewById(R.id.deviceAddress);
			btConnect = (Button) view.findViewById(R.id.connect_bt);
		}
	}

	public interface OnContentBtClickListener{
		void OnClickConnect(DeviceVo deviceEntity);
		void  OnClickDisconnect(DeviceVo deviceEntity);
	}
}
