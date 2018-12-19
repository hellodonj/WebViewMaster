package com.lqwawa.libs.mediapaper.io;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Xml;
import com.lqwawa.libs.mediapaper.PaperUtils;
import com.lqwawa.libs.mediapaper.PaperUtils.childViewData;
import com.lqwawa.libs.mediapaper.R;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.LinkedList;

public class Saver {
   final static private String VERSION = "1.00.01";
   private Context mContext = null;
   protected String mSavePath = null;
   protected LinkedList<childViewData> mSaveData = null;
   
   public final static String XML_FILE_NAME = "index.xml";
   public final static String HEAD_FILE = "head.jpg";

//   public static String CSS_FILE = "stylesheet.css";
//   public static String MICRO_BAR_IMAGE = "micro_bar.jpg";
//   public static String VIDEO_BAR_IMAGE = "video_play.jpg";
   
   public Saver(Context context, String pathName) {
      mContext = context;
      mSavePath = pathName;
      if (pathName != null) {
         getSavePathName(pathName);
      }
      
   }
   
   public void UpdatePath(String pathName) {
      mSavePath = pathName;
      if (pathName != null) {
         getSavePathName(pathName);
      }
   }
   
   private void getSavePathName(String pathName){
      String savePath = pathName;
      if (savePath == null) {
         return;
      }
      File distFile = new File(savePath);
      if (!distFile.getParentFile().exists()) {
         distFile.getParentFile().mkdirs();
      }
      if (!distFile.exists()) {
         distFile.mkdirs();
         /*create picture folder*/
         String picFolderPath = savePath + PaperUtils.SUB_IMAGE;//"SUB_IMAGE";
         distFile = new File(picFolderPath);
         distFile.mkdirs();
         /*create audio folder*/
         String audioFolderPath = savePath + PaperUtils.SUB_RECORD;
         distFile = new File(audioFolderPath);
         distFile.mkdirs();
         /*create video folder*/
         String videoFolderPath = savePath + PaperUtils.SUB_VIDEO;
         distFile = new File(videoFolderPath);
         distFile.mkdirs();
//         PaperUtils.saveAssetsFileToLocal(mContext, savePath, "stylesheet.css");
//         PaperUtils.saveAssetsFileToLocal(mContext, savePath + PaperUtils.SUB_IMAGE, "micro_bar.jpg");
//         PaperUtils.saveAssetsFileToLocal(mContext, savePath + PaperUtils.SUB_IMAGE, "video_bar.jpg");
      }
   }
   

   
   public void save(String userTitle, LinkedList<childViewData> data) {
      mSaveData = data;
      writeDataToFile(userTitle, data);
   }


