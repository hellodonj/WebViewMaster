package com.lqwawa.intleducation.base.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BaseDialog extends Dialog {
   Context mContext = null;
   LinearLayout mContainer = null;
   TextView mMessage = null;
   TextView mPositiveBtn = null;
   TextView mNegativeBtn = null;
   OnClickListener mPositiveButtonListener = null;
   OnClickListener mNegativeButtonListener = null;
   int mPosetiveTextId = 0;
   int mNegativeTextId = 0;
   String mMessageStr = null;

   public BaseDialog(Context context) {
      super(context, ResourceUtils.getStyleId(context, "Theme_PageDialog"));
      mContext = context;
      // TODO Auto-generated constructor stub
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      // TODO Auto-generated method stub
      super.onCreate(savedInstanceState);
      setContentView(ResourceUtils.getLayoutId(mContext, "ecourse_dialog_view"));
      findViews();
   }

   private void findViews() {
      mContainer = (LinearLayout) findViewById(ResourceUtils.getId(mContext, "container"));
      mMessage = (TextView) findViewById(ResourceUtils.getId(mContext, "msg"));
      mPositiveBtn = (TextView) findViewById(ResourceUtils.getId(mContext, "positive_btn"));
      mNegativeBtn = (TextView) findViewById(ResourceUtils.getId(mContext, "negative_btn"));
      if (mPosetiveTextId > 0) {
         mPositiveBtn.setVisibility(View.VISIBLE);
         mPositiveBtn.setText(mPosetiveTextId);
      }
      if (mNegativeTextId > 0) {
         mNegativeBtn.setVisibility(View.VISIBLE);
         mNegativeBtn.setText(mNegativeTextId);
      }
      if (mMessageStr != null) {
         mMessage.setText(mMessageStr);
      }
      mPositiveBtn.setOnClickListener(new View.OnClickListener() {

         @Override
         public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mPositiveButtonListener != null) {
               mPositiveButtonListener.onClick(BaseDialog.this,
                     ResourceUtils.getId(mContext, "positive_btn"));
            }
            BaseDialog.this.dismiss();
         }
      });
      mNegativeBtn.setOnClickListener(new View.OnClickListener() {

         @Override
         public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mNegativeButtonListener != null) {
               mNegativeButtonListener.onClick(BaseDialog.this,
                     ResourceUtils.getId(mContext, "negative_btn"));
            }
            BaseDialog.this.dismiss();
         }
      });
   }

   public BaseDialog setPositiveButton(int textId, OnClickListener listener) {
      mPosetiveTextId = textId;
      if (mPositiveBtn != null && textId > 0) {
         mPositiveBtn.setVisibility(View.VISIBLE);
         mPositiveBtn.setText(textId);
      }
      mPositiveButtonListener = listener;
      return BaseDialog.this;
   }

   public BaseDialog setNegativeButton(int textId, OnClickListener listener) {
      mNegativeTextId = textId;
      if (mNegativeBtn != null && textId > 0) {
         mPositiveBtn.setVisibility(View.VISIBLE);
         mNegativeBtn.setText(textId);
      }
      mNegativeButtonListener = listener;
      return BaseDialog.this;
   }
   
   public BaseDialog setMessage(int msgId) {
      return setMessage(mContext.getString(msgId));
   }
   
   public BaseDialog setMessage(String msgStr) {
      mMessageStr = msgStr;
      if (mMessage != null) {
         mMessage.setText(msgStr);
      }
      return BaseDialog.this;
   }
   
   public BaseDialog setView(View view) {
      if (mMessage != null) {
         mMessage.setVisibility(View.GONE);
      }
      mContainer.addView(view);
      return BaseDialog.this;
   }
   

}
