package com.lqwawa.libs.mediapaper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import java.io.File;
import java.io.FileFilter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ImportDialog extends Dialog {
   Context mContext = null;
   private int mWidth, mHeight;
   private TextView mFolderText;
   private List<FolderItem> mFolderAy;
   String mCurrentPath;
   private ListView mListView;
   private String mParentPath = "/mnt";/*
                                        * Environment.getExternalStorageDirectory
                                        * () .getParentFile().toString();
                                        */
   private String mFilePath = null;
   private ImageAdapter mAdapter = null;
   private ImportDialogHandler mImportDialogHandler = null;
   private int mSelectFileType = 1;
   private int mAttachmentBtnRes = 0;

   final static public int FILE_FILTER_TYPE_IMAGE = 1;
   final static public int FILE_FILTER_TYPE_AUIDO = 2;
   final static public int FILE_FILTER_TYPE_VIDEO = 3;

   final static private String[] IMAGE_FORMAT = {
           ".jpg", ".jpeg", ".png",".bmp"
   };
   final static private String[] AUDIO_FORMAT = {".mp3", ".m4a"};
   final static private String[] VIDEO_FORMAT = {".mp4"};


   class FolderItem {
      String path;
      boolean bFolder;

      FolderItem(String path, boolean bFolder) {
         this.path = path;
         this.bFolder = bFolder;
      }
   }

   public ImportDialog(Context context, int width, int height,
                       int selectFileType, int attachmentBtnRes, ImportDialogHandler importDialogHandler) {
      super(context, R.style.Theme_mpPageDialogFullScreen);
      mContext = context;
      mSelectFileType = selectFileType;
      mAttachmentBtnRes = attachmentBtnRes;
      mWidth = width;
      mHeight = height;
      mImportDialogHandler = importDialogHandler;

   }

//   public void setupAttachButton(int imageRes, final View.OnClickListener clickListener) {
//
//      ImageView attachBtn = (ImageView)findViewById(R.id.attach_btn);
//      if (attachBtn != null) {
//         attachBtn.setImageResource(imageRes);
//         attachBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               if (clickListener != null) {
//                  clickListener.onClick(view);
//               }
//               ImportDialog.this.dismiss();
//            }
//         });
//      }
//   }


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.import_dialog_mp);

      String lastFolder = mParentPath;

      ImageView attachBtn = (ImageView)findViewById(R.id.attach_btn);
      if (attachBtn != null) {
         attachBtn.setImageResource(mAttachmentBtnRes);
         attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (mImportDialogHandler != null) {
                  mImportDialogHandler.onAttachmentButtonClick(mSelectFileType);
               }
               ImportDialog.this.dismiss();
            }
         });
      }

      TextView dialogTitle = (TextView)findViewById(R.id.dialog_title);
      if (dialogTitle != null) {
         if (mSelectFileType == FILE_FILTER_TYPE_IMAGE) {
            dialogTitle.setText(R.string.photolib);
         }  else if (mSelectFileType == FILE_FILTER_TYPE_AUIDO) {
            dialogTitle.setText(R.string.audio_lib);
         }
      }

      mListView = (ListView)findViewById(R.id.listview);
      mAdapter = new ImageAdapter(this.getContext(), R.layout.import_list_item_mp);
      mListView.setOnItemClickListener(new ClickViewItemAction());
      mListView.setAdapter(mAdapter);
