package com.galaxyschool.app.wawaschool.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.AttendOrAttentionSchoolActivity;
import com.galaxyschool.app.wawaschool.BookDetailActivity;
import com.galaxyschool.app.wawaschool.BroadcastNoteActivity;
import com.galaxyschool.app.wawaschool.CampusOnlineWebActivity;
import com.galaxyschool.app.wawaschool.CaptureActivity;
import com.galaxyschool.app.wawaschool.ExcellentBooksActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.NewBookStoreActivity;
import com.galaxyschool.app.wawaschool.NocWorksActivity;
import com.galaxyschool.app.wawaschool.OriginalShowMoreActivity;
import com.galaxyschool.app.wawaschool.PictureBooksActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourcePadAdapterViewHelper;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.db.dto.BookStoreBook;
import com.galaxyschool.app.wawaschool.pojo.BookStoreBookListResult;
import com.galaxyschool.app.wawaschool.pojo.CampusOnline;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.SubscribeUserInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeUserListResult;
import com.galaxyschool.app.wawaschool.pojo.TempShowNewResourceInfo;
import com.galaxyschool.app.wawaschool.views.HalfRoundedImageView;
import com.galaxyschool.app.wawaschool.views.MyGridView;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/11.
 */
public class SchoolsBooksFragment extends ContactsListFragment {
    public static final String TAG = SchoolsBooksFragment.class.getSimpleName();
    private final static String BOOKS_GRIDVIEW_TAG = "booksGridviewTag";
    private final static String PIC_BOOKS_GRIDVIEW_TAG = "picbooksGridviewTag";
    private final static String SHOW_BOOKS_GRIDVIEW_TAG = "showbooksGridviewTag";
    private final static String CAMPUS_ONLINE_GRIDVIEW_TAG="campusOnlineGridviewTag";
    private static final int MAX_BOOKS_PER_ROW = 3;
    private static final int MAX_PIC_BOOKS_PER_ROW = 2;
    private static final int MAX_SHOW_BOOK_NUM = 4;
    private MyGridView schoolsGridview;
    private MyGridView booksGridview;
    private BookStoreBook emptyBook = new BookStoreBook();
    private TextView rightBtn;
    private MyGridView campusOnlineGridview;
    private final static String NOTICE_BOOKS_GRIDVIEW_TAG = "noticebooksGridviewTag";
   // private long timeHistory= 0L;
   private ImageView imageSlider;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schools_books, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        loadViews();
    }


    @Override
    public void onResume() {
        super.onResume();
//        loadViews();
       // imageSlider.startAutoCycle();
    }

    @Override
    public void onPause() {
        super.onPause();
        //imageSlider.stopAutoCycle();
    }

    private void loadViews() {
//        if (isLogin()) {
//            rightBtn.setVisibility(View.VISIBLE);
//            rightBtn.setOnClickListener(this);
//        } else {
//            rightBtn.setVisibility(View.GONE);
//        }
//        long timeNow=System.currentTimeMillis();
//        if(DateUtils.isUp3Minites(timeNow,timeHistory)){
//            timeHistory=timeNow;
//            try {
//                Date date=DateUtils.longToDate(timeHistory,"yyyy-MM-dd HH:mm:ss");
//                String updateTime = getActivity().getString(R.string.cs_update_time);
//                pullToRefreshView.setLastUpdated(updateTime + date.toLocaleString());
//                loadDatas();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
        loadDatas();
//        if (getCurrAdapterViewHelper().hasData()) {
//            getCurrAdapterViewHelper().update();
//            getAdapterViewHelper(NOTICE_BOOKS_GRIDVIEW_TAG).update();
//        } else {
//            loadDatas();
//        }
    }

    private void loadDatas() {
//        loadSchoolData();
//        loadBookData();
//        loadPicBooks();
        loadShowBooks();
        loadNoticeBooks();
        loadCampusOnlineData();
    }

    private void loadPicBooks() {
        Map<String, Object> params = new HashMap();
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<NewResourceInfoListResult>(
                        NewResourceInfoListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        NewResourceInfoListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updatePicBookListView(result);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.PICBOOKS_GET_DEFAULT_PICBOOKLIST_URL, params, listener);
    }

    private void updatePicBookListView(NewResourceInfoListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<NewResourceInfo> list = result.getModel().getData();
            if (getPageHelper().isFetchingFirstPage()) {
                if (getAdapterViewHelper(PIC_BOOKS_GRIDVIEW_TAG).hasData()) {
                    getAdapterViewHelper(PIC_BOOKS_GRIDVIEW_TAG).clearData();
                }
            }
            if (list == null || list.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_excellent_pic_book));
                } else {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_more_data));
                }
                return;
            }
            if (list.size() > MAX_SHOW_BOOK_NUM) {
                list = list.subList(0, MAX_SHOW_BOOK_NUM);
            }
            getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(
                    getPageHelper().getFetchingPageIndex());
            if (getAdapterViewHelper(PIC_BOOKS_GRIDVIEW_TAG).hasData()) {
                getAdapterViewHelper(PIC_BOOKS_GRIDVIEW_TAG).getData().addAll(list);
                getAdapterViewHelper(PIC_BOOKS_GRIDVIEW_TAG).update();
            } else {
                getAdapterViewHelper(PIC_BOOKS_GRIDVIEW_TAG).setData(list);
            }
        }
    }

    private void loadSchoolData() {
        Map<String, Object> params = new HashMap();
        if (!TextUtils.isEmpty(getMemeberId())) {
            params.put("MemberId", getMemeberId());
        }
        params.put("Type", 1);
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<SubscribeUserListResult>(
                        SubscribeUserListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()
                                || getResult().getModel() == null) {
                            return;
                        }
                        updateSchoolListView(getResult());
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.SUBSCRIBE_SEARCH_URL, params, listener);
    }

    private void updateSchoolListView(SubscribeUserListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<SubscribeUserInfo> list = result.getModel().getData();
            if (list == null || list.size() <= 0) {
                if (getCurrAdapterViewHelper().hasData()) {
                    getCurrAdapterViewHelper().clearData();
                }
                TipsHelper.showToast(getActivity(),
                        getString(R.string.no_schools));
                return;
            } else {
                if (list.size() > MAX_BOOKS_PER_ROW) {
                    list = list.subList(0, MAX_BOOKS_PER_ROW);
                }
                getCurrAdapterViewHelper().setData(list);
            }
        }
    }

    private void loadBookData() {
        Map<String, Object> params = new HashMap();
        params.put("IsExcellentBook", true);
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<BookStoreBookListResult>(
                        BookStoreBookListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        BookStoreBookListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updateBookListView(result);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.BOOKSTORE_SEARCH_BOOKLIST_URL, params, listener);
    }

    private void updateBookListView(BookStoreBookListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<BookStoreBook> list = result.getModel().getData();
            if (getPageHelper().isFetchingFirstPage()) {
                if (getAdapterViewHelper(BOOKS_GRIDVIEW_TAG).hasData()) {
                    getAdapterViewHelper(BOOKS_GRIDVIEW_TAG).clearData();
                }
            }
            if (list == null || list.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_book_class));
                } else {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_more_data));
                }
                return;
            }
            if (list.size() > MAX_BOOKS_PER_ROW) {
                list = list.subList(0, MAX_BOOKS_PER_ROW);
            }
            if (list != null && list.size() > 0) {
                Iterator<BookStoreBook> iterator = list.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next() == emptyBook) {
                        iterator.remove();
                    }
                }
                while (list.size() % MAX_BOOKS_PER_ROW != 0) {
                    list.add(emptyBook);
                }
            }
            getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(
                    getPageHelper().getFetchingPageIndex());
            if (getAdapterViewHelper(BOOKS_GRIDVIEW_TAG).hasData()) {
                int i = 0;
                while (getAdapterViewHelper(BOOKS_GRIDVIEW_TAG).getData().size() > 0) {
                    i = getAdapterViewHelper(BOOKS_GRIDVIEW_TAG).getData().size() - 1;
                    if (getAdapterViewHelper(BOOKS_GRIDVIEW_TAG).getData().get(i) == emptyBook) {
                        getAdapterViewHelper(BOOKS_GRIDVIEW_TAG).getData().remove(i);
                    } else {
                        break;
                    }
                }
                int position = getAdapterViewHelper(BOOKS_GRIDVIEW_TAG).getData().size();
                if (position > 0) {
                    position--;
                }
                getAdapterViewHelper(BOOKS_GRIDVIEW_TAG).getData().addAll(list);
                while (getAdapterViewHelper(BOOKS_GRIDVIEW_TAG).getData().size() % MAX_BOOKS_PER_ROW != 0) {
                    getAdapterViewHelper(BOOKS_GRIDVIEW_TAG).getData().add(this.emptyBook);
                }
                getCurrAdapterView().setSelection(position);
            } else {
                while (list.size() % MAX_BOOKS_PER_ROW != 0) {
                    list.add(this.emptyBook);
                }
                getAdapterViewHelper(BOOKS_GRIDVIEW_TAG).setData(list);
            }
        }
    }
    private void initSlideImage(){
        //NOC大赛
        imageSlider = (ImageView) findViewById(R.id.noc_works_icon);
        LinearLayout.LayoutParams layoutParams=( LinearLayout.LayoutParams)imageSlider.getLayoutParams();
        layoutParams.width= (ScreenUtils.getScreenWidth(getActivity()));
        layoutParams.height=layoutParams.width*2/5;
        imageSlider.setOnClickListener(this);
//        imageSlider = (SliderLayout) findViewById(R.id.noc_works_icon);
//        imageSlider.setPresetTransformer(SliderLayout.Transformer.Default);
//        imageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
//        imageSlider.setCustomAnimation(new DescriptionAnimation());
//        imageSlider.setDuration(30000);
//        DefaultSliderView sliderView = new DefaultSliderView(getActivity());
//        sliderView.description(String.valueOf(1))
//                .image(R.drawable.noc_banner_logo)
//                .setScaleType(BaseSliderView.ScaleType.Fit);
//        imageSlider.addSlider(sliderView);
//        sliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener(){
//            @Override
//            public void onSliderClick(BaseSliderView slider) {
//                enterNOcWorksEvent();
//            }
//        });
    }

    private void initViews() {
        initSlideImage();


        ImageView imageView = ((ImageView) findViewById(R.id.contacts_header_left_btn));
        imageView.setVisibility(View.INVISIBLE);
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        textView.setText(R.string.wawatv);
        rightBtn = (TextView) findViewById(R.id.contacts_header_right_btn);
        //校园空间增加扫一扫
        rightBtn.setText(R.string.qrcode_scanning);
        rightBtn.setTextColor(getResources().getColor(R.color.text_green));
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setOnClickListener(this);
        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh_all);
        setStopPullUpState(true);
        setPullToRefreshView(pullToRefreshView);
