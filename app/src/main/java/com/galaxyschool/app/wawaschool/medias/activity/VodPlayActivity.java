package com.galaxyschool.app.wawaschool.medias.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.CollectionHelper;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.lecloud.skin.videoview.VideoPlayActivity;
import com.osastudio.common.popmenu.CustomPopWindow;
import com.osastudio.common.popmenu.EntryBean;
import com.osastudio.common.popmenu.PopMenuAdapter;

import java.util.ArrayList;
import java.util.List;



/**
  * ================================================
  * 作    者：Blizzard-liu
  * 版    本：1.0
  * 创建日期：2017/5/27 11:06
  * 描    述：点播音视频播放界面,支持声音,亮度,进度调节,增加右上角收藏弹出框
             需配合LetvVodHelper类使用:eg:
                                        new LetvVodHelper.VodVideoBuilder(getActivity())
                                        .setNewUI(true)//使用自定义UI
                                        .setTablet(true)//平板为true
                                        .setTitle(data.getTitle())//视频标题
                                        .setUrl(filePath)//路径
                                        .setMediaType(VodVideoSettingUtil.AUDIO_TYPE)//设置媒体类型
                                        .create();
              播放方式1:传入url
              播放方式2:设置vuid,uuid
  * 修订历史：
  * ================================================
  */
public class VodPlayActivity extends VideoPlayActivity  {
    public static final String KEY_ISPUBLICRES = "IsPublicRes";//公共资源
    public static final String KEY_ISFROMCHOICELIB = "isFromChoiceLib";//是否从校本,精品资源库进入
    public static final String KEY_COLLECTIONSCHOOLID = "CollectionSchoolId";//学校id

    private Bundle mBundle;
    private CustomPopWindow mPopWindow;
    private String mResId;
    private int mResourceType;
    private String mAuthorId;
    private String mTitle;
    private static final int KEY_COLLECT = 0;
    private boolean isPublicRes = true;//公共资源
    private boolean isFromChoiceLib = false;//是否从校本,精品资源库进入
    private String mCollectionSchoolId;//学校id


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();

    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            mBundle = intent.getBundleExtra(DATA);
            if (mBundle == null) {
                Toast.makeText(this, "no data", Toast.LENGTH_LONG).show();
            } else {
                mResId = mBundle.getString(KEY_RESID);
                mResourceType = mBundle.getInt(KEY_RESOURCETYPE,-1);
                mAuthorId = mBundle.getString(KEY_AUTHORID);
                mTitle = mBundle.getString(KEY_TITLE,"");
                mCollectionSchoolId = mBundle.getString(KEY_COLLECTIONSCHOOLID,"");
                isFromChoiceLib = mBundle.getBoolean(KEY_ISFROMCHOICELIB,false);
                isPublicRes = mBundle.getBoolean(KEY_ISPUBLICRES,true);
            }
        }
    }


    /**
     * 点击右上角更多按钮
     * @param view
     */
    @Override
     public void showPopwindow(View view) {
         super.showPopwindow(view);

         View contentView = LayoutInflater.from(this).inflate(R.layout.pop_menu,null);
         //处理popWindow 显示内容
         handleLogic(contentView);

         mPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                 .setView(contentView)//显示的布局，还可以通过设置一个View
//                 .size(260,80) //设置显示的大小，不设置就默认包裹内容
                 .setFocusable(true)//是否获取焦点，默认为ture
                 .setOutsideTouchable(true)//是否PopupWindow 以外触摸dissmiss
                 .create()//创建PopupWindow
                 .showAsDropDown(view,-200, 20);
     }

     /**
      * 处理弹出显示内容、点击事件等逻辑
      * @param contentView
      */
     private void handleLogic(View contentView){
         List<EntryBean> list = new ArrayList<>();
         EntryBean entryBean = new EntryBean();
         entryBean.value = getString(R.string.collection);
         entryBean.id = KEY_COLLECT;
         list.add(entryBean);
         ListView listView = (ListView) contentView.findViewById(R.id.pop_menu_list);
         PopMenuAdapter adapter = new PopMenuAdapter(this,list);
         listView.setAdapter(adapter);
         listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 if(mPopWindow!=null){
                     mPopWindow.dissmiss();
                 }
                 handleCollectLogic();
             }
         });

     }

    /**
     * 点击收藏
     */
     private void handleCollectLogic() {
         if (TextUtils.isEmpty(mAuthorId) || TextUtils.isEmpty(mResId)) {
             return;
         }
         String tag = "";

             if (mResId.contains("-")) {
                 String[] split = mResId.split("-");
                 mResourceType =  Integer.valueOf(split[1]);
             }


         if (mResourceType == ResType.RES_TYPE_VOICE) {
             tag = getString(R.string.audios);

         } else if (mResourceType == ResType.RES_TYPE_VIDEO) {
             tag = getString(R.string.videos);
         }

         CollectionHelper collectionHelper = new CollectionHelper(this);
         collectionHelper.setIsPublicRes(isPublicRes);
         collectionHelper.setCollectSchoolId(mCollectionSchoolId);
         collectionHelper.setFromChoiceLib(isFromChoiceLib);
         collectionHelper.collectDifferentResource(
                 mResId,
                 mTitle,
                 mAuthorId,
                 tag);
     }

 }

