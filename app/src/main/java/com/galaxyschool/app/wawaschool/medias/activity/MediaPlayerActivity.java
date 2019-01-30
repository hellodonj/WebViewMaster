package com.galaxyschool.app.wawaschool.medias.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.CollectionHelper;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.pojo.ResType;

import static com.galaxyschool.app.wawaschool.medias.activity.VodPlayActivity.KEY_COLLECTIONSCHOOLID;
import static com.galaxyschool.app.wawaschool.medias.activity.VodPlayActivity.KEY_ISFROMCHOICELIB;
import static com.galaxyschool.app.wawaschool.medias.activity.VodPlayActivity.KEY_ISPUBLICRES;
import static com.lecloud.skin.videoview.VideoPlayActivity.KEY_AUTHORID;
import static com.lecloud.skin.videoview.VideoPlayActivity.KEY_RESID;
import static com.lecloud.skin.videoview.VideoPlayActivity.KEY_RESOURCETYPE;
import static com.lecloud.skin.videoview.VideoPlayActivity.KEY_TITLE;

/**
 * <br/>================================================
 * <br/> 作    者：Blizzard-liu
 * <br/> 版    本：1.0
 * <br/> 创建日期：2017/12/8 13:45
 * <br/> 描    述：视频播放 使用url
 * <br/> 修订历史：
 * <br/>================================================
 */

public class MediaPlayerActivity extends com.libs.mediaplay.MediaPlayerActivity {


    private String mResId;
    private int mResourceType;
    private String mAuthorId;
    /**
     * 公共资源
     */
    private boolean isPublicRes = true;
    /**
     * 是否从校本,精品资源库进入
     */
    private boolean isFromChoiceLib = false;
    /**
     * 学校id
     */
    private String mCollectionSchoolId;

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
           Bundle mBundle = intent.getBundleExtra(DATA);
            if (mBundle == null) {
                TipMsgHelper.ShowMsg(this, "no data");
            } else {
                mResId = mBundle.getString(KEY_RESID);
                mResourceType = mBundle.getInt(KEY_RESOURCETYPE,-1);
                mAuthorId = mBundle.getString(KEY_AUTHORID);
                VIDEO_TITLE = mBundle.getString(KEY_TITLE,"");
                VIDEO_URL = mBundle.getString(EXTRA_VIDEO_PATH);
                mCollectionSchoolId = mBundle.getString(KEY_COLLECTIONSCHOOLID,"");
                isFromChoiceLib = mBundle.getBoolean(KEY_ISFROMCHOICELIB,false);
                isPublicRes = mBundle.getBoolean(KEY_ISPUBLICRES,true);
                isHideBtnMore = mBundle.getBoolean(EXTRA_ISHIDEBTNMORE,true);
            }
        }
    }

    @Override
    protected void handleCollectLogic() {
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
                VIDEO_TITLE,
                mAuthorId,
                tag);
    }
}
