package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.R;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.umeng.socialize.media.UMImage;

public class NocMatchInfoFragment extends ContactsListFragment {

    public static final String TAG = NocMatchInfoFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_noc_match_info, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void init() {
        initViews();
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        textView.setText(R.string.activity_details);
        textView= (TextView) findViewById(R.id.contacts_header_right_btn);
        textView.setVisibility(View.VISIBLE);
        textView.setText(getString(R.string.share_to));
        textView.setOnClickListener(this);
        findViewById(R.id.link_view).setOnClickListener(this);
        textView= (TextView) findViewById(R.id.noc_match_address);
        textView.setText(ServerUrl.NOC_BASE_SERVER);
    }
   @Override
   public void onClick(View v) {
             super.onClick(v);
       switch (v.getId()) {
           case R.id.contacts_header_right_btn:
               shareNoc();
               break;
           case R.id.link_view:
               enterMoreActivityInfo();
               break;
       }


   }
    private void enterMoreActivityInfo(){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(ServerUrl.NOC_BASE_SERVER);
        intent.setData(content_url);
        startActivity(intent);
//        ActivityUtils.openNews(getActivity(),getString(R.string.noc_match_address),
//                getString(R.string.activity_details));
    }

    private void shareNoc() {
        String title=getString(R.string.noc_works_title);
        String icon=ServerUrl.NOC_BASE_SERVER +"resources/common/images/noc_compitition_icon.png";
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(title);
        shareInfo.setContent(" ");

        String url=ServerUrl.NOC_BASE_SERVER ;
        shareInfo.setTargetUrl(url);
        UMImage umImage = null;
        if (!TextUtils.isEmpty(icon)) {
            umImage = new UMImage(getActivity(), AppSettings.getFileUrl(icon));
        } else {
            umImage = new UMImage(getActivity(), R.drawable.noc_compitition_icon);
        }
        shareInfo.setuMediaObject(umImage);
        SharedResource resource = new SharedResource();
        //resource.setId(schoolInfo.getSchoolId());
        resource.setTitle(title);
        resource.setDescription("");
        resource.setShareUrl(url);
        if (!TextUtils.isEmpty(icon)) {
            resource.setThumbnailUrl(icon);
        }
        resource.setType(SharedResource.RESOURCE_TYPE_HTML);
        // resource.setFieldPatches(SharedResource.FIELD_PATCHES_SCHOOL_SHARE_URL);
        shareInfo.setSharedResource(resource);
        ShareUtils shareUtils = new ShareUtils(getActivity());
        shareUtils.share(getView(), shareInfo);
    }
 }
