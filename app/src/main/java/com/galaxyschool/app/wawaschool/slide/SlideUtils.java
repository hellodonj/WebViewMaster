package com.galaxyschool.app.wawaschool.slide;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class SlideUtils {

   public static final String TAG = "eHomework";

   /**
    * create the folder with selected path.
    * 
    * @param path
    */
   public static void createLocalDiskPath(String path) {
      File folder = new File(path);
      try {
         if (!folder.exists()) {

            boolean rtn = folder.mkdirs();
            outLog("createLocalDiskPath", "rtn=" + rtn);
         }
      } catch (Exception e) {
         outLog("createLocalDiskPath", "Exception");
      }
   }

   public static boolean safeDeleteDirectory(String dir) {
      File dirFile = new File(dir);
      if (!dirFile.exists() || !dirFile.isDirectory()) {
         return false;
      }

      String path = dir.endsWith("/") ? dir.substring(0, dir.length() - 1)
            : dir;
      File file = new File(path);
      File to = new File(path + System.currentTimeMillis());
      if (file.renameTo(to)) {
         return deleteDirectory(to.getAbsolutePath());
      }
      return false;
   }

   private static boolean deleteDirectory(String dir) {
      if (!dir.endsWith(File.separator)) {
         dir = dir + File.separator;
      }
      File dirFile = new File(dir);
      if (!dirFile.exists() || !dirFile.isDirectory()) {
         return false;
      }
      boolean flag = true;
      File[] files = dirFile.listFiles();
      for (int i = 0; i < files.length; i++) {
         if (files[i].isFile()) {
            flag = deleteFile(files[i].getAbsolutePath());
            if (!flag) {
               break;
            }
         } else {
            flag = deleteDirectory(files[i].getAbsolutePath());
            if (!flag) {
               break;
            }
         }
      }

      if (!flag) {
         System.out.println("delete dir fail");
         return false;
      }

      if (dirFile.delete()) {
         return true;
      } else {
         return false;
      }
   }

   public static boolean deleteFile(String filePath) {
      if (filePath == null) {
         return false;
      }
      File file = new File(filePath);
      if (file.isFile() && file.exists()) {
         file.delete();
         return true;
      } else {
         return false;
      }
   }

   static public void unzip(String inputFilePath, String outputFilePath)
           throws Exception {
      if (TextUtils.isEmpty(inputFilePath) || TextUtils.isEmpty(outputFilePath)) {
         return;
      }
      if (!outputFilePath.endsWith(File.separator)) {
         outputFilePath = outputFilePath + File.separator;
      }
      ZipInputStream in = new ZipInputStream(new FileInputStream(inputFilePath));
      ZipEntry z;
      String name = "";
      int counter = 0;

      while ((z = in.getNextEntry()) != null) {
         name = z.getName();

         if (name.contains("\\\\")) {
            name = name.replace("\\\\","/");
         }
         if (name.contains("\\")) {
            name = name.replace("\\", "/");
         }
         int index = name.indexOf(File.separator);
         if (index > 0 && ++index < name.length()) {
            name = name.substring(index);
         }
         if (z.isDirectory()) {

            name = name.substring(0, name.length() - 1);

            File folder = new File(outputFilePath + name);
            folder.mkdir();
            if (counter == 0) {
            }
            counter++;
         } else {
            File file = new File(outputFilePath + name);
            if (!file.getParentFile().exists()) {
               file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
               file.createNewFile();
            }
            // get the output stream of the file
            FileOutputStream out = new FileOutputStream(file);
            int ch;
            byte[] buffer = new byte[1024];
            // read (ch) bytes into buffer
            while ((ch = in.read(buffer)) > 0) {
               // write (ch) byte from buffer at the position 0
               out.write(buffer, 0, ch);
               out.flush();
            }
            out.close();
         }
      }

      in.close();
   }


   public static void outLog(String tag, String msg) {
      Log.d(TAG, tag + " >>>>>>>>>>>>>>>>>> " + msg);
   }
}
