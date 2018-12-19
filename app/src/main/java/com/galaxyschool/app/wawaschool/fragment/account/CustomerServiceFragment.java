package com.galaxyschool.app.wawaschool.fragment.account;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.ContactsListFragment;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CustomerServer;
import com.galaxyschool.app.wawaschool.pojo.CustomerServerListResult;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;

import java.util.List;

import static android.content.Intent.ACTION_VIEW;

public class CustomerServiceFragment extends ContactsListFragment implements View.OnClickListener {

    public static final String TAG = CustomerServiceFragment.class.getSimpleName();


    private ToolbarTopView toolbarTopView;
    private TextView phoneServiceBtn;
    private int sourceType = Constatnts.SOURCE_TYPE_CUSTOMER_SERVICE;

    public interface Constatnts {
        String SOURCE_TYPE = "source_type";
        //联系客服
        int SOURCE_TYPE_CUSTOMER_SERVICE = 0;
        //开通咨询
        int SOURCE_TYPE_OPEN_CONSULTION = 1;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_service, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDatas();
    }

    private void updateListView(CustomerServerListResult result) {
        List<CustomerServer> list = result.getModel().getData();
        if (list != null) {
            getCurrAdapterViewHelper().setData(list);
        }
    }

    private void loadDatas() {

        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<CustomerServerListResult>(
                        CustomerServerListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        Log.e(TAG, jsonString);
                        CustomerServerListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            if (getCurrAdapterViewHelper().hasData()) {
                                getCurrAdapterViewHelper().clearData();
                            }
                            return;
                        }
                        updateListView(result);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_QQ_CUSTOMER_LIST_URL, null, listener);
    }

    private void initViews() {
        Intent intent = getActivity().getIntent();
        sourceType = intent.getIntExtra(Constatnts.SOURCE_TYPE, Constatnts.SOURCE_TYPE_CUSTOMER_SERVICE);
        View rootView = getView();
        TextView customerServiceTextView = (TextView) findViewById(R.id.customer_service_view);
        TextView wawachatSupport = (TextView) findViewById(R.id.wawachat_support);

        if (rootView != null) {
            toolbarTopView = (ToolbarTopView) rootView.findViewById(R.id.toolbartopview);
            toolbarTopView.getBackView().setVisibility(View.VISIBLE);
            if (sourceType == Constatnts.SOURCE_TYPE_CUSTOMER_SERVICE) {
                toolbarTopView.getTitleView().setText(R.string.contact_customer_service);
                customerServiceTextView.setText(R.string.wawachat_service);
                wawachatSupport.setVisibility(View.VISIBLE);
            } else {
                toolbarTopView.getTitleView().setText(R.string.open_consultion);
                customerServiceTextView.setText(R.string.open_consultion_content);
                wawachatSupport.setVisibility(View.GONE);
            }
            toolbarTopView.getBackView().setOnClickListener(this);
            phoneServiceBtn = (TextView) rootView.findViewById(R.id.phone_service_btn);
            phoneServiceBtn.setOnClickListener(this);
        }
        initListView();
    }

    private void initListView() {
        ListView listview = (ListView) findViewById(R.id.listview);
        if (listview != null) {
            AdapterViewHelper gridViewHelper = new AdapterViewHelper(getActivity(),
                    listview, R.layout.item_customer_service) {
                @Override
                public void loadData() {
                    loadDatas();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final CustomerServer data = (CustomerServer) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    TextView textView = (TextView) view.findViewById(R.id.name_view);
                    textView.setText(data.getQQName());
                    ImageView imageView = (ImageView) view.findViewById(R.id.head_view);
                    getThumbnailManager().displayThumbnailWithDefault(
                            AppSettings.getFileUrl(data.getQQHeadPic()), imageView,
                            R.drawable.customer_service_qq_logo);
                    textView = (TextView) view.findViewById(R.id.contact_view);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + data.getQQNumber();
                                startActivity(new Intent(ACTION_VIEW, Uri.parse(url)));
                            } catch (Exception e) {
                                TipMsgHelper.ShowMsg(getActivity(), R.string.install_qq);
                                e.printStackTrace();
                            }

                        }
                    });
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {

                }
            };
            setCurrAdapterViewHelper(listview, gridViewHelper);
        }
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getFragmentManager();
        switch (v.getId()) {
            case R.id.toolbar_top_back_btn:
                if (fm != null) {
                    fm.popBackStack();
                }
                if (getActivity() != null) {
                    getActivity().finish();
                }
                break;
            case R.id.phone_service_btn:
                ActivityUtils.gotoTelephone(getActivity());
                break;
        }
    }
}
