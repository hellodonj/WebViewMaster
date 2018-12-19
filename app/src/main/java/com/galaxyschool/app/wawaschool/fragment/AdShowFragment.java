package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;


/**
 * 机构入驻引导页
 */
public class AdShowFragment extends ContactsListFragment {

    public static final String TAG = AdShowFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_show_ad, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {

        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null){
            textView.setText(getString(R.string.wawachat_agency));
        }
        findViewById(R.id.tv_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.gotoTelephone(getActivity());
            }
        });

    }
}
