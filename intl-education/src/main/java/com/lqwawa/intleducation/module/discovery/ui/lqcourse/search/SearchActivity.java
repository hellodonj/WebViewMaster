package com.lqwawa.intleducation.module.discovery.ui.lqcourse.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.ToolbarActivity;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.NoScrollGridView;
import com.lqwawa.intleducation.base.widgets.adapter.TextWatcherAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.online.OnlineConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.online.OnlineStudyEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.module.discovery.ui.ChildCourseListActivity;
import com.lqwawa.intleducation.module.discovery.ui.ChildCourseListActivityEA;
import com.lqwawa.intleducation.module.discovery.ui.ClassifyIndexActivity;
import com.lqwawa.intleducation.module.discovery.ui.CourseListActivity;
import com.lqwawa.intleducation.module.discovery.ui.LQCourseCourseListActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.CourseFiltrateActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.courselist.LQCourseListActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.state.GroupFiltrateState;
import com.lqwawa.intleducation.module.discovery.ui.study.OnlineStudyType;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.NewOnlineStudyFiltrateActivity;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.NewOnlineStudyFiltrateParams;
import com.lqwawa.intleducation.module.discovery.ui.study.filtratelist.OnlineStudyFiltrateActivity;
import com.lqwawa.intleducation.module.discovery.vo.ClassifyVo;
import com.lqwawa.intleducation.module.organcourse.ShopResourceData;
import com.lqwawa.intleducation.module.organcourse.filtrate.OrganCourseFiltrateActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 搜索页面
 * @date 2018/05/03 12:42
 * @history v1.0
 * **********************************
 */
