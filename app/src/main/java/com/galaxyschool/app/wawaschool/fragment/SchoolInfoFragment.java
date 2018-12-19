package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.ContactsQrCodeDetailsActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.common.WebUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfoResult;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.umeng.socialize.media.UMImage;

import java.util.HashMap;
import java.util.Map;

public class SchoolInfoFragment extends BaseFragment implements OnClickListener {

	public static final String TAG = SchoolInfoFragment.class.getSimpleName();

	public interface Constants {
		public static final String EXTRA_SCHOOL_ID = "schoolId";
		public static final String EXTRA_SCHOOL_NAME = "schoolName";
	}

	private String schoolId;
	private String schoolName;
	private SchoolInfo schoolInfo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_online_school_info, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (getUserVisibleHint()) {
			if (schoolInfo == null) {
				loadSchoolInfo();
			}
		}
	}

	@Override
	public void finish() {
		super.finish();
		getActivity().finish();
	}

	private void init() {
		schoolId = getArguments().getString(Constants.EXTRA_SCHOOL_ID);
		schoolName = getArguments().getString(Constants.EXTRA_SCHOOL_NAME);

		initViews();
	}

	private void initViews() {
		ImageView imageView = (ImageView) getView().findViewById(R.id.contacts_header_left_btn);
		if (imageView != null) {
			imageView.setOnClickListener(this);
		}
		TextView textView = (TextView) findViewById(R.id.contacts_header_title);
		if (textView != null) {
			textView.setText(R.string.school_intro);
		}
		textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
		if (textView != null) {
			textView.setText(R.string.share);
			textView.setTextColor(getResources().getColor(R.color.text_green));
			textView.setVisibility(View.VISIBLE);
			textView.setOnClickListener(this);
		}

		imageView = (ImageView) findViewById(R.id.contacts_user_icon);
		if (imageView != null) {
			imageView.setImageResource(R.drawable.default_school_icon);
		}
		textView = (TextView) findViewById(R.id.contacts_user_name);
		if (textView != null) {
			textView.setText(schoolName);
		}
		textView = (TextView) findViewById(R.id.contacts_user_description);
		if (textView != null) {
			textView.setVisibility(View.GONE);
		}

		View itemView = findViewById(R.id.school_intro_attr);
		if (itemView != null) {
			textView = (TextView) itemView.findViewById(R.id.contacts_attribute_key);
			if (textView != null) {
				textView.setText(R.string.subs_school_introduction);
			}
			textView = (TextView) itemView.findViewById(R.id.contacts_attribute_value);
			if (textView != null) {
				textView.setText(null);
			}
			itemView.setOnClickListener(this);
		}
		itemView = findViewById(R.id.school_qrcode_attr);
		if (itemView != null) {
			textView = (TextView) itemView.findViewById(R.id.contacts_attribute_key);
			if (textView != null) {
				textView.setText(R.string.school_qrcode);
			}
			textView = (TextView) itemView.findViewById(R.id.contacts_attribute_value);
			if (textView != null) {
				textView.setText(null);
			}
			itemView.setOnClickListener(this);
		}
		itemView = findViewById(R.id.school_address_attr);
		if (itemView != null) {
			textView = (TextView) itemView.findViewById(R.id.contacts_attribute_key);
			if (textView != null) {
				textView.setText(R.string.subs_school_adrress);
			}
			textView = (TextView) itemView.findViewById(R.id.contacts_attribute_value);
			if (textView != null) {
				textView.setText(null);
			}
			imageView = (ImageView) itemView.findViewById(R.id.contacts_attribute_indicator);
			if (imageView != null) {
				imageView.setVisibility(View.GONE);
			}
		}
		itemView = findViewById(R.id.school_phone_attr);
		if (itemView != null) {
			textView = (TextView) itemView.findViewById(R.id.contacts_attribute_key);
			if (textView != null) {
				textView.setText(R.string.subs_school_phone);
			}
			textView = (TextView) itemView.findViewById(R.id.contacts_attribute_value);
			if (textView != null) {
				textView.setText(null);
			}
			imageView = (ImageView) itemView.findViewById(R.id.contacts_attribute_indicator);
			if (imageView != null) {
				imageView.setVisibility(View.GONE);
			}
		}
	}

	private void loadSchoolInfo() {
		if (getUserInfo() == null){
			return;
		}
		Map<String, Object> params = new HashMap();
		params.put("MemberId", getUserInfo().getMemberId());
		params.put("SchoolId", schoolId);
		params.put("VersionCode", 1);
		DefaultListener listener =
				new DefaultListener<SchoolInfoResult>(SchoolInfoResult.class) {
			@Override
			public void onSuccess(String jsonString) {
				if (getActivity() == null) {
					return;
				}
				super.onSuccess(jsonString);
				if (getResult() == null || !getResult().isSuccess()) {
					return;
				}
				schoolInfo = getResult().getModel();
				updateViews();
			}
		};
		listener.setShowLoading(true);
		RequestHelper.sendPostRequest(getActivity(),
				ServerUrl.SUBSCRIBE_SCHOOL_INFO_URL, params, listener);
	}

	private void updateViews() {
		ImageView imageView = (ImageView) findViewById(R.id.contacts_user_icon);
		if (imageView != null) {
			getThumbnailManager().displayUserIconWithDefault(
					AppSettings.getFileUrl(schoolInfo.getSchoolLogo()),
					imageView, R.drawable.default_school_icon);
		}
		TextView textView = (TextView) findViewById(R.id.contacts_user_name);
		if (textView != null) {
			textView.setText(schoolInfo.getSchoolName());
		}

		View itemView = findViewById(R.id.school_address_attr);
		if (itemView != null) {
			textView = (TextView) itemView.findViewById(R.id.contacts_attribute_value);
			if (textView != null) {
				textView.setText(schoolInfo.getSchoolAddress());
			}
		}
		itemView = findViewById(R.id.school_phone_attr);
		if (itemView != null) {
			textView = (TextView) itemView.findViewById(R.id.contacts_attribute_value);
			if (textView != null) {
				textView.setText(schoolInfo.getSchoolPhone());
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.contacts_header_left_btn) {
			finish();
		} else if (v.getId() == R.id.contacts_header_right_btn) {
			share();
		} else if (v.getId() == R.id.school_intro_attr) {
			enterSchoolIntroduction();
		} else if (v.getId() == R.id.school_qrcode_attr) {
			enterSchoolQrCode();
		}
	}

	private void enterSchoolIntroduction() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("SchoolId", schoolId);
		WebUtils.openWebView(getActivity(),
				ServerUrl.SUBSCRIBE_SCHOOL_INTRODUCTION, params, schoolName);
	}

	private void enterSchoolQrCode() {
		if (schoolInfo == null) {
			return;
		}

		Bundle args = new Bundle();
		args.putString(ContactsQrCodeDetailsActivity.EXTRA_TITLE,
				getActivity().getString(R.string.school_qrcode));
		args.putInt(ContactsQrCodeDetailsActivity.EXTRA_TARGET_TYPE,
				ContactsQrCodeDetailsActivity.TARGET_TYPE_SCHOOL);
		args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_ID,
				schoolInfo.getSchoolId());
		args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_ICON,
				schoolInfo.getSchoolLogo());
		args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_NAME,
				schoolInfo.getSchoolName());
		args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_QR_CODE,
				schoolInfo.getQRCode());
		Intent intent = new Intent(getActivity(), ContactsQrCodeDetailsActivity.class);
		intent.putExtras(args);
        startActivity(intent);
	}


	private void share() {
		if (schoolInfo == null) {
			return;
		}
		ShareInfo shareInfo = new ShareInfo();
		shareInfo.setTitle(schoolName);
		shareInfo.setContent(getString(R.string.school_intro));
		UMImage umImage = new UMImage(getActivity(), R.drawable.ic_launcher);
		if (!TextUtils.isEmpty(schoolInfo.getSchoolLogo())) {
			umImage = new UMImage(getActivity(), AppSettings.getFileUrl(schoolInfo.getSchoolLogo()));
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("SchoolId", schoolId);
		params.put("Type", "1");
		String url = WebUtils.getShareUrl(
				ServerUrl.SUBSCRIBE_SCHOOL_INTRODUCTION, params, schoolName);
		shareInfo.setTargetUrl(url);
		shareInfo.setuMediaObject(umImage);
        SharedResource resource = new SharedResource();
        resource.setTitle(schoolName);
        resource.setDescription(getString(R.string.school_intro));
        resource.setShareUrl(url);
        resource.setThumbnailUrl(AppSettings.getFileUrl(schoolInfo.getSchoolLogo()));
        resource.setType(SharedResource.RESOURCE_TYPE_HTML);
        shareInfo.setSharedResource(resource);
        new ShareUtils(getActivity()).share(getView(), shareInfo);
	}

}
