package com.galaxyschool.app.wawaschool.fragment.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.root.robot_pen_sdk.BleConnectActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.ShareScreenActivity;
import com.galaxyschool.app.wawaschool.fragment.ContactsListFragment;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wangchao
 * @date: 2016/10/17 12:43
 */

public class DeviceManagementFragment extends ContactsListFragment {

    public static final String TAG = DeviceManagementFragment.class.getSimpleName();

    public interface Constants {
        public static final int ENTRY_TYPE_SHARE_SCREEN = 1;
        public static final int ENTRY_TYPE_BLUETOOTH_PEN = 2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_management, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    public void onResume() {
        super.onResume();
        loadViews();
    }

    private void initViews() {
        ToolbarTopView toolbarTopView = (ToolbarTopView)findViewById(R.id.toolbartopview);
        toolbarTopView.getBackView().setOnClickListener(this);
        toolbarTopView.getTitleView().setText(R.string.device_manager);

        ListView listView = (ListView) findViewById(R.id.device_listview);
        if (listView != null) {
            AdapterViewHelper helper = new AdapterViewHelper(getActivity(), listView, R.layout
                    .device_management_list_item) {
                @Override
                public void loadData() {
                    loadEntryInfos();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    EntryInfo data = (EntryInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    TextView textView = (TextView) view.findViewById(R.id
                            .device_management_title_txt);
                    if (textView != null) {
                        textView.setText(data.title);
                    }
                    view.setTag(holder);

                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    enterEntry((EntryInfo) holder.data);
                }
            };

            setCurrAdapterViewHelper(listView, helper);
        }
    }

    private void loadViews() {
        loadEntryInfos();
    }

    private void loadEntryInfos() {
        List<EntryInfo> entryInfos = new ArrayList<EntryInfo>();
        EntryInfo entryInfo = null;

//        entryInfo = new EntryInfo();
//        entryInfo.title = R.string.sharescreen;
//        entryInfo.type = Constants.ENTRY_TYPE_SHARE_SCREEN;
//        entryInfos.add(entryInfo);

        entryInfo = new EntryInfo();
        entryInfo.title = R.string.bluetooth_pen;
        entryInfo.type = Constants.ENTRY_TYPE_BLUETOOTH_PEN;
        entryInfos.add(entryInfo);

        if(getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().clearData();
        }
        getCurrAdapterViewHelper().setData(entryInfos);
    }

    private void enterEntry(EntryInfo data) {
        if(data == null) {
            return;
        }

        switch (data.type) {
            case Constants.ENTRY_TYPE_SHARE_SCREEN:
                enterShareScreenDeviceList();
                break;

            case Constants.ENTRY_TYPE_BLUETOOTH_PEN:
                BleConnectActivity.start(getActivity(), false);
                break;
        }
    }

    private void enterShareScreenDeviceList() {
        Intent intent = new Intent(getActivity(), ShareScreenActivity.class);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.toolbar_top_back_btn) {
            finish();
        }
    }

    private class EntryInfo {
        public int type;
        public int title;
    }
}
