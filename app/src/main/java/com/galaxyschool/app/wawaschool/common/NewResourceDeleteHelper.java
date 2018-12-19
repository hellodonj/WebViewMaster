package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.galaxyschool.app.wawaschool.ClassResourceListActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.ClassResourceListBaseFragment;
import com.galaxyschool.app.wawaschool.fragment.ClassSpaceFragment;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.lqwawa.lqbaselib.net.library.DataResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by KnIghT on 16-4-26.
 */
public class NewResourceDeleteHelper {
    public static final int SCHOOL_MESSAGE_COURSE = 1;
    public static final int SHOW_CLASS_HOMEWORK = 2;
    private int type = SCHOOL_MESSAGE_COURSE;//type=1校园动态、公开课 type=2秀秀、翻转课堂、作业通知
    private Context context;
    private AdapterViewHelper currAdapterViewHelper;
    private NewResourceInfo data;
    private ImageView imageView;
    private DeleteListener deleteListener;
    private int channelType;
    public NewResourceDeleteHelper(Context context, AdapterViewHelper currAdapterViewHelper, int type, NewResourceInfo data, ImageView imageView) {
        this.context = context;
        this.currAdapterViewHelper = currAdapterViewHelper;
        this.type = type;
        this.data = data;
        this.imageView = imageView;
    }

    public NewResourceDeleteHelper(Context context, AdapterViewHelper currAdapterViewHelper, int
            type, NewResourceInfo data, ImageView imageView,int channelType) {
        this.context = context;
        this.currAdapterViewHelper = currAdapterViewHelper;
        this.type = type;
        this.data = data;
        this.imageView = imageView;
        this.channelType=channelType;
    }

    public void initImageViewEvent(String memeberId, final String tip,boolean isHeadMaster){
        if (isOwnerResource(memeberId) || isHeadMaster) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data != null) {
                        showDeleteResourceDialog(tip);
                    }
                }
            });

        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    public void initImageViewEvent(String memeberId, final String tip) {
        initImageViewEvent(memeberId,tip,false);
    }

    public void initImageViewEvent(final String tip, boolean isHeadMaster, int channelType) {
        if (isHeadMaster && (channelType == ClassResourceListActivity.CHANNEL_TYPE_SHOW ||
                channelType == ClassResourceListActivity.CHANNEL_TYPE_NOTICE)){
            imageView.setVisibility(View.VISIBLE);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data != null) {
                    showDeleteResourceDialog(tip);
                }
            }
        });
    }

    public void initImageViewEventByType(String memeberId, final String tip, boolean isHeadMaster
    ,int type) {
        if (data != null) {
            if (isHeadMaster) {
                //班主任能删除自己所有类型的东西，但是只能删除其他人的秀秀。
                if (data.getAuthorId() != null && memeberId != null) {
                    if (data.getAuthorId() == memeberId) {
                        //自己的东西
                        imageView.setVisibility(View.VISIBLE);
                    } else {
                        //别人的东西
                        if (type != -1) {
                            if (type == NewResourceInfo.TYPE_CLASS_SHOW) {
                                //只展示秀秀
                                imageView.setVisibility(View.VISIBLE);
                            } else {
                                imageView.setVisibility(View.GONE);
                            }
                        }
                    }
                }

            } else {
                if (isOwnerResource(memeberId)) {
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setVisibility(View.GONE);
                }
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewResourceInfo data = (NewResourceInfo) v.getTag();
                    if (data != null) {
                        showDeleteResourceDialog(tip);
                    }
                }
            });
        }
    }

    public boolean isOwnerResource(String memeberId) {
        boolean tag = false;
        if (!TextUtils.isEmpty(memeberId) && !TextUtils.isEmpty(data.getAuthorId()) && memeberId.equals(data.getAuthorId())) {
            tag = true;
        }
        return tag;
    }

    public void showDeleteResourceDialog(String tip) {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                context, null, tip,
                context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                context.getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteResource();
                    }
                });
        messageDialog.show();
    }

    public void deleteResource() {
        Map<String, Object> params = new HashMap();
        params.put("IdList", data.getId());
        params.put("Type", type);
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestListener<DataResult>(
                        (Activity) context, DataResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if ((Activity) context == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            TipsHelper.showToast((Activity) context, R.string.delete_failure);
                            return;
                        } else {
                            if(channelType == NewResourceInfo
                                    .TYPE_CLASS_SHOW||channelType==NewResourceInfo.TYPE_CLASS_SCHOOL_MOVEMENT){
                                deleteBroadcast();//删除秀秀和校园动态的同时，删除播报厅
                            }
                            if(channelType==NewResourceInfo.TYPE_CLASS_LECTURE){
                                deleteOrignalShow();//删除创意学堂的同时，删除创意秀
                            }
                            TipsHelper.showToast((Activity) context, R.string.delete_success);
                            //删除成功，且该消息属于未读消息，需要刷新消息列表。
                            if (!data.isRead()){
                                //设置刷新标志位
                                ClassSpaceFragment.setHasDeletedResource(true);
                                ClassSpaceFragment.setHasContentChanged(true);
                                ClassResourceListBaseFragment.setHasDeletedResource(true);
                            }
                            currAdapterViewHelper.getData().remove(data);
                            currAdapterViewHelper.update();
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest((Activity) context, ServerUrl.DELETE_OWNER_POSTBAR_URL,
                params, listener);
    }
    public void deleteOrignalShow() {
        Map<String, Object> params = new HashMap();
        params.put("MicroId", data.getMicroId()+"-"+data.getResourceType());
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestListener<DataResult>(
                        (Activity) context, DataResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if ((Activity) context == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        } else {
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest((Activity) context, ServerUrl.DELETE_SHOW_URL,
                params, listener);
    }
    public void deleteBroadcast() {
        Map<String, Object> params = new HashMap();
        params.put("Id", data.getId());
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestListener<DataResult>(
                        (Activity) context, DataResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if ((Activity) context == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        } else {
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest((Activity) context, ServerUrl.DELETE_BROADCAST_URL,
                params, listener);
    }


    public interface   DeleteListener{
   void onDelete();
}

    public DeleteListener getDeleteListener() {
        return deleteListener;
    }

    public void setDeleteListener(DeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }
}
