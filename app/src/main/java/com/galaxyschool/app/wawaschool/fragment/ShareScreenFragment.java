package com.galaxyschool.app.wawaschool.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.NotificationHelper;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.oosic.apps.iemaker.base.BaseUtils;
import com.oosic.apps.iemaker.base.ooshare.ConnectedDevice;
import com.oosic.apps.iemaker.base.ooshare.MyShareManager;

import java.util.List;


/**
 * Created by Administrator on 2016/9/19.
 */

public class ShareScreenFragment extends ContactsListFragment {

    public static final String TAG = ShareScreenFragment.class.getSimpleName();

    private MyShareManager myShareManager = null;

    private int notificationId = 9999;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BaseUtils.MSG_SHARE_DEVICE_DISCONNECTED:
                    if (myShareManager != null) {
                        getCurrAdapterViewHelper().clearData();
                        getCurrAdapterViewHelper().setData(myShareManager.getConnectedDevice());
                    }
                    break;
            }
        }
    };

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sharescreen, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadViews();
    }

    private void loadViews() {
        loadConnectedDevices();
    }

    private void loadConnectedDevices() {
        if (myShareManager == null) {
            myShareManager = MyShareManager.getInstance(getActivity(), handler);
        }
        List<ConnectedDevice> devices = myShareManager.getConnectedDevice();
        getCurrAdapterViewHelper().setData(devices);
    }

    private void initViews() {
        final ToolbarTopView toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbartopview);
        if (toolbarTopView != null) {
            toolbarTopView.getTitleView().setText(R.string.sharescreen);
            toolbarTopView.getBackView().setVisibility(View.VISIBLE);
            toolbarTopView.getBackView().setOnClickListener(this);
        }

        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(R.id
                .pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);
        setStopPullUpState(true);
        setPullToRefreshView(pullToRefreshView);

        GridView gridView = (GridView) findViewById(R.id.gridview);
        if (gridView != null) {
            gridView.setNumColumns(1);
            gridView.setVerticalSpacing(1);
            AdapterViewHelper helper = new AdapterViewHelper(getActivity(), gridView, R.layout
                    .sharescreen_device_list_item) {
                @Override
                public void loadData() {
                    loadConnectedDevices();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    ConnectedDevice data = (ConnectedDevice) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    TextView textView = (TextView) view.findViewById(R.id.device_name);
                    if (textView != null) {
                        String deviceName = getDeviceName(data);
                        textView.setText(deviceName);
                    }
                    textView = (TextView) view.findViewById(R.id.device_state);
                    if (textView != null) {
                        textView.setText(data.bShare ? R.string.disconnect : R.string.connect);
//                        textView.setTextColor(data.bShare ? getResources().getColor(R.color
//                                .text_light_gray) : getResources().getColor(R.color.text_green));
                        textView.setTextColor( getResources().getColor(R.color.text_green));
                    }

                    LinearLayout layout = (LinearLayout) view.findViewById(R.id
                            .device_state_layout);
                    if (layout != null) {
//                        layout.setBackgroundResource(data.bShare ? R.drawable.btn_gray_stroke_shape_bg : R
//                                .drawable.btn_green_stroke_shape_bg);
                        layout.setBackgroundResource( R.drawable.btn_green_stroke_shape_bg);
                        layout.setTag(data);
                        layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ConnectedDevice device = (ConnectedDevice) v.getTag();
                                showConnectMessageDialog(device);
                            }
                        });
                    }


                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {

                }
            };
            setCurrAdapterViewHelper(gridView, helper);
        }
    }

    private String getDeviceName(ConnectedDevice data) {
        if (data == null || data.device == null) {
            return null;
        }
        String deviceName = data.device.getName();
        int index = deviceName.lastIndexOf("_");
        if (index <= 0) {
            index = deviceName.length();
        }
        deviceName = deviceName.substring(0, index);
        return deviceName;
    }

    private void showConnectMessageDialog(final ConnectedDevice data) {
        if (data == null) {
            return;
        }

        String title;
        String msg;
        String deviceName = getDeviceName(data);
        if (data.bShare) {
            title = getString(R.string.disconnect);
            msg = getString(R.string.n_disconnect_device, deviceName);
        } else {
            title = getString(R.string.connect);
            msg = getString(R.string.n_connect_device, deviceName);
        }

        ContactsMessageDialog dialog = new ContactsMessageDialog(getActivity(), title, msg,
                getString(R.string.cancel), null, getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!data.bShare) {
                            List<ConnectedDevice> connectedDevices = getCurrAdapterViewHelper()
                                    .getData();
                            if (connectedDevices != null && connectedDevices.size() > 0) {
                                for (ConnectedDevice item : connectedDevices) {
                                    if (item != null) {
                                        if (item.bShare) {
                                            item.bShare = false;
                                        }
                                    }
                                }
                            }
                            NotificationHelper.showShareScreenNotification(getActivity(), data, notificationId);
                        } else {
                            NotificationHelper.removeShareScreenNotification(getActivity(), notificationId);
                        }
                        data.bShare = !data.bShare;
                        getCurrAdapterViewHelper().update();
                    }
                });
        dialog.show();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toolbar_top_back_btn) {
            finish();
        } else {
            super.onClick(v);
        }
    }
}
