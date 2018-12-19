package com.galaxyschool.app.wawaschool.fragment.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.AppUtils;
import com.galaxyschool.app.wawaschool.fragment.BaseFragment;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;

/**
 * @author 作者 shouyi
 * @version 创建时间：Aug 29, 2015 3:04:46 PM 类说明
 */
public class AboutFragment extends BaseFragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_about, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initViews();
	}

	private void initViews() {
		ToolbarTopView toolbarTopView = (ToolbarTopView) getView()
				.findViewById(R.id.toolbartopview);
		toolbarTopView.getTitleView().setText(R.string.about);
		toolbarTopView.getBackView().setOnClickListener(this);
		String version = AppUtils.getVersionName(getActivity());
		//版本号
		TextView versionView = (TextView) findViewById(R.id.version);
		if (versionView != null){
			versionView.setText("V "+version);
		}
		//联系电话
		TextView contactsView = (TextView) findViewById(R.id.contacts_us);
		if (contactsView != null) {
			contactsView.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.toolbar_top_back_btn) {
			if (getActivity() != null) {
				getActivity().finish();
			}
		} else if (v.getId() == R.id.contacts_us) {
			ActivityUtils.gotoTelephone(getActivity());
		}
	}
}
