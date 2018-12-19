package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.galaxyschool.app.wawaschool.pojo.CatalogSplitCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.lqwawa.client.pojo.ResourceInfo;
import com.oosic.apps.share.ShareSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CatalogSplitCourseListFragment extends ContactsListFragment
    implements View.OnClickListener {

    public static final String TAG = CatalogSplitCourseListFragment.class.getSimpleName();

    private CourseInfo courseInfo;

    private boolean isPlaying = false;

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        return inflater.inflate(R.layout.catalog_course_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadViews();
        isPlaying = false;
    }

    @Override
    public void finish() {
        getActivity().finish();
    }

    void initViews() {
        if (getArguments() != null) {
            courseInfo = getArguments().getParcelable(CourseInfo.class.getSimpleName());
        }
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setOnClickListener(this);
            if (courseInfo != null) {
                textView.setText(courseInfo.getNickname());
            }
        }

        textView = (TextView) findViewById(R.id.contacts_header_right_btn);
        if (textView != null) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(R.string.confirm);
            textView.setBackgroundResource(R.drawable.sel_nav_button_bg);
            textView.setOnClickListener(this);
        }

        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }

        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
            R.id.pull_to_refresh);
        setStopPullUpState(true);
        setStopPullDownState(true);
        setPullToRefreshView(pullToRefreshView);

        GridView gridView = (GridView) findViewById(R.id.catalog_course_grid_view);
        if (gridView == null) {
            return;
        }
        int margin = getResources().getDimensionPixelSize(R.dimen.min_margin) * 2;
        final int gridItemSize = (ScreenUtils.getScreenWidth(getActivity()) - margin * 3) / 2;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) gridView.getLayoutParams();
        layoutParams.leftMargin = margin;
        layoutParams.rightMargin = margin;
        layoutParams.topMargin = margin;
        gridView.setLayoutParams(layoutParams);
        gridView.setVerticalSpacing(margin);
        gridView.setHorizontalSpacing(margin);
        gridView.setNumColumns(2);
        gridView.setBackgroundDrawable(null);

        AdapterViewHelper importGridViewHelper = new AdapterViewHelper(
            getActivity(),
            gridView, R.layout.catalog_split_course_grid_item) {
            @Override
            public void loadData() {
                loadSplitCourses(courseInfo.getId());
            }

            @Override
            public View getView(int position, View convertView, final ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                view.setLayoutParams(new AbsListView.LayoutParams(gridItemSize, gridItemSize));
                CatalogSplitCourseInfo data = (CatalogSplitCourseInfo) getDataAdapter().getItem(position);
                if (data == null) {
                    return view;
                }
                ViewHolder holder = (ViewHolder) view.getTag();
                if (holder == null) {
                    holder = new ViewHolder();
                }
                holder.data = data;

                ImageView imageView = (ImageView) view.findViewById(R.id.image);
                if (imageView != null) {
                    getThumbnailManager().displayThumbnail(
                        AppSettings.getFileUrl(data.getThumbUrl()), imageView);
                }

                imageView = (ImageView) view.findViewById(R.id.flag);
                if (imageView != null) {
                    imageView.setImageResource(data.isSelect() ? R.drawable.select : R.drawable.unselect);
                }

                imageView = (ImageView) view.findViewById(R.id.play_icon);
                if (imageView != null) {
                    imageView.setTag(data);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CatalogSplitCourseInfo data = (CatalogSplitCourseInfo) v.getTag();
                            if (data != null) {
                                play(data);
                            }
                        }
                    });
                }
                TextView textView = (TextView) view.findViewById(R.id.name);
                if (textView != null) {
                    textView.setText(data.getSubResName());
                }

                view.setTag(holder);
                return view;
            }

            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                ViewHolder holder = (ViewHolder) view.getTag();
                if (holder == null || holder.data == null) {
                    return;
                }
                CatalogSplitCourseInfo data = (CatalogSplitCourseInfo) holder.data;
                if (data != null) {
                    data.setSelect(!data.isSelect());
                    getCurrAdapterViewHelper().update();
                }
            }
        };
        setCurrAdapterViewHelper(gridView, importGridViewHelper);
    }

    void loadViews() {
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().update();
        } else {
            loadSplitCourses(courseInfo.getId());
        }
    }

    void loadSplitCourses(int courseId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pid", courseId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonString = jsonObject.toString();
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonString);
        String url = ServerUrl.GET_SPLIT_COURSE_LIST_URL + builder.toString();
        ThisStringRequest request = new ThisStringRequest(
            Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                if (jsonString != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        if (jsonObject != null) {
                            int code = jsonObject.optInt("code");
                            if (code == 0) {
                                JSONArray jsonArray = jsonObject.optJSONArray("data");
                                if (jsonArray != null && jsonArray.length() > 0) {
                                    List<CatalogSplitCourseInfo> list = JSON.parseArray(
                                        jsonArray.toString(),
                                        CatalogSplitCourseInfo.class);
                                    if (list != null && list.size() > 0) {
                                        getPageHelper().setCurrIndex(getPageHelper().getFetchingPageIndex());
                                    }

                                    if (getCurrAdapterViewHelper().hasData()) {
                                        int position = getCurrAdapterViewHelper().getData().size();
                                        if (position > 0) {
                                            position--;
                                        }
                                        getCurrAdapterViewHelper().getData().addAll(list);
                                        getCurrAdapterView().setSelection(position);
                                    } else {
                                        getCurrAdapterViewHelper().setData(list);
                                    }
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
                TipMsgHelper.ShowMsg(getActivity(), getString(R.string.network_error));
            }

        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_left_btn) {
            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
            }
        } else if (v.getId() == R.id.contacts_header_right_btn) {
            if(!getCurrAdapterViewHelper().hasData()) {
                return;
            }
            ArrayList<ResourceInfo> resourceInfos = getSelectedResources();
            if(resourceInfos == null || resourceInfos.size() == 0) {
                TipMsgHelper.ShowLMsg(getActivity(), R.string.no_resource_select);
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("resourseInfoList", resourceInfos);
            Intent intent = new Intent();
            intent.putExtras(bundle);
            if(getActivity() != null) {
                getActivity().setResult(getActivity().RESULT_OK, intent);
                getActivity().finish();
            }
        }
    }

    private void play(CatalogSplitCourseInfo data) {
        if (data != null && courseInfo != null) {
            if (!isPlaying) {
                isPlaying = true;
                CourseInfo splitData = courseInfo;
                splitData.setId(data.getId());
                splitData.setThumbnail(data.getThumbUrl());
                splitData.setNickname(data.getSubResName());
                splitData.setResourceurl(data.getPlayUrl());
                ActivityUtils.playOnlineCourse(getActivity(), splitData, false, null);
            }

        }
    }

    private ArrayList<ResourceInfo> getSelectedResources() {
        ArrayList<ResourceInfo> resourceInfos = new ArrayList<ResourceInfo>();
        List datas = getCurrAdapterViewHelper().getData();
        if(datas != null && datas.size() > 0) {
            for (Object item : datas) {
                CatalogSplitCourseInfo courseInfo = (CatalogSplitCourseInfo)item;
                if(courseInfo != null && courseInfo.isSelect()) {
                    ResourceInfo resourceInfo = new ResourceInfo();
                    resourceInfo.setType(0);
                    resourceInfo.setImgPath(courseInfo.getThumbUrl());
                    resourceInfo.setTitle(courseInfo.getSubResName());
                    resourceInfo.setResourcePath(courseInfo.getPlayUrl());
                    StringBuilder builder = new StringBuilder();
                    builder.append(ShareSettings.WAWAWEIKE_SHARE_URL + courseInfo.getId());
                    builder.append("&pType=16&subFlag=1");
                    resourceInfo.setShareAddress(builder.toString());
                    resourceInfos.add(resourceInfo);
                }
            }
        }

        return resourceInfos;
    }
}
