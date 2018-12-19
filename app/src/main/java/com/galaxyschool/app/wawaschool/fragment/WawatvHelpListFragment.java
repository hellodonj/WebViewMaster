package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.duowan.mobile.netroid.request.StringRequest;
import com.galaxyschool.app.wawaschool.CommonFragmentActivity;
import com.galaxyschool.app.wawaschool.CustomerServiceActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.bitmapmanager.ThumbnailManager;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CollectionHelper;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.account.FeedbackFragment;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandDataAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandListViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.Netroid;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.HelpCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.ShareSettings;
import com.oosic.apps.share.SharedResource;
import com.umeng.socialize.media.UMImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Author: wangchao
 * Time: 2015/08/25 09:34
 */
public class WawatvHelpListFragment extends ContactsExpandListFragment {

    public static final String TAG = WawatvHelpListFragment.class.getSimpleName();

    private ThumbnailManager thumbnailManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wawatv_help_list, container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        thumbnailManager = MyApplication.getThumbnailManager(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadViews();
    }


    void initViews() {
        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }

        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(R.string.help);
        }

        textView = (TextView) findViewById(R.id.contacts_header_right_btn);
        if (textView != null) {
            textView.setVisibility(View.INVISIBLE);
        }

        final ExpandableListView listView = (ExpandableListView) findViewById(R.id.wawatv_help_list_view);
        if (listView != null) {
            listView.setGroupIndicator(null);
            final ExpandDataAdapter dataAdapter = new ExpandDataAdapter(
                getActivity(), null, R.layout.wawatv_help_list_item,
                R.layout.wawatv_list_item) {
                @Override
                public int getChildrenCount(int groupPosition) {
                    HelpCourseInfo helpCourseInfo = (HelpCourseInfo) getGroup(groupPosition);
                    if (helpCourseInfo.getList() != null) {
                        return helpCourseInfo.getList().size();
                    }
                    return 0;
                }

                @Override
                public Object getChild(int groupPosition, int childPosition) {
                    HelpCourseInfo helpCourseInfo = (HelpCourseInfo) getGroup(groupPosition);
                    return helpCourseInfo.getList().get(childPosition);
                }

                @Override
                public View getChildView(
                    int groupPosition, int childPosition, boolean isLastChild, View convertView,
                    ViewGroup parent) {
                    View view = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);

                    final CourseInfo data = (CourseInfo) getChild(groupPosition, childPosition);
                    MyChildViewHolder holder = (MyChildViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new MyChildViewHolder();
                    }

                    holder.data = data;

                    ImageView thumbnail = (ImageView) view.findViewById(R.id.wawatv_image);
                    if (thumbnail != null) {
                        holder.thumbnailView = thumbnail;
                        thumbnailManager.displayThumbnail(data.getImgurl(), thumbnail);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.wawatv_title);
                    if (textView != null) {
                        holder.titleView = textView;
                        textView.setText(data.getNickname());
                    }

                    textView = (TextView) view.findViewById(R.id.wawatv_author);
                    if (textView != null) {
                        holder.authorView = textView;
                        textView.setText(data.getCreatename());
                    }

                    textView = (TextView) view.findViewById(R.id.wawatv_date);
                    if (textView != null) {
                        holder.dateView = textView;
                        String dateStr = data.getCreatetime();
                        if (!TextUtils.isEmpty(dateStr)) {
                            dateStr = dateStr.substring(0, dateStr.lastIndexOf(" "));
                            textView.setText(dateStr);
                        }
                    }

                    textView = (TextView) view.findViewById(R.id.wawatv_learn_level);
                    if (textView != null) {
                        holder.learnLevelView = textView;
                        textView.setVisibility(View.INVISIBLE);
                    }

                    textView = (TextView) view.findViewById(R.id.wawatv_viewcount);
                    if (textView != null) {
                        holder.viewCountView = textView;
                        textView.setText(getString(R.string.n_viewcount, String.valueOf(data.getViewcount())));
                    }

                    textView = (TextView) view.findViewById(R.id.wawatv_collection);
                    if (textView != null) {
                        holder.shareView = textView;
                        textView.setTag(data);
                        textView.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CourseInfo info = (CourseInfo) v.getTag();
                                    if (info != null) {
                                        CollectionHelper helper = new CollectionHelper(getActivity());
                                        CourseData courseData = new CourseData();
                                        courseData.id = info.getId();
                                        courseData.type = info.getType();
                                        helper.setFromLQTools(true);
                                        helper.collectResource(courseData.getIdType(), info.getNickname(), info.getCode());
                                    }
                                }
                            });
                    }

                    textView = (TextView) view.findViewById(R.id.wawatv_share);
                    if (textView != null) {
                        holder.collectionView = textView;
                        textView.setTag(data);
                        textView.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    share((CourseInfo) v.getTag());
                                }
                            });
                    }

                    view.setTag(holder);
                    return view;
                }

                @Override
                public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                    View view = super.getGroupView(groupPosition, isExpanded, convertView, parent);
                    HelpCourseInfo data = (HelpCourseInfo) getGroup(groupPosition);
                    if (data == null) {
                        return view;
                    }
                    MyViewHolder holder = (MyViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new MyViewHolder();
                    }
                    holder.data = data;

                    ImageView imageView = (ImageView) view.findViewById(R.id.wawatv_help_item_arrow);
                    if (imageView != null) {
                        holder.indicatorView = imageView;
                        if (data.getList() != null && data.getList().size() > 0 && !isExpanded) {
                            imageView.setImageResource(R.drawable.list_exp_down);
                        } else {
                            imageView.setImageResource(R.drawable.list_exp_up);
                        }
                    }
                    TextView textView = (TextView) view.findViewById(R.id.wawatv_help_item_title);
                    if (textView != null) {
                        holder.titleView = textView;
                        textView.setText(data.getHelpName());
                    }
                    view.setTag(holder);
                    return view;
                }
            };
            listView.setOnGroupExpandListener(
                new ExpandableListView.OnGroupExpandListener() {
                    @Override
                    public void onGroupExpand(int groupPosition) {

                        for (int i = 0; i < dataAdapter.getGroupCount(); i++) {
                            if (i != groupPosition && listView.isGroupExpanded(i)) {
                                listView.collapseGroup(i);
                            }
                        }
                    }
                });
            ExpandListViewHelper listViewHelper = new ExpandListViewHelper(getActivity(), listView, dataAdapter) {
                @Override
                public void loadData() {
                    loadHelpCourses();
                }

                @Override
                public boolean onChildClick(
                    ExpandableListView parent, View v, int groupPosition, int childPosition,
                    long id) {
                    MyChildViewHolder holder = (MyChildViewHolder) v.getTag();
                    if (holder == null || holder.data == null) {
                        return true;
                    }
                    CourseInfo data = (CourseInfo) holder.data;
                    if(data != null) {
                        ActivityUtils.openCourseDetail(getActivity(), data.toNewResourceInfo(),
                                PictureBooksDetailActivity.FROM_OTHRE);
                    }
                    return true;
                }

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    MyViewHolder holder = (MyViewHolder) v.getTag();
                    if (holder == null || holder.data == null) {
                        return false;
                    }
                    HelpCourseInfo data = (HelpCourseInfo) holder.data;
                    if (data.getList() != null && data.getList().size() > 0) {
                        return false;
                    }
                    getDataAdapter().notifyDataSetChanged();
                    return false;
                }
            };
            listViewHelper.setData(null);

            setCurrListViewHelper(listView, listViewHelper);
        }
        getView().findViewById(R.id.join_layout)
                .setOnClickListener(this);
        getView().findViewById(R.id.userinfo_customer_service_layout)
            .setOnClickListener(this);
        getView().findViewById(R.id.setting_feedback_layout)
            .setOnClickListener(this);
    }


    void loadViews() {
        if (getCurrListViewHelper().hasData()) {
            getCurrListViewHelper().update();
        } else {
            loadHelpCourses();
        }
    }

    private void share(CourseInfo shareCourseInfo) {
        if (shareCourseInfo != null && shareCourseInfo.getId() >= 0) {
                ShareInfo shareInfo = new ShareInfo();
                shareInfo.setTitle(shareCourseInfo.getNickname());
                shareInfo.setContent(shareCourseInfo.getCreatename());
                int resType = shareCourseInfo.getType() % ResType.RES_TYPE_BASE;
                if(resType == ResType.RES_TYPE_COURSE
                    || shareCourseInfo.getType() == ResType.RES_TYPE_OLD_COURSE) {
                    shareInfo.setTargetUrl(ShareSettings.WAWAWEIKE_SHARE_URL + shareCourseInfo.getId());
                } else if(resType == ResType.RES_TYPE_NOTE) {
                    shareInfo.setTargetUrl(ShareSettings.WAWAWEIKE_DIARY_SHARE_URL + shareCourseInfo.getId());
                } else {
                    if(!TextUtils.isEmpty(shareCourseInfo.getShareurl())) {
                        shareInfo.setTargetUrl(shareCourseInfo.getShareurl());
                    }
                }
                UMImage umImage;
                if (!TextUtils.isEmpty(shareCourseInfo.getImgurl())) {
                    umImage = new UMImage(getActivity(), shareCourseInfo.getImgurl());
                } else {
                    umImage = new UMImage(getActivity(), R.drawable.ic_launcher);
                }
                shareInfo.setuMediaObject(umImage);
                SharedResource resource = shareCourseInfo.getSharedResource();
                shareInfo.setSharedResource(resource);
                new ShareUtils(getActivity()).share(getView(), shareInfo);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_left_btn) {
            finishActivity();
        } else if(v.getId() == R.id.userinfo_customer_service_layout) {
            CustomerServiceActivity.start(getActivity());
        } else if(v.getId() == R.id.setting_feedback_layout) {
            CommonFragmentActivity.start(getActivity(), FeedbackFragment.class);
        }else if (v.getId() == R.id.join_layout){
            CommonFragmentActivity.start(getActivity(), AdShowFragment.class);
        }
    }

    class MyViewHolder extends ViewHolder {
        TextView titleView;
        ImageView indicatorView;
    }

    class MyChildViewHolder extends ViewHolder {
        ImageView thumbnailView;
        TextView titleView;
        TextView authorView;
        TextView learnLevelView;
        TextView dateView;
        TextView viewCountView;
        TextView shareView;
        TextView collectionView;
    }

    void loadHelpCourses() {
        showLoadingDialog();
        JSONObject jsonObject = new JSONObject();
        try {
            if(AppSettings.DEBUG) {
                jsonObject.put("data", "test"); //test for debug
            } else {
                jsonObject.put("data", "help"); //help for release
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        String url = ServerUrl.WAWATV_HELP_LIST_URL + builder.toString();
        StringRequest request = new StringRequest(
            Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                dismissLoadingDialog();
                if (jsonString != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        if (jsonObject != null) {
                            int code = jsonObject.optInt("code");
                            if (code == 0) {
                                JSONArray jsonArray = jsonObject.optJSONArray("data");
                                if (jsonArray != null) {
                                    List<HelpCourseInfo> helpCourseInfos = JSON.parseArray(
                                        jsonArray.toString(),
                                        HelpCourseInfo.class);
                                    getCurrListViewHelper().setData(helpCourseInfos);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(NetroidError error) {
                if (getActivity() == null) {
                    return;
                }
                dismissLoadingDialog();
                TipMsgHelper.ShowMsg(getActivity(), getString(R.string.network_error));
            }
        });
        request.addHeader("Accept-Encoding", "*");
        Netroid.newRequestQueue(getActivity()).add(request);
    }

}
