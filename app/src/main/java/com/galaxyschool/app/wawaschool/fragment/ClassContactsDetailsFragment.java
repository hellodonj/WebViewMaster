package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.ContactsActivity;
import com.galaxyschool.app.wawaschool.ContactsClassManagementActivity;
import com.galaxyschool.app.wawaschool.ContactsPickerActivity;
import com.galaxyschool.app.wawaschool.ContactsQrCodeDetailsActivity;
import com.galaxyschool.app.wawaschool.QrcodeProcessActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SearchStudentAccountActivity;
import com.galaxyschool.app.wawaschool.chat.activity.ChatActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.DataAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberListResult;
import com.galaxyschool.app.wawaschool.pojo.QrcodeClassInfo;
import com.galaxyschool.app.wawaschool.pojo.Reporter;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfoResult;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.ContactsListDialog;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.HeadTeacherCreateStudentInputBoxDialog;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.osastudio.common.library.ActivityStack;
import com.umeng.socialize.media.UMImage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ClassContactsDetailsFragment extends GroupContactsListFragment {

	public static final String TAG = ClassContactsDetailsFragment.class.getSimpleName();
	public static final int REQUEST_ADD_TEACHER = 1;


	private List students;
	private List teachers;
	private boolean isOnlineSchool;

	public interface Constants extends GroupContactsListFragment.Constants {
		int REQUEST_CODE_CLASS_DETAILS = 6102;
		String EXTRA_CLASS_HEADTEACHER_ID = "headTeacherId";
		String EXTRA_CLASS_HEADTEACHER_NAME = "headTeacherName";
		String EXTRA_CLASS_STATUS = "classStatus";
		String EXTRA_CLASS_DETAILS_CHANGED = "detailsChanged";
		String EXTRA_CLASS_NAME_CHANGED = "nameChanged";
		String EXTRA_CLASS_HEADTEACHER_CHANGED = "headTeacherChanged";
		String EXTRA_CLASS_STATUS_CHANGED = "statusChanged";
		int CLASS_STATUS_HISTORY = ContactsClassManagementActivity.CLASS_STATUS_HISTORY;
		int CLASS_STATUS_PRESENT = ContactsClassManagementActivity.CLASS_STATUS_PRESENT;
		int CLASS_STATUS_END_THE_LECTURE = ContactsClassManagementActivity.CLASS_STATUS_END_THE_LECTURE;
		String IS_ONLINE_SCHOOL = "is_online_school";
	}


	private GridView teachersGridView;
	private GridView studentsGridView;
	private GridView parentsGridView;
	private Button sendMessageBtn;
	private ImageView teachersArrow, studentsArrow, parentsArrow;
	private ContactsClassMemberInfo headTeacherInfo;
	private String headTeacherId;
	private SubscribeClassInfo classInfo;
	private ContactsClassMemberListResult classMemberListResult;
	private String from = null;
	private boolean isAddReporterPermission;
	private List<SelectHelper> teacherData = new ArrayList<>();
	private List<SelectHelper> studentData = new ArrayList<>();
	private List<SelectHelper> parentData = new ArrayList<>();
	private static boolean hasClassContentChanged;
	private List<Reporter> reporters = new ArrayList<>();
	private boolean isFromCreateOnlline;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.contacts_class_details, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	@Override
	public void onResume() {
		super.onResume();
		loadClassInfo();
	}

	private void loadViews() {
		AdapterViewHelper teachersHelper = getAdapterViewHelper(
				String.valueOf(this.teachersGridView.getId()));
		AdapterViewHelper studentsHelper = getAdapterViewHelper(
				String.valueOf(this.studentsGridView.getId()));
		AdapterViewHelper parentsHelper = getAdapterViewHelper(
				String.valueOf(this.parentsGridView.getId()));
		if (classMemberListResult != null) {
			teachersHelper.update();
			studentsHelper.update();
			parentsHelper.update();
		} else {
			loadContacts();
		}
	}

	private void init() {
		isOnlineSchool = getArguments().getBoolean(Constants.IS_ONLINE_SCHOOL, false);
		this.classStatus = getArguments().getInt(Constants.EXTRA_CLASS_STATUS);
		this.from = getArguments().getString("from");
		if (from != null && from.equals(ReporterIdentityFragment.TAG)) {
			isAddReporterPermission = true;
			reporters = getArguments().getParcelableArrayList(ContactsActivity
					.EXTRA_CONTACTS_FOR_REPORTER);
			//如果是来自创建直播 没有传直接的memberId
			if (reporters == null) {
				isFromCreateOnlline = true;
			}
		}
		initTitle();
		initViews();
	}

	private void controlBackLogic() {

		ActivityStack stack = ActivityStack.getInstance();
		int count = stack.getCount();
		if (!TextUtils.isEmpty(from) && this.from.equals(GroupExpandListFragment.TAG)) {
			//从班级通讯录里面进入，只返回一级即可。
			getActivity().finish();
		} else {
			if (!TextUtils.isEmpty(from) && this.from.equals(ClassDetailsFragment.TAG)) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putBoolean("back", true);
				intent.putExtras(bundle);
				getActivity().setResult(Activity.RESULT_OK, intent);
				getActivity().finish();
			} else {
				//其他情况都退回两级。
				if (count > 2) {
					for (int i = 0; i < 2; i++) {
						stack.pop().finish();
					}
				}
			}
		}
	}

	private void initTitle() {
		TextView textView = (TextView) findViewById(R.id.contacts_header_title);
		if (textView != null) {
			textView.setOnClickListener(this);
			textView.setText(this.groupName);
		}

		ImageView imageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
		if (imageView != null) {
			imageView.setOnClickListener(this);
		}

		textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
		if (textView != null) {
			textView.setText(R.string.class_management);
			textView.setTextColor(getResources().getColor(R.color.text_green));
			textView.setVisibility(View.INVISIBLE);
			textView.setOnClickListener(this);
		}
	}

	private void initViews() {
		View view = findViewById(R.id.contacts_teachers_title_layout);
		if (view != null) {
			view.setOnClickListener(this);
			this.teachersArrow = (ImageView) view.findViewById(R.id.contacts_teachers_arrow);
		}
		TextView textView = (TextView) findViewById(R.id.contacts_teachers_title);
		if (textView != null) {
			textView.setText(R.string.teacher);
			textView.setVisibility(View.INVISIBLE);
		}

		AdapterViewHelper gridViewHelper = null;
		GridView gridView = (GridView) findViewById(R.id.contacts_teachers);
		if (gridView != null) {
			gridViewHelper = new MyAdapterViewHelper(getActivity(),
					gridView, R.layout.contacts_grid_item) {
				@Override
				public void loadData() {
					loadContacts();
				}

				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View view = super.getView(position, convertView, parent);
					ContactsClassMemberInfo data = (ContactsClassMemberInfo) getDataAdapter().getItem(position);

					ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
					TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
					TextView indicatorTextView = (TextView) view.findViewById(R.id.contacts_item_indicator);
					if (data.getId() == null) {
						getThumbnailManager().displayUserIconWithDefault(
								AppSettings.getFileUrl(data.getHeadPicUrl()), imageView,
								R.drawable.create_student);
						textView.setTextColor(getResources().getColor(R.color.text_green));
						textView.setText(R.string.add_teacher);
						indicatorTextView.setVisibility(View.GONE);
						if (isHeadTeacher()) {
							textView.setVisibility(View.VISIBLE);
							imageView.setVisibility(View.VISIBLE);
						} else {
							imageView.setVisibility(View.GONE);
							textView.setVisibility(View.GONE);
						}
					} else {
						textView.setTextColor(getResources().getColor(R.color.black));
						getThumbnailManager().displayUserIconWithDefault(
								AppSettings.getFileUrl(data.getHeadPicUrl()), imageView,
								R.drawable.default_user_icon);
					}

					if (isAddReporterPermission) {
						ImageView ivIcon = (ImageView) view.findViewById(R.id.circle_icon);
						if (ivIcon != null) {
							getIsSelect(teacherData, ivIcon, position);
							ivIcon.setVisibility(View.VISIBLE);
						}
					}
					return view;
				}

				@Override
				public void onItemClick(AdapterView parent, View view, int position, long id) {
					if (!isAddReporterPermission) {
						ViewHolder holder = (ViewHolder) view.getTag();
						if (holder == null) {
							return;
						}
						ContactsClassMemberInfo item = (ContactsClassMemberInfo) holder.data;
						if (item.getId() == null) {
							enterTeacherContacts();
						} else {
							super.onItemClick(parent, view, position, id);
						}
					} else {
						ContactsClassMemberInfo data = (ContactsClassMemberInfo) getDataAdapter().getItem(position);
						setIsSelect(teacherData, data, position, String.valueOf(teachersGridView.getId()));
					}
				}
			};
			addAdapterViewHelper(String.valueOf(gridView.getId()),
					gridViewHelper);
			this.teachersGridView = gridView;
		}

		view = findViewById(R.id.contacts_students_title_layout);
		if (view != null) {
			view.setOnClickListener(this);
			this.studentsArrow = (ImageView) view.findViewById(R.id.contacts_students_arrow);
		}
		textView = (TextView) findViewById(R.id.contacts_students_title);
		if (textView != null) {
			textView.setText(R.string.student);
			textView.setVisibility(View.INVISIBLE);
		}
		gridView = (GridView) findViewById(R.id.contacts_students);
		if (gridView != null) {
			gridViewHelper = new MyAdapterViewHelper(getActivity(),
					gridView, R.layout.contacts_grid_item) {
				@Override
				public void loadData() {
					loadContacts();
				}

				//重写一下父类的方法
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View view = super.getView(position, convertView, parent);
					ContactsClassMemberInfo data = (ContactsClassMemberInfo) getDataAdapter().getItem(position);
					if (data == null) {
						return view;
					}
					ViewHolder holder = (ViewHolder) view.getTag();
					if (holder == null) {
						holder = new ViewHolder();
					}
					holder.data = data;
					ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
					TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
					TextView indicatorTextView = (TextView) view.findViewById(R.id.contacts_item_indicator);
					//根据字段来控制资源
					if (data.getId() == null) {
						getThumbnailManager().displayUserIconWithDefault(
								AppSettings.getFileUrl(data.getHeadPicUrl()), imageView,
								R.drawable.create_student);
						textView.setTextColor(getResources().getColor(R.color.text_green));
						textView.setText(R.string.add_student);
						indicatorTextView.setVisibility(View.GONE);
						//只有班主任才有权创建、移出学生。
						if (isHeadTeacher()) {
							textView.setVisibility(View.VISIBLE);
							imageView.setVisibility(View.VISIBLE);
						} else {
							imageView.setVisibility(View.GONE);
							textView.setVisibility(View.GONE);
						}
					} else {
						textView.setTextColor(getResources().getColor(R.color.black));
						getThumbnailManager().displayUserIconWithDefault(
								AppSettings.getFileUrl(data.getHeadPicUrl()), imageView,
								R.drawable.default_user_icon);
					}
					if (isAddReporterPermission) {
						imageView = (ImageView) view.findViewById(R.id.circle_icon);
						if (imageView != null) {
							getIsSelect(studentData, imageView, position);
							imageView.setVisibility(View.VISIBLE);
						}
					}
					return view;
				}

				@Override
				public void onItemClick(AdapterView parent, View view, int position, long id) {
					if (!isAddReporterPermission) {
						ViewHolder holder = (ViewHolder) view.getTag();
						if (holder == null) {
							return;
						}
						ContactsClassMemberInfo item = (ContactsClassMemberInfo) holder.data;
						if (item.getId() == null) {
							//过滤班主任，防止切换班主任数据没刷新。
							if (isHeadTeacher()) {
								showPopupWindow();
							}
						} else {
							showMemberMenu(item);
						}
					} else {
						ContactsClassMemberInfo data = (ContactsClassMemberInfo) getDataAdapter().getItem(position);
						setIsSelect(studentData, data, position, String.valueOf(studentsGridView.getId()));
					}

				}
			};
			addAdapterViewHelper(String.valueOf(gridView.getId()),
					gridViewHelper);
			this.studentsGridView = gridView;
		}

		view = findViewById(R.id.contacts_parents_title_layout);
		if (view != null) {
			view.setOnClickListener(this);
			this.parentsArrow = (ImageView) view.findViewById(R.id.contacts_parents_arrow);
		}
		textView = (TextView) findViewById(R.id.contacts_parents_title);
		if (textView != null) {
			textView.setText(R.string.parent);
			textView.setVisibility(View.INVISIBLE);
		}
		gridView = (GridView) findViewById(R.id.contacts_parents);
		if (gridView != null) {
			gridViewHelper = new MyAdapterViewHelper(getActivity(),
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
							getIsSelect(parentData, imageView, position);
							imageView.setVisibility(View.VISIBLE);
						}
					}
					return view;
				}

				@Override
				public void onItemClick(AdapterView parent, View view, int position, long id) {
					if (!isAddReporterPermission) {
						super.onItemClick(parent, view, position, id);
					} else {
						ContactsClassMemberInfo data = (ContactsClassMemberInfo) getDataAdapter().getItem(position);
						setIsSelect(parentData, data, position, String.valueOf(parentsGridView.getId()));
					}
				}
			};
			addAdapterViewHelper(String.valueOf(gridView.getId()),
					gridViewHelper);
			this.parentsGridView = gridView;
		}

		View itemView = findViewById(R.id.contacts_qrcode_attr);
		if (itemView != null) {
			textView = (TextView) itemView.findViewById(R.id.contacts_attribute_key);
			if (textView != null) {
				textView.setText(getText(R.string.class_qrcode));
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

		//退出班级
		itemView = findViewById(R.id.contacts_exit_class_layout);
		if (itemView != null) {
			textView = (TextView) itemView.findViewById(R.id.contacts_attribute_key);
			if (textView != null) {
				textView.setText(getText(R.string.exit_class));
			}
			textView = (TextView) itemView.findViewById(R.id.contacts_attribute_value);
			if (textView != null) {
				textView.setText("");
			}
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showExitFromClassDialog();
				}
			});
			itemView.setVisibility(View.GONE);
		}

		itemView = findViewById(R.id.contacts_assign_homework_layout);
		if (itemView != null) {
			textView = (TextView) itemView.findViewById(R.id.contacts_attribute_key);
			if (textView != null) {
				textView.setText(getText(R.string.assign_homework));
			}
			textView = (TextView) itemView.findViewById(R.id.contacts_attribute_value);
			if (textView != null) {
				textView.setText("");
			}
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!TextUtils.isEmpty(getStudentIds())) {
						assignHomework();
					} else {
						TipsHelper.showToast(getActivity(), getString(R.string.no_students));
					}
				}
			});
			itemView.setVisibility(View.GONE);
		}
		itemView = findViewById(R.id.contacts_forbid_chat_layout);
		if (itemView != null) {
			textView = (TextView) itemView.findViewById(R.id.contacts_attribute_key);
			if (textView != null) {
				textView.setText(getText(R.string.forbid_class_chat));
			}
			textView = (TextView) itemView.findViewById(R.id.contacts_attribute_value);
			if (textView != null) {
				textView.setText("");
			}
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					forbidClassChat();
				}
			});
			itemView.setVisibility(View.GONE);
		}
		itemView = findViewById(R.id.contacts_invite_layout);
		if (itemView != null) {
			textView = (TextView) itemView.findViewById(R.id.contacts_attribute_key);
			if (textView != null) {
				textView.setText(getText(R.string.invite_parents_to_join_class));
			}
			textView = (TextView) itemView.findViewById(R.id.contacts_attribute_value);
			if (textView != null) {
				textView.setText("");
			}
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					inviteParents();
				}
			});
			itemView.setVisibility(View.GONE);
		}

		//班级分组
		itemView = findViewById(R.id.contacts_class_group);
		if (itemView != null) {
			textView = (TextView) itemView.findViewById(R.id.contacts_attribute_key);
			if (textView != null) {
				textView.setText(getText(R.string.str_class_group));
			}
			textView = (TextView) itemView.findViewById(R.id.contacts_attribute_value);
			if (textView != null) {
				textView.setText("");
			}
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					doGroup();
				}
			});

			itemView.setVisibility(View.GONE);
		}


		Button button = (Button) findViewById(R.id.contacts_send_message);
		if (button != null) {
			button.setOnClickListener(this);
			this.sendMessageBtn = button;
		}

		view = findViewById(R.id.contacts_class_details_layout);
		if (view != null) {
			view.setVisibility(View.INVISIBLE);
		}
	}

	private void enterTeacherContacts() {
		Bundle args = new Bundle();
		if (classInfo.isClass() || classInfo.isSchool()) {
			args.putInt(ContactsActivity.EXTRA_CONTACTS_TYPE, ContactsActivity.CONTACTS_TYPE_SCHOOL);
			args.putString(ContactsActivity.EXTRA_CONTACTS_NAME,classInfo.getSchoolName());
			args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_ID,classInfo.getSchoolId());
			args.putString(ContactsActivity.EXTRA_CONTACTS_CLASS_ID,classInfo.getClassId());
			args.putBoolean(SchoolContactsDetailsFragment.IS_ADD_TEACHERS,true);
			args.putSerializable(SchoolContactsDetailsFragment.STUDENT_DATA,(Serializable)students);
			args.putSerializable(SchoolContactsDetailsFragment.TEACHER_DATA,(Serializable)teachers);
			args.putString("from", ReporterIdentityFragment.TAG);
		}
		Intent intent = new Intent(getActivity(), ContactsActivity.class);
		intent.putExtras(args);
		startActivityForResult(intent,REQUEST_ADD_TEACHER);
	}

	private void showExitFromClassDialog() {
		ContactsMessageDialog messageDialog = new ContactsMessageDialog(getActivity(), null,
				getString(R.string.want_to_exit_class), getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}, getString(R.string.confirm), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				exitFromClass();
			}
		});
		messageDialog.show();
		//        Window window = messageDialog.getWindow();
		//        WindowManager windowManager = getActivity().getWindowManager();
		//        Display display = windowManager.getDefaultDisplay();
		//        WindowManager.LayoutParams lp = window.getAttributes();
		//        lp.width = (int)(display.getWidth());
		//        window.setAttributes(lp);

	}

	/**
	 * 退出班级,目前仅对家长和学生开放。
	 */
	private void exitFromClass() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ClassId", classId);
		params.put("StudentId", getMemeberId());

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
							//                            if (classInfo != null) {
							//                                ConversationHelper.deleteGroupConversation(classInfo.getGroupId());
							//                            }
							//设置标志位
							setHasClassContentChanged(true);
							TipsHelper.showToast(getActivity(), R.string.exit_class_success);
							//返回
							controlBackLogic();
							//关注/取消关注成功后，向校园空间发广播
							MySchoolSpaceFragment.sendBrocast(getActivity());
						}
					}
				};
		listener.setShowLoading(true);
		RequestHelper.sendPostRequest(getActivity(), ServerUrl.REMOVE_CLASS_MEMBER_FROM_CLASS_URL,
				params, listener);
	}

	/**
	 * 班主任移出班级某个成员，不包括班主任自己。
	 *
	 * @param info
	 */
	@Override
	protected void removeFromClass(ContactsClassMemberInfo info) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ClassId", classId);
		params.put("StudentId", info.getMemberId());
		//可选，0：老师，1：学生，2：家长 不传会删除studentId
		params.put("Role", info.getRole());
		//该参数用来判断是否有权限移出用户（只有班主任才有此权限）
		params.put("HeadmasterId", headTeacherId);

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
							TipsHelper.showToast(getActivity(), R.string.out_of_class_success);
							loadContacts();
						}
					}
				};
		listener.setShowLoading(true);
		RequestHelper.sendPostRequest(getActivity(), ServerUrl.REMOVE_CLASS_MEMBER_FROM_CLASS_URL,
				params, listener);
	}

	private void showPopupWindow() {
		List<String> contentList = new ArrayList();
		contentList.add(getString(R.string.create_student));
		contentList.add(getString(R.string.search_student_account));
		contentList.add(getString(R.string.import_student_from_address_book));

		ContactsListDialog dialog = new ContactsListDialog(getActivity(),
				R.style.Theme_ContactsDialog, null,
				contentList, R.layout.contacts_dialog_list_text_item,
				new DataAdapter.AdapterViewCreator() {
					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						if (convertView != null) {
							String title = (String) convertView.getTag();
							TextView textView = (TextView) convertView.findViewById(
									R.id.contacts_dialog_list_item_title);
							if (textView != null) {
								textView.setTextColor(Color.parseColor("#038bff"));
								textView.setText(title);
							}
						}
						return convertView;
					}
				},
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						String title = (String) view.getTag();
						if (getString(R.string.create_student).equals(title)) {
							//新建学生
							showCreateStudentDialog();
						} else if (getString(R.string.search_student_account).equals(title)) {
							//搜索学生账号
							searchStudentAccount();
							classMemberListResult = null; //force update after add student
						} else if (getString(R.string.import_student_from_address_book).equals(title)) {
							//从通讯录导入
							importStudentFromAddressBook();
						}
					}
				}, getString(R.string.cancel), null);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		dialog.show();
	}

	private void searchStudentAccount() {
		Intent intent = new Intent(getActivity(), SearchStudentAccountActivity.class);
		intent.putExtra("title", getString(R.string.search_student_account));
		intent.putExtra(ContactsPickerActivity.EXTRA_MY_CLASS_ID, classId);
		startActivity(intent);
	}

	private void importStudentFromAddressBook() {
		enterContactsPicker();
		classMemberListResult = null; //force update after add student
	}

	private void enterContactsPicker() {
		Bundle args = new Bundle();
		args.putBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER, true);
		args.putInt(ContactsPickerActivity.EXTRA_PICKER_TYPE,
				ContactsPickerActivity.PICKER_TYPE_PERSONAL | ContactsPickerActivity.PICKER_TYPE_MEMBER);
		args.putInt(
				ContactsPickerActivity.EXTRA_GROUP_TYPE,
				ContactsPickerActivity.GROUP_TYPE_CLASS);
		args.putInt(ContactsPickerActivity.EXTRA_MEMBER_TYPE,
				ContactsPickerActivity.MEMBER_TYPE_ALL);
		args.putInt(ContactsPickerActivity.EXTRA_PICKER_MODE,
				ContactsPickerActivity.PICKER_MODE_MULTIPLE);
		args.putString(
				ContactsPickerActivity.EXTRA_PICKER_CONFIRM_BUTTON_TEXT, getString(R.string.confirm));
		args.putBoolean(ContactsPickerActivity.EXTRA_PUBLISH_CHAT_RESOURCE, true);
		args.putBoolean(ContactsPickerActivity.EXTRA_PICKER_SUPERUSER, true);

		if (classId != null) {
			args.putString(ContactsPickerActivity.EXTRA_MY_CLASS_ID, classId);
			args.putBoolean(ContactsPickerActivity.EXTRA_ADD_STUDENT, true);
		}

		Intent intent = new Intent(getActivity(), ContactsPickerActivity.class);
		intent.putExtras(args);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	HeadTeacherCreateStudentInputBoxDialog doubleInputDialog;

	private void showCreateStudentDialog() {
		doubleInputDialog = new HeadTeacherCreateStudentInputBoxDialog(getActivity(),
				getResources().getString(R.string.add_student), null, "upperInputBoxHintText"
				, null, "lowerInputBoxHintText", "leftButtonText",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//取消
						dialog.dismiss();
					}
				}, getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//确认
				createStudent(doubleInputDialog);
			}
		});
		doubleInputDialog.setRealNameInputLength(20);
		doubleInputDialog.setIsAutoDismiss(false);
		doubleInputDialog.setCanceledOnTouchOutside(false);
		doubleInputDialog.show();
		doubleInputDialog.resizeDialog(0.9f);
	}

	private void createStudent(HeadTeacherCreateStudentInputBoxDialog dialog) {
		String userName = dialog.getUserNameInputText();
		String realName = dialog.getRealNameInputText();

		if (TextUtils.isEmpty(userName)) {
			TipMsgHelper.ShowMsg(getActivity(),
					getString(R.string.pls_input_username));
			return;
		}
		//用户名3—20个字符(仅限英文字母和数字)
		int length = userName.length();
		if (!(length >= 3 && length <= 20)) {
			TipMsgHelper.ShowMsg(getActivity(),
					getString(R.string.user_name_length_is_not_legal));
			return;
		}
		if (!Utils.isContainEnglish(userName)) {
			TipMsgHelper.ShowMsg(getActivity(),
					getString(R.string.user_name_not_containe_char));
			return;
		}
		//真实姓名（选填，一旦填写，就需要限制字符）
		if (!TextUtils.isEmpty(realName)) {
			//过滤特殊字符和表情
			if (!Utils.checkTitleValid(getActivity(), realName)) {
				return;
			}
			//用户真实姓名<=20个字符
			int size = realName.length();
			if (size > 20) {
				TipMsgHelper.ShowMsg(getActivity(),
						getString(R.string.real_name_length_is_not_legal));
				return;
			}
		}
		//默认密码
		String password = "111111";
		UIUtils.hideSoftKeyboard(getActivity());
		doubleInputDialog.dismiss();
		createStudent(userName, password, realName);
	}

	private void createStudent(String userName, String password, String realName) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (!TextUtils.isEmpty(userName)) {
			params.put("UserName", userName);
		}
		if (!TextUtils.isEmpty(password)) {
			params.put("Password", password);
		}

		if (!TextUtils.isEmpty(realName)) {
			params.put("RealName", realName);
		}
		params.put("ClassId", classId);
		//班主任Id
		params.put("MemberId", getMemeberId());
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
							TipsHelper.showToast(getActivity(), R.string.create_student_success);
							loadContacts();
						}
					}
				};
		listener.setShowLoading(true);
		RequestHelper.sendPostRequest(getActivity(), ServerUrl.CREATE_STUDENT_INTO_CLASS_URL,
				params, listener);
	}

	private void showViews(boolean show) {
		TextView textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
		if (textView != null && !isAddReporterPermission) {
			if (headTeacherInfo != null && headTeacherInfo.getMemberId().equals(getUserInfo().getMemberId())
					&& classStatus == ContactsClassManagementActivity.CLASS_STATUS_PRESENT) {
				textView.setVisibility(View.VISIBLE);
				textView = ((TextView) findViewById(R.id.contacts_header_title));
				if (textView != null) {
					RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
					lp.leftMargin = (int) (80 * getMyApplication().getScreenDensity(getActivity()));
					lp.rightMargin = lp.leftMargin;
					textView.setLayoutParams(lp);
					textView.setEllipsize(TextUtils.TruncateAt.END);
				}
			} else {
				textView.setVisibility(View.INVISIBLE);
			}
		}

		int visible = show ? View.VISIBLE : View.INVISIBLE;
		textView = (TextView) findViewById(R.id.contacts_teachers_title);
		if (textView != null) {
			textView.setVisibility(visible);
		}

		textView = (TextView) findViewById(R.id.contacts_students_title);
		if (textView != null) {
			textView.setVisibility(visible);
		}

		textView = (TextView) findViewById(R.id.contacts_parents_title);
		if (textView != null) {
			textView.setVisibility(visible);
		}
		if (isAddReporterPermission) {
			findViewById(R.id.contacts_forbid_chat_layout).setVisibility(View.GONE);
			findViewById(R.id.contacts_qrcode_attr).setVisibility(View.GONE);
			findViewById(R.id.contacts_invite_layout).setVisibility(View.GONE);
			findViewById(R.id.contacts_class_details_layout).setVisibility(View.GONE);
			findViewById(R.id.contacts_exit_class_layout).setVisibility(View.GONE);
			findViewById(R.id.contacts_add_class).setVisibility(View.GONE);
			findViewById(R.id.contacts_class_group).setVisibility(View.GONE);
			Button button = (Button) findViewById(R.id.contacts_send_message);
			button.setText(getString(R.string.confirm));
			//显示班级列表信息
			findViewById(R.id.contacts_class_details_layout).setVisibility(View.VISIBLE);
		} else {
			View itemView = findViewById(R.id.contacts_qrcode_attr);
			if (itemView != null) {
				if (classStatus == Constants.CLASS_STATUS_END_THE_LECTURE){
					itemView.setVisibility(View.GONE);
				} else {
					itemView.setVisibility(visible);
				}
			}

			itemView = findViewById(R.id.contacts_assign_homework_layout);
			if (itemView != null) {
				if (isTeacher()) {
					itemView.setVisibility(View.GONE);
				} else {
					itemView.setVisibility(View.GONE);
				}
			}
			itemView = findViewById(R.id.contacts_forbid_chat_layout);
			if (itemView != null) {
				if (isHeadTeacher() && classStatus == ContactsClassManagementActivity.CLASS_STATUS_PRESENT) {
					//                    itemView.setVisibility(View.VISIBLE);
					itemView.setVisibility(View.GONE);
				} else {
					itemView.setVisibility(View.GONE);
				}
			}

			itemView = findViewById(R.id.contacts_invite_layout);
			if (itemView != null) {
				if (classStatus == ContactsClassManagementActivity.CLASS_STATUS_PRESENT) {
					itemView.setVisibility(View.VISIBLE);
				} else {
					itemView.setVisibility(View.GONE);
				}
			}
			View view = findViewById(R.id.contacts_class_details_layout);
			if (view != null) {
				view.setVisibility(View.VISIBLE);
			}
			Button button = (Button) findViewById(R.id.contacts_send_message);
			if (classStatus == ContactsClassManagementActivity.CLASS_STATUS_PRESENT) {
				//                button.setVisibility(View.VISIBLE);
				button.setVisibility(View.GONE);
			} else {
				button.setVisibility(View.GONE);
			}

			//班主任不能退出班级，其他都可以。
			View exitClassView = findViewById(R.id.contacts_exit_class_layout);
			if (exitClassView != null) {
				if (isHeadTeacher()) {
					exitClassView.setVisibility(View.GONE);
				} else {
					if (classStatus == ContactsClassManagementActivity.CLASS_STATUS_PRESENT) {
						exitClassView.setVisibility(View.VISIBLE);
					} else {
						exitClassView.setVisibility(View.GONE);
					}
				}
			}

			//加入班级
			itemView = findViewById(R.id.contacts_add_class);
			if (classStatus == Constants.CLASS_STATUS_PRESENT) {
				if (itemView != null) {

					textView = (TextView) itemView.findViewById(R.id.contacts_attribute_key);
					if (textView != null) {
						textView.setText(getText(R.string.participate_class));
					}
					textView = (TextView) itemView.findViewById(R.id.contacts_attribute_value);
					if (textView != null) {
						textView.setText("");
					}
					itemView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							joinClass();
						}
					});
				}
			}
			if (classStatus == ContactsClassManagementActivity.CLASS_STATUS_PRESENT) {
				if (classInfo.isParentByRoles() || classInfo.isTeacherByRoles()) {
					itemView.setVisibility(View.VISIBLE);
				} else {
					itemView.setVisibility(View.GONE);
				}
			} else {
				itemView.setVisibility(View.GONE);
			}

			//班级分组
			itemView = findViewById(R.id.contacts_class_group);
			if (itemView != null) {
				if (classStatus == ContactsClassManagementActivity.CLASS_STATUS_PRESENT && !isOnlineSchool) {
					itemView.setVisibility(View.VISIBLE);
				} else {
					itemView.setVisibility(View.GONE);
				}
			}
		}
	}

	/**
	 * 班级分组
	 */
	private void doGroup() {
		ContactsClassGroupFragment fragment = ContactsClassGroupFragment.newInstance(isHeadTeacher(), isTeacher(), classMemberListResult, schoolId, classId);

		getFragmentManager().beginTransaction()
				.add(R.id.contacts_layout, fragment, ContactsClassGroupFragment.TAG)
				.hide(ClassContactsDetailsFragment.this)
				.addToBackStack(ContactsClassGroupFragment.TAG)
				.commit();
	}

	private void loadContacts() {
		if (getUserInfo() == null) {
			return;
		}
		Map<String, Object> params = new HashMap();
		params.put("MemberId", getUserInfo().getMemberId());
		params.put("Id", this.groupId);
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
		String serverUrl;
		if (classStatus == Constants.CLASS_STATUS_HISTORY) {
			//历史班
			serverUrl = ServerUrl.LOAD_HISTORY_CLASSMAIL_LIST_DETAIL_BASE_URL;
		} else {
			//开课班
			serverUrl = ServerUrl.CONTACTS_CLASS_MEMBER_LIST_URL;
		}
		postRequest(serverUrl, params, listener);
	}

	private void updateViews(ContactsClassMemberListResult result) {
		List<ContactsClassMemberInfo> list = result.getModel().getClassMailListDetailList();
		if (list == null || list.size() <= 0) {
			return;
		}

		TextView textView = (TextView) getView().findViewById(R.id.contacts_header_title);
		if (textView != null) {
			textView.setOnClickListener(this);
			int memberCount = getClassMemberCount(list);
			textView.setText(new StringBuilder(this.groupName)
					.append("(").append(memberCount).append(")").toString());
		}

		teachers = new ArrayList();
		students = new ArrayList();
		List parents = new ArrayList();
		List leavedTeachers = new ArrayList();
		List leavedStudents = new ArrayList();
		List leavedParents = new ArrayList();

		UserInfo userInfo = getUserInfo();
		boolean myselfFound = false;
		this.membersMap.clear();
		for (ContactsClassMemberInfo obj : list) {
			this.membersMap.put(obj.getMemberId(), obj);

			if (obj.isHeadTeacher() && obj.getRole() == RoleType.ROLE_TYPE_TEACHER) {
				this.headTeacherInfo = obj;
				if (this.headTeacherId == null) {
					this.headTeacherId = obj.getMemberId();
				}
			}

			if (obj.getRole() == RoleType.ROLE_TYPE_TEACHER) {
				if (obj.getWorkingState() != 0) {
					if (obj.isHeadTeacher()){
						teachers.add(0,obj);
					} else {
						teachers.add(obj);
					}
				} else {
					leavedTeachers.add(obj);
				}
			} else if (obj.getRole() == RoleType.ROLE_TYPE_STUDENT) {
				if (obj.getWorkingState() != 0) {
					students.add(obj);
				} else {
					leavedStudents.add(obj);
				}
			} else if (obj.getRole() == RoleType.ROLE_TYPE_PARENT) {
				if (obj.getWorkingState() != 0) {
					parents.add(obj);
				} else {
					leavedParents.add(obj);
				}
			}
			if (isAddReporterPermission) {
				findViewById(R.id.contacts_class_members_layout)
						.setVisibility(View.VISIBLE);
				this.sendMessageBtn.setVisibility(View.VISIBLE);
			} else {
				if (!myselfFound && userInfo.getMemberId().equals(obj.getMemberId())) {
					myselfFound = true;
					myselfInfo = obj;
					findViewById(R.id.contacts_class_members_layout)
							.setVisibility(View.VISIBLE);
					//                findViewById(R.id.contacts_assign_homework_layout)
					//                        .setVisibility(View.VISIBLE);
					//                        findViewById(R.id.contacts_forbid_chat_layout).setVisibility(View.VISIBLE);
					findViewById(R.id.contacts_forbid_chat_layout).setVisibility(View.GONE);
					if (!getArguments().getBoolean(Constants.EXTRA_CONTACTS_FROM_CHAT)) {
						//                        this.sendMessageBtn.setVisibility(View.VISIBLE);
						this.sendMessageBtn.setVisibility(View.GONE);
					}
				}
			}

			//班主任不能退出班级，其他都可以。
			if (!isAddReporterPermission) {
				View exitClassView = findViewById(R.id.contacts_exit_class_layout);
				if (exitClassView != null) {
					if (isHeadTeacher()) {
						exitClassView.setVisibility(View.GONE);
					} else {
						if (classStatus == ContactsClassManagementActivity.CLASS_STATUS_PRESENT) {
							exitClassView.setVisibility(View.VISIBLE);
						} else {
							exitClassView.setVisibility(View.GONE);
						}
					}
				}
			}
		}


		//只有是班主任才创建“新建学生”布局
		if (!isAddReporterPermission) {
			if (isHeadTeacher() && classStatus == ContactsClassManagementActivity.CLASS_STATUS_PRESENT) {
				ContactsClassMemberInfo info = new ContactsClassMemberInfo();
				info.setId(null);
				leavedStudents.add(info);
//				if (isOnlineSchool){
				//添加老师身份
					leavedTeachers.add(info);
//				}
			}
		}

		teachers.addAll(leavedTeachers);
		students.addAll(leavedStudents);
		parents.addAll(leavedParents);
		if (isAddReporterPermission && reporters != null && reporters.size() > 0) {
			//筛选已经存在的记者
			for (int i = 0; i < reporters.size(); i++) {
				boolean flag = true;
				String memberId = reporters.get(i).getMemberId();
				if (teachers.size() > 0) {
					for (int j = 0; j < teachers.size(); j++) {
						ContactsClassMemberInfo memberInfo = (ContactsClassMemberInfo) teachers.get(j);
						if (memberId.equals(memberInfo.getMemberId())) {
							teachers.remove(memberInfo);
							flag = false;
							break;
						}
					}
				}
				if (flag) {
					if (students.size() > 0) {
						for (int j = 0; j < students.size(); j++) {
							ContactsClassMemberInfo memberInfo = (ContactsClassMemberInfo) students.get(j);
							if (memberId.equals(memberInfo.getMemberId())) {
								students.remove(memberInfo);
								flag = false;
								break;
							}
						}
					}
				}
				if (flag) {
					if (parents.size() > 0) {
						for (int j = 0; j < parents.size(); j++) {
							ContactsClassMemberInfo memberInfo = (ContactsClassMemberInfo) parents.get(j);
							if (memberId.equals(memberInfo.getMemberId())) {
								parents.remove(memberInfo);
								break;
							}
						}
					}
				}
			}
		}
		if (isAddReporterPermission) {
			int count = 0;
			if (teachers.size() > 0) {
				count = count + teachers.size();
			} else {
				findViewById(R.id.contacts_teachers_title_layout).setVisibility(View.GONE);
			}
			//如果不是来自创建直播显示学生的列表
			if (!isFromCreateOnlline) {
				if (students.size() > 0) {
					count = count + students.size();
				} else {
					findViewById(R.id.contacts_students_title).setVisibility(View.GONE);
				}
			}
			//            if (parents.size()>0){
			//                count=count+parents.size();
			//            }else {
			//                findViewById(R.id.contacts_parents_title_layout).setVisibility(View.GONE);
			//            }
			//隐藏添加家长为小记者的功能
			findViewById(R.id.contacts_parents_title_layout).setVisibility(View.GONE);
			findViewById(R.id.contacts_parents).setVisibility(View.GONE);


			//如果是来自创建直播隐藏学生
			if (isFromCreateOnlline) {
				findViewById(R.id.contacts_students_title_layout).setVisibility(View.GONE);
				findViewById(R.id.contacts_students).setVisibility(View.GONE);
			}
			if (teachers.size() == 0 && students.size() == 0) {
				this.sendMessageBtn.setVisibility(View.GONE);
				TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.no_more_data));
			}
			TextView tvTitle = (TextView) getView().findViewById(R.id.contacts_header_title);
			if (tvTitle != null) {
				tvTitle.setText(new StringBuilder(this.groupName)
						.append("(").append(count).append(")").toString());
			}
		}
		getAdapterViewHelper(String.valueOf(this.teachersGridView.getId()))
				.setData(teachers);
		getAdapterViewHelper(String.valueOf(this.studentsGridView.getId()))
				.setData(students);
		getAdapterViewHelper(String.valueOf(this.parentsGridView.getId()))
				.setData(parents);

		showViews(true);
		showViewsByData(students, parents);

		classMemberListResult = result;
	}

	private void showViewsByData(List<ContactsClassMemberInfo> students,
								 List<ContactsClassMemberInfo> parents) {
		//家长
		View parentLayout = findViewById(R.id.layout_parent);
		if (parentLayout != null) {
			if (parents == null || parents.size() <= 0) {
				parentLayout.setVisibility(View.GONE);
			} else {
				parentLayout.setVisibility(View.VISIBLE);
			}
		}

		//学生
		View studentLayout = findViewById(R.id.layout_student);
		if (studentLayout != null) {
			if (students == null || students.size() <= 0) {
				//为空的情况只有一种，那就是确实没有学生，而且用户不是班主任身份。（班主任身份在学生为空
				// 的时候，默认创建了一个id为空的“学生”）
				studentLayout.setVisibility(View.GONE);
			} else {
				studentLayout.setVisibility(View.VISIBLE);
			}
		}

	}

	private int getClassMemberCount(List<ContactsClassMemberInfo> list) {
		if (list == null || list.size() == 0) {
			return 0;
		}
		Map<String, ContactsClassMemberInfo> hashMap = new HashMap<String, ContactsClassMemberInfo>();
		for (ContactsClassMemberInfo item : list) {
			if (item != null && !TextUtils.isEmpty(item.getMemberId())) {
				hashMap.put(item.getMemberId(), item);
			}
		}
		return hashMap.size();
	}

	private void enterQrCodeDetails() {
		Bundle args = new Bundle();
		args.putString(ContactsQrCodeDetailsActivity.EXTRA_TITLE,
				getString(R.string.class_qrcode));
		args.putInt(ContactsQrCodeDetailsActivity.EXTRA_TARGET_TYPE,
				ContactsQrCodeDetailsActivity.TARGET_TYPE_CLASS);
		args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_ID, this.groupId);
		args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_NAME, this.groupName);
		args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_DESCRIPTION, this.schoolName);
		Intent intent = new Intent(getActivity(), ContactsQrCodeDetailsActivity.class);
		intent.putExtras(args);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {

		}
	}

	private void assignHomework() {

	}

	private void inviteParents() {
		String memberId = getUserInfo().getMemberId();
		String classContactId = this.groupId;
		String serverUrl = ServerUrl.SHARE_CLASS_INVITATION_URL;
		String url = serverUrl + String.format(
				ServerUrl.SHARE_CLASS_INVITATION_PARAMS, memberId, classContactId);
		ShareInfo shareInfo = new ShareInfo();
		shareInfo.setTitle(getString(R.string.invite_parents_to_join_class));
		String content = getString(R.string.str_invite_join_class,classInfo.getClassName());
		UserInfo userInfo = getUserInfo();
		if (userInfo != null){
			if (TextUtils.isEmpty(userInfo.getRealName())){
				content = userInfo.getNickName() + content;
			} else {
				content = userInfo.getRealName() + content;
			}
		}
		shareInfo.setContent(content);
		shareInfo.setTargetUrl(url);

		SharedResource resource = new SharedResource();
		resource.setTitle(getString(R.string.app_name));
		resource.setDescription(getString(R.string.invite_parents_to_join_class));
		resource.setShareUrl(serverUrl);
		resource.setThumbnailUrl(null);
		resource.setType(SharedResource.RESOURCE_TYPE_HTML);
		resource.setClassPrimaryKey(classContactId);
		resource.setFromUserId(memberId);
		resource.setFieldPatches(SharedResource.FIELD_PATCHES_CLASS_SHARE_URL);
		shareInfo.setSharedResource(resource);

		UMImage umImage = new UMImage(getActivity(), R.drawable.ic_launcher);
		shareInfo.setuMediaObject(umImage);
		ShareUtils shareUtils = new ShareUtils(getActivity());
		//        List<ShareItem> shareItems = new ArrayList<ShareItem>();
		//        shareItems.add(new ShareItem(com.oosic.apps.share.R.string.wechat, com.oosic.apps.share.R.drawable.umeng_share_wechat_btn, ShareType.SHARE_TYPE_WECHAT));
		//        shareItems.add(new ShareItem(
		//                com.oosic.apps.share.R.string.wxcircle, com.oosic.apps.share.R.drawable.umeng_share_wxcircle_btn, ShareType
		//                .SHARE_TYPE_WECHATMOMENTS));
		//        shareItems.add(new ShareItem(R.string.wawachat, R.drawable.umeng_share_wawachat_btn, ShareType
		//                .SHARE_TYPE_CONTACTS));
		//        shareItems.add(new ShareItem(com.oosic.apps.share.R.string.qq_friends, com.oosic.apps.share.R.drawable.umeng_share_qq_btn, ShareType.SHARE_TYPE_QQ));
		//        shareItems.add(new ShareItem(R.string.qzone, R.drawable.umeng_share_qzone_btn, ShareType.SHARE_TYPE_QZONE));
		//        shareUtils.setShareItems(shareItems);

		Map<String, Object> contactsPickerParams = new HashMap();
		contactsPickerParams.put(ContactsPickerActivity.EXTRA_PICKER_TYPE,
				ContactsPickerActivity.PICKER_TYPE_PERSONAL |
						ContactsPickerActivity.PICKER_TYPE_GROUP);
		contactsPickerParams.put(ContactsPickerActivity.EXTRA_PICKER_MODE,
				ContactsPickerActivity.PICKER_MODE_SINGLE);

		shareUtils.share(getView(), shareInfo, contactsPickerParams);
	}

	private String getStudentIds() {
		List<ContactsClassMemberInfo> list =
				(List<ContactsClassMemberInfo>) getAdapterViewHelper(
						String.valueOf(this.studentsGridView.getId())).getData();
		StringBuilder builder = new StringBuilder();
		if (list != null && list.size() > 0) {
			UserInfo userInfo = getUserInfo();
			for (ContactsClassMemberInfo item : list) {
				if (item != null && !TextUtils.isEmpty(item.getMemberId())) {
					if (userInfo != null
							&& item.getMemberId().equals(userInfo.getMemberId())) {
						continue;
					}
					builder.append(item.getMemberId() + ",");
				}
			}
		}
		return builder.toString();
	}

	@Override
	public boolean onBackPressed() {
		super.onBackPressed();
		if (isAddReporterPermission) {
			getActivity().finish();
		} else {
			notifyChanges();
		}
		return true;
	}

	@Override
	public void finish() {
		getActivity().setResult(getResultCode(), getResultData());
		super.finish();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.contacts_send_message) {
			if (isAddReporterPermission) {
				//区分来源显示不同的数据
				if (isFromCreateOnlline) {
					callBackData();
				} else {
					AddReporterPermission();
				}
			} else {
				enterGroupConversation(this.hxGroupId, this.groupName, ChatActivity.FROM_CLASS);
			}
		} else if (v.getId() == R.id.contacts_teachers_title_layout) {
			v.setSelected(!v.isSelected());
			this.teachersArrow.setImageResource(!v.isSelected() ?
					R.drawable.list_exp_up : R.drawable.list_exp_down);
			this.teachersGridView.setVisibility(!v.isSelected() ?
					View.VISIBLE : View.GONE);
		} else if (v.getId() == R.id.contacts_students_title_layout) {
			v.setSelected(!v.isSelected());
			this.studentsArrow.setImageResource(!v.isSelected() ?
					R.drawable.list_exp_up : R.drawable.list_exp_down);
			this.studentsGridView.setVisibility(!v.isSelected() ?
					View.VISIBLE : View.GONE);
		} else if (v.getId() == R.id.contacts_parents_title_layout) {
			v.setSelected(!v.isSelected());
			this.parentsArrow.setImageResource(!v.isSelected() ?
					R.drawable.list_exp_up : R.drawable.list_exp_down);
			this.parentsGridView.setVisibility(!v.isSelected() ?
					View.VISIBLE : View.GONE);
		} else if (v.getId() == R.id.contacts_header_right_btn) {
			enterClassManagement();
		} else if (v.getId() == R.id.contacts_header_left_btn) {
			if (isAddReporterPermission) {
				getActivity().finish();
			} else {
				notifyChanges();
			}
		} else {
			super.onClick(v);
		}
	}

	/**
	 * 回传数据到创建直播的界面
	 */
	private void callBackData() {
		if (teacherData.size() > 0) {
			List<ContactsClassMemberInfo> hostData = new ArrayList<>();
			for (int i = 0; i < teacherData.size(); i++) {
				SelectHelper helper = teacherData.get(i);
				ContactsClassMemberInfo info = new ContactsClassMemberInfo();
				info.setMemberId(helper.memberId);
				info.setSchoolId(helper.schoolId);
				info.setClassId(helper.classId);
				info.setNoteName(helper.noteName);
				info.setHeadPicUrl(helper.headPic);
				hostData.add(info);
			}
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putParcelableArrayList("teacherHost", (ArrayList<? extends Parcelable>) hostData);
			intent.putExtras(bundle);
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
		}

	}

	private void AddReporterPermission() {
		List<SelectHelper> allSelectData = new ArrayList<>();
		if (teacherData.size() > 0) {
			allSelectData.addAll(teacherData);
		}
		if (studentData.size() > 0) {
			allSelectData.addAll(studentData);
		}
		if (parentData.size() > 0) {
			allSelectData.addAll(parentData);
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

	private void enterClassManagement() {
		Bundle args = new Bundle();
		args.putString(ContactsClassManagementActivity.EXTRA_SCHOOL_ID, this.schoolId);
		args.putString(ContactsClassManagementActivity.EXTRA_SCHOOL_NAME, this.schoolName);
		args.putString(ContactsClassManagementActivity.EXTRA_CLASS_ID, this.classId);
		args.putString(ContactsClassManagementActivity.EXTRA_CLASS_NAME, this.groupName);
		args.putString(ContactsClassManagementActivity.EXTRA_CLASS_MAILID, this.groupId);
		args.putString(ContactsClassManagementActivity.EXTRA_CLASS_HEADTEACHER_ID,
				this.headTeacherInfo.getMemberId());
		args.putString(ContactsClassManagementActivity.EXTRA_CLASS_HEADTEACHER_NAME,
				this.headTeacherInfo.getNoteName());
		args.putInt(ContactsClassManagementActivity.EXTRA_CLASS_STATUS, this.classStatus);
		Intent intent = new Intent(getActivity(),
				ContactsClassManagementActivity.class);
		intent.putExtras(args);
		startActivityForResult(intent,
				ContactsClassManagementActivity.REQUEST_CODE_MODIFY_CLASS_ATTRIBUTES);
	}

	private void loadClassInfo() {
		if (getUserInfo() == null) {
			return;
		}
		Map<String, Object> params = new HashMap();
		params.put("MemberId", getUserInfo().getMemberId());
		params.put("ClassId", classId);
		DefaultDataListener listener =
				new DefaultDataListener<SubscribeClassInfoResult>(
						SubscribeClassInfoResult.class) {
					@Override
					public void onSuccess(String jsonString) {
						if (getActivity() == null) {
							return;
						}
						super.onSuccess(jsonString);
						SubscribeClassInfoResult result = getResult();
						if (result == null || !result.isSuccess()
								|| result.getModel() == null) {
							return;
						}
						updateClassInfo(result);
					}

					@Override
					public void onFinish() {
						super.onFinish();
						loadViews();
					}
				};
		RequestHelper.sendPostRequest(getActivity(),
				ServerUrl.CONTACTS_CLASS_INFO_URL, params, listener);
	}

	private void updateClassInfo(SubscribeClassInfoResult result) {
		classInfo = result.getModel().getData();
		if (classInfo != null) {
			if (classStatus != Constants.CLASS_STATUS_END_THE_LECTURE) {
				//授课结束不需要重新赋值
				classStatus = classInfo.getIsHistory();
			}
			Bundle args = getArguments();
			if (args != null && !args.containsKey(Constants.EXTRA_CLASS_STATUS)) {
				args.putInt(Constants.EXTRA_CLASS_STATUS, classStatus);
			}
		}
	}

	private void joinClass() {
		QrcodeClassInfo data = new QrcodeClassInfo();
		data.setClassId(classInfo.getClassId());
		StringBuilder builder = new StringBuilder();
		builder.append(classInfo.getClassName());
		data.setCname(builder.toString());
		data.setHeadPicUrl(classInfo.getHeadPicUrl());
		data.setSname(classInfo.getSchoolName());
		Bundle args = new Bundle();
		args.putSerializable(ActivityUtils.KEY_QRCODE_CLASS_INFO, data);
		Intent intent = new Intent(getActivity(), QrcodeProcessActivity.class);
		intent.putExtras(args);
		try {
			startActivity(intent);
		} catch (Exception e) {

		}
	}

	private void notifyChanges() {
		boolean classNameChanged = !this.groupName.equals(
				getArguments().getString(Constants.EXTRA_CONTACTS_NAME));
		boolean classHeadTeacherChanged = headTeacherInfo != null &&
				!headTeacherInfo.getMemberId().equals(this.headTeacherId);
		boolean classStatusChanged = this.classStatus !=
				getArguments().getInt(Constants.EXTRA_CLASS_STATUS);

		boolean changed = false;
		if (classNameChanged || classHeadTeacherChanged || classStatusChanged) {
			changed = true;
		}
		if (changed) {
			Bundle data = new Bundle();
			data.putString(Constants.EXTRA_CONTACTS_CLASS_ID, this.classId);
			data.putBoolean(Constants.EXTRA_CLASS_DETAILS_CHANGED, changed);
			if (classNameChanged) {
				data.putBoolean(Constants.EXTRA_CLASS_NAME_CHANGED,
						classNameChanged);
				data.putString(Constants.EXTRA_CONTACTS_CLASS_NAME, this.groupName);
			}
			if (classHeadTeacherChanged) {
				data.putBoolean(Constants.EXTRA_CLASS_HEADTEACHER_CHANGED,
						classHeadTeacherChanged);
				data.putString(Constants.EXTRA_CLASS_HEADTEACHER_ID,
						this.headTeacherInfo.getMemberId());
				data.putString(Constants.EXTRA_CLASS_HEADTEACHER_NAME,
						this.headTeacherInfo.getRealName());
			}
			if (classStatusChanged) {
				data.putBoolean(Constants.EXTRA_CLASS_STATUS_CHANGED,
						classStatusChanged);
				data.putInt(Constants.EXTRA_CLASS_STATUS, this.classStatus);
			}
			Intent intent = new Intent();
			intent.putExtras(data);
			setResult(Activity.RESULT_OK, intent);
		}
		finish();
	}

	public static void setHasClassContentChanged(boolean hasClassContentChanged) {
		ClassContactsDetailsFragment.hasClassContentChanged = hasClassContentChanged;
	}

	public static boolean hasClassContentChanged() {
		return hasClassContentChanged;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		setResult(resultCode, data);
		if (ContactsClassRequestListFragment.hasMessageHandled()) {
			ContactsClassRequestListFragment.setHasMessageHandled(false);
			//刷新页面
			loadContacts();
		}
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == ContactsClassManagementActivity.REQUEST_CODE_MODIFY_CLASS_ATTRIBUTES) {
				boolean changed = data.getBooleanExtra(
						ContactsClassManagementActivity.EXTRA_CLASS_ATTRIBUTES_CHANGED, false);
				if (!changed) {
					return;
				}
				boolean classNameChanged = data.getBooleanExtra(
						ContactsClassManagementActivity.EXTRA_CLASS_NAME_CHANGED, false);
				boolean classHeadTeacherChanged = data.getBooleanExtra(
						ContactsClassManagementActivity.EXTRA_CLASS_HEADTEACHER_CHANGED, false);
				boolean classStatusChanged = data.getBooleanExtra(
						ContactsClassManagementActivity.EXTRA_CLASS_STATUS_CHANGED, false);
				if (classNameChanged) {
					//设置标志位
					setHasClassContentChanged(true);
					TextView textView = (TextView) getView().findViewById(R.id.contacts_header_title);
					if (textView != null) {
						this.groupName = data.getStringExtra(
								ContactsClassManagementActivity.EXTRA_CLASS_NAME);
						textView.setText(new StringBuilder(this.groupName)
								.append("(").append(this.membersMap.size()).append(")").toString());
						this.className = this.groupName;
					}
				}
				if (classHeadTeacherChanged) {
					//设置标志位
					setHasClassContentChanged(true);
					String classHeadTeacherId = data.getStringExtra(
							ContactsClassManagementActivity.EXTRA_CLASS_HEADTEACHER_ID);
					if (this.membersMap.containsKey(classHeadTeacherId)) {
						headTeacherInfo.setIsHeadTeacher(false);
						ContactsClassMemberInfo memberInfo = membersMap.get(classHeadTeacherId);
						memberInfo.setIsHeadTeacher(true);
						headTeacherInfo = memberInfo;
						TextView textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
						if (textView != null) {
							textView.setVisibility(View.INVISIBLE);
						}
						if (isOnlineSchool && teachers != null && teachers.size() > 0){
							teachers.remove(teachers.size()-1);
						}
						getAdapterViewHelper(String.valueOf(this.teachersGridView.getId())).update();
						//新增学生item隐藏
						AdapterViewHelper studentsHelper = getAdapterViewHelper(
								String.valueOf(this.studentsGridView.getId()));
						if (studentsHelper != null) {
							List<ContactsClassMemberInfo> list = new ArrayList<>();
							list = studentsHelper.getData();
							if (list != null && list.size() > 0) {
								for (ContactsClassMemberInfo info : list) {
									if (info.getId() == null) {
										list.remove(info);
										break;
									}
								}
								studentsHelper.update();
							}
						}
					} else {
						loadContacts();
					}
				}
				if (classStatusChanged) {
					//设置标志位
					setHasClassContentChanged(true);
					this.classStatus = data.getIntExtra(
							ContactsClassManagementActivity.EXTRA_CLASS_STATUS,
							ContactsClassManagementActivity.CLASS_STATUS_PRESENT);
					loadContacts();
				}
				showViews(true);
			}else if (requestCode == REQUEST_ADD_TEACHER){
				loadContacts();
			}
		}
	}

	private class SelectHelper {
		String schoolId;
		String memberId;
		String classId;
		String headPic;
		String noteName;
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

	private void setIsSelect(List<SelectHelper> selectHelpers, ContactsClassMemberInfo data, int
			position, String adapterId) {
		if (selectHelpers.size() == 0) {
			SelectHelper selectHelper = new SelectHelper();
			selectHelper.classId = classId;
			selectHelper.memberId = data.getMemberId();
			selectHelper.schoolId = schoolId;
			selectHelper.position = position;
			selectHelper.headPic = data.getHeadPicUrl();
			selectHelper.noteName = data.getNoteName();
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
				selectHelper.classId = classId;
				selectHelper.memberId = data.getMemberId();
				selectHelper.schoolId = schoolId;
				selectHelper.position = position;
				selectHelper.headPic = data.getHeadPicUrl();
				selectHelper.noteName = data.getNoteName();
				selectHelpers.add(selectHelper);
			}
		}
		getAdapterViewHelper(adapterId).update();
	}
}
