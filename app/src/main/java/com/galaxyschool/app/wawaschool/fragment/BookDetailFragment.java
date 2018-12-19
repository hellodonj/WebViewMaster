package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.AccountActivity;
import com.galaxyschool.app.wawaschool.CatalogLessonListActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CheckAuthorizationPmnHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.config.VipConfig;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.AuthorizationInfo;
import com.galaxyschool.app.wawaschool.pojo.AuthorizationInfoResult;
import com.galaxyschool.app.wawaschool.pojo.BookDetail;
import com.galaxyschool.app.wawaschool.pojo.BookDetailResult;
import com.galaxyschool.app.wawaschool.db.dto.BookStoreBook;
import com.galaxyschool.app.wawaschool.pojo.CalalogListResult;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfoResult;
import com.lqwawa.lqbaselib.pojo.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookDetailFragment extends BookStoreDetailBaseFragment implements View.OnClickListener {

    public static final String TAG = BookDetailFragment.class.getSimpleName();
    public static final String IS_TEACHER = "BookDetailFragment_is_teacher";
    public static final String COURSE_TYPE = "BookDetailFragment_course_type";
    private String bookId;
    BookStoreBook data;
    private String schoolId;
    private int fromType;
    private TextView storeTextView;
    private BookDetail book;
    private boolean logininSussessTag = false;//登录成功的标识，登录成功先进入onActivityResult，如果onActivityResult有异步线程，会进入onResume，
    private boolean isTeacher;
    private int courseType;

    // 如果onResume也有异步线程会互相干扰，所以进入onActivityResult设置标识，在进入onResume后让他们根据标识串行执行
    public interface Constants {
        String SCHOOL_ID = "SchoolId";
        String BOOK_ID = "BookId";
        String BOOK_SOURCE = "BookSource";
        String FROM_TYPE = "fromType";
        String ORIGIN_SCHOOL_ID = "originSchoolId";
        String COLLECT_ORIGIN_SCHOOLID = "collectSchoolId";
        String CURRENT_ORZ_SCHOOLID = "current_orz_schoolId";
        String BOOK_OUTLINE_ID = "book_outline_id";
        int FROM_BOOK_STORE = 1;
        int FROM_BOOK_SHELF = 2;
        int REQUEST_CODE_DELETE_BOOK = 10021;
        int EXCELLENT_BOOK = 1;
        int OTHER_BOOK = 2;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        loadIntentData();
        super.initCataLogExpandListView(loadInfo, bookId, schoolId, fromType, data);//调用父类
        super.onActivityCreated(savedInstanceState);
        loadBookDetailData();
        checkAuthorization();
    }

    public final LoadInfo loadInfo = new LoadInfo() {
        @Override
        public void loadInfo() {
            if (fromType == Constants.FROM_BOOK_STORE) {
                loadBookStoreCatalog();
            } else {
                loadBookShelfCatalog();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void loadBookDetailData() {
        if (fromType == Constants.FROM_BOOK_STORE) {
            loadBookStoreMainViewData();
            loadBookStoreCatalog();
        } else {
            loadBookShelfMainViewData();
            loadBookShelfCatalog();
        }
    }

    //    private void jump2BookDetailActivity(){
//        Intent intent = new Intent();
//        intent.setClass(getActivity(), BookDetailActivity.class);
//        intent.putExtra(BookDetailActivity.SCHOOL_ID, data.getSchoolId());
//        intent.putExtra(BookDetailActivity.BOOK_ID, data.getId());
//        intent.putExtra(BookDetailActivity.FROM_TYPE, BookDetailActivity.FROM_BOOK_SHELF);
//        startActivity(intent);
//    }
    private void loadIntentData() {
        courseType = getArguments().getInt(COURSE_TYPE);
        isTeacher = getArguments().getBoolean(IS_TEACHER);
        bookId = getArguments().getString(Constants.BOOK_ID) != null ? getArguments().getString(Constants
                .BOOK_ID) : "";
        schoolId = getArguments().getString(CatalogLessonListActivity.EXTRA_SCHOOL_ID) != null ? getArguments()
                .getString(CatalogLessonListActivity.EXTRA_SCHOOL_ID) : "";
        fromType = getArguments().getInt(Constants.FROM_TYPE, Constants.FROM_BOOK_STORE);
        BookType = getArguments().getInt(Constants.BOOK_SOURCE, Constants.OTHER_BOOK);
        data = (BookStoreBook) getArguments().getSerializable("data");
        originSchoolId = getArguments().getString(Constants.ORIGIN_SCHOOL_ID);
        collectSchoolId = getArguments().getString(Constants.COLLECT_ORIGIN_SCHOOLID);
        isFromChoiceLib = getArguments().getBoolean(ActivityUtils.IS_FROM_CHOICE_LIB, false);
        //当前机构的schoolId
        currentOrzSchoolId = getArguments().getString(Constants.CURRENT_ORZ_SCHOOLID);
        //当前机构是不是vip机构
        isVipSchool = getArguments().getBoolean(ActivityUtils.IS_LQWWA_VIP_SCHOOL);
    }

    private void loadCalalog(Map<String, Object> params, String url) {
        RequestHelper.RequestDataResultListener listener =
                new RequestHelper.RequestDataResultListener<CalalogListResult>(getActivity(),
                        CalalogListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        CalalogListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        BookDetailFragment.super.updateCalalogView(result);//调用父类
                    }
                };
        postRequest(url, params, listener);
    }

    private void loadBookStoreCatalog() {
        Map<String, Object> params = new HashMap();
        params.put(Constants.BOOK_ID, bookId);
        params.put(Constants.SCHOOL_ID, schoolId);
        params.put("MemberId", getMemeberId());
        String schoolMaterialType = "1,2,3,4,5,6,7,8,9,10,11";
        if (isPickSchoolResource) {
            if (getArguments() != null && getArguments().containsKey(MediaListFragment
                    .EXTRA_SHOW_MEDIA_TYPES)) {
                List<Integer> mediaTypes = getArguments().getIntegerArrayList(MediaListFragment
                        .EXTRA_SHOW_MEDIA_TYPES);
                if (mediaTypes != null && mediaTypes.size() > 0) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0, size = mediaTypes.size(); i < mediaTypes.size(); i++) {
                        Integer item = mediaTypes.get(i);
                        builder.append(item);
                        if (i != size - 1) {
                            builder.append(",");
                        }
                    }
                    schoolMaterialType = builder.toString();
                }
            }
        }
        params.put("SchoolMaterialType", schoolMaterialType);
        String url = ServerUrl.BOOKSTORE_BOOK_DETAIL_CATALOG_URL;
        loadCalalog(params, url);
    }

    private void loadBookShelfCatalog() {
        Map<String, Object> params = new HashMap();
        params.put(Constants.BOOK_ID, bookId);
        params.put(Constants.SCHOOL_ID, schoolId);
        params.put("SchoolMaterialType", "1,2,3,4,5,6,7,8,9,10,11");
        String url = ServerUrl.GET_MY_COLLECTION_BOOK_CATALOG_URL;
        loadCalalog(params, url);
    }

    private void loadBookStoreMainViewData() {
        Map<String, Object> params = new HashMap();
        params.put(Constants.BOOK_ID, bookId);
        params.put(Constants.SCHOOL_ID, schoolId);
        params.put("MemberId", getMemeberId());
        String url = ServerUrl.BOOKSTORE_BOOK_DETAIL_URL;
        loadMainViewData(params, url);
    }

    ;

    private void loadBookShelfMainViewData() {
        Map<String, Object> params = new HashMap();
        params.put(Constants.BOOK_ID, bookId);
        params.put("MemberId", getMemeberId());
        String url = ServerUrl.GET_MY_COLLECTION_BOOK_DETAIL_URL;
        loadMainViewData(params, url);
    }

    ;

    private void loadMainViewData(Map<String, Object> params, String url) {
        RequestHelper.RequestDataResultListener listener =
                new RequestHelper.RequestDataResultListener<BookDetailResult>(getActivity(),
                        BookDetailResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        BookDetailResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        book = null;
                        book = result.getModel().getData();
                        if (book != null) {
                            if (courseType != Integer.valueOf(book.getCourseType())) {
                                //资源类型发生改变
                                if (TextUtils.equals("2", book.getCourseType()) && !(isTeacher||VipConfig.isVip(getActivity()))) {
                                    showDialog(book.getCourseType());
                                    return;
                                } else {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("book_type", book.getCourseType());
                                    EventBus.getDefault().post(new MessageEvent(bundle, "update_local_book_dao"));
                                }
                            }

                        }
                        if (fromType == Constants.FROM_BOOK_STORE) {
                            BookDetailFragment.super.updateMainView(book, Constants.FROM_BOOK_STORE);//调用父类
                        } else {
                            BookDetailFragment.super.updateMainView(book, Constants.FROM_BOOK_SHELF);//调用父类
                        }
                        storeOrDeleteEvent(book);//收藏或者移除书架
                        if (logininSussessTag) {
                            storeOrDelete(book);
                        }
                    }
                };
        postRequest(url, params, listener);
    }

    private void showDialog(final String type) {
        ContactsMessageDialog dialog = new ContactsMessageDialog(getActivity(), "", getString(R
                .string.only_teacher_can_see_the_course),
                "", null, getString(R.string.confirm), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Bundle bundle = new Bundle();
                bundle.putString("book_type", type);
                EventBus.getDefault().post(new MessageEvent(bundle, "update_local_book_dao"));
                dialogInterface.dismiss();
                finish();
            }
        });
        dialog.setIsAutoDismiss(true);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void storeOrDeleteEvent(final BookDetail book) {
        storeTextView = (TextView) findViewById(R.id.store_textview);
        storeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!loginState()) {
                    //登录
                    Intent intent = new Intent(getActivity(), AccountActivity.class);
                    Bundle args = new Bundle();
                    args.putBoolean(AccountActivity.EXTRA_HAS_LOGINED, false);
                    args.putBoolean(AccountActivity.EXTRA_ENTER_HOME_AFTER_LOGIN, false);
                    intent.putExtras(args);
                    try {
                        startActivityForResult(intent, 1008);
                    } catch (ActivityNotFoundException e) {
                    }
                } else {
                    storeOrDelete(book);
                }
            }
        });
    }

    private boolean loginState() {
        boolean loginState = false;
        if (getUserInfo() != null && !TextUtils.isEmpty(getUserInfo().getMemberId())) {
            loginState = true;
        } else {
            loginState = false;
        }
        return loginState;
    }

    private void storeOrDelete(final BookDetail book) {
        if (fromType == Constants.FROM_BOOK_STORE) {
            if (!book.isColStatus()) {
                if (isFromChoiceLib && TextUtils.isEmpty(collectSchoolId) && !VipConfig.isVip(getActivity())) {
                    checkChoicePer(book);
                } else {
                    addBookShelf();//加入书架
                    book.setColStatus(true);
                    book.setColCount(book.getColCount() + 1);
                }

                super.updateStoreCountTextview();
            }
        } else {
            removeBookShelf(); //移除书架
        }
    }

    /**
     * 校验精品资源的权限
     */
    private void checkChoicePer(final BookDetail book) {
        if (TextUtils.isEmpty(currentOrzSchoolId) || TextUtils.isEmpty(getMemeberId())) {
            return;
        }
        CheckAuthorizationPmnHelper helper = new CheckAuthorizationPmnHelper(getActivity());
        helper.setSchoolId(currentOrzSchoolId).setMemberId(getMemeberId())
                .setListener(new CheckAuthorizationPmnHelper.checkResultListener() {
                    @Override
                    public void onResult(boolean isSuccess) {
                        if (isSuccess) {
                            addBookShelf();
                            book.setColStatus(true);
                            book.setColCount(book.getColCount() + 1);
                        } else {
//                            if (isPick){
//                                popStack();
//                            }else {
//                                getActivity().finish();
//                            }
                        }
                    }
                }).check();

    }

    private void addBookShelf() {
        if (getUserInfo() == null) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("OutlineId", bookId);
        params.put(Constants.SCHOOL_ID, schoolId);
        String collectSchoolId = DemoApplication.getInstance().getPrefsManager().getLatestSchool
                (getActivity(), getMemeberId());
        params.put("CollectionOrigin", collectSchoolId);
        params.put("IsQualityCourse", isFromChoiceLib);
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.ADD_MY_COLLECTION_BOOK_SHELF_URL, params,
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()
                                ) {
                            TipsHelper.showToast(getActivity(), getString(R.string.add_book_faliure));
                            return;
                        } else {
                            //放入成功
                            TipsHelper.showToast(getActivity(), getString(R.string.collect_to_my_course));
                            storeTextView.setTextColor(getResources().getColor(R.color.white));
                            storeTextView.setText(getResources().getString(R.string.stored_to_bookshelf));
                            storeTextView.setBackgroundResource(R.drawable.gray_5dp_gray);
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        logininSussessTag = false;
                    }
                });
    }

    ;

    private void removeBookShelf() {
        Map<String, Object> params = new HashMap();
        params.put(Constants.BOOK_ID, bookId);
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.REMOVE_MY_COLLECTION_BOOK_SHELF_URL, params,
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()
                                ) {
                            return;
                        } else {
                            TipsHelper.showToast(getActivity(), getString(R.string.delete_book_success));
                            Intent intent = new Intent();
                            intent.putExtra(Constants.BOOK_ID, bookId);
                            getActivity().setResult(Activity.RESULT_OK, intent);
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        logininSussessTag = false;
                    }
                });
    }

    ;

    private void checkAuthorization() {
        if (TextUtils.isEmpty(schoolId) && TextUtils.isEmpty(originSchoolId)) {
            setShareButtonVisible(View.VISIBLE);
            setAddBookButtonVisible(View.VISIBLE);
            return;
        }
        if (!TextUtils.isEmpty(originSchoolId) && !TextUtils.isEmpty(schoolId)) {
            if (originSchoolId.equalsIgnoreCase(schoolId)) {
                loadSchool(schoolId, false);
            } else {
                loadSchool(originSchoolId, true);
            }
        } else if (!TextUtils.isEmpty(schoolId)) {
            if (TextUtils.isEmpty(currentOrzSchoolId)) {
                loadSchool(schoolId, false);
            } else {
                loadSchool(currentOrzSchoolId, false);
            }
        }
    }

    /**
     * 获取学校详情
     *
     * @param id
     * @param isCheckAuthorization 该值为true执行授权检查
     */
    private void loadSchool(final String id, final boolean isCheckAuthorization) {
        if (getUserInfo() == null) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", id);
        params.put("VersionCode", 1);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.SUBSCRIBE_SCHOOL_INFO_URL,
                params,
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
                        SchoolInfo schoolInfo = getResult().getModel();
                        if (!isCheckAuthorization) {
                            if (schoolInfo != null) {
                                if (schoolInfo.hasJoinedSchool()) {
                                    setShareButtonVisible(View.VISIBLE);
                                    setAddBookButtonVisible(View.VISIBLE);
                                } else {
                                    if (isVipSchool) {
                                        setShareButtonVisible(View.VISIBLE);
                                    } else {
                                        setShareButtonVisible(View.GONE);
                                    }
                                    setAddBookButtonVisible(View.GONE);
                                }
                            }
                        } else {
                            checkAuthorizationCondition(originSchoolId, schoolId);
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }

                    @Override
                    public void onError(NetroidError error) {
                        super.onError(error);
                    }

                });
    }


    private boolean checkAuthorizationCondition(final String schoolId, final String feeSchoolId) {
        if (getUserInfo() == null) {
            return false;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolId);
        params.put("CourseId", feeSchoolId);
        RequestHelper.RequestDataResultListener<AuthorizationInfoResult> listener =
                new RequestHelper.RequestDataResultListener<AuthorizationInfoResult>
                        (getActivity(), AuthorizationInfoResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        AuthorizationInfoResult result = (AuthorizationInfoResult) getResult();

                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            setShareButtonVisible(View.GONE);
                            setAddBookButtonVisible(View.GONE);
                            return;
                        }
                        AuthorizationInfo authorizationInfo = result.getModel().getData();
                        if (authorizationInfo != null) {
                            if (authorizationInfo.isIsMemberAuthorized()) {
                                setShareButtonVisible(View.VISIBLE);
                                setAddBookButtonVisible(View.VISIBLE);
                            } else {
                                if (isVipSchool) {
                                    setShareButtonVisible(View.VISIBLE);
                                } else {
                                    setShareButtonVisible(View.GONE);
                                }
                                setAddBookButtonVisible(View.GONE);
                            }
                        }
                    }
                };
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.CHECK_AUTHONRIZE_CONDITION_URL,
                params, listener);
        return false;
    }

    private void setShareButtonVisible(int visibility) {
        setButtonVisible(R.id.contacts_header_right_btn, visibility);
    }

    private void setAddBookButtonVisible(int visibility) {
        setButtonVisible(R.id.store_textview, visibility);
    }

    private void setButtonVisible(int resId, int visibility) {
        TextView textView = ((TextView) findViewById(resId));
        if (textView != null) {
            textView.setVisibility(visibility);
        }
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == 1008) {
            logininSussessTag = true;
        }
        if (data != null) {
            if (requestCode == TeachResourceFragment.REQUEST_PERSONAL_CLOUD_RESOURCE) {
                Intent intent = new Intent();
                Bundle bundle = data.getExtras();
                intent.putExtras(bundle);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        }
    }
}