   private void writeDataToFile(String userTitle, LinkedList<childViewData> data) {
      XmlSerializer serializer = Xml.newSerializer();
      StringWriter writer = new StringWriter();
      String xmlString = null;
      String filePath = mSavePath + "/" + XML_FILE_NAME;
      String fileName = PaperUtils.getFileNameFromPaperPath(mSavePath);
      File file = new File(filePath);
      FileOutputStream fos;
      boolean bUpdateMedia = false; 
      
      if (file.exists()) {
         file.delete();
      }

      if ((userTitle == null || userTitle.equals(""))&&
            (data == null || (data != null && data.size() <= 0))) {
//         if (PaperUtils.getDiaryDB(mContext) != null) {
//            PaperUtils.getDiaryDB(mContext).updateDiaryTitle(fileName, userTitle);
//            PaperUtils.getDiaryDB(mContext).updateDiaryMedia(fileName, null, null);
//         }
         return;
      }
         
      try {
         serializer.setOutput(writer);     
         serializer.startTag("", "resources");
         serializer.attribute("", "version", VERSION);
         serializer.text("\n");
         childViewData itemData = null;
         if(!TextUtils.isEmpty(userTitle)) {
               serializer.startTag("", "h");
               serializer.attribute("", "type", PaperUtils.translatePaperChildViewNameToTypeId(PaperUtils.USERTITLE));
//               int index = 0;
               String tempStr = userTitle;
//               index = tempStr.indexOf("\n");
//               while(index >=0) {
//                  if (index == 0) {
//                     serializer.startTag("", "br");
//                     serializer.endTag("", "br");
//                     tempStr = tempStr.substring(index + 1, tempStr.length());
//                  } else if (index > 0) {
//                     serializer.text(tempStr.substring(0, index));
//                     serializer.startTag("", "br");
//                     serializer.endTag("", "br");
//                     tempStr = tempStr.substring(index + 1, tempStr.length());
//                  }
//                  index = tempStr.indexOf("\n");
//               }
               if (tempStr != null) {
                  serializer.text(tempStr);
               }
               serializer.endTag("", "h");
         }

         if (data != null) {
            boolean bHeadReady = false;
            File headFile = new File(mSavePath, HEAD_FILE);
            if (headFile.exists()) {
               headFile.delete();
            }
            for (int i = 0; i < data.size(); i++) {
               String tempViewData = null;
               itemData = data.get(i);
               if (itemData.mViewName == null || itemData.mViewData == null)
                  continue;
               if (!PaperUtils.EDITVIEW.equals(itemData.mViewName)) {
                  if (itemData.mViewName.equals(PaperUtils.RECORDVIEW)) {
                     if (TextUtils.isEmpty(itemData.mViewData) ) {
                        continue;
                     } else {
                        File audioFile = new File(itemData.mViewData);
                        if (!itemData.mViewData.startsWith("http") && (!audioFile.isFile() || !audioFile.exists())) {
                           continue;
                        }
                     }
                     //added rmpan  needn't this default head image because of new design
//                     if (!bHeadReady) {
//                        bHeadReady = true;
//                        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.audio);
//                        if (bmp != null && !bmp.isRecycled()) {
//                           PaperUtils.writeToCacheJPEG(bmp, headFile.getPath());
//                        }
//                     }
                  } else if (itemData.mViewName.equals(PaperUtils.IMAGEVIEW)) {
                     if (!bHeadReady) {
                        bHeadReady = true;
                        PaperUtils.copyFile(new File(itemData.mViewData), headFile);
                     }
                  } else if (itemData.mViewName.equals(PaperUtils.VIDEOVIEW)) {
                     if (!bHeadReady) {
                        bHeadReady = true;
                        PaperUtils.copyFile(new File(itemData.mViewData2), headFile);
                     }
                  } else if (itemData.mViewName.equals(PaperUtils.COURSEVIEW) || itemData.mViewName.equals(PaperUtils.COURSEVIEW2)) {
                     if (!bHeadReady) {
                        bHeadReady = true;
                        PaperUtils.copyFile(new File(itemData.mViewData2), headFile);
                     }
                  } else if (itemData.mViewName.equals(PaperUtils.HWPAGEVIEW)) {
                     if (!bHeadReady) {
                        bHeadReady = true;
                        PaperUtils.copyFile(new File(itemData.mViewData2), headFile);
                     }
                  }

                  int strIndex = itemData.mViewData.indexOf(mSavePath);
                  if (strIndex >=0) {
                     strIndex = strIndex + mSavePath.length();
                     if (strIndex < itemData.mViewData.length()){
   //                     itemData.mViewData = itemData.mViewData.substring(strIndex);
                        tempViewData = itemData.mViewData.substring(strIndex);
                     }
                  } else {
                     tempViewData = itemData.mViewData;
                  }
                  tempViewData = tempViewData;

               }

               if (PaperUtils.EDITVIEW.equals(itemData.mViewName)) {
                  if (TextUtils.isEmpty(itemData.mViewData)) {
                     continue;
                  }
                  serializer.text("\n");
                  serializer.startTag("", "p");
                  serializer.attribute("", "type", PaperUtils.translatePaperChildViewNameToTypeId(itemData.mViewName));
                  serializer.attribute("", "id", String.valueOf(itemData.mViewId));
//                  int index = 0;
                  String tempStr = itemData.mViewData;
//                  index = tempStr.indexOf("\n");
//                  while(index >=0) {
//                     if (index == 0) {
//                        serializer.startTag("", "br");
//                        serializer.endTag("", "br");
//                        tempStr = tempStr.substring(index + 1, tempStr.length());
//                     } else if (index > 0) {
//                        serializer.text(tempStr.substring(0, index));
//                        serializer.startTag("", "br");
//                        serializer.endTag("", "br");
//                        tempStr = tempStr.substring(index + 1, tempStr.length());
//                     }
//                     index = tempStr.indexOf("\n");
//                  }
                  if (tempStr != null) {
                     serializer.text(tempStr);
                  }
                  serializer.endTag("", "p");
               } else if (PaperUtils.IMAGEVIEW.equals(itemData.mViewName) ||
                     PaperUtils.SKETCHPAD.equals(itemData.mViewName)) {
                  serializer.text("\n");
                  serializer.startTag("", "img");
                  serializer.attribute("", "type", PaperUtils.translatePaperChildViewNameToTypeId(itemData.mViewName));
                  serializer.attribute("", "id", String.valueOf(itemData.mViewId));
                  if (tempViewData != null) {
                     serializer.attribute("", "src", tempViewData);//itemData.mViewData);
                  }
                  serializer.endTag("", "img");
               } else if (PaperUtils.VIDEOVIEW.equals(itemData.mViewName)) {
                  serializer.text("\n");
                  serializer.startTag("", "video");
                  serializer.attribute("", "type", PaperUtils.translatePaperChildViewNameToTypeId(itemData.mViewName));
                  serializer.attribute("", "src", tempViewData);//itemData.mViewData);
                  if (itemData.mViewData2 != null) {
                     String thumbData = null;
                     int strIndex = itemData.mViewData2.indexOf(mSavePath);
                     if (strIndex >= 0) {
                        strIndex = strIndex + mSavePath.length();
                        if (strIndex < itemData.mViewData2.length()) {
                           //                     itemData.mViewData = itemData.mViewData.substring(strIndex);
                           thumbData = itemData.mViewData2.substring(strIndex);
                        }
                     } else {
                        thumbData = itemData.mViewData2;
                     }
                     serializer.attribute("", "poster", thumbData);
                  }
                  serializer.endTag("", "video");
               } else if (PaperUtils.RECORDVIEW.equals(itemData.mViewName))  {
                  serializer.text("\n");
                  serializer.startTag("", "audio");
                  serializer.attribute("", "type", PaperUtils.translatePaperChildViewNameToTypeId(itemData.mViewName));
                  serializer.attribute("", "id", String.valueOf(itemData.mViewId));
                  serializer.attribute("", "src", tempViewData);
                  serializer.endTag("", "audio");
               } else if (PaperUtils.COURSEVIEW.equals(itemData.mViewName)) {
                  serializer.text("\n");
                  serializer.startTag("", "cmc");
                  serializer.attribute("", "type", PaperUtils.translatePaperChildViewNameToTypeId(itemData.mViewName));
                  serializer.attribute("", "id", String.valueOf(itemData.mViewId));
                  serializer.attribute("", "src", tempViewData);//itemData.mViewData);
                  if (itemData.mViewData2 != null) {
                     String thumbData = null;
                     int strIndex = itemData.mViewData2.indexOf(mSavePath);
                     if (strIndex >= 0) {
                        strIndex = strIndex + mSavePath.length();
                        if (strIndex < itemData.mViewData2.length()) {
                           //                     itemData.mViewData = itemData.mViewData.substring(strIndex);
                           thumbData = itemData.mViewData2.substring(strIndex);
                        }
                     } else {
                        thumbData = itemData.mViewData2;
                     }
                     serializer.attribute("", "poster", thumbData);
                  }
                  if (itemData.mViewData3 != null) {
                     serializer.attribute("", "cmctitle", itemData.mViewData3);
                  }
                  if (itemData.mViewData4 != null) {
                     serializer.attribute("", "webplayurl", itemData.mViewData4);
                  }
                  serializer.endTag("", "cmc");
               } else if (PaperUtils.HWPAGEVIEW.equals(itemData.mViewName)){
                  serializer.text("\n");
                  serializer.startTag("", "chw");
                  serializer.attribute("", "type", PaperUtils.translatePaperChildViewNameToTypeId(itemData.mViewName));
                  serializer.attribute("", "id", String.valueOf(itemData.mViewId));
                  serializer.attribute("", "src", tempViewData);//itemData.mViewData);
                  if (itemData.mViewData2 != null) {
                     String thumbData = null;
                     int strIndex = itemData.mViewData2.indexOf(mSavePath);
                     if (strIndex >= 0) {
                        strIndex = strIndex + mSavePath.length();
                        if (strIndex < itemData.mViewData2.length()) {
                           //                     itemData.mViewData = itemData.mViewData.substring(strIndex);
                           thumbData = itemData.mViewData2.substring(strIndex);
                        }
                     } else {
                        thumbData = itemData.mViewData2;
                     }
                     serializer.attribute("", "poster", thumbData);
                  }
                  if (itemData.mViewData3 != null) {
                     serializer.attribute("", "chwtitle", itemData.mViewData3);
                  }
                  if (itemData.mViewData4 != null) {
                     serializer.attribute("", "webplayurl", itemData.mViewData4);
                  }
                  serializer.endTag("", "chw");
               } else if (PaperUtils.COURSEVIEW2.equals(itemData.mViewName)) {
                  serializer.text("\n");
                  serializer.startTag("", "cmc2");
                  serializer.attribute("", "type", PaperUtils.translatePaperChildViewNameToTypeId(itemData.mViewName));
                  serializer.attribute("", "id", String.valueOf(itemData.mViewId));
                  serializer.attribute("", "src", tempViewData);//itemData.mViewData);
                  serializer.attribute("", "orientation", String.valueOf(itemData.mViewData5));
                  if (itemData.mViewData2 != null) {
                     String thumbData = null;
                     int strIndex = itemData.mViewData2.indexOf(mSavePath);
                     if (strIndex >= 0) {
                        strIndex = strIndex + mSavePath.length();
                        if (strIndex < itemData.mViewData2.length()) {
                           //                     itemData.mViewData = itemData.mViewData.substring(strIndex);
                           thumbData = itemData.mViewData2.substring(strIndex);
                        }
                     } else {
                        thumbData = itemData.mViewData2;
                     }
                     serializer.attribute("", "poster", thumbData);
                  }
                  if (itemData.mViewData3 != null) {
                     serializer.attribute("", "cmctitle", itemData.mViewData3);
                  }
                  if (itemData.mViewData4 != null) {
                     serializer.attribute("", "webplayurl", itemData.mViewData4);
                  }
                  serializer.endTag("", "cmc2");
               }

            }
         }
         serializer.text("\n");
         serializer.endTag("", "resources");
         serializer.text("\n");
         serializer.flush();
         xmlString = writer.toString();
         
       } catch (Exception e1) {
          e1.printStackTrace();
       }
       
//       if (!bUpdateMedia && PaperUtils.getDiaryDB(mContext) != null) {
//          PaperUtils.getDiaryDB(mContext).updateDiaryMedia(fileName, null, null);
//       }
    
      if (true) {
          try {
             fos = new FileOutputStream(file);
             OutputStreamWriter outStreamWriter = new OutputStreamWriter(fos);
             outStreamWriter.write(xmlString);
             outStreamWriter.close();
             fos.close();

          } catch (Exception e) {
             e.printStackTrace();
          }
       }
   }
}