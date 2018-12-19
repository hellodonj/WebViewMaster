package com.lqwawa.lqbaselib.net.library;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.AuthFailureError;
import com.duowan.mobile.netroid.Delivery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.NetworkResponse;
import com.duowan.mobile.netroid.Response;
import com.duowan.mobile.netroid.ServerError;
import com.osastudio.common.library.Base64;
import com.osastudio.common.utils.LogUtils;
import com.osastudio.common.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MapParamsStringRequest extends MyStringRequest {

	protected Map<String, Object> params;
	protected Map<String, String> newParams;

	private String requestCookies;
	private String responseCookies;

	private boolean encryptEnabled;
	private boolean authEnabled;
	private TokenResultStringRequest tokenRequest;
	private String accessToken;

	public MapParamsStringRequest(String url,
            Map<String, Object> params, Listener<String> listener) {
		super(Method.POST, url, listener);
		this.params = params;
	}

	public MapParamsStringRequest(int method, String url,
			Map<String, Object> params, Listener<String> listener) {
		super(method, url, listener);
		this.params = params;
	}

	public boolean isEncryptEnabled() {
		return encryptEnabled;
	}

	public void setEncryptEnabled(boolean encryptEnabled) {
		this.encryptEnabled = encryptEnabled;
	}

	public boolean isAuthEnabled() {
		return authEnabled;
	}

	public void setAuthEnabled(boolean authEnabled) {
		this.authEnabled = authEnabled;
	}

//    public String getRequestCookies() {
//		return requestCookies;
//	}
//
//	public void setRequestCookies(String cookies) {
//		addHeader("Cookie", cookies);
//		requestCookies = cookies;
//		try {
//			Map<String, String> headers = getHeaders();
//			if (headers != null && headers.size() > 0) {
//				JSONObject jsonObject = new JSONObject();
//				jsonObject.putAll(headers);
//				Utils.log("TEST", "HEADERS: " + jsonObject.toJSONString());
//			}
//		} catch (AuthFailureError authFailureError) {
//
//		}
//	}
//
//	public String getResponseCookies() {
//		return responseCookies;
//	}


	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String token) {
		addHeader("Authorization", token);
		this.accessToken = token;
	}

	@Override
	public void prepare() {
		LogUtils.log("TEST", "URL: " + getUrl());
	}

	@Override
	public String getUrl() {
		if (isEncryptEnabled()) {
			return getEncryptedUrl();
		}

		return super.getUrl();
	}
	private String getEncryptedUrl() {
		return super.getUrl();
	}

	@Override
	public String getParamsEncoding() {
		return super.getParamsEncoding();
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		if (isEncryptEnabled()) {
			return getEncryptedBody();
		}

		String bodyString = "";
		String paramString = JSON.toJSONString(this.params);
		LogUtils.log("TEST", "PARAMS: " + paramString);
		try {
			StringEntity entity = new StringEntity(paramString, "utf-8");
			entity.setContentType("application/json");
			bodyString = inputStreamToString(entity.getContent());
		} catch (Exception e) {

		}
		return bodyString.getBytes();
	}

	private byte[] getEncryptedBody() throws AuthFailureError {
		String paramString = JSON.toJSONString(this.params);
		LogUtils.log("TEST", "PARAMS: " + paramString);
		String baseString = new String(Base64.encode(paramString.getBytes()));
		if (newParams == null) {
			newParams = new HashMap();
			newParams.put("j", baseString);
		}
		paramString = JSON.toJSONString(newParams);
		String bodyString = "";
		try {
			StringEntity entity = new StringEntity(paramString, "UTF-8");
			entity.setContentType("application/json");
			bodyString = inputStreamToString(entity.getContent());
		} catch (Exception e) {

		}
		LogUtils.log("TEST", "ENCRYPTED PARAMS: " + paramString);
		return bodyString.getBytes();
	}

	@Override
	public String getBodyContentType() {
		if (isEncryptEnabled()) {
			return getEncryptedBodyContentType();
		}

		return "application/json; charset=" + this.getParamsEncoding();
	}

	private String getEncryptedBodyContentType() {
		return "application/json; charset=" + this.getParamsEncoding();
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
//		try {
//			String resultString = new String(response.data, response.charset);
//			Utils.log("TEST", "RESULT: " + resultString);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		return super.parseNetworkResponse(response);
	}

	@Override
	public byte[] handleResponse(HttpResponse response, Delivery delivery)
			throws IOException, ServerError {
//		Header header = response.getFirstHeader("Set-Cookie");
//		if (header != null) {
//			responseCookies = header.getValue();
//		}
//		Header[] headers = response.getAllHeaders();
//		if (headers != null && headers.length > 0) {
//			for (int i = 0; i < headers.length; i++) {
//				Utils.log("TEST",
//						headers[i].getName() + ":" + headers[i].getValue());
//			}
//		}

		if (isEncryptEnabled()) {
			return handleEncryptedResponse(response, delivery);
		}

		byte[] data = super.handleResponse(response, delivery);
		if (data != null && data.length > 0) {
			String jsonString = new String(data);
			LogUtils.log("TEST", "RESULT: " + jsonString);
		}
		return data;
	}

	public byte[] handleEncryptedResponse(HttpResponse response, Delivery delivery)
			throws IOException, ServerError {
		byte[] data = super.handleResponse(response, delivery);
		if (response.getStatusLine().getStatusCode() == 200
				&& data != null && data.length > 0) {
			String baseString = new String(data);
			LogUtils.log("TEST", "ENCRYPTED RESULT: " + baseString);
			String jsonString = new String(Base64.decode(baseString));
			LogUtils.log("TEST", "RESULT: " + jsonString);
			return jsonString.getBytes();
		}
		return data;
	}

	@Override
	protected NetroidError parseNetworkError(NetroidError netroidError) {
		if (isAuthEnabled()) {
			return parseAuthNetworkError(netroidError);
		}

		return super.parseNetworkError(netroidError);
	}

	private NetroidError parseAuthNetworkError(NetroidError netroidError) {
		if (netroidError != null && netroidError.networkResponse != null
				&& netroidError.networkResponse.statusCode == 401) {
			if (tokenRequest == null) {
				tokenRequest = new TokenResultStringRequest(null, null);
				tokenRequest.setForceUpdate(true);
			}
			tokenRequest.setHostRequest(this);
			tokenRequest.run(getContext());
		}
		return super.parseNetworkError(netroidError);
	}

	@Override
	public void run(Context context) {
		if (context != null ) {
			if (this.params == null) {
				this.params = new HashMap();
			}
			this.params.put("clientVersion", Utils.getApplicationStamp(context));
		}
		super.run(context);
	}

	public static String inputStreamToString(InputStream in) throws Exception{
		final int BUFFER_SIZE = 1 * 1024;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] data = new byte[BUFFER_SIZE];
		int count = -1;
		while((count = in.read(data, 0, BUFFER_SIZE)) > 0) {
			out.write(data, 0, count);
		}
		return new String(out.toByteArray(), "UTF-8");
	}

	public static byte[] encodeParams(Map<String, String> params, String paramsEncoding) {
		StringBuilder encodedParams = new StringBuilder();
		try {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				encodedParams.append(entry.getKey());
				encodedParams.append('=');
				encodedParams.append(entry.getValue());
				encodedParams.append('&');
			}
			return encodedParams.toString().getBytes(paramsEncoding);
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
		}
	}

}