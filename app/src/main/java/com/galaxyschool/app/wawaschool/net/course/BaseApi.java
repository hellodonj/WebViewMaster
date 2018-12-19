package com.galaxyschool.app.wawaschool.net.course;

import android.content.Context;
import android.text.TextUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class BaseApi {

    public static final boolean DEFAULT_CHECK_CONNECTIVITY_STATUS = false;
    public static final int DEFAULT_RETRY_TIMES_ON_ERROR = 3;

   
   public static String postFile(Context context, String urlServer, String file_path, String thumbnail_path,
		   String param_str) throws ClientProtocolException, IOException, JSONException {
      String uri = urlServer;
      StringBuilder builder = new StringBuilder(uri.trim());
      if (param_str != null && !param_str.equals("")) {
      try {
         builder.append("?j=").append(URLEncoder.encode(param_str.trim(), "UTF-8"));
      } catch (UnsupportedEncodingException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
        uri = builder.toString().trim();
      }

      try {

          DefaultHttpClient http_client = new DefaultHttpClient();
          //set connection & read timeout to 60000ms
          http_client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
          http_client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
          HttpPost http_post = new HttpPost(uri);

//          MultipartEntityBuilder bodyEntry = MultipartEntityBuilder.create();
//          bodyEntry.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
          Charset chars = Charset.forName(HTTP.UTF_8);
//          bodyEntry.setCharset(chars);
          MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,
                  null, chars);

          if (!TextUtils.isEmpty(file_path) && new File(file_path).exists()) {
             File file = new File(file_path);
//             bodyEntry.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, file.getName());
              entity.addPart("file", new FileBody(file));
          }
          if (!TextUtils.isEmpty(thumbnail_path) && new File(thumbnail_path).exists()) {
            File thumb = new File(thumbnail_path);
//            bodyEntry.addBinaryBody("thumbnail", thumb, ContentType.DEFAULT_BINARY, thumb.getName());
              entity.addPart("thumnail", new FileBody(thumb));
          }

//          http_post.setEntity(bodyEntry.build());
          http_post.setEntity(entity);

          HttpResponse httpResponse = null;

          httpResponse = http_client.execute(http_post);

          String responseData = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);

          int responseCode = httpResponse.getStatusLine().getStatusCode();
          
          return responseData;

      } catch (UnsupportedEncodingException ex) {
          ex.printStackTrace();
      } catch (IOException ex) {
          ex.printStackTrace();
      }

      return null;
      
    }

    public static String postFile(String urlServer, List<String> paths, String param_str) throws ClientProtocolException, IOException, JSONException {
        String uri = urlServer;
        StringBuilder builder = new StringBuilder(uri.trim());
        if (param_str != null && !param_str.equals("")) {
            try {
                builder.append("?j=").append(URLEncoder.encode(param_str.trim(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            uri = builder.toString().trim();
        }

        try {
            DefaultHttpClient http_client = new DefaultHttpClient();
            HttpPost http_post = new HttpPost(uri);

            Charset chars = Charset.forName(HTTP.UTF_8);
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,
                null, chars);

            for(int i=0, size = paths.size(); i< size; i++) {
                String path = paths.get(i);
                if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                    File file = new File(path);
                    entity.addPart("file" + i, new FileBody(file));
                }
            }


            http_post.setEntity(entity);

            HttpResponse httpResponse = null;
            httpResponse = http_client.execute(http_post);
            String responseData = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            return responseData;

        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;

    }

    protected static String formJSONString(Map<String, Object> parameters) {

        return formJSONObject(parameters).toString();
    }

    protected static JSONObject formJSONObject(Map<String, Object> parameters) {
        // form json string
        JSONObject jsonObject = new JSONObject();
        // sign the parameters (add "timestamp", "nounce" and "signature" to
        // parameters)

        // put parameters into jsonObject
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {

            try {
                jsonObject.put(
                    entry.getKey(),
                    (entry.getValue() == null) ? JSONObject.NULL : entry
                        .getValue());
            } catch (JSONException e) {
                throw new RuntimeException(
                    "A value is a String, Boolean, Integer, Long, Double.", e);
            }
        }

        return jsonObject;
    }

}
