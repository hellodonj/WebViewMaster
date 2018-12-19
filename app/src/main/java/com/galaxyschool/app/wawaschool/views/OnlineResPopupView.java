package com.galaxyschool.app.wawaschool.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.fragment.TeachResourceFragment;
import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.oosic.apps.share.ShareItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OnlineResPopupView extends PopupWindow implements OnTouchListener {

    private LayoutInflater layoutInflater;
    private Context context;
    private View rootView;
    private GridView gridView;
    private RelativeLayout cancelButton;
    private ShareAdapter shareAdapter;
    private List<ShareItem> shareItems;
    private SimpleAdapter saImageItems;
    private LinearLayout layoutGridView;
    private ContainsEmojiEditText contentEditText;
    private ImageView addOnlineRes;
    private int currentStatus=-1;
    private boolean tempFlag;
    private Handler handler;
    public interface CurrentStatus{
        int addResource=1;
        int editContent=2;
    }
    private int[] name = {
            R.string.start_image,
            R.string.take_shoot,
            R.string.videos,
            R.string.audios,
//            R.string.personal_resource_library,
//            R.string.public_course
    };
    private int[] image = {
            R.drawable.airclass_picture,
            R.drawable.airclass_take_video,
            R.drawable.airclass_video,
            R.drawable.airclass_audio,
//            R.drawable.airclass_person_library,
//            R.drawable.airclass_public_library,
            };

    private int[] type={
            TeachResourceFragment.ResourceType.PHOTO_ABLUM,
            TeachResourceFragment.ResourceType.TAKE_PHOTO,
            TeachResourceFragment.ResourceType.VIDEO,
            TeachResourceFragment.ResourceType.AUDIO,
//            TeachResourceFragment.ResourceType.PERSONAL_LIBRARY,
//            TeachResourceFragment.ResourceType.PUBLIC_LIBRARY
    };

    public OnlineResPopupView(Context context,Handler handler,int currentStatus) {
        super(context);
        this.context = context;
        this.handler=handler;
        this.currentStatus=currentStatus;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initViews();
        setListeners();
    }


    private void initViews() {
        rootView = layoutInflater.inflate(R.layout.online_res_popup_view, null);
        gridView = (GridView) rootView.findViewById(R.id.share_gridView);
        layoutGridView= (LinearLayout) rootView.findViewById(R.id.layout_gridView);
        contentEditText= (ContainsEmojiEditText) rootView.findViewById(R.id.comment_edittext);
        addOnlineRes= (ImageView) rootView.findViewById(R.id.add_online_resource);
        addOnlineRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStatus==CurrentStatus.editContent){
                    currentStatus=CurrentStatus.addResource;
                    hideGridViewData(false);
                    UIUtils.hideSoftKeyboardValid((Activity) context,contentEditText);
                }else if (currentStatus==CurrentStatus.addResource){
                    currentStatus=CurrentStatus.editContent;
                    UIUtils.showSoftKeyboardOnEditText(contentEditText);
                    hideGridViewData(true);
                }
            }
        });

        rootView.setOnTouchListener(this);
        setContentView(rootView);
        setFocusable(true);
        setAnimationStyle(R.style.AnimBottom);
        setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        setHeight(ViewGroup.LayoutParams.FILL_PARENT);
        int color = context.getResources().getColor(R.color.popup_root_view_bg);
        ColorDrawable colorDrawable = new ColorDrawable(color);
        setBackgroundDrawable(colorDrawable);
    }
    private void setListeners(){
        if (contentEditText!=null){
            contentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus){
                        currentStatus=CurrentStatus.addResource;
                        if (tempFlag) {
                            hideGridViewData(true);
                        }
                        if (!tempFlag){
                            tempFlag=true;
                        }
                        UIUtils.showSoftKeyboardOnEditText(contentEditText);
                    }else {
                        currentStatus=CurrentStatus.editContent;
                        hideGridViewData(false);
                        UIUtils.hideSoftKeyboardValid((Activity) context,contentEditText);
                    }
                }
            });
        }
        contentEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_DONE){
                    Bundle bundle=new Bundle();
                    String content=contentEditText.getText().toString().trim();
                    if (TextUtils.isEmpty(content)){
                        TipMsgHelper.ShowMsg(context,R.string.send_content_cannot_null);
                        return false;
                    }
                    bundle.putString("data",content);
                    Message message=Message.obtain();
                    message.setData(bundle);
                    message.what=TeachResourceFragment.MSG_SEND_TEXT_FINISH;
                    handler.sendMessage(message);
                    contentEditText.setText("");
                    UIUtils.hideSoftKeyboardValid((Activity) context,contentEditText);
                    dismiss();
                }
                return true;
            }
        });
        contentEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStatus=CurrentStatus.editContent;
                hideGridViewData(true);
                UIUtils.showSoftKeyboardOnEditText(contentEditText);
            }
        });
    }

    public void setDefaultEditText(String editText){
        if (contentEditText != null){
            contentEditText.setText(editText);
           if (!TextUtils.isEmpty(editText)){
               contentEditText.setSelection(editText.length());
           }
        }
    }

    public String getCurrentEditTextContent(){
        return contentEditText.getText().toString();
    }
    public void setIsNew(boolean isNew) {
        if(!isNew) {
            List<HashMap<String, Object>> shareList = new ArrayList<HashMap<String, Object>>();
            for (int i = 0; i < image.length; i++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemImage", image[i]);//添加图像资源的ID
                map.put("ItemText", context.getString(name[i]));//按序号做ItemText
                shareList.add(map);
            }

            saImageItems = new SimpleAdapter(
                context, shareList, R.layout.share_item, new String[]{"ItemImage", "ItemText"},
                new int[]{R.id.imageView1, R.id.textView1});
            gridView.setAdapter(saImageItems);
        } else {
            initShareItems();
            shareAdapter = new ShareAdapter();
            gridView.setAdapter(shareAdapter);
        }
    }
    private void initShareItems(){
        shareItems = new ArrayList<ShareItem>();
        for (int i=0;i<name.length;i++){
            ShareItem shareItem=new ShareItem(name[i],image[i],type[i]);
            shareItems.add(shareItem);
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int height = rootView.findViewById(R.id.share_pop_layout).getTop();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (y < height) {
                dismiss();
            }
        }
        return true;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
        gridView.setOnItemClickListener(listener);
    }
    public void setTempFlag(boolean tempFlag) {
        this.tempFlag = tempFlag;
    }

    public void hideGridViewData(boolean isShow){
        if (isShow){
            layoutGridView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    layoutGridView.setVisibility(View.GONE);
                }
            },1);
        }else {
            layoutGridView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    layoutGridView.setVisibility(View.VISIBLE);
                }
            },200);
        }

    }
    private class ShareAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return shareItems == null ? 0 : shareItems.size();
        }

        @Override
        public Object getItem(int position) {
            return shareItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.popwindow_item, null);
            TextView textView = (TextView) view.findViewById(R.id.textView1);
            ImageView imageView = (ImageView)view.findViewById(R.id.imageView1);
            String resName=context.getResources().getString(shareItems.get(position).getTitleId());
            textView.setText(resName);
            imageView.setImageResource(shareItems.get(position).getIconId());
            return view;
        }
    }
}