//        initSchoolGridview();
//        initBooksGridview();
//        initPicBooksGridview();
        initShowBooksGridview();
        initNoticeBooksGridview();
        initCampusOnlineGridview();
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.school_more_layout);
        linearLayout.setOnClickListener(this);
        linearLayout = (LinearLayout) findViewById(R.id.book_more_layout);
        linearLayout.setOnClickListener(this);
        linearLayout = (LinearLayout) findViewById(R.id.pic_book_more_layout);
        linearLayout.setOnClickListener(this);
        linearLayout = (LinearLayout) findViewById(R.id.show_book_more_layout);
        linearLayout.setOnClickListener(this);
        linearLayout = (LinearLayout) findViewById(R.id.notice_book_more_layout);
        linearLayout.setOnClickListener(this);
        linearLayout= (LinearLayout) findViewById(R.id.campus_online_more_layout);
        linearLayout.setOnClickListener(this);
    }



    private void initShowBooksGridview() {
        MyGridView gridView = (MyGridView) findViewById(R.id.show_book_gridview);
        if (gridView != null) {
            gridView.setNumColumns(MAX_PIC_BOOKS_PER_ROW);
            AdapterViewHelper adapterViewHelper = new NewResourcePadAdapterViewHelper(
                    getActivity(), gridView) {
                @Override
                public void loadData() {
                //    timeHistory=System.currentTimeMillis();
                    loadDatas();
                }


                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    NewResourceInfo data = (NewResourceInfo) holder.data;
                    if (data != null) {
                        if(data.isMicroCourse()||data.isOnePage()){
                            ActivityUtils.openPictureDetailActivity(getActivity(), data);
                        }
                    }
                }
            };
            setCurrAdapterViewHelper(gridView, adapterViewHelper);
            //addAdapterViewHelper(SHOW_BOOKS_GRIDVIEW_TAG, adapterViewHelper);
        }
    }

    private void loadNoticeBooks() {
        Map<String, Object> params = new HashMap();
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<NewResourceInfoListResult>(
                        NewResourceInfoListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        NewResourceInfoListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updateNoteBookListView(result);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_BROADCAST_LIST_URL, params, listener);
    }

    private void updateNoteBookListView(NewResourceInfoListResult result) {
        List<NewResourceInfo> list = result.getModel().getData();
        if (getPageHelper().isFetchingFirstPage()) {
            if(getAdapterViewHelper(NOTICE_BOOKS_GRIDVIEW_TAG).hasData()){
                getAdapterViewHelper(NOTICE_BOOKS_GRIDVIEW_TAG).clearData();
            }
        }
        if (list == null || list.size() <= 0) {
            if (getPageHelper().isFetchingFirstPage()) {
//                    TipsHelper.showToast(getActivity(),
//                            getString(R.string.no_data));
            } else {
                TipsHelper.showToast(getActivity(),
                        getString(R.string.no_more_data));
            }
            return;
        }
        if (list.size() > MAX_PIC_BOOKS_PER_ROW * 2) {
            list = list.subList(0, MAX_PIC_BOOKS_PER_ROW*2);
        }
//            getPageHelper().updateByPagerArgs(result.getModel().getPager());
//            getPageHelper().setCurrPageIndex(
//                    getPageHelper().getFetchingPageIndex());
        if ( getAdapterViewHelper(NOTICE_BOOKS_GRIDVIEW_TAG).hasData()) {
            getAdapterViewHelper(NOTICE_BOOKS_GRIDVIEW_TAG).getData().addAll(list);
            getAdapterViewHelper(NOTICE_BOOKS_GRIDVIEW_TAG).update();
        } else {
            getAdapterViewHelper(NOTICE_BOOKS_GRIDVIEW_TAG).setData(list);
        }
    }



    private void initNoticeBooksGridview() {
        MyGridView gridView = (MyGridView) findViewById(R.id.notice_book_gridview);
        if (gridView != null) {
            gridView.setNumColumns(MAX_PIC_BOOKS_PER_ROW);
            AdapterViewHelper adapterViewHelper = new NewResourcePadAdapterViewHelper(
                    getActivity(), gridView) {
                @Override
                public void loadData() {
                    loadDatas();
                }


                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    NewResourceInfo data = (NewResourceInfo) holder.data;
                    if (data != null) {
                        ActivityUtils.openOnlineNote(getActivity(), data
                                .getCourseInfo(),false, false);
                    }
                }
            };
            addAdapterViewHelper(NOTICE_BOOKS_GRIDVIEW_TAG, adapterViewHelper);
        }
    }

    private void loadShowBooks() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", 1);
            jsonObject.put("pageIndex", getPageHelper().getFetchingPageIndex());
            jsonObject.put("pageSize", getPageHelper().getPageSize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, ServerUrl.GET_SHOW_LIST_URL + builder.toString(), new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                parseShowBooks(jsonString);
            }

            @Override
            public void onError(NetroidError error) {
                if (getActivity() == null) {
                    return;
                }
                TipMsgHelper.ShowMsg(getActivity(), getString(R.string.network_error));
            }

            @Override
            public void onFinish() {
                if (getActivity() == null) {
                    return;
                }
                super.onFinish();
                dismissLoadingDialog();
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    private void parseShowBooks(String jsonString) {
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject != null) {
                    int code = jsonObject.optInt("Code");
                    if (code == 0) {
                        // getPageHelper().updateTotalCountByJsonString(jsonString);
                        JSONArray jsonArray = jsonObject.optJSONArray("Data");
                        if (jsonArray != null) {
                            List<TempShowNewResourceInfo> datas = JSON.parseArray(
                                    jsonArray.toString(), TempShowNewResourceInfo.class);
                            if (datas != null && datas.size() > 0) {
                                List<NewResourceInfo> items = new ArrayList<NewResourceInfo>();
                                for (TempShowNewResourceInfo tempInfo : datas) {
                                    NewResourceInfo item = TempShowNewResourceInfo.pase2NewResourceInfo(tempInfo);
                                    items.add(item);
                                }
                                if (items.size() > MAX_PIC_BOOKS_PER_ROW*2) {
                                    items = items.subList(0, MAX_PIC_BOOKS_PER_ROW*2);
                                }
                                getCurrAdapterViewHelper().setData(items);
                            }else{
                                if( getCurrAdapterViewHelper().hasData()){
                                    getCurrAdapterViewHelper().clearData();
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initPicBooksGridview() {
        MyGridView gridView = (MyGridView) findViewById(R.id.pic_book_gridview);
        if (gridView != null) {
            gridView.setNumColumns(MAX_PIC_BOOKS_PER_ROW);
            AdapterViewHelper adapterViewHelper = new NewResourcePadAdapterViewHelper(
                    getActivity(), gridView) {
                @Override
                public void loadData() {
                    loadPicBooks();
                }


                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    NewResourceInfo data = (NewResourceInfo) holder.data;
                    if (data != null) {
                        //ActivityUtils.loadCourseDetail(getActivity(), data);
                        ActivityUtils.openPictureDetailActivity(getActivity(), data);
                    }
                }
            };
            addAdapterViewHelper(PIC_BOOKS_GRIDVIEW_TAG, adapterViewHelper);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.school_more_layout:
                enterMoreSchoolEvent();
                break;
            case R.id.book_more_layout:
                enterMoreBookEvent();
                break;
            case R.id.pic_book_more_layout:
                enterMorePicBookEvent();
                break;
            case R.id.show_book_more_layout:
                enterMoreShowBookEvent();
                break;
            case R.id.notice_book_more_layout:
                enterMoreNoticeBookEvent();
                break;
            case R.id.contacts_header_right_btn:
//                if (isLogin()) {
//                    enterAttentionSchoolEvent();
//                }
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                startActivity(intent);
                break;
            case R.id.campus_online_more_layout:
                enterMoreCampusOnlineEvent();
                break;
            //noc大赛
            case R.id.noc_works_icon:
                enterNOcWorksEvent();
                break;

        }
    }
    private void enterNOcWorksEvent(){
        Intent intent=new Intent(getActivity(),NocWorksActivity.class);
        startActivity(intent);
    }
    private void enterMoreCampusOnlineEvent(){
        Intent intent=new Intent(getActivity(),BroadcastNoteActivity.class);
        intent.putExtra(BroadcastNoteActivity.IS_FROM_CAMPUS_ONLINE,true);
        startActivity(intent);
    }
    private void enterMoreNoticeBookEvent() {
        Intent intent = new Intent(getActivity(), BroadcastNoteActivity.class);
        startActivity(intent);
    }

    private void enterMorePicBookEvent() {
        Intent intent = new Intent(getActivity(), PictureBooksActivity.class);
        startActivity(intent);
    }

    private void enterMoreShowBookEvent() {
        Intent intent = new Intent(getActivity(), OriginalShowMoreActivity.class);
        startActivity(intent);
    }

    private void enterMoreBookEvent() {
        Intent intent = new Intent(getActivity(), ExcellentBooksActivity.class);
        startActivity(intent);
    }

    private void enterMoreSchoolEvent() {
        Intent intent = new Intent(getActivity(), AttendOrAttentionSchoolActivity.class);
        intent.putExtra(AttendOrAttentionSchoolActivity.SCHOOL_TYPE,
                AttendOrAttentionSchoolActivity.SCHOOL_TYPE_ATTEND);
        startActivity(intent);
    }

    private void enterAttentionSchoolEvent() {
        Intent intent = new Intent(getActivity(), AttendOrAttentionSchoolActivity.class);
        intent.putExtra(AttendOrAttentionSchoolActivity.SCHOOL_TYPE,
                AttendOrAttentionSchoolActivity.SCHOOL_TYPE_ATTENTION);
        startActivity(intent);
    }

    private void initSchoolGridview() {
        schoolsGridview = (MyGridView) findViewById(R.id.schools_gridview);
        if (schoolsGridview != null) {
            schoolsGridview.setNumColumns(MAX_BOOKS_PER_ROW);
            AdapterViewHelper gridViewHelper = new AdapterViewHelper(getActivity(),
                    schoolsGridview, R.layout.book_store_main_item) {
                @Override
                public void loadData() {
                    loadSchoolData();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    SubscribeUserInfo data = (SubscribeUserInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.item_book_cover);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                            frameLayout.getLayoutParams();
                    params.width = DensityUtils.dp2px(getActivity(), 65);
                    params.height = DensityUtils.dp2px(getActivity(), 65);
                    frameLayout.setLayoutParams(params);

                    ImageView imageView = (ImageView) view.findViewById(R.id.item_shelf);
                    if (imageView != null) {
                        imageView.setVisibility(View.GONE);
                    }
                    imageView = (ImageView) view.findViewById(R.id.item_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayThumbnailWithDefault(
                                AppSettings.getFileUrl(AppSettings.getFileUrl(data.getThumbnail())),
                                imageView,
                                R.drawable.default_school_icon);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.item_title);
                    if (textView != null) {
                        textView.setText(data.getName());
                    }
                    ImageView imageView_desc = (ImageView) view.findViewById(R.id.item_description);
                    if (imageView_desc != null) {
                        imageView_desc.setVisibility(View.GONE);
                    }
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
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    SubscribeUserInfo data = (SubscribeUserInfo) holder.data;
                    itemClickEvent(data != null ? data.getId() : null);
                }
            };
            setCurrAdapterViewHelper(schoolsGridview, gridViewHelper);
        }
    }

    private void itemClickEvent(String schoolId) {
        Intent intent = new Intent(getActivity(), NewBookStoreActivity.class);
        Bundle args = new Bundle();
        args.putString(NewBookStoreActivity.SCHOOL_ID, schoolId);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void initBooksGridview() {
        booksGridview = (MyGridView) findViewById(R.id.books_gridview);
        if (booksGridview != null) {
            booksGridview.setNumColumns(MAX_BOOKS_PER_ROW);
            AdapterViewHelper gridViewHelper = new AdapterViewHelper(getActivity(),
                    booksGridview, R.layout.book_store_main_item) {
                @Override
                public void loadData() {
                    loadBookData();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    BookStoreBook data = (BookStoreBook) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ImageView imageView = (ImageView) view.findViewById(R.id.item_shelf);
                    if (imageView != null) {
                        if (position % MAX_BOOKS_PER_ROW == 0) {
                            imageView.setBackgroundResource(R.drawable.book_shelf_l);
                        } else if (position % MAX_BOOKS_PER_ROW == 1) {
                            imageView.setBackgroundResource(R.drawable.book_shelf_m);
                        } else if (position % MAX_BOOKS_PER_ROW == 2) {
                            imageView.setBackgroundResource(R.drawable.book_shelf_r);
                        }
                    }
                    imageView = (ImageView) view.findViewById(R.id.item_icon);
                    if (imageView != null) {
                        if (data != emptyBook) {
                            getThumbnailManager().displayThumbnailWithDefault(
                                    AppSettings.getFileUrl(data.getCoverUrl()), imageView,
                                    R.drawable.default_book_cover);
                        } else {
                            imageView.setImageBitmap(null);
                        }
                    }
                    TextView textView = (TextView) view.findViewById(R.id.item_title);
                    if (textView != null) {
                        textView.setText(data.getBookName());
                    }
                    ImageView imageView_desc = (ImageView) view.findViewById(R.id.item_description);
                    if (imageView_desc != null) {
                        imageView_desc.setVisibility(View.GONE);
//                        if (data != emptyBook) {
//                            imageView_desc.setVisibility(View.VISIBLE);
//                            if (data.getStatus() == 1) {
//                                imageView_desc.setImageResource(R.drawable.ywc_ico);
//                            } else {
//                                imageView_desc.setImageResource(R.drawable.jsz_ico);
//                            }
//                        } else {
//                            imageView_desc.setVisibility(View.GONE);
//                        }
                    }

                    FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.item_book_cover);
                    if (frameLayout != null) {
                        if (data != emptyBook) {
                            frameLayout.setBackgroundColor(Color.WHITE);
                        } else {
                            frameLayout.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }

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
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    BookStoreBook data = (BookStoreBook) holder.data;
                    if (TextUtils.isEmpty(data.getId())) {
                        return;
                    }
                    enterBookDetailActivity(data);
                }
            };
            addAdapterViewHelper(BOOKS_GRIDVIEW_TAG, gridViewHelper);
        }
    }

    private void enterBookDetailActivity(BookStoreBook data) {
        if (data == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getActivity(), BookDetailActivity.class);
        intent.putExtra(BookDetailActivity.SCHOOL_ID, data.getSchoolId());
        intent.putExtra(BookDetailActivity.BOOK_ID, data.getId());
        intent.putExtra(BookDetailActivity.FROM_TYPE, BookDetailActivity.FROM_BOOK_STORE);
        intent.putExtra(BookDetailActivity.BOOK_SOURCE, BookDetailActivity.EXCELLENT_BOOK);
        intent.putExtra("data", data);
        startActivity(intent);
    }
    //加载校园直播台的数据
    private void loadCampusOnlineData(){
        String url=ServerUrl.GET_CAMPUS_ONLINE_LIST_BASE_URL;
        ThisStringRequest request=new ThisStringRequest(Request.Method.GET,url,new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                parserCampusJsonString(jsonString);
            }

            @Override
            public void onError(NetroidError error) {
                if (getActivity() == null) {
                    return;
                }
                TipMsgHelper.ShowMsg(getActivity(), getString(R.string.network_error));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }
    private void parserCampusJsonString(String jsonString){
        if (jsonString==null) return;
        List<CampusOnline> campusOnLineData=JSON.parseArray(jsonString, CampusOnline.class);
        if (campusOnLineData!=null&&campusOnLineData.size()>4){
            campusOnLineData=campusOnLineData.subList(0,MAX_SHOW_BOOK_NUM);
        }
        if ( getAdapterViewHelper(CAMPUS_ONLINE_GRIDVIEW_TAG).hasData()) {
            getAdapterViewHelper(CAMPUS_ONLINE_GRIDVIEW_TAG).clearData();
            getAdapterViewHelper(CAMPUS_ONLINE_GRIDVIEW_TAG).getData().addAll(campusOnLineData);
            getAdapterViewHelper(CAMPUS_ONLINE_GRIDVIEW_TAG).update();
        } else {
            getAdapterViewHelper(CAMPUS_ONLINE_GRIDVIEW_TAG).setData(campusOnLineData);
        }
    }
    private void initCampusOnlineGridview(){
        campusOnlineGridview= (MyGridView) findViewById(R.id.campus_online_gridview);
        if (campusOnlineGridview!=null){
            campusOnlineGridview.setNumColumns(MAX_PIC_BOOKS_PER_ROW);
            AdapterViewHelper adapterViewHelper=new AdapterViewHelper(getActivity(),
                    campusOnlineGridview,R.layout.resource_item_pad) {
                @Override
                public void loadData() {
                    loadDatas();
                }
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    CampusOnline data = (CampusOnline) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    TextView textView = (TextView) view.findViewById(R.id.resource_title);
                    if (textView != null) {
                        textView.setText(data.getLname());
                    }
                    FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.resource_frameLayout);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
                    WindowManager wm = (WindowManager) getActivity()
                            .getSystemService(Context.WINDOW_SERVICE);
                    int windowWith = wm.getDefaultDisplay().getWidth();//屏幕宽度
                    int itemWidth = (windowWith - getActivity().getResources().getDimensionPixelSize(R.dimen
                            .separate_20dp) * 3) / 2;
                    params.width = itemWidth;
                    params.height = params.width * 9 / 16;
                    frameLayout.setLayoutParams(params);
                    params = (LinearLayout.LayoutParams)textView.getLayoutParams();
                    params.width = itemWidth;
                    textView.setLayoutParams(params);
                    ImageView imageView = (HalfRoundedImageView) view.findViewById(R.id.resource_thumbnail);
                    if (imageView != null) {
                        MyApplication.getThumbnailManager(getActivity()).displayThumbnailWithDefault(
                                data.getLimg(), imageView, R.drawable.default_cover);
                    }
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder= (ViewHolder) view.getTag();
                    if (holder==null)  return;
                    CampusOnline campusOnline= (CampusOnline) holder.data;
                    if (campusOnline!=null){
                        Intent intent=new Intent(getActivity(), CampusOnlineWebActivity.class);
                        intent.putExtra(CampusOnlineWebFragment.Constants.EXTRA_CONTENT_URL,
                                campusOnline.getLink());
                        intent.putExtra(CampusOnline.class.getSimpleName(),campusOnline);
                        getActivity().startActivity(intent);
                    }
                }
            };
            addAdapterViewHelper(CAMPUS_ONLINE_GRIDVIEW_TAG,adapterViewHelper);
        }
    }
}
