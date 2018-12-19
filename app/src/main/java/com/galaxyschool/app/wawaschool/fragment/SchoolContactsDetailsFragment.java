package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.CommonFragmentActivity;
import com.galaxyschool.app.wawaschool.ContactsActivity;
import com.galaxyschool.app.wawaschool.ContactsQrCodeDetailsActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.activity.ChatActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberListResult;
import com.galaxyschool.app.wawaschool.pojo.HeadMasterClassMailInfo;
import com.galaxyschool.app.wawaschool.pojo.HeadMasterInfoList;
import com.galaxyschool.app.wawaschool.pojo.Reporter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;

import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchoolContactsDetailsFragment extends GroupContactsListFragment {

	public static final String TAG = SchoolContactsDetailsFragment.class.getSimpleName();

	public static final String IS_ADD_TEACHERS = "SchoolContactsDetailsFragment_is_add_teachers";
	public static final String STUDENT_DATA = "SchoolContactsDetailsFragment_student_data";
	public static final String TEACHER_DATA = "SchoolContactsDetailsFragment_teacher_data";

	private GridView teachersGridView;
	private Button sendMessageBtn;
	private ImageView teachersArrow;
	private String from;
	private boolean isAddReporterPermission;
	private List<SelectHelper> teacherData = new ArrayList<>();
	private List<Reporter> reporters = new ArrayList<>();

	private boolean isAddTeachers = false;
	private List<ContactsClassMemberInfo> students;
	private List<ContactsClassMemberInfo> teachers;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.contacts_school_details, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getIntent();
		initTitle();
		initViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getUserVisibleHint()) {
			loadViews();
		}
	}

	private void getIntent() {
		isAddTeachers = getArguments().getBoolean(IS_ADD_TEACHERS);
		students = (List<ContactsClassMemberInfo>) getArguments().getSerializable(STUDENT_DATA);
		teachers = (List<ContactsClassMemberInfo>) getArguments().getSerializable(TEACHER_DATA);
		from = getArguments().getString("from");
		if (from != null && from.equals(ReporterIdentityFragment.TAG)) {
			isAddReporterPermission = true;
			reporters = getArguments().getParcelableArrayList(ContactsActivity
					.EXTRA_CONTACTS_FOR_REPORTER);
		}
	}

	@Override
	protected void removeFromClass(ContactsClassMemberInfo info) {
		checkTeacherRoleInfo(info, true, false);
	}

	/**
	 * @param info
	 * @param CheckIsHeadMaster true  检验身份 false 移出通讯录
	 */
	private void checkTeacherRoleInfo(final ContactsClassMemberInfo info, final boolean
			CheckIsHeadMaster, final boolean changeHeadTeacher) {
		Map<String, Object> params = new HashMap();
		params.put("SchoolId", schoolId);
		params.put("TeacherId", info.getMemberId());
		params.put("CheckIsHeadMaster", CheckIsHeadMaster);
		DefaultModelListener listener = new DefaultModelListener<ModelResult>(
				ModelResult.class) {
			@Override
			public void onSuccess(String jsonString) {
				if (getActivity() == null) {
					return;
				}
				super.onSuccess(jsonString);
				ModelResult result = getResult();
				if (result != null && result.isSuccess()) {
					JSONObject jsonObject = JSON.parseObject(jsonString);
					JSONObject model = jsonObject.getJSONObject("Model");
					HeadMasterClassMailInfo masterClassMailInfo = JSONObject.parseObject(model.toString(),
							HeadMasterClassMailInfo.class);
					if (masterClassMailInfo != null) {
						if (CheckIsHeadMaster) {
							//校验角色身份
							if (masterClassMailInfo.isHasInspectAuth()) {
								outOfTeacherAddressBook(info, false, true);
							} else if (masterClassMailInfo.isHeadMaster()) {
								enterChangeHeadMasterRoleDetail(info, masterClassMailInfo.getHeadMasterClassMailList());
							} else {
								outOfTeacherAddressBook(info, false, false);
							}
						} else {
							if (changeHeadTeacher) {
								outOfTeacherAddressBook(info, true, false);
							} else {
								upDateListData(info);
								//移出老师通讯录
								TipsHelper.showToast(getActivity(), getString(R.string
										.str_success_remove_master, info.getNoteName()));
							}
						}
					}
				}

			}

			@Override
			public void onError(NetroidError error) {
				super.onError(error);
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}
		};
		listener.setShowLoading(true);
		RequestHelper.sendPostRequest(getActivity(), ServerUrl.CHECK_TEACHER_ROLEOFCLASS_AND_REMOVE_BASE_URL, params, listener);
	}

	private void enterChangeHeadMasterRoleDetail(ContactsClassMemberInfo info,
												 List<HeadMasterInfoList> classMailInfo) {
		Intent intent = new Intent(getActivity(), CommonFragmentActivity.class);
		Bundle bundle = getArguments();
		bundle.putSerializable(CommonFragmentActivity.EXTRA_CLASS_OBJECT, RemoveAddressBookFragment.class);
		bundle.putSerializable(RemoveAddressBookFragment.HEADMASTER_CLASSMAIL_LIST, (Serializable) classMailInfo);
		bundle.putParcelable(RemoveAddressBookFragment.HEADMASTER_MEMBER_INFO, info);
		intent.putExtras(bundle);
		startActivityForResult(intent, ActivityUtils.REQUEST_CODE_RETURN_REFRESH);
	}

	/**
	 * @param item              角色信息
	 * @param changeHeadTeacher 是不是更换班主任
	 * @param hasInspectAuth    是不是拥有校长助手的权限用户
	 */
	private void outOfTeacherAddressBook(final ContactsClassMemberInfo item,
										 final boolean changeHeadTeacher,
										 final boolean hasInspectAuth) {
		String contentText = getString(R.string.str_confirm_remove_master, item.getNoteName());
		String cancelBtn = getString(R.string.cancel);
		if (hasInspectAuth) {
			cancelBtn = "";
			contentText = getString(R.string.str_has_inspect_auth_permission_person, item.getNoteName());
		} else if (changeHeadTeacher) {
			cancelBtn = "";
			contentText = getString(R.string.str_success_remove_master, item.getNoteName());
		}
		ContactsMessageDialog messageDialog = new ContactsMessageDialog(getActivity(), null,
				contentText, cancelBtn, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}, getString(R.string.confirm), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (hasInspectAuth) {
					//空方法
				} else if (changeHeadTeacher) {
					upDateListData(item);
				} else {
					checkTeacherRoleInfo(item, false, false);
				}
				dialog.dismiss();
			}
		});
		if (changeHeadTeacher) {
			String teacherNoteName = item.getNoteName();
			if (TextUtils.isEmpty(teacherNoteName)) {
				return;
			}
			int length = contentText.indexOf(teacherNoteName);
			Spannable wordToSpan = new SpannableString(contentText);
			wordToSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red)), length,
					teacherNoteName.length() + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			TextView contentTextV = (TextView) messageDialog.getContentView().findViewById(R.id
					.contacts_dialog_content_text);
			contentTextV.setText(wordToSpan);
		}
		messageDialog.show();
	}

	private void initTitle() {
		View rootView = getView();
		if (rootView == null) {
			return;
		}

		TextView textView = (TextView) rootView.findViewById(R.id.contacts_header_title);
		if (textView != null) {
			textView.setOnClickListener(this);
			textView.setText(this.groupName);
			RelativeLayout.LayoutParams params =
					(RelativeLayout.LayoutParams) textView.getLayoutParams();
			params.leftMargin *= 1.4f;
			params.rightMargin *= 1.4f;
			textView.setLayoutParams(params);
		}

		ImageView imageView = (ImageView) rootView.findViewById(R.id.contacts_header_left_btn);
		if (imageView != null) {
			imageView.setOnClickListener(this);
		}

		textView = (TextView) rootView.findViewById(R.id.contacts_header_right_btn);
		if (textView != null) {
			textView.setOnClickListener(this);
			if (isAddReporterPermission){
				textView.setText(R.string.confirm);
				textView.setVisibility(View.VISIBLE);
			}else {
				textView.setText(R.string.build_class);
				textView.setBackgroundResource(R.drawable.sel_nav_button_bg);
				textView.setVisibility(View.INVISIBLE);
			}
		}
	}

	private void initViews() {
		View rootView = getView();
		if (rootView == null) {
			return;
		}

		View view = rootView.findViewById(R.id.contacts_teachers_title_layout);
		if (view != null) {
			view.setOnClickListener(this);
			this.teachersArrow = (ImageView) view.findViewById(R.id.contacts_teachers_arrow);
		}

		GridView gridView = (GridView) rootView.findViewById(R.id.contacts_teachers);
		if (gridView == null) {
			return;
		}
		AdapterViewHelper gridViewHelper = new MyAdapterViewHelper(getActivity(),
				gridView, R.layout.contacts_grid_item) {
			@Override
			public void loadData() {
				loadContacts();
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				if (isAddReporterPermission) {
					ImageView imageView = (ImageView) view.findViewById(R.id.circle_icon);
					if (imageView != null) {
						getIsSelect(teacherData, imageView, position);
						imageView.setVisibility(View.VISIBLE);
					}
				}
				TextView textView = (TextView) view.findViewById(R.id.contacts_item_indicator);
				textView.setVisibility(View.INVISIBLE);
				return view;
			}

			@Override
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				if (!isAddReporterPermission) {
					super.onItemClick(parent, view, position, id);
				} else {
					ContactsClassMemberInfo data = (ContactsClassMemberInfo) getDataAdapter().getItem(position);
					if (isAddTeachers && isExist(data)){
						ToastUtil.showToast(getActivity(),R.string.unable_to_add_teacher);
					}else {
						setIsSelect(teacherData, data, position);
					}
				}
			}
		};
		this.teachersGridView = gridView;

		View itemView = rootView.findViewById(R.id.contacts_qrcode_attr);
		if (itemView != null) {
			TextView textView = (TextView) itemView.findViewById(R.id.contacts_attribute_key);
			if (textView != null) {
				textView.setText(getText(R.string.school_qrcode));
			}
			textView = (TextView) itemView.findViewById(R.id.contacts_attribute_value);
			if (textView != null) {
				textView.setText("");
			}
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					enterQrCodeDetails();
				}
			});
			itemView.setVisibility(View.INVISIBLE);
		}

		Button button = (Button) rootView.findViewById(R.id.contacts_send_message);
		if (button != null) {
			button.setOnClickListener(this);
			this.sendMessageBtn = button;
			if (isAddReporterPermission) {
				button.setText(getString(R.string.confirm));
			}
		}

		view = rootView.findViewById(R.id.contacts_school_details_layout);
		if (view != null) {
			view.setVisibility(View.INVISIBLE);
		}

		setCurrAdapterViewHelper(gridView, gridViewHelper);
	}

	/**
	 * 判断老师是否在本班级存在(老师或者学生)
	 * @param data
	 * @return
	 */
	private boolean isExist(ContactsClassMemberInfo data) {
		String memberId = data.getMemberId();
		for (ContactsClassMemberInfo info : students){
			if (memberId.equals(info.getMemberId())){
				return true;
			}
		}
		for (ContactsClassMemberInfo info : teachers){
			if (memberId.equals(info.getMemberId())){
				return true;
			}
		}
		return false;
	}

	private void showViews(boolean show) {
		View rootView = getView();
		if (rootView == null) {
			return;
		}

		int visible = show ? View.VISIBLE : View.INVISIBLE;
		View itemView = rootView.findViewById(R.id.contacts_qrcode_attr);
		if (itemView != null) {
			if (isAddReporterPermission) {
				itemView.setVisibility(View.GONE);
			} else {
				itemView.setVisibility(visible);
			}
		}

		View view = rootView.findViewById(R.id.contacts_school_details_layout);
		if (view != null) {
			view.setVisibility(visible);
		}
	}

	private void loadViews() {
		if (getCurrAdapterViewHelper().hasData()) {
			getCurrAdapterViewHelper().update();
		} else {
			loadContacts();
		}
	}

	private void loadContacts() {
		if (getUserInfo() == null) {
			return;
		}
		Map<String, Object> params = new HashMap();
		params.put("MemberId", getUserInfo().getMemberId());
		params.put("SchoolId", this.schoolId);
		DefaultPullToRefreshListener listener =
				new DefaultPullToRefreshListener<ContactsClassMemberListResult>(
						ContactsClassMemberListResult.class) {
					@Override
					public void onSuccess(String jsonString) {
						if (getActivity() == null) {
							return;
						}
						super.onSuccess(jsonString);
						ContactsClassMemberListResult result = getResult();
						if (result == null || !result.isSuccess()
								|| result.getModel() == null) {
							return;
						}
						updateViews(result);
					}
				};
		listener.setShowLoading(true);
		postRequest(ServerUrl.CONTACTS_CLASS_MEMBER_LIST_URL, params, listener);
	}

	private void updateViews(ContactsClassMemberListResult result) {
		List<ContactsClassMemberInfo> list = result.getModel().getClassMailListDetailList();
		if (list == null || list.size() <= 0) {
			return;
		}

		List teachers = new ArrayList();
		List leavedTeachers = new ArrayList();

		UserInfo userInfo = getUserInfo();

		if (list != null && list.size() > 0) {
			for (ContactsClassMemberInfo obj : list) {

				if (obj != null) {
					if (obj.getWorkingState() != 0) {
						//过滤重复数据,用于显示统计的人数。
						this.membersMap.put(obj.getMemberId(), obj);
						teachers.add(obj);
					} else {
						leavedTeachers.add(obj);
					}
				}

				if (userInfo.getMemberId().equals(obj.getMemberId())) {
					getView().findViewById(R.id.contacts_school_members_layout)
							.setVisibility(View.VISIBLE);
					if (!getArguments().getBoolean(Constants.EXTRA_CONTACTS_FROM_CHAT)) {
						//                        this.sendMessageBtn.setVisibility(View.VISIBLE);
						this.sendMessageBtn.setVisibility(View.GONE);
					}
				}
			}
		}
		if (isAddReporterPermission && reporters != null && reporters.size() > 0) {
			//筛选已经存在的记者
			for (int i = 0; i < reporters.size(); i++) {
				String memberId = reporters.get(i).getMemberId();
				if (teachers.size() > 0) {
					for (int j = 0; j < teachers.size(); j++) {
						ContactsClassMemberInfo memberInfo = (ContactsClassMemberInfo) teachers.get(j);
						if (memberId.equals(memberInfo.getMemberId())) {
							teachers.remove(memberInfo);
							break;
						}
					}
				}
			}
		}
		if (isAddReporterPermission) {
			int count = 0;
			if (teachers.size() > 0) {
				count = count + teachers.size();
				//this.sendMessageBtn.setVisibility(View.VISIBLE);
			} else {
				findViewById(R.id.contacts_teachers_title_layout).setVisibility(View.GONE);
				this.sendMessageBtn.setVisibility(View.GONE);
				TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.no_more_data));
			}
			TextView tvTitle = (TextView) getView().findViewById(R.id.contacts_header_title);
			if (tvTitle != null) {
				tvTitle.setText(new StringBuilder(this.groupName)
						.append("(").append(count).append(")").toString());
			}
		} else {
			TextView textView = (TextView) getView().findViewById(R.id.contacts_header_title);
			if (textView != null) {
				textView.setOnClickListener(this);
				//显示的时候，需要过滤memberId重复的数据。
				textView.setText(new StringBuilder(this.groupName)
						.append("(").append(this.membersMap.size()).append(")").toString());
			}
		}
		//        teachers.addAll(leavedTeachers);
		getCurrAdapterViewHelper().setData(teachers);
		showViews(true);

	}

	private void enterQrCodeDetails() {
		Bundle args = new Bundle();
		args.putString(ContactsQrCodeDetailsActivity.EXTRA_TITLE,
				getString(R.string.school_qrcode));
		args.putInt(ContactsQrCodeDetailsActivity.EXTRA_TARGET_TYPE,
				ContactsQrCodeDetailsActivity.TARGET_TYPE_CLASS);
		args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_ID, this.groupId);
		args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_NAME, this.groupName);
		args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_DESCRIPTION, this.schoolName);
		Intent intent = new Intent(getActivity(), ContactsQrCodeDetailsActivity.class);
		intent.putExtras(args);
		startActivity(intent);
	}

	private void enterBuildClass() {
		ActivityUtils.gotoCreateClass(getActivity(), schoolId);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.contacts_header_right_btn) {
			if (isAddReporterPermission){
				if (isAddTeachers) {
					addTeachers();
				} else {
					AddReporterPermission();
				}
			}else {
				enterBuildClass();
			}
		} else if (v.getId() == R.id.contacts_send_message) {
			if (isAddReporterPermission) {
				if (isAddTeachers) {
					addTeachers();
				} else {
					AddReporterPermission();
				}
			} else {
				enterGroupConversation(this.hxGroupId, this.groupName, ChatActivity.FROM_SCHOOL);
			}
		} else if (v.getId() == R.id.contacts_teachers_title_layout) {
			v.setSelected(!v.isSelected());
			this.teachersArrow.setImageResource(!v.isSelected() ?
					R.drawable.list_exp_up : R.drawable.list_exp_down);
			this.teachersGridView.setVisibility(!v.isSelected() ?
					View.VISIBLE : View.GONE);
		} else {
			super.onClick(v);
		}
	}

	/**
	 * 添加老师到指定班级
	 */
	private void addTeachers() {
		List<SelectHelper> allSelectData = new ArrayList<>();
		if (teacherData.size() == 0){
			ToastUtil.showToast(getActivity(),R.string.pls_select_a_teacher);
			return;
		}
		if (teacherData.size() > 0) {
			allSelectData.addAll(teacherData);
		}
		List<String> teachers = new ArrayList<>();
		if (allSelectData.size() > 0) {
			for (int i = 0; i < allSelectData.size(); i++) {
				teachers.add(allSelectData.get(i).memberId);
			}
		}
		Map<String, Object> params = new HashMap<>();
		params.put("ClassId", classId);
		params.put("TeacherIdList", teachers);
		RequestHelper.RequestModelResultListener listener = new RequestHelper.RequestModelResultListener<ModelResult>(getActivity(), ModelResult.class) {
			@Override
			public void onSuccess(String jsonString) {
				if (TextUtils.isEmpty(jsonString)) {
					return;
				}
				org.json.JSONObject jsonObject = null;
				try {
					jsonObject = new org.json.JSONObject(jsonString);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				boolean hasError = jsonObject.optBoolean("HasError");
				if (!hasError) {
					ToastUtil.showToast(getActivity(),R.string.add_teacher_success);
					getActivity().setResult(Activity.RESULT_OK);
					finish();
				}
			}
		};

		listener.setShowLoading(true);
		RequestHelper.sendPostRequest(getActivity(), ServerUrl.ADD_TEACHERS_TO_CLASS,
				params, listener);


	}

	private void AddReporterPermission() {
		List<SelectHelper> allSelectData = new ArrayList<>();
		if (teacherData.size() > 0) {
			allSelectData.addAll(teacherData);
		}
		if (allSelectData.size() > 0) {
			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < allSelectData.size(); i++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("SchoolId", schoolId);
				jsonObject.put("MemberId", allSelectData.get(i).memberId);
				jsonObject.put("ClassId", classId);
				jsonArray.add(jsonObject);
			}
			addReporterItem(jsonArray);
		}
	}

	/**
	 * 增加一个记者的权限
	 */
	private void addReporterItem(JSONArray jsonArray) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Data", jsonArray);
		RequestHelper.RequestListener listener =
				new RequestHelper.RequestDataResultListener<DataModelResult>(
						getActivity(), DataModelResult.class) {
					@Override
					public void onSuccess(String jsonString) {
						if (getActivity() == null) {
							return;
						}
						super.onSuccess(jsonString);
						if (getResult() == null || !getResult().isSuccess()) {
							return;
						} else {
							String errorMessage = getResult().getErrorMessage();
							if (TextUtils.isEmpty(errorMessage)) {
								getActivity().setResult(Activity.RESULT_OK);
								getActivity().finish();
								TipsHelper.showToast(getActivity(), getString(R.string.add_reporterId_success));
							} else {
								TipMsgHelper.ShowLMsg(getActivity(), errorMessage);
							}
						}
					}
				};
		listener.setShowLoading(true);
		RequestHelper.sendPostRequest(getActivity(), ServerUrl.ADD_REPORTER_PERMISSION_BASE_URL,
				params, listener);
	}

	private class SelectHelper {
		String schoolId;
		String memberId;
		String classId;
		int position;
	}

	private void getIsSelect(List<SelectHelper> selectHelpers, ImageView imageView, int position) {
		boolean flag = false;
		if (selectHelpers != null && selectHelpers.size() > 0) {
			for (int i = 0; i < selectHelpers.size(); i++) {
				int pos = selectHelpers.get(i).position;
				if (pos == position) {
					imageView.setSelected(true);
					flag = true;
				}
			}
		}
		if (!flag) {
			imageView.setSelected(false);
		}
	}

	private void setIsSelect(List<SelectHelper> selectHelpers, ContactsClassMemberInfo data, int position) {
		if (selectHelpers.size() == 0) {
			SelectHelper selectHelper = new SelectHelper();
			selectHelper.classId = data.getClassId();
			selectHelper.memberId = data.getMemberId();
			selectHelper.schoolId = data.getSchoolId();
			selectHelper.position = position;
			selectHelpers.add(selectHelper);
		} else {
			boolean flag = false;
			for (int i = 0; i < selectHelpers.size(); i++) {
				int pos = selectHelpers.get(i).position;
				if (pos == position) {
					selectHelpers.remove(i);
					flag = true;
				}
			}
			if (!flag) {
				SelectHelper selectHelper = new SelectHelper();
				selectHelper.classId = data.getClassId();
				selectHelper.memberId = data.getMemberId();
				selectHelper.schoolId = data.getSchoolId();
				selectHelper.position = position;
				selectHelpers.add(selectHelper);
			}
		}
		getCurrAdapterViewHelper().update();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ActivityUtils.REQUEST_CODE_RETURN_REFRESH && data != null) {
			boolean masterChanged = data.getBooleanExtra(ContactsSelectClassHeadTeacherFragment.Constants
					.EXTRA_CLASS_HEADTEACHER_CHANGED, false);
			if (masterChanged) {
				ContactsClassMemberInfo info = data.getParcelableExtra(RemoveAddressBookFragment
						.HEADMASTER_MEMBER_INFO);
				if (info != null) {
					checkTeacherRoleInfo(info, false, true);
				}
			}
		}
	}

	/**
	 * 更新列表数据
	 */
	private void upDateListData(ContactsClassMemberInfo memberInfo) {
		List<ContactsClassMemberInfo> infoList = getCurrAdapterViewHelper().getData();
		if (infoList != null && infoList.size() > 0) {
			for (int i = 0, len = infoList.size(); i < len; i++) {
				if (TextUtils.equals(memberInfo.getMemberId(), infoList.get(i).getMemberId())) {
					infoList.remove(i);
					break;
				}
			}
			getCurrAdapterViewHelper().update();
		}
	}

}