public class SearchActivity extends PresenterActivity<SearchContract.Presenter>
    implements SearchContract.View,View.OnClickListener{

    public static final String KEY_EXTRA_SEARCH_KEYWORD = "KEY_EXTRA_SEARCH_KEYWORD";

    // 显示的历史消息记录数据
    private static final int SHOWING_COUNT = 6;

    private static final String KEY_EXTRA_SORT = "KEY_EXTRA_SORT";

    private static final String KEY_EXTRA_TITLE = "KEY_EXTRA_TITLE";

    private static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";

    private static final String KEY_ONLINE_SCHOOL_ENTER = "KEY_ONLINE_ENTER";

    // 单个实体
    private static final String KEY_EXTRA_CLASSIFY_ENTITY = "KEY_EXTRA_CLASSIFY_ENTITY";
    // 在线学习的单个实体
    private static final String KEY_EXTRA_ONLINE_CLASSIFY_ENTITY = "KEY_EXTRA_ONLINE_CLASSIFY_ENTITY";
    // 是否是学程馆选择资源
    private static final String KEY_EXTRA_ORGAN_SHOP_SELECT_RESOURCE = "KEY_EXTRA_ORGAN_SHOP_SELECT_RESOURCE";
    // 是否是班级学程中过来
    private static final String KEY_EXTRA_CLASS_COURSE_ENTER = "KEY_EXTRA_CLASS_COURSE_ENTER";
    // 学程馆选择资源相关数据
    private static final String KEY_EXTRA_ORGAN_SHOP_RESOURCE_DATA = "KEY_EXTRA_ORGAN_SHOP_RESOURCE_DATA";
    // 线下机构学程馆使用,是否直接进入筛选页面
    private static final String KEY_EXTRA_HOST_ENTER = "KEY_EXTRA_HOST_ENTER";
    // 讲授课堂搜索页面的关键参数
    private static final String KEY_EXTRA_TEACH_ONLINE_CLASS_PARAMS = "KEY_EXTRA_TEACH_ONLINE_CLASS_PARAMS";


    // 搜索框
    private EditText mSearchText;
    // 清除按钮
    private ImageView mIvClear;
    // 取消文本
    private TextView mTvCancel;
    // 删除按钮
    private ImageView mIvDelete;
    // 最近搜索列表
    private NoScrollGridView mHistoryView;
    // 搜索Adapter
    private SearchKeyAdapter mKeyAdapter;
    // 历史数据
    private List<String> mHistoriesKey;

    // 数据
    private String mSortType;
    private String mTitle;
    private String mSchoolId;
    // 分类数据,筛选搜索会用到
    private LQCourseConfigEntity mClassifyEntity;
    // 在线学习的实体
    private OnlineConfigEntity mConfigEntity;

    // 讲授课堂分类筛选的搜索页面
    private NewOnlineStudyFiltrateParams mFiltrateParams;

    private boolean isOnlineSchoolEnter;
    // 是否是选择资源
    private boolean mSelectResource;
    // 是否是班级学程过来的
    private boolean isClassCourseEnter;
    // 选择资源
    private ShopResourceData mResourceData;
    // 是否直接从线下机构学程馆进来的
    private boolean isHostEnter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_lq_search;
    }

    @Override
    protected SearchContract.Presenter initPresenter() {
        return new SearchPresenter(this);
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mSortType = (String) bundle.get(KEY_EXTRA_SORT);
        mTitle = (String) bundle.get(KEY_EXTRA_TITLE);
        mSchoolId = bundle.getString(KEY_EXTRA_SCHOOL_ID);
        mClassifyEntity = (LQCourseConfigEntity) bundle.get(KEY_EXTRA_CLASSIFY_ENTITY);
        mConfigEntity = (OnlineConfigEntity) bundle.get(KEY_EXTRA_ONLINE_CLASSIFY_ENTITY);
        isOnlineSchoolEnter = bundle.getBoolean(KEY_ONLINE_SCHOOL_ENTER);
        mSelectResource = bundle.getBoolean(KEY_EXTRA_ORGAN_SHOP_SELECT_RESOURCE);
        isClassCourseEnter = bundle.getBoolean(KEY_EXTRA_CLASS_COURSE_ENTER);

        if(bundle.containsKey(KEY_EXTRA_TEACH_ONLINE_CLASS_PARAMS))
        mFiltrateParams = (NewOnlineStudyFiltrateParams) bundle.getSerializable(KEY_EXTRA_TEACH_ONLINE_CLASS_PARAMS);

        if(mSelectResource)
        mResourceData = (ShopResourceData) bundle.getSerializable(KEY_EXTRA_ORGAN_SHOP_RESOURCE_DATA);
        isHostEnter = bundle.getBoolean(KEY_EXTRA_HOST_ENTER);
        if(mSelectResource && EmptyUtil.isEmpty(mResourceData)) return false;
        if(EmptyUtil.isEmpty(mSortType) || EmptyUtil.isEmpty(mTitle)){
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mSearchText = (EditText) findViewById(R.id.et_search);
        mIvClear = (ImageView) findViewById(R.id.search_clear_iv);
        mTvCancel = (TextView) findViewById(R.id.tv_cancel);
        mIvDelete = (ImageView) findViewById(R.id.iv_clear_history);
        mHistoryView = (NoScrollGridView) findViewById(R.id.history_list);

        mKeyAdapter = new SearchKeyAdapter();
        mHistoryView.setAdapter(mKeyAdapter);

        mHistoryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = (String) mKeyAdapter.getItem(position);
                if(TextUtils.equals(mSortType, HideSortType.TYPE_SORT_CLASSIFY)){
                    // 筛选页面搜索
                    GroupFiltrateState state = new GroupFiltrateState(mClassifyEntity);
                    CourseFiltrateActivity.show(SearchActivity.this,mClassifyEntity,state,key);
                }else if(TextUtils.equals(mSortType, HideSortType.TYPE_SORT_HOT_RECOMMEND)){
                    // 热门推荐搜索
                    LQCourseListActivity.showFromSearch(SearchActivity.this,mSortType,mTitle,key,isOnlineSchoolEnter);
                }else if(TextUtils.equals(mSortType, HideSortType.TYPE_SORT_ONLINE_COURSE)){
                    // 在线课堂列表搜索
                    LQCourseListActivity.showFromSearch(SearchActivity.this,mSortType,mTitle,key,mSchoolId,isOnlineSchoolEnter);
                }else if(TextUtils.equals(mSortType,HideSortType.TYPE_SORT_SCHOOL_SHOP)) {
                    OrganCourseFiltrateActivity.showFromSearch(SearchActivity.this, mClassifyEntity, key,mSelectResource,isClassCourseEnter,mResourceData,isHostEnter);
                }else if(TextUtils.equals(mSortType,Integer.toString(OnlineStudyType.SORT_ONLINE_STUDY_SEARCH))){
                    // 在线学习
                    OnlineStudyFiltrateActivity.show(SearchActivity.this,mConfigEntity,true,key);
                }else if(TextUtils.equals(mSortType,HideSortType.TYPE_SORT_TEACH_ONLINE_CLASS)){
                    // 讲授课堂分类筛选
                    // NewOnlineStudyFiltrateParams params = NewOnlineStudyFiltrateParams.copy(true,key,mFiltrateParams);
                    // NewOnlineStudyFiltrateActivity.show(SearchActivity.this,params);
                    Intent intent = new Intent();
                    intent.putExtra(KEY_EXTRA_SEARCH_KEYWORD,key);
                    setResult(Activity.RESULT_OK,intent);
                    finish();
                }else if(TextUtils.equals(mSortType,HideSortType.TYPE_SORT_TEACH_ONLINE_CLASS_SUPER)){
                    // 发送广播
                    mFiltrateParams.setHideTop(true);
                    mFiltrateParams.setKeyWord(key);
                    mFiltrateParams.setConfigValue(key);

                    NewOnlineStudyFiltrateActivity.show(SearchActivity.this,mFiltrateParams);
                    // EventBus.getDefault().postSticky(new EventWrapper(key,EventConstant.TRIGGER_SEARCH_CALLBACK_EVENT));
                    finish();
                }
            }
        });
        // 监听键盘搜索按钮
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String keyString = mSearchText.getText().toString();
                    if (!EmptyUtil.isEmpty(keyString)) {
                        //检查该搜索关键字是否在历史记录中
                        if(mHistoriesKey.contains(keyString)){
                            mHistoriesKey.remove(keyString);
                        }

                        //新增到历史搜索关键字中
                        mHistoriesKey.add(0, keyString);
                        mKeyAdapter.notifyDataSetChanged();
                        // 更新
                        mPresenter.updateSearchHistories(mHistoriesKey);
                        if(TextUtils.equals(mSortType,HideSortType.TYPE_SORT_CLASSIFY)){
                            // 筛选页面搜索
                            GroupFiltrateState state = new GroupFiltrateState(mClassifyEntity);
                            CourseFiltrateActivity.show(SearchActivity.this,mClassifyEntity,state,keyString);
                        }else if(TextUtils.equals(mSortType,HideSortType.TYPE_SORT_HOT_RECOMMEND)){
                            // 热门推荐搜索
                            LQCourseListActivity.showFromSearch(SearchActivity.this,mSortType,mTitle,keyString,isOnlineSchoolEnter);
                        }else if(TextUtils.equals(mSortType, HideSortType.TYPE_SORT_ONLINE_COURSE)){
                            // 在线课堂列表搜索
                            LQCourseListActivity.showFromSearch(SearchActivity.this,mSortType,mTitle,keyString,mSchoolId,isOnlineSchoolEnter);
                        }else if(TextUtils.equals(mSortType,HideSortType.TYPE_SORT_SCHOOL_SHOP)) {
                            OrganCourseFiltrateActivity.showFromSearch(SearchActivity.this, mClassifyEntity, keyString,mSelectResource,isClassCourseEnter,mResourceData,isHostEnter);
                        }else if(TextUtils.equals(mSortType,Integer.toString(OnlineStudyType.SORT_ONLINE_STUDY_SEARCH))){
                            // 在线学习
                            OnlineStudyFiltrateActivity.show(SearchActivity.this,mConfigEntity,true,keyString);
                        }else if(TextUtils.equals(mSortType,HideSortType.TYPE_SORT_TEACH_ONLINE_CLASS)){
                            // 讲授课堂分类筛选
                            // NewOnlineStudyFiltrateParams params = NewOnlineStudyFiltrateParams.copy(true,keyString,mFiltrateParams);
                            // NewOnlineStudyFiltrateActivity.show(SearchActivity.this,params);
                            Intent intent = new Intent();
                            intent.putExtra(KEY_EXTRA_SEARCH_KEYWORD,keyString);
                            setResult(Activity.RESULT_OK,intent);
                            finish();
                        }else if(TextUtils.equals(mSortType,HideSortType.TYPE_SORT_TEACH_ONLINE_CLASS_SUPER)){
                            // 发送广播
                            mFiltrateParams.setHideTop(true);
                            mFiltrateParams.setKeyWord(keyString);
                            mFiltrateParams.setConfigValue(keyString);

                            NewOnlineStudyFiltrateActivity.show(SearchActivity.this,mFiltrateParams);
                            // EventBus.getDefault().postSticky(new EventWrapper(key,EventConstant.TRIGGER_SEARCH_CALLBACK_EVENT));
                            finish();
                        }
                    }
                    return true;
                }

                return false;
            }
        });
        // 监听文本改变
        mSearchText.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                String keyWord = mSearchText.getText().toString();
                mIvClear.setVisibility(keyWord.isEmpty() ? View.GONE : View.VISIBLE);
            }
        });
        mIvClear.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
        mIvDelete.setOnClickListener(this);

        // 代码设置参数
        mSearchText.setMaxLines(1);
        mSearchText.setInputType(EditorInfo.TYPE_CLASS_TEXT
                | EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE
                | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    @Override
    protected void initData() {
        super.initData();
        mHistoriesKey =  mPresenter.getSearchHistories(SHOWING_COUNT);
        if (!EmptyUtil.isEmpty(mHistoriesKey)) {
            mHistoryView.setVisibility(View.VISIBLE);
            mKeyAdapter.setData(mHistoriesKey);
            mKeyAdapter.notifyDataSetChanged();
        } else {
            mHistoryView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.search_clear_iv) {
            mSearchText.setText("");
        }else if(view.getId() == R.id.iv_clear_history) {
            mHistoriesKey.clear();
            mKeyAdapter.setData(mHistoriesKey);
            mKeyAdapter.notifyDataSetChanged();

            mPresenter.clearAllSearchHistories(SHOWING_COUNT);
        }else if(view.getId() == R.id.tv_cancel) {
            List<Activity> listActivity = UIUtil.getListActivity();

            finish();
        }
    }

    /**
     * 搜索页面的入口  热门推荐
     *
     * @param context 上下问对象
     * @param sort    "1"是热门推荐 "-1"分类数据
     * @param title   标题
     * @param isOnlineSchoolEnter 是否从机构主页进来
     */
    public static void show(@NonNull Context context, @HideSortType.SortRes String sort,
                            @NonNull String title,boolean isOnlineSchoolEnter){
        Intent intent = new Intent(context,SearchActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_SORT,sort);
        bundle.putString(KEY_EXTRA_TITLE,title);
        bundle.putBoolean(KEY_ONLINE_SCHOOL_ENTER,isOnlineSchoolEnter);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 在线课堂搜索页面的入口
     *
     * @param context 上下问对象
     * @param sort    "1"是热门推荐 "-1"分类数据
     * @param title   标题
     * @param schoolId 机构Id
     * @param isOnlineSchoolEnter 是否从机构主页进来
     */
    public static void show(@NonNull Context context, @HideSortType.SortRes String sort,
                            @NonNull String title,@NonNull String schoolId,boolean isOnlineSchoolEnter){
        Intent intent = new Intent(context,SearchActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_SORT,sort);
        bundle.putString(KEY_EXTRA_TITLE,title);
        bundle.putString(KEY_EXTRA_SCHOOL_ID,schoolId);
        bundle.putBoolean(KEY_ONLINE_SCHOOL_ENTER,isOnlineSchoolEnter);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    /**
     * 搜索页面的入口  分类数据入口
     *
     * @param context 上下问对象
     * @param entity  筛选页面传入,搜索的时候需要传给CourseFiltrateActivity
     * @param sort    "1"是热门推荐 "-1"分类数据
     * @param title   标题
     */
    public static void show(@NonNull Context context, LQCourseConfigEntity entity,
                            @HideSortType.SortRes String sort,
                            @NonNull String title){
        Intent intent = new Intent(context,SearchActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_SORT,sort);
        bundle.putString(KEY_EXTRA_TITLE,title);
        bundle.putSerializable(KEY_EXTRA_CLASSIFY_ENTITY,entity);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 搜索页面的入口  分类数据入口
     *
     * @param context 上下问对象
     * @param entity  筛选页面传入,搜索的时候需要传给OnlineStudyFiltrateActivity
     * @param sort    搜索类型
     * @param title   标题
     */
    public static void show(@NonNull Context context, OnlineConfigEntity entity,
                            @OnlineStudyType.OnlineStudyRes int sort,
                            @NonNull String title){
        String sortStr = Integer.toString(sort);
        Intent intent = new Intent(context,SearchActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_SORT,sortStr);
        bundle.putString(KEY_EXTRA_TITLE,title);
        bundle.putSerializable(KEY_EXTRA_ONLINE_CLASSIFY_ENTITY,entity);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 搜索页面的入口  分类数据入口
     *
     * @param context 上下问对象
     * @param entity  筛选页面传入,搜索的时候需要传给OrganCourseFiltrateActivity
     * @param sort    1000
     * @param title   标题
     * @param isHostEnter 是否从机构主页进来的
     * @param isClassCourseEnter 是否是班级学程中进来
     *
     */
    public static void show(@NonNull Context context, LQCourseConfigEntity entity,
                            @HideSortType.SortRes String sort,
                            @NonNull String title,
                            boolean isHostEnter,
                            boolean isClassCourseEnter){
        Intent intent = new Intent(context,SearchActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_SORT,sort);
        bundle.putString(KEY_EXTRA_TITLE,title);
        bundle.putSerializable(KEY_EXTRA_CLASSIFY_ENTITY,entity);
        bundle.putBoolean(KEY_EXTRA_HOST_ENTER,isHostEnter);
        bundle.putBoolean(KEY_EXTRA_CLASS_COURSE_ENTER,isClassCourseEnter);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 搜索页面的入口  分类数据入口
     *
     * @param context 上下问对象
     * @param entity  筛选页面传入,搜索的时候需要传给CourseFiltrateActivity
     * @param sort    "1"是热门推荐 "-1"分类数据
     * @param title   标题
     */
    public static void show(@NonNull Context context, LQCourseConfigEntity entity,
                            @HideSortType.SortRes String sort,
                            @NonNull String title,
                            boolean selectResource,
                            @NonNull ShopResourceData data,
                            boolean isHostEnter){
        Intent intent = new Intent(context,SearchActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_SORT,sort);
        bundle.putString(KEY_EXTRA_TITLE,title);
        bundle.putSerializable(KEY_EXTRA_CLASSIFY_ENTITY,entity);
        bundle.putBoolean(KEY_EXTRA_ORGAN_SHOP_SELECT_RESOURCE,selectResource);
        bundle.putSerializable(KEY_EXTRA_ORGAN_SHOP_RESOURCE_DATA,data);
        bundle.putBoolean(KEY_EXTRA_HOST_ENTER,isHostEnter);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    /**
     * 讲授课堂的搜索入口
     *
     * @param activity 上下文对象
     * @param params 核心参数对象
     */
    public static void show(@NonNull Activity activity,
                            @HideSortType.SortRes String sort,
                            @NonNull NewOnlineStudyFiltrateParams params){
        Intent intent = new Intent(activity,SearchActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_SORT,sort);
        bundle.putString(KEY_EXTRA_TITLE,params.getConfigValue());
        bundle.putSerializable(KEY_EXTRA_TEACH_ONLINE_CLASS_PARAMS,params);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent,NewOnlineStudyFiltrateActivity.SEARCH_REQUEST_CODE);
    }
}
