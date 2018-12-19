package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.imagebrowser.GalleryActivity;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.lqbaselib.net.library.ResourceResult;
import com.galaxyschool.app.wawaschool.pojo.CheckMarkInfo;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.PlaybackParam;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.libs.gallery.ImageInfo;
import com.oosic.apps.iemaker.base.PlaybackActivity;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/11/23 16:28
 * 描    述：天赋密码
 * 修订历史：
 * ================================================
 */

public class TalentipherItemFragment extends ContactsListFragment {

    public static final String TAG = TalentipherItemFragment.class.getSimpleName();
    private ListView listView;
    private TextView textViewBtn;
    /**
     * 界面条目标题集合
     */
    private List<String> dataList;
    /**
     * 图片浏览索引集合
     */
    private List<Integer> indexList;
    /**
     * 图片url集合
     */
    private List<ImageInfo> mediaInfos;
    private String mShareUrl;


    public interface Constatnts {
        String EXTRA_MEMBERID = "memberID";
        String EXTRA_NAME = "name";
    }


    public static TalentipherItemFragment newInstance(String id, String name) {

        Bundle bundle = new Bundle();
        bundle.putString(Constatnts.EXTRA_MEMBERID,id);
        bundle.putString(Constatnts.EXTRA_NAME,name);

        TalentipherItemFragment fragment = new TalentipherItemFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_talentipher_item, container, false);
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.listview_talent);
        //解读对比
        textViewBtn = (TextView) findViewById(R.id.tv_talent_contrast);
        textViewBtn.setOnClickListener(this);
        AdapterViewHelper topGridViewHelper = new AdapterViewHelper(getActivity(),
                listView, R.layout.item_talent_ipher) {
            @Override
            public void loadData() {

            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                String title = (String) getDataAdapter().getItem(position);

                //标题
                TextView textView = (TextView) view.findViewById(R.id.item_title);
                if (textView != null) {
                    textView.setText(title);
                }

                ViewHolder holder = (ViewHolder) view.getTag();
                if (holder == null) {
                    holder = new ViewHolder();
                }
                holder.data = title;

                view.setTag(holder);
                return view;
            }

            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                if (indexList.size() == 0) {
                    TipMsgHelper.ShowLMsg(getActivity(),R.string.ppt_pdf_not_have_pic);
                    return;
                }
                GalleryActivity.newInstance(getActivity(), mediaInfos,false,indexList.get(position),true,false,false,true);

            }
        };
        setCurrAdapterViewHelper(listView, topGridViewHelper);

        
    }

    private void ininData() {
        indexList = new ArrayList<>();
        dataList = new ArrayList<>(7);
        dataList.add(getString(R.string.str_talent_basic_data));
        dataList.add(getString(R.string.str_talent_multiple_intelligence));
        dataList.add(getString(R.string.str_talent_genetic_temperament));
        dataList.add(getString(R.string.str_talent_character_expression));
        dataList.add(getString(R.string.str_talent_selection_of_interest_classes));
        dataList.add(getString(R.string.str_talent_gene_intelligence));
        dataList.add(getString(R.string.str_talent_professional_tendencies));

        getCurrAdapterViewHelper().setData(dataList);

        loadTalentData();

    }

    @Override
    public void loadDataLazy() {
        super.loadDataLazy();
        initView();
        ininData();
    }

    private void loadTalentData() {
        String memberId = getArguments().getString(Constatnts.EXTRA_MEMBERID);
        Map<String, Object> params = new ArrayMap<>();
        params.put("memberId",memberId);

        DefaultResourceListener listener =
                new DefaultResourceListener(ResourceResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        
                        CheckMarkInfo result = JSON.parseObject(jsonString, CheckMarkInfo.class);
                        if (result == null) {
                            return;
                        }
                        List<CheckMarkInfo.ModelBean> data = result.getData();
                        if (data != null && data.size() > 0) {
                            mediaInfos = new ArrayList<>();
                            for (int i = 0; i < data.size(); i++) {
                                CheckMarkInfo.ModelBean bean = data.get(i);
                                List<String> thumbArray = bean.getThumbArray();
                                if (i == 0) {
                                    indexList.add(0);
                                } else {
                                    indexList.add(indexList.get(i - 1) + data.get(i-1).getThumbArray().size());
                                }

                                for (String url : thumbArray) {
                                    ImageInfo imageInfo = new ImageInfo();
                                    imageInfo.setResourceUrl(url);
                                    imageInfo.setTitle(dataList.get(i));
                                    mediaInfos.add(imageInfo);
                                }
                            }
                        }
                        mShareUrl = result.getShareUrl();
                    }

                    @Override
                    public void onError(NetroidError error) {

                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendGetRequest(getActivity(),ServerUrl.GET_GETREPORTBYMEMBERID, params, listener);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_talent_contrast) {
            //解读对比
            getInterpretation();

        }
    }

    /**
     * 获取解读对比
     */
    private void getInterpretation() {
        DefaultResourceListener listener =
                new DefaultResourceListener(ResourceResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        ResourceResult<List<CheckMarkInfo.ModelBean>> result = JSON.parseObject(jsonString, new
                                TypeReference<ResourceResult<List<CheckMarkInfo.ModelBean>>>(){});

                        List<CheckMarkInfo.ModelBean> data = result.getData();
                        if (data != null && data.size() > 0) {
                            String resId = data.get(0).getResId();
                            String resType = data.get(0).getResType();
                            if (!TextUtils.isEmpty(resType)) {
                                resType = "-" + resType;
                            } else {
                                resType = "";
                            }
                            openCourse(resId + resType);
                        } else {
                            TipMsgHelper.ShowMsg(getContext(),getString(R.string.str_talent_no_interpretation_tips));
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendGetRequest(getActivity(),ServerUrl.GET_GETINTERPRETATION, null, listener);


    }


    /**
     * 打开课件
     * @param resId
     */
    public void openCourse(String resId) {

        String tempResId = resId;
        int resType = 0;
        if(resId.contains("-")) {
            String[] ids = resId.split("-");
            if(ids.length == 2) {
                tempResId = ids[0];
                if(ids[1] != null) {
                    resType = Integer.parseInt(ids[1]);
                }
            }
        }
        if(resType > ResType.RES_TYPE_BASE) {
            //分页信息
            if (TextUtils.isEmpty(tempResId)) {
                return;
            }
            WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
            wawaCourseUtils.loadSplitCourseDetail(Integer.parseInt(tempResId));
            wawaCourseUtils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils
                    .OnSplitCourseDetailFinishListener() {
                @Override
                public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                    if(info != null) {
                        CourseData courseData = info.getCourseData();
                        if(courseData != null) {
                            processOpenImageData(courseData);
                        }
                    }
                }
            });
        }else {
            //非分页信息
            WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
            wawaCourseUtils.loadCourseDetail(resId);
            wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.
                    OnCourseDetailFinishListener() {
                @Override
                public void onCourseDetailFinish(CourseData courseData) {
                    processOpenImageData(courseData);
                }
            });
        }
    }

    /**
     * 打开逻辑
     * @param courseData
     */
    private void processOpenImageData(CourseData courseData) {
        if (courseData != null) {
            NewResourceInfo newResourceInfo = courseData.getNewResourceInfo();
            PlaybackParam playbackParam = new PlaybackParam();
            playbackParam.mIsHideToolBar = true;
            if (newResourceInfo.isOnePage() || newResourceInfo.isStudyCard()){
                ActivityUtils.openOnlineOnePage(getActivity(), newResourceInfo, false, playbackParam);
            } else {
                playCourseForTalentCipher(newResourceInfo.getCourseInfo());
            }

        }

    }


    /**
     * 解读对比不播放点读页
     * @param courseInfo
     */
    private void playCourseForTalentCipher(CourseInfo courseInfo) {
        if (courseInfo != null
                && !TextUtils.isEmpty(courseInfo.getResourceurl())) {
            // 去除.zip及之后字符串, 获得课件打开路径
            String courseUrl = courseInfo.getResourceurl();
            if (courseUrl.endsWith(".zip")) {
                courseUrl = courseUrl.substring(0, courseUrl.lastIndexOf('.'));
            } else if (courseUrl.contains(".zip?")) {
                courseUrl = courseUrl.substring(0, courseUrl.lastIndexOf(".zip?"));
            }
            Bundle extras = new Bundle();
            extras.putString(PlaybackActivity.FILE_PATH, courseUrl);
            extras.putInt(PlaybackActivity.ORIENTATION, courseInfo.getScreenType());
            extras.putBoolean(PlaybackActivity.IS_SHOW_SLIDE, false);
            extras.putBoolean(PlaybackActivity.IS_PLAY_ORIGIN_VOICE, true);
            Intent intent = new Intent(getActivity(), PlaybackActivity.class);
            intent.putExtras(extras);
            startActivity(intent);
        }
    }

    public void doShare() {
        if (TextUtils.isEmpty(mShareUrl)) {
            return;
        }
        String name = getArguments().getString(Constatnts.EXTRA_NAME);

        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(getString(R.string.whose_parent,name,getString(R.string.str_talent_cipher)));
        shareInfo.setContent("");
        shareInfo.setTargetUrl(mShareUrl);
        UMImage umImage = null;
        umImage = new UMImage(getActivity(), R.drawable.ic_launcher);
        shareInfo.setuMediaObject(umImage);
        SharedResource resource = new SharedResource();
        resource.setTitle(getString(R.string.whose_parent,name,getString(R.string.str_talent_cipher)));
        resource.setDescription("");
        resource.setShareUrl(mShareUrl);
        resource.setThumbnailUrl("");
        resource.setType(SharedResource.RESOURCE_TYPE_HTML);
        shareInfo.setSharedResource(resource);
        ShareUtils shareUtils = new ShareUtils(getActivity());
        shareUtils.share(getView(), shareInfo);
    }

}
