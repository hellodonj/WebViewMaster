package com.lqwawa.lqbaselib.net;

import android.util.Log;

import com.osastudio.common.utils.LogUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class FileApi {
	public static String postFile(
	         String actionUrl, Map<String, File> files) throws IOException {

	         String BOUNDARY = java.util.UUID.randomUUID().toString();
	         String PREFIX = "--", LINEND = "\r\n";
	         String MULTIPART_FROM_DATA = "multipart/form-data";
	         String CHARSET = "UTF-8";
	         URL uri = new URL(actionUrl);
	         HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		     conn.setChunkedStreamingMode(0);
	         conn.setReadTimeout(60 * 1000);
	         conn.setDoInput(true);// 允许输入
	         conn.setDoOutput(true);// 允许输出
	         conn.setUseCaches(false);
	         conn.setRequestMethod("POST"); // Post方式
	         conn.setRequestProperty("connection", "keep-alive");
	         conn.setRequestProperty("Charsert", "UTF-8");
	         conn.setRequestProperty(
	             "Content-Type", MULTIPART_FROM_DATA
	                 + ";boundary=" + BOUNDARY);

	         DataOutputStream outStream = new DataOutputStream(
	             conn
	                 .getOutputStream());

	         // 发送文件数据
	         if (files != null)
	             for (Map.Entry<String, File> file : files.entrySet()) {
//					 File fileVaule = file.getValue();
//					 String filePath = fileVaule.getPath();
//					 String fileName = Utils.getFileNameFromPath(filePath);
					 StringBuilder sb1 = new StringBuilder();
	                 sb1.append(PREFIX);
	                 sb1.append(BOUNDARY);
	                 sb1.append(LINEND);
	                 sb1
	                     .append(
	                         "Content-Disposition: form-data; name=\"file\"; filename=\""
	                             + file.getKey() + "\"" + LINEND);
	                 sb1.append(
	                     "Content-Type: application/octet-stream; charset="
	                         + CHARSET + LINEND);
	                 sb1.append(LINEND);
	                 outStream.write(sb1.toString().getBytes());
	                 InputStream is = new FileInputStream(file.getValue());
	                 byte[] buffer = new byte[1024];
	                 int len = 0;
	                 while ((len = is.read(buffer)) != -1) {
	                     outStream.write(buffer, 0, len);
	                 }

	                 is.close();
	                 outStream.write(LINEND.getBytes());
	             }

	         // 请求结束标志
	         byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
	         outStream.write(end_data);
	         outStream.flush();

	         // 得到响应码
	         int res = conn.getResponseCode();
	         InputStream in = conn.getInputStream();
	         String result = "";
	         if (res == 200) {

	             InputStreamReader isReader = new InputStreamReader(in);
	             BufferedReader bufReader = new BufferedReader(isReader);
	        	 bufReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	             
	             String line = null;
	             while((line = bufReader.readLine())!=null){
	               result += line;
	             }
	             Log.i("post-------------", result);
	           
	         }
	         outStream.close();
	         conn.disconnect();
	         return result;
	     }
	
	public static String getFile(String url, final String filePath) {
		return getFile(url, filePath, false);
	}

	public static String getFile(String url, final String filePath, boolean isUseCache) {
		URL newurl = null;
		File output = null;
		boolean isOk = false;
		try {
			newurl = new URL(url);

			URLConnection conn = newurl.openConnection();
			conn .setRequestProperty("Accept-Encoding", "identity");
			conn.setConnectTimeout(20 * 1000);
			
			int fileSize = conn.getContentLength();

//			if (fileSize <= 0) {
//				return null;
//			}

			output = new File(filePath);
			if (!output.getParentFile().exists()) {
				output.getParentFile().mkdirs();
			}
			if (output.exists()) {
				if (isUseCache && output.length() == fileSize) {
					return filePath;
				} else {
					output.delete();
				}
			}
			output.createNewFile();
			InputStream is = conn.getInputStream();
			FileOutputStream out = new FileOutputStream(output);
			int ch, count = 0;
			byte[] buffer = new byte[1024];
			// read (ch) bytes into buffer
			while ((ch = is.read(buffer)) > 0) {
				// write (ch) byte from buffer at the position 0
				count += ch;
				out.write(buffer, 0, ch);
				out.flush();
			}
			out.close();
			if (count == fileSize) {
				isOk = true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtils.logd("getFile error", e.toString());
			e.printStackTrace();
		}
		//shouyi add
		if (isOk) {
			return filePath;
		} else {
			if (output != null && output.exists()) {
				output.delete();
			}
		}

		return null;
	}
}
