package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.EnglishWritingCommentDetailsActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.EnglishWritingUtils;
import com.galaxyschool.app.wawaschool.common.SharedPreferencesHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.MapParamsStringRequest;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.EnglishWritingConfig;
import com.galaxyschool.app.wawaschool.pojo.EnglishWritingWordCnt;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.EnglishWritingEditText;
import com.oosic.apps.iemaker.base.pennote.PenNoteRecognizerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


/**
 * 英文写作---构建文章页面
 */

public class EnglishWritingBuildFragment extends ContactsListFragment {

    public static final String TAG = EnglishWritingBuildFragment.class.getSimpleName();

    public static final int REQUEST_CODE_HAND_WRITE = 0;

    private View rootView;
    private TextView commonHeaderTitleTextView, commonHeaderRightTextView;
    private EnglishWritingEditText contentEditText;
    private TextView wordCountTextView;
    private String studentId, sortStudentId, taskId;
    private int roleType = -1;
    private String taskType;
    private String localAccessToken;
    private int wordCount;//作文字数
    private boolean isFinish;
    private StudyTask studyTask;
    private CommitTask commitTask;
    private static boolean hasCommented;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 10:
                    String result = (String) msg.obj;
                    updateResultToServer(result);
                    break;

                case 20:
                    //同步成功
                    commitArticleContent();
                    break;
            }
        }
    };
    //原始的内容
    private String originContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_english_writing_build, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getIntent();
        initViews();
    }

    private void getIntent() {
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            //标题
            studyTask = (StudyTask) bundle.getSerializable("studyTask");
            studentId = bundle.getString(EnglishWritingCompletedFragment.Constant.STUDENTID);
            sortStudentId = bundle.getString(EnglishWritingCompletedFragment.Constant.SORTSTUDENTID);
            taskId = bundle.getString(EnglishWritingCompletedFragment.Constant.TASKID);
            commitTask = (CommitTask) bundle.getSerializable(EnglishWritingCompletedFragment.Constant.COMMITTASK);
            if (commitTask != null) {
                studentId = bundle.getString(EnglishWritingCompletedFragment.Constant.STUDENTID);
                sortStudentId = bundle.getString(EnglishWritingCompletedFragment.Constant.SORTSTUDENTID);
                taskId = bundle.getString(EnglishWritingCompletedFragment.Constant.TASKID);
            } else {
                roleType = bundle.getInt(EnglishWritingCompletedFragment.Constant.ROLETYPE);
                taskType = bundle.getString(EnglishWritingCompletedFragment.Constant.TASKTYPE);
                studentId = bundle.getString(EnglishWritingCompletedFragment.Constant.STUDENTID);
                sortStudentId = bundle.getString(EnglishWritingCompletedFragment.Constant.SORTSTUDENTID);
                taskId = bundle.getString(EnglishWritingCompletedFragment.Constant.TASKID);
            }
        }
    }

    void initViews() {
        //标题
        commonHeaderTitleTextView = (TextView) findViewById(R.id.contacts_header_title);
        if (commonHeaderTitleTextView != null) {
            commonHeaderTitleTextView.setText(R.string.english_writing);
        }

        //学生提交作文
        commonHeaderRightTextView = (TextView) findViewById(R.id.contacts_header_right_btn);
        if (commonHeaderRightTextView != null) {
            commonHeaderRightTextView.setText(R.string.commit);
            commonHeaderRightTextView.setVisibility(View.VISIBLE);
            commonHeaderRightTextView.setTextColor(getResources().getColor(R.color.text_green));
            commonHeaderRightTextView.setOnClickListener(this);
        }

        //编辑的内容
        contentEditText = (EnglishWritingEditText) findViewById(R.id.et_content);
        wordCountTextView = (TextView) findViewById(R.id.article_word_number);
        if (contentEditText != null) {
            contentEditText.setFocusable(true);
            contentEditText.requestFocus();
            if (wordCountTextView != null) {
                contentEditText.setParams(wordCountTextView);
            }
        }
        if (commitTask != null) {
            //获得原始的内容
            originContent = commitTask.getWritingContent();
            contentEditText.setText(originContent);
        }

        TextView tvHandInput = (TextView) findViewById(R.id.tv_handwrite);
        tvHandInput.setOnClickListener(this);
    }

    /**
     * 获得授权token
     *
     * @param content
     */
    private void getAccessToken(final String content) {
        Map<String, Object> params = new HashMap<String, Object>();
        //必须，值为client_credentials
        params.put("grant_type", "client_credentials");
        //必须,应用的唯一标识,创建应用时分配的App key
        params.put("client_id", EnglishWritingUtils.APP_KEY);
        //必须,创建应用时分配的App secret
        params.put("client_secret", EnglishWritingUtils.APP_SECRET);

        final String url = EnglishWritingUtils.GET_ACCESS_TOKEN_URL;

        MapParamsStringRequest request = new MapParamsStringRequest(Request.Method.POST, url, params,
                new Listener<String>() {
                    @Override
                    public void onSuccess(String response) {

                        if (!TextUtils.isEmpty(response)) {
                            try {
                                JSONObject rootObject = new JSONObject(response);
                                if (rootObject != null) {
                                    String accessToken = rootObject.optString("access_token");
                                    //带上请求的token
                                    if (!TextUtils.isEmpty(accessToken)) {
                                        //记录获得当前token的时间
                                        SharedPreferencesHelper.setString(getActivity(),
                                                EnglishWritingUtils.TIME_AT_GET_ACCESS_TOKEN,
                                                String.valueOf(System.currentTimeMillis()));
                                        //缓存token到本地
                                        SharedPreferencesHelper.setString(getActivity(),
                                                EnglishWritingUtils.GET_ACCESS_TOKEN_URL, accessToken);
                                        applyToPigaiRequest(accessToken, content);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    /**
     * 请求批改网数据
     *
     * @param accessToken
     * @param content
     */
    private void applyToPigaiRequest(String accessToken, String content) {

        final Map<String, Object> params = new HashMap<String, Object>();
        //必须，这个token如何获取是通过授权流程得到这个token
        params.put("access_token", accessToken);
        //必须，作文标题
        if (studyTask != null) {
            //必须，作文标题
            params.put("title", studyTask.getTaskTitle());
//            批改网作文号(高级版功能) 打分公式
            params.put("rid", String.valueOf(studyTask.getMarkFormula()));
            //选填,自定义打分配置项(高级版功能)
//        config 格式如下:
//
//        {
//            "word_cnt": { //字数限制
//            "low": 100,
//                    "high": 200
//        }
            int minWord = studyTask.getWordCountMin();
            int maxWord = studyTask.getWordCountMax();
            if (minWord > 0 && maxWord > 0) {

                EnglishWritingWordCnt cnt = new EnglishWritingWordCnt();
                cnt.setLow(minWord);
                cnt.setHigh(maxWord);

                EnglishWritingConfig config = new EnglishWritingConfig();
                config.setWord_cnt(cnt);

                params.put("config", config);
            }
        }
        //必须，作文内容
        params.put("comcontext", content);
        //资源访问控制，固定值:all_json
        params.put("scope", "all_json");

        //选填(只支持post)，作文标准答案范文(用于跑题度检测)，string
//        params.put("solution","");
        //选填，zh_cn, zh_tw, en
//        params.put("lang","zh");

        final String url = EnglishWritingUtils.COMPOSITION_ANALYSIS_URL;

        new Thread(new MyRunnable() {
            @Override
            public void run() {
                String result = sendPostRequest(url, params, EnglishWritingUtils
                        .UTF_EIGHT_ENCODING);
                if (!TextUtils.isEmpty(result)) {
                    handler.sendMessage(handler.obtainMessage(10, result));
                }
            }
        }).start();
    }

    /**
     * 发送post请求
     *
     * @param urlPath
     * @param map
     * @param encoding
     * @return
     */
    private String sendPostRequest(String urlPath, Map<String, Object> map, String encoding) {
        String result = "";
        if (TextUtils.isEmpty(urlPath)) {
            result = null;
        } else {
            try {
                //建立连接
                URL url = new URL(urlPath);
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                //设置参数
                httpConn.setDoOutput(true);   //需要输出
                httpConn.setDoInput(true);   //需要输入
                httpConn.setUseCaches(false);  //不允许缓存
                httpConn.setRequestMethod("POST");   //设置POST方式连接
                //设置超时时间
                httpConn.setReadTimeout(60 * 1000);
                //设置请求属性
                httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
                httpConn.setRequestProperty("Charset", encoding);
                //连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
//                httpConn.connect();
                //建立输入流，向指向的URL传入参数
                DataOutputStream dos = new DataOutputStream(httpConn.getOutputStream());
                //拼接发给url的参数
                String params = null;
                StringBuffer stringBuffer = new StringBuffer();
                if (map != null) {
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        //title=5&content=abc&params=abc
                        stringBuffer.append(key).append("=").append(value).append("&");
                    }
                    //删除最后一个“&”
                    stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                    params = stringBuffer.toString();
                }
                dos.writeBytes(params);
                dos.flush();
                dos.close();
                //获得响应状态
                int resultCode = httpConn.getResponseCode();
                //成功返回200
                if (HttpURLConnection.HTTP_OK == resultCode) {
                    StringBuffer sb = new StringBuffer();
                    String readLine = new String();
                    BufferedReader responseReader = new BufferedReader(new
                            InputStreamReader(httpConn.getInputStream(), encoding));
                    while ((readLine = responseReader.readLine()) != null) {
                        sb.append(readLine);
                    }
                    responseReader.close();
                    result = sb.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 同步数据到server
     *
     * @param result
     */
    private void updateResultToServer(String result) {

        if (!TextUtils.isEmpty(result)) {

            try {
                JSONObject rootObject = new JSONObject(result);
                if (rootObject != null) {
                    int code = rootObject.optInt("error_code");
                    if (code == 0) {
                        //请求成功
                        String correctResult = "";
                        String score = "";
                        //获得批改网返回的数据
                        correctResult = result;
                        JSONObject dataObject = rootObject.optJSONObject("data");
                        if (dataObject != null) {
                            if (dataObject.has("score")) {
                                score = dataObject.optString("score");
                            }
                        }
                        //同步数据
//                        startToUpdate(correctResult,score);

                        //同步成功后调用提交接口
                        commitEnglishWritingTaskToServer(correctResult, score);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 开始同步
     *
     * @param correctResult
     * @param score
     */
    private void startToUpdate(String correctResult, String score) {

        Map<String, Object> params = new HashMap<String, Object>();
        //任务表ID，必填
        params.put("TaskId", taskId);
        //学生ID，必填
        params.put("StudentId", studentId);
        //批改网返回结果，必填，不能为空
        if (!TextUtils.isEmpty(correctResult)) {
            params.put("CorrectResult", correctResult);
        }
        //作文分数，必填，不能为空
        if (!TextUtils.isEmpty(score)) {
            params.put("Score", score);
        }
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        } else {
//                            //同步成功后调用提交接口
//                            commitEnglishWritingTaskToServer(wordCount);
                            TipsHelper.showToast(getActivity(), R.string.commit_success);
                            handler.sendMessage(handler.obtainMessage(20));
                        }
                    }
                };

        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.UPDATE_CORRECT_RESULT_URL,
                params, listener);
    }

    @Override
    public void onDestroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        super.onDestroy();
    }

    /**
     * 监听返回键
     *
     * @param keyCode
     * @param event
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            //弹出返回提示框
            doExit();
            return true;
        }
        return false;
    }

    private static class MyRunnable implements Runnable {

        @Override
        public void run() {
            SystemClock.sleep(1 * 1000);
        }
    }

    /**
     * 请求批改网逻辑
     *
     * @param wordContent
     */
    private void requestPigaiInterface(String wordContent) {
        if (isNeedToRetryAccessToken()) {
            //需要重新获取token
            getAccessToken(wordContent);
        } else {
            //直接请求
            applyToPigaiRequest(localAccessToken, wordContent);
        }
    }

    /**
     * 判断是否需要重新获取token
     *
     * @return
     */
    private boolean isNeedToRetryAccessToken() {

        boolean isNeed = false;

        localAccessToken = SharedPreferencesHelper.getString(getActivity(),
                EnglishWritingUtils.GET_ACCESS_TOKEN_URL);

        if (TextUtils.isEmpty(localAccessToken)) {
            //如果本地没有token就获取
            isNeed = true;
        } else {
            //如果本地有token，但是失效的话，也需要重新获取。
            long nowTime = System.currentTimeMillis();
            long savedTime = Long.parseLong(SharedPreferencesHelper.getString(getActivity(),
                    EnglishWritingUtils.TIME_AT_GET_ACCESS_TOKEN));
            if (savedTime > 0 && nowTime - savedTime >
                    EnglishWritingUtils.DELTA_TIME_ACCESS_TOKEN_EXPIRE) {
                //大于失效范围，token失效，需要重新获取。
                isNeed = true;
            } else {
                //未失效的话，用缓存的token直接请求。
                isNeed = false;
            }
        }
        return isNeed;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_right_btn) {
            //提交作业
            commit();
        } else if (v.getId() == R.id.contacts_header_left_btn) {
            doExit();
        } else if (v.getId() == R.id.tv_handwrite) {
            Intent intent = new Intent(getActivity(), PenNoteRecognizerActivity.class);
            getActivity().startActivityForResult(intent, REQUEST_CODE_HAND_WRITE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data != null) {
                String inputResult = data.getStringExtra(PenNoteRecognizerActivity.EXTRA_NOTE_RESULT);
                //Log.v("ashin", "result = " + inputResult.length());
                insertText(processData(inputResult));
            }
        }
    }

    /**
     * 处理返回结果
     */
    private String processData(String input) {
        if (!TextUtils.isEmpty(input)){
            String substring = input.substring(0, input.length() - 2);
            StringBuilder sb = new StringBuilder(substring);
            if (!substring.endsWith("\n")){
                sb.append(" ");
            }
            return sb.toString();
        }
        return input;
    }

    private void insertText(String input) {
        String currentText = contentEditText.getText().toString().trim();
       /* if (TextUtils.isEmpty(currentText)){
            contentEditText.setText(input);
        }else {
            int index = contentEditText.getSelectionStart();
            contentEditText.getEditableText().insert(index, input);
        }*/
        int index = contentEditText.getSelectionStart();
        if (index < 0 || index >= currentText.length()) {
            contentEditText.getEditableText().append(input);
        } else {
            contentEditText.getEditableText().insert(index, input);
        }
    }

    /**
     * 判断是否编辑了或者内容是否为空
     */
    private void doExit() {
        //提交的内容
        String content = contentEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(content)) {
            //比较一下原始的数据和现在的数据是否一样
            if (!TextUtils.isEmpty(originContent) && originContent.equals(content)) {
                //没编辑直接返回。
                exit();
            } else {
                showExitDialog();
            }
        } else {
            exit();
        }
    }

    /**
     * 提交作业
     */
    private void commit() {
        //学生提交已经编写的作文内容
        String wordContent = wordCountTextView.getText().toString().trim();
        int count = Integer.valueOf(wordContent);
        if (TextUtils.isEmpty(wordContent) || count <= 0) {
            TipsHelper.showToast(getActivity(), R.string.pls_input_article_content);
            return;
        }
        this.wordCount = count;
        //提交的内容
        String content = contentEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(content)) {
            //比较一下原始的数据和现在的数据是否一样
            if (!TextUtils.isEmpty(originContent) && originContent.equals(content)) {
                //没编辑直接返回。
                exit();
                return;
            }
            if (!isFinish) {
                isFinish = true;
                requestPigaiInterface(content);
            }
        }
    }

    /**
     * 退出
     */
    private void exit() {
        Intent intent = new Intent();
        intent.putExtra("isNeedRefresh", false);
        getActivity().setResult(RESULT_OK, intent);
        getActivity().finish();
    }

    /**
     * 弹出退出对话框
     */
    private void showExitDialog() {
        String tips = getActivity().getString(R.string.be_sure_to_commit);
        String leftButtonText = getActivity().getString(R.string.cancel);
        String rightButtonText = getActivity().getString(R.string.confirm);
        final ContactsMessageDialog dialog = new ContactsMessageDialog(getActivity(), null, tips,
                leftButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //放弃
                dialog.dismiss();
                exit();
            }
        }, rightButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //提交
                dialog.dismiss();
                commit();
            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
    }

    /**
     * commit英文写作的作业To server
     */
    private void commitEnglishWritingTaskToServer(final String correctResult, final String score) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("TaskId", String.valueOf(taskId));
        params.put("StudentId", String.valueOf(studentId));
        params.put("WritingContent", contentEditText.getText().toString().trim());
        params.put("WordCount", wordCount);
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            isFinish = false;
                            return;
                        }
                        //同步数据
                        startToUpdate(correctResult, score);
//                        TipsHelper.showToast(getActivity(), R.string.commit_success);
//                        handler.sendMessage(handler.obtainMessage(20));
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.STUDENT_COMMIT_ENGLISH_WRITING_BASE_URL,
                params, listener);
    }

    /**
     * 提交已经编辑好的作文内容
     */
    private void commitArticleContent() {

        Intent intent = new Intent(getActivity(), EnglishWritingCommentDetailsActivity.class);
        intent.putExtra("roleType", roleType);
        intent.putExtra(EnglishWritingCompletedFragment.Constant.TASKID, taskId);
        if (roleType == RoleType.ROLE_TYPE_PARENT) {
            //填写孩子的id
            intent.putExtra(EnglishWritingCompletedFragment.Constant.STUDENTID, studentId);
            intent.putExtra(EnglishWritingCompletedFragment.Constant.SORTSTUDENTID, sortStudentId);
        } else if (roleType == RoleType.ROLE_TYPE_STUDENT) {
            intent.putExtra(EnglishWritingCompletedFragment.Constant.STUDENTID, getMemeberId());
            intent.putExtra(EnglishWritingCompletedFragment.Constant.SORTSTUDENTID, getMemeberId());
        }
        if (!TextUtils.isEmpty(studentId)) {
            boolean isTaskBelongsToChildrenOrOwner = isTaskBelongsToChildrenOrOwner(studentId);
            intent.putExtra(EnglishWritingCompletedFragment.Constant.
                    IS_TASK_BELONGS_TO_CHILDREN_OR_OWNER, isTaskBelongsToChildrenOrOwner);
        }
        //通知学习任务需要刷新数据
        setHasCommented(true);
        intent.putExtras(getArguments());
        getActivity().startActivity(intent);
        isFinish = false;//重置标志位
        getActivity().finish();
    }

    public static void setHasCommented(boolean hasCommented) {
        EnglishWritingBuildFragment.hasCommented = hasCommented;
    }

    public static boolean hasCommented() {
        return hasCommented;
    }

    /**
     * 判断作业是否属于自己的或者是孩子的
     *
     * @param studentId 学生id
     * @return
     */
    private boolean isTaskBelongsToChildrenOrOwner(String studentId) {

        boolean isTaskBelongsToChildrenOrOwner = false;
        //家长需要判断孩子是不是自己的
        if (roleType == RoleType.ROLE_TYPE_STUDENT) {
            if (!TextUtils.isEmpty(studentId)
                    && !TextUtils.isEmpty(getMemeberId())
                    && studentId.equals(getMemeberId())) {
                //是自己的可以编辑
                isTaskBelongsToChildrenOrOwner = true;
            }
        } else if (roleType == RoleType.ROLE_TYPE_PARENT) {
            //家长帮孩子提交作业
            isTaskBelongsToChildrenOrOwner = true;
        }
        return isTaskBelongsToChildrenOrOwner;
    }
}
