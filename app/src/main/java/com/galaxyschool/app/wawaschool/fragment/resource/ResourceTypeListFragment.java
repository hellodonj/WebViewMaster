package com.galaxyschool.app.wawaschool.fragment.resource;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.BookListActivity;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.ContactsListFragment;
import com.galaxyschool.app.wawaschool.fragment.BookListFragment;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.PostByMapParamsModelRequest;
import com.galaxyschool.app.wawaschool.pojo.MaterialType;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: wangchao
 * Time: 2015/11/06 23:02
 */
public class ResourceTypeListFragment extends ContactsListFragment {

    public static final String TAG = ResourceTypeListFragment.class.getSimpleName();

    public static final int RESOURCE_TYPE_MATERIAL = 0;


    private List<ResourceTypeItem> cloudResourceTypes;

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_resource_type_list, null);
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

    private void initViews() {
        ToolbarTopView toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbar_top_view);
        if (toolbarTopView != null) {
            toolbarTopView.getBackView().setOnClickListener(this);
            toolbarTopView.getTitleView().setText(R.string.cloud_resources);
        }
        ListView listView = (ListView) findViewById(R.id.listview);
        if (listView != null) {
            AdapterViewHelper helper = new AdapterViewHelper(
                getActivity(), listView, R.layout.resource_type_list_item) {
                @Override
                public void loadData() {
                    loadCloudResourceTypes();
                }

                @Override
                public View getView(int position, final View convertView, ViewGroup parent) {
                    final View view = super.getView(position, convertView, parent);
                    ResourceTypeItem data = (ResourceTypeItem) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                    TextView textView = (TextView) view.findViewById(R.id.textView);

                    imageView.setImageResource(data.iconId);
                    textView.setText(data.title);

                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    if (cloudResourceTypes != null && cloudResourceTypes.size() > 0) {
                        if (position < cloudResourceTypes.size()) {
                            ResourceTypeItem item = cloudResourceTypes.get(position);
                            if (item != null) {
                                enterSearchCourse(item);
                            }
                        }
                    }
                }
            };
            setCurrAdapterViewHelper(listView, helper);
        }
    }

    private void loadViews() {
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().update();
        } else {
            loadCloudResourceTypes();
        }
    }

    private void loadCloudResourceTypes() {
        Map<String, String> mParams = new HashMap<String, String>();
        PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
            ServerUrl.LOAD_RESOURCE_TYPE_URL, mParams, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                if (jsonString != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        if (jsonObject != null) {
                            JSONArray jsonArray = jsonObject.optJSONArray("MaterialTypeList");
                            if (jsonArray != null) {
                                List<MaterialType> materialTypes = JSON.parseArray(
                                    jsonArray.toString(), MaterialType.class);
                                if (materialTypes != null) {
                                    if (cloudResourceTypes == null) {
                                        cloudResourceTypes = new ArrayList<ResourceTypeItem>();
                                    }
                                    for (MaterialType type : materialTypes) {
                                        if (type != null && type.getTypeCode() == 5) {
                                            ResourceTypeItem item = new ResourceTypeItem();
                                            item.type = type.getTypeCode();
                                            item.title = type.getTitle();
                                            item.materialId = type.getMaterialId();
                                            item.iconId = R.drawable.resource_type_pic_ico;
                                            cloudResourceTypes.add(item);
                                        }
                                    }
                                    ResourceTypeItem item = new ResourceTypeItem();
                                    item.title = getString(R.string.microcourse);
                                    item.iconId = R.drawable.resource_type_material_ico;
                                    item.type = RESOURCE_TYPE_MATERIAL;
                                    cloudResourceTypes.add(item);
                                    getCurrAdapterViewHelper().setData(cloudResourceTypes);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(NetroidError error) {
                if (getActivity() == null) {
                    return;
                }
                TipMsgHelper.ShowMsg(getActivity(), getString(R.string.network_error));
            }

        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    void enterSearchCourse(ResourceTypeItem item) {
//        Bundle args = new Bundle();
//        args.putBoolean(BookListActivity.EXTRA_IS_IMPORT, true);
//        args.putInt(BookListActivity.EXTRA_MATERIAL_TYPE, item.type);
//        if (item.type == 5) {
//            args.putString(BookListActivity.EXTRA_MATERIAL_ID, item.materialId);
//            args.putBoolean(BookListActivity.EXTRA_IS_FROM_MATERIAL, true);
//        } else {
//            args.putString(BookListActivity.EXTRA_SCHOOL_ID, AppSettings.VIRTUAL_SCHOOL_ID);
//        }
//        Fragment fragment = new BookListFragment();
//        fragment.setArguments(args);
//        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//        ft.add(R.id.container, fragment, BookListFragment.TAG);
//        ft.addToBackStack(null);
//        ft.commit();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.toolbar_top_back_btn) {
            if(getActivity() != null) {
                getActivity().finish();
            }
        }
    }

    public class ResourceTypeItem {
        public String title;
        public int iconId;
        public int type;
        public String materialId;
    }
}