//      if (mHeight > 0) {
//         View root = findViewById(R.id.root);
//         LayoutParams lp = root.getLayoutParams();
//         lp.height = mHeight;
//         root.setLayoutParams(lp);
//      }

      mFolderText = (TextView) findViewById(R.id.path);
      mFolderText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

      mFolderText.setOnClickListener(new View.OnClickListener() {

         public void onClick(View v) {
            if (!mCurrentPath.equals(mParentPath)) {
               File file = new File(mCurrentPath);

               loadFolderItem(file.getParent());
            }
         }
      });

      mFolderAy = new ArrayList<FolderItem>();

      loadFolderItem(lastFolder);
      if (mFilePath != null) {
         selectFile(mFilePath);
      }
   }

   private static Comparator<FolderItem> sTitleComparator = new Comparator<FolderItem>() {
      public int compare(FolderItem obj1, FolderItem obj2) {
         if ((obj1.bFolder == obj2.bFolder))
            return Collator.getInstance().compare(obj1.path, obj2.path);
         else if (obj1.bFolder && !obj2.bFolder)
            return -1;
         else if (obj2.bFolder && !obj1.bFolder)
            return 1;
         return 0;
      }
   };




   private final class ClickViewItemAction implements OnItemClickListener {

      public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
            long arg3) {
         if (mFolderAy.get(arg2).bFolder) {
            if (mFolderAy.get(arg2).path.equals("..."))
               mFolderText.performClick();
            else
               loadFolderItem(mFolderAy.get(arg2).path);
         } else {
            final String path = mFolderAy.get(arg2).path;
            selectFile(path);
         }

      }
   }

   private String getFileTitle(String path) {
      String name = "New File";
      int firstIndex = path.lastIndexOf('/');

      try {
         name = path.subSequence(firstIndex + 1, path.length()).toString();
      } catch (Exception e) {
         e.printStackTrace();
      }

      return name;
   }



   public static String getFileTitle(String path, String whiteobardfolder,
         String whiteboardsuffix) {
      int firstIndex = path.lastIndexOf('/');
      int lastIndex = path.lastIndexOf('.');
      if (lastIndex <= firstIndex)
         lastIndex = path.length();
      String temp = "New File";
      try {
         temp = path.subSequence(firstIndex + 1, lastIndex).toString();
      } catch (Exception e) {
         temp = "New File";
         e.printStackTrace();
      }

      String name = temp;
      int i = 1;
      while (new File(whiteobardfolder + name + whiteboardsuffix).exists()) {
         name = temp + "-" + i;
         i++;
      }

      return name;
   }

   @Override
   public void onBackPressed() {
      // File sdcard = Environment.getExternalStorageDirectory();

      if (!mCurrentPath.equals(mParentPath)) {
         File file = new File(mCurrentPath);

         loadFolderItem(file.getParent());
      } else {
         super.onBackPressed();
      }
   }


   FileFilter filter = new FileFilter() {
      public boolean accept(File file) {

         if (file.isDirectory()) {
            if (file.isHidden()) {
               return false;
            } else {
               if (file.listFiles() != null && file.listFiles().length > 0) {
                  return true;
               } else {
                  return false;
               }
            }
            // return true;
         } else if (file.isFile()
               && !file.isHidden()
               && (checkFileFormatByExt(mSelectFileType, file.getName()))/*(file.getName().toLowerCase().endsWith(".mp3")|| file.getName().toLowerCase().endsWith(".m4a"))*/) {
            return true;
         }
         return false;
      }
   };

   private static boolean checkFileFormatByExt(int selectFileType, String fileName) {
      boolean rtn = false;
      if (TextUtils.isEmpty(fileName)) {
         return rtn;
      }
      String[] formatGrp = null;
      switch (selectFileType) {
         case FILE_FILTER_TYPE_IMAGE:
            formatGrp = IMAGE_FORMAT;
            break;
         case FILE_FILTER_TYPE_AUIDO:
            formatGrp = AUDIO_FORMAT;
            break;
         case FILE_FILTER_TYPE_VIDEO:
            formatGrp = VIDEO_FORMAT;
            break;
      }

      if(formatGrp != null && formatGrp.length > 0) {
         for (int i = 0; i < formatGrp.length; i++) {
            if (fileName.toLowerCase().endsWith(formatGrp[i])) {
               rtn = true;
               break;
            }
         }
      }
      return rtn;
   }

   private void loadFolderItem(String path) {
      File folder = new File(path);

      mCurrentPath = path;

      mFolderText.setText(mCurrentPath);

      File list[] = folder.listFiles(filter);
      if (list == null)
         return;

      List<FolderItem> folderList = new ArrayList<FolderItem>();
      if (!mCurrentPath.equals(mParentPath))
         folderList.add(new FolderItem("...", true));

      for (int i = 0; i < list.length; i++) {
         if (list[i].isDirectory()) {
//            if (!list[i].getPath().equals(Utils.APP_FOLDER)) {
            if (!new File(list[i].getPath(), ".nomedia").exists()) {
               File sublist[] = list[i].listFiles();
               if (sublist != null && sublist.length > 0)
                  folderList.add(new FolderItem(list[i].getPath(), true));
            }
         }
      }

      for (int i = 0; i < list.length; i++) {
         if (list[i].isFile())
            folderList.add(new FolderItem(list[i].getPath(), false));
      }
      Collections.sort(folderList, sTitleComparator);

      mFolderAy = folderList;
      if (mAdapter != null) {
         mAdapter.notifyDataSetChanged();
      }
      mListView.setSelection(1);
   }


   class ImageAdapter extends BaseAdapter {

      private Context mContext;
      int layout;

      public ImageAdapter(Context c, int layout) {

         mContext = c;
         this.layout = layout;
      }

      public int getCount() {
         if (mFolderAy == null)
            return 0;
         return mFolderAy.size();
      }

      public Object getItem(int position) {
         if (mFolderAy == null)
            return null;
         return mFolderAy.get(position);
      }

      public long getItemId(int position) {
         return position;
      }

      // create a new ImageView for each item referenced by the Adapter
      public View getView(int position, View convertView, ViewGroup parent) {
         View itemView;
         if (convertView == null) {
            itemView = LayoutInflater.from(mContext).inflate(layout, null);
         } else {
            itemView = convertView;
         }
         ImageView iv = (ImageView) itemView.findViewById(R.id.fileIcon);
         TextView title = (TextView) itemView.findViewById(R.id.fileName);

         if (mFolderAy == null)
            return itemView;

         // imageView.setImageResource(mThumbIds[position]);
         String path = mFolderAy.get(position).path;

         if (mFolderAy.get(position).bFolder) {
            if (path.equals("..."))
               iv.setImageBitmap(null);
            else
               iv.setImageResource(R.drawable.icon_folder);
         } else {
            if(mSelectFileType == FILE_FILTER_TYPE_AUIDO) {
               iv.setImageResource(R.drawable.list_audio);
            } else if(mSelectFileType == FILE_FILTER_TYPE_IMAGE) {
               iv.setImageResource(R.drawable.list_picture);
            } else if(mSelectFileType == FILE_FILTER_TYPE_VIDEO) {
               iv.setImageResource(R.drawable.list_video);
            }
         }

         String temp = getFileTitle(path);
         title.setText(temp);

         return itemView;
      }
   }

   private void selectFile(String filePath) {
      if (mImportDialogHandler != null) {
         mImportDialogHandler.onFileSelect(mSelectFileType, filePath);
      }
      dismiss();
   }

//   public interface FileSelectHandler {
//      public void onFileSelect(int fileType, String filePath);
//   }

   public interface ImportDialogHandler {
      public void onFileSelect(int fileType, String filePath);
      public void onAttachmentButtonClick(int fileType);
   }


}
