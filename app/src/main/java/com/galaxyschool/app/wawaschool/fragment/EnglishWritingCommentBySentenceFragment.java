package com.galaxyschool.app.wawaschool.fragment;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.EnglishWritingUtils;
import com.galaxyschool.app.wawaschool.common.SharedPreferencesHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandDataAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandListViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.MapParamsStringRequest;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.EnglishWritingConfig;
import com.galaxyschool.app.wawaschool.pojo.EnglishWritingWordCnt;
import com.galaxyschool.app.wawaschool.pojo.HomeworkCommitObjectInfo;
import com.galaxyschool.app.wawaschool.pojo.HomeworkCommitObjectResult;
import com.galaxyschool.app.wawaschool.pojo.PigaiResultCommentInfo;
import com.galaxyschool.app.wawaschool.pojo.PigaiResultSentenceInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;
import com.galaxyschool.app.wawaschool.views.ArcProgressbar;
import com.galaxyschool.app.wawaschool.views.EnglishWritingModifyPopupView;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 英文写作---按句点评
 */

public class EnglishWritingCommentBySentenceFragment extends ContactsExpandListFragment implements
        ArcProgressbar.OnCenterDraw{

    public static final String TAG = EnglishWritingCommentBySentenceFragment.class.getSimpleName();

    private View rootView;
    private TextView commonHeaderTitleTextView;
    private TextView scroeRangeTextView;
    private TextView commitedPersonCountTextView;
    private TextView wordsCountTextView, commitedTimesCountTextView;
    private ArcProgressbar scoreProgressBar;
    private ExpandableListView listView;
    private int roleType = -1;
    private String gradeShow;
    private View headerView;
    private String localAccessToken;
    private static boolean hasContentChanged;
    private String studentId,sortStudentId,taskId;
    private StudyTask task;
    private String modifiedWordContent;//修改后的作文内容
    private int modifiedWordCount;//修改后的作文字数
    private CommitTask commitTask;
    private boolean shouldShowModifyButton;//需要显示修改按钮
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 10 :
                    String result = (String) msg.obj;
                    updateResultToServer(result);
                    break;
            }
        }
    };

    private PullToRefreshView pullToRefreshView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_english_wiring_comment_by_sentence, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getIntent();
        initViews();
        refreshData();
    }
    private void getIntent(){
        if (getArguments() != null) {
            roleType = getArguments().getInt("roleType");
            taskId = getArguments().getString(EnglishWritingCompletedFragment.Constant.TASKID);
            studentId = getArguments().getString(EnglishWritingCompletedFragment.Constant.
                    STUDENTID);
            sortStudentId = getArguments().getString(EnglishWritingCompletedFragment.Constant
                    .SORTSTUDENTID);
            shouldShowModifyButton = getArguments().getBoolean(EnglishWritingCompletedFragment.
                    Constant.SHOULD_SHOW_MODIFY_BUTTON);
        }
    }
    private void refreshData() {
        loadViews();
    }

    private void loadViews() {
        loadCommonData();
    }


    void initViews() {

        //标题
        commonHeaderTitleTextView = (TextView) findViewById(R.id.contacts_header_title);
        if (commonHeaderTitleTextView != null) {
            commonHeaderTitleTextView.setText(R.string.comment_by_sentence);
        }

        //显示分数的进度条
        scoreProgressBar= (ArcProgressbar) findViewById(R.id.myProgress);
        if (scoreProgressBar != null) {
            showStudentScore(scoreProgressBar, "0");
        }
        //顶部布局
        headerView = findViewById(R.id.contacts_header_layout);

        //分数排名
        scroeRangeTextView = (TextView) findViewById(R.id.tv_score_range);

        //提交人数
        commitedPersonCountTextView = (TextView) findViewById(R.id.tv_count_person);

        //单词个数
        wordsCountTextView = (TextView) findViewById(R.id.tv_count_word);

        //提交次数
        commitedTimesCountTextView = (TextView) findViewById(R.id.tv_count_commit);

        //下拉刷新
        pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);
        listView = (ExpandableListView) findViewById(R.id.comment_by_sentence_expandable_list_view);
        if (listView != null) {
            listView.setGroupIndicator(null);
            listView.setDividerHeight(0);
            final ExpandDataAdapter dataAdapter = new ExpandDataAdapter(getActivity(), null,
                    R.layout.item_english_writing_comment_by_sentence_group_view,
                    R.layout.item_english_writing_comment_by_sentence_child_view) {

                @Override
                public int getChildrenCount(int groupPosition) {
                    if (hasData()&&groupPosition<getGroupCount()) {
                        PigaiResultSentenceInfo groupObject = (PigaiResultSentenceInfo) getData().get(groupPosition);
                        if (groupObject!=null){
                            return groupObject.getComment().size();
                        }
                    }

                    return 0;
                }

                @Override
                public Object getChild(int groupPosition, int childPosition) {
                    PigaiResultSentenceInfo groupObject= (PigaiResultSentenceInfo) getData().get(groupPosition);
                    PigaiResultCommentInfo childObject=groupObject.getComment().get(childPosition);
                    return childObject;
                }

                @Override
                public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                         View convertView, ViewGroup parent) {

                    View childView=super.getChildView(groupPosition, childPosition, isLastChild,
                            convertView, parent);
                    final PigaiResultCommentInfo data= (PigaiResultCommentInfo) getChild(groupPosition,childPosition);
                    MyExpandableListViewHolder holder= (MyExpandableListViewHolder) childView.getTag();
                    if (holder==null){
                        holder=new MyExpandableListViewHolder();
                    }

                    holder.childPosition=childPosition;
                    holder.groupPosition=groupPosition;
                    holder.data=data;

                    //类别
                    TextView textView = (TextView) childView.findViewById(R.id.tv_type);
                    if (textView != null){
                        textView.setText(getString(R.string.category_bracket,data.getCat()));
                        //错误的信息显示红色字体
                        String type = data.getType();
                        if (!TextUtils.isEmpty(type)){
                            if (type.equals("error")){
                                textView.setTextColor(getResources().getColor(R.color.red));
                            }else {
                                textView.setTextColor(getResources().getColor(R.color.text_normal));
                            }
                        }
                    }

                    //信息
                    textView = (TextView) childView.findViewById(R.id.tv_msg);
                    if (textView != null){
                        String msg = data.getMsg();
                        if (!TextUtils.isEmpty(msg)) {
                            SpannableStringBuilder ss = (SpannableStringBuilder) Html.fromHtml(msg);
                            if (ss != null) {
                                textView.setText(ss.toString());
                            }
                        }
                    }

                    childView.setTag(holder);

                    return childView;
                }

                @Override
                public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                                         ViewGroup parent) {
                    View groupView=super.getGroupView(groupPosition, isExpanded, convertView,
                            parent);

                    final PigaiResultSentenceInfo data= (PigaiResultSentenceInfo) getGroup(groupPosition);
                    if (data==null){
                        return groupView;
                    }

                    MyExpandableListViewHolder holder= (MyExpandableListViewHolder) groupView.getTag();
                    if (holder==null){
                        holder=new MyExpandableListViewHolder();
                        groupView.setTag(holder);
                    }
                    holder.data=data;
                    groupView.setClickable(true);

                    //段落
                    TextView textView = (TextView) groupView.findViewById(R.id.tv_pid);
                    if (textView != null){
                        textView.setText(getString(R.string.pid_num,
                                String.valueOf(data.getPid())));
                    }

                    //句子
                    textView = (TextView) groupView.findViewById(R.id.tv_sid);
                    if (textView != null){
                        textView.setText(String.valueOf(data.getPid())
                                + "."
                                + String.valueOf(groupPosition + 1));
                    }

                    //句子内容
                    textView = (TextView) groupView.findViewById(R.id.tv_content);
                    if (textView != null){
                        textView.setText(data.getText());
                    }

                    //修改
                    textView = (TextView) groupView.findViewById(R.id.tv_modify);
                    if (textView != null){
                        if (shouldShowModifyButton){
                            textView.setVisibility(View.VISIBLE);
                        }else {
                            textView.setVisibility(View.GONE);
                        }

                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //修改句子
                                String text = data.getText();
                                if (!TextUtils.isEmpty(text)){
                                    int pIndex = data.getPid() - 1;//段落
                                    int sIndex = data.getSid();//句子，从0开始。
                                    testPigaiRequest(pIndex,sIndex,text);
                                }
                            }
                        });
                    }
                    return groupView;
                }
            };

            ExpandListViewHelper expandListViewHelper=new ExpandListViewHelper(getActivity(),
                    listView,dataAdapter) {
                @Override
                public void loadData() {
                    loadCommonData();
                }

                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    MyExpandableListViewHolder holder= (MyExpandableListViewHolder) v.getTag();
                    if (holder==null||holder.data==null){
                        return false;
                    }
                    return true;
                }

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    return false;
                }
            };
            expandListViewHelper.setData(null);
            setCurrListViewHelper(listView, expandListViewHelper);
        }
    }

    private class MyExpandableListViewHolder extends ViewHolder {
        int groupPosition;
        int childPosition;
    }

    private void testPigaiRequest(int pIndex, int sIndex, String text) {
        showRequestPopupWindow(pIndex,sIndex,text);
    }

    /**
     * 判断是否需要重新获取token
     * @return
     */
    private boolean isNeedToRetryAccessToken(){

        boolean isNeed = false;

        localAccessToken = SharedPreferencesHelper.getString(getActivity(),
                EnglishWritingUtils.GET_ACCESS_TOKEN_URL);

        if (TextUtils.isEmpty(localAccessToken)) {
            //如果本地没有token就获取
            isNeed = true;
        }else {
            //如果本地有token，但是失效的话，也需要重新获取。
            long nowTime = System.currentTimeMillis();
            long savedTime = Long.parseLong(SharedPreferencesHelper.getString(getActivity(),
                    EnglishWritingUtils.TIME_AT_GET_ACCESS_TOKEN));
            if (savedTime > 0 && nowTime - savedTime >
                    EnglishWritingUtils.DELTA_TIME_ACCESS_TOKEN_EXPIRE){
                //大于失效范围，token失效，需要重新获取。
                isNeed = true;
            }else {
                //未失效的话，用缓存的token直接请求。
                isNeed = false;
            }
        }
        return isNeed;
    }

    /**
     * 请求批改网逻辑
     * @param content
     */
    private void requestPigaiInterface(String content){
        if (isNeedToRetryAccessToken()){
            //需要重新获取token
            getAccessToken(content);
        }else {
            //直接请求
            applyToPigaiRequest(localAccessToken,content);
        }
    }

    @SuppressLint("NewApi")
    private void showRequestPopupWindow(final int pIndex, final int sIndex, String content) {
        EnglishWritingModifyPopupView popupView = new EnglishWritingModifyPopupView(getActivity()
                , new EnglishWritingModifyPopupView.OnSentenceModifiedListener() {
            @Override
            public void onSentenceModified(String content) {
                //句子修改之后提交
                //提交之前需要拼接数据
                splicingData(pIndex,sIndex,content);
            }
        });
        popupView.setContent(content);
        //展示窗口
        popupView.showPopupWindowAtLocation(pullToRefreshView);
    }

    /**
     * 拼接数据
     * @param pIndex 段落index
     * @param sIndex 句子index
     * @param content 修改后的句子内容
     */
    private void splicingData(int pIndex, int sIndex, String content) {

        if (pIndex < 0 || sIndex < 0 || TextUtils.isEmpty(content)){
            return;
        }
        //首先找到所有的段落
        List<PigaiResultSentenceInfo> sentenceInfoList = getCurrListViewHelper().getData();
        if (sentenceInfoList != null && sentenceInfoList.size() > 0){

            int currentPid = -1;
            int lastPid = 1;//pid从1开始
            int currentSid = 0;//当前句子id
            int lastSid = 0;//记录上一次句子id
            StringBuilder sb = new StringBuilder();

            for (PigaiResultSentenceInfo info :sentenceInfoList){
                if (info != null){

                    //先替换修改所在段落里面的某句子文字
                    currentPid = info.getPid();
                    //分析句子
                    currentSid = info.getSid();

                    //找到需要替换的原始句子
                    if (currentPid == pIndex + 1 && currentSid == sIndex){
                        //替换句子
                        info.setText(content);
                    }
                    //处理句子末尾加空格
                    if (currentSid > lastSid){
                        //sid有变化，需要加上空格
                        info.setText(" " + info.getText());
                    }
                    lastSid = currentSid;

                    //处理段尾加换行符
                    String pText = "";//段落里面的字符
                    if (currentPid > lastPid){
                        //pid有变化，需要拼接换行符\n
                        pText = "\n" + info.getText();
                    }else {
                        //pid没变化就直接返回
                        pText = info.getText();
                    }
                    sb.append(pText);
                    lastPid = currentPid;
                }
            }

            //得到最终的结果，进行请求。
            String resultContent = sb.toString();
            if (!TextUtils.isEmpty(resultContent)){
                //修改后的内容
                modifiedWordContent = resultContent;
                //修改后的内容字数
                modifiedWordCount = EnglishWritingUtils.calculateWordsCount(resultContent);
                requestPigaiInterface(resultContent);
            }
        }
    }

    /**
     * 获得授权token
     * @param content
     */
    private void getAccessToken(final String content) {
        Map<String, Object> params = new HashMap<String, Object>();
        //必须，值为client_credentials
        params.put("grant_type","client_credentials");
        //必须,应用的唯一标识,创建应用时分配的App key
        params.put("client_id", EnglishWritingUtils.APP_KEY);
        //必须,创建应用时分配的App secret
        params.put("client_secret",EnglishWritingUtils.APP_SECRET);

        final String url = EnglishWritingUtils.GET_ACCESS_TOKEN_URL;

        MapParamsStringRequest request = new MapParamsStringRequest(Request.Method.POST,url, params,
                new Listener<String>() {
                    @Override
                    public void onSuccess(String response) {

                        if (!TextUtils.isEmpty(response)){
                            try {
                                JSONObject rootObject = new JSONObject(response);
                                if (rootObject != null){
                                    String accessToken = rootObject.optString("access_token");
                                    //带上请求的token
                                    if (!TextUtils.isEmpty(accessToken)){
                                        //记录获得当前token的时间
                                        SharedPreferencesHelper.setString(getActivity(),
                                                EnglishWritingUtils.TIME_AT_GET_ACCESS_TOKEN,
                                                String.valueOf(System.currentTimeMillis()));
                                        //缓存token到本地
                                        SharedPreferencesHelper.setString(getActivity(),
                                                EnglishWritingUtils.GET_ACCESS_TOKEN_URL,accessToken);
                                        applyToPigaiRequest(accessToken,content);
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
     * @param accessToken
     * @param content
     */
    private void applyToPigaiRequest(String accessToken, String content) {

        final Map<String, Object> params = new HashMap<String, Object>();
        //必须，这个token如何获取是通过授权流程得到这个token
        params.put("access_token",accessToken);
        //必须，作文标题
        if (task != null) {
            params.put("title", task.getTaskTitle());
            //        批改网作文号(高级版功能) 打分公式
            params.put("rid", String.valueOf(task.getMarkFormula()));

            //选填,自定义打分配置项(高级版功能)
//        config 格式如下:
//
//        {
//            "word_cnt": { //字数限制
//            "low": 100,
//                    "high": 200
//        }
//        }

            int minWord = task.getWordCountMin();
            int maxWord = task.getWordCountMax();
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
        params.put("comcontext",content);
        //资源访问控制，固定值:all_json
        params.put("scope","all_json");

        //选填(只支持post)，作文标准答案范文(用于跑题度检测)，string
//        params.put("solution","");
        //选填，zh_cn, zh_tw, en
//        params.put("lang","zh");

        final String url = EnglishWritingUtils.COMPOSITION_ANALYSIS_URL;

        new Thread(new MyRunnable() {
            @Override
            public void run() {
                String result = sendPostRequest(url,params,EnglishWritingUtils
                        .UTF_EIGHT_ENCODING);
                if (!TextUtils.isEmpty(result)) {
                    handler.sendMessage(handler.obtainMessage(10,result));
                }
            }
        }).start();
    }

    /**
     * 发送post请求
     * @param urlPath
     * @param map
     * @param encoding
     * @return
     */
    private String sendPostRequest(String urlPath, Map<String,Object> map,String encoding){
        String result = "";
        if (TextUtils.isEmpty(urlPath)){
            result = null;
        }else {
            try {
                //建立连接
                URL url = new URL(urlPath);
                HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
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
                httpConn.setRequestProperty("Charset",encoding);
                //连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
//                httpConn.connect();
                //建立输入流，向指向的URL传入参数
                DataOutputStream dos = new DataOutputStream(httpConn.getOutputStream());
                //拼接发给url的参数
                String params = null;
                StringBuffer stringBuffer = new StringBuffer();
                if (map != null){
                    for (Map.Entry<String,Object> entry : map.entrySet()){
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        //title=5&content=abc&params=abc
                        stringBuffer.append(key).append("=").append(value).append("&");
                    }
                    //删除最后一个“&”
                    stringBuffer.deleteCharAt(stringBuffer.length()-1);
                    params = stringBuffer.toString();
                }
                dos.writeBytes(params);
                dos.flush();
                dos.close();
                //获得响应状态
                int resultCode = httpConn.getResponseCode();
                //成功返回200
                if(HttpURLConnection.HTTP_OK == resultCode) {
                    StringBuffer sb = new StringBuffer();
                    String readLine = new String();
                    BufferedReader responseReader = new BufferedReader(new
                            InputStreamReader(httpConn.getInputStream(),encoding));
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
     * @param result
     */
    private void updateResultToServer(String result) {

        if (!TextUtils.isEmpty(result)){

            try {
                JSONObject rootObject = new JSONObject(result);
                if (rootObject != null){
                    int code = rootObject.optInt("error_code");
                    if (code == 0){
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
                        startToUpdate(correctResult,score);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 开始同步
     * @param correctResult
     * @param score
     */
    private void startToUpdate(String correctResult, String score) {

        Map<String, Object> params = new HashMap<String, Object>();
        //任务表ID，必填
        if (!TextUtils.isEmpty(taskId)) {
            params.put("TaskId",taskId);
        }
        //学生ID，必填
        if (!TextUtils.isEmpty(studentId)) {
            params.put("StudentId", studentId);
        }
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
                            //刷新标志位
                            setHasContentChanged(true);
                            //提交作业到服务器
                            commitEnglishWritingTaskToServer();
                        }
                    }
                };

        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.UPDATE_CORRECT_RESULT_URL,
                params, listener);
    }

    /**
     * commit英文写作的作业To server
     */
    private void commitEnglishWritingTaskToServer() {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("TaskId",String.valueOf(taskId));
        params.put("StudentId",String.valueOf(studentId));
        params.put("WritingContent",modifiedWordContent);
        params.put("WordCount",modifiedWordCount);
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
                        }
                        TipsHelper.showToast(getActivity(), R.string.commit_success);
                        //提交成功后需要刷新数据
                        refreshData();
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.STUDENT_COMMIT_ENGLISH_WRITING_BASE_URL,
                params, listener);
    }

    private static class MyRunnable implements Runnable{

        @Override
        public void run() {
            SystemClock.sleep(1 * 1000);
        }
    }

    @Override
    public void onDestroy() {
        if (handler != null){
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        super.onDestroy();
    }

    /**
     * 拉取数据
     */
    private void loadCommonData() {
        loadCommitDetails();
    }

    /**
     * 拉取提交详情数据
     */
    private void loadCommitDetails() {

        Map<String, Object> params = new HashMap();
        //任务Id，必填
        if (!TextUtils.isEmpty(taskId)) {
            params.put("TaskId", taskId);
        }
        //学生id，
        if (!TextUtils.isEmpty(studentId)) {
            params.put("SortStudentId", studentId);
        }
        //非必填,如果填写就把该学生对应的任务排在前面,支持多个ID排序,多个ID时用逗号分隔传值.
        //这里只拉取一个学生的
        if (!TextUtils.isEmpty(sortStudentId)) {
            params.put("SortStudentId", sortStudentId);
        }
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_COMMITTED_TASK_BY_TASK_ID_URL, params,
                new DefaultPullToRefreshDataListener<HomeworkCommitObjectResult>(
                        HomeworkCommitObjectResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()
                                || getResult().getModel() == null) {
                            return;
                        }

                        updateViews(getResult());
                    }
                });
    }

    private void updateViews(HomeworkCommitObjectResult result) {
        HomeworkCommitObjectInfo info = result.getModel().getData();
        if (info != null){
            task = info.getTaskInfo();
            List<CommitTask> list = info.getListCommitTask();
            if (list == null || list.size() <= 0) {
                return;
            } else {
                updateHeaderView(list);
            }
        }
    }

    /**
     **更新头部内容
     * @param result
     */
    private void updateHeaderView(List<CommitTask> result) {
        if (result == null || result.size() <= 0) {
            return;
        }
        //截取第一个
        commitTask = result.get(0);
        if (commitTask == null){
            return;
        }

        //更新头部内容
        //分数
        if (scoreProgressBar != null) {
            String score = commitTask.getScore();
            if (!TextUtils.isEmpty(score)) {
                showStudentScore(scoreProgressBar,score);
            }
        }

        //单词个数
        if (wordsCountTextView != null) {
            String wordCount = String.valueOf(commitTask.getWordCount());
            if (!TextUtils.isEmpty(wordCount)) {
                wordsCountTextView.setText(getString(R.string.word_num,wordCount));
            }
        }

        //提交次数
        if (commitedTimesCountTextView != null) {
            String commitTimes = String.valueOf(commitTask.getModifyTimes());
            if (!TextUtils.isEmpty(commitTimes)) {
                commitedTimesCountTextView.setText(getString(R.string.commit_times,commitTimes));
            }
        }

        String correctResult = commitTask.getCorrectResult();
        //解析批改网返回的数据
        if (!TextUtils.isEmpty(correctResult)) {
            parseCorrectResultJSON(correctResult);
        }
    }


    /**
     * 解析批改网返回数据
     * @param correctResult
     */
    private void parseCorrectResultJSON(String correctResult) {

        try {
            JSONObject root = new JSONObject(correctResult);
            if (root != null){
                JSONObject data = root.optJSONObject("data");
                if (data != null) {
                    JSONArray array = data.optJSONArray("sentences");
                    if (array != null && array.length() > 0){
                        List<PigaiResultSentenceInfo> sentenceInfoList = com.alibaba.fastjson.
                                JSONArray.parseArray(array.toString(),PigaiResultSentenceInfo.class);
                        if (sentenceInfoList != null && sentenceInfoList.size() > 0){
                            getCurrListViewHelper().setData(sentenceInfoList);
                            //展开
                            expandAllView();
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 展开所有的项
     */
    private void expandAllView() {
        int groupCount = listView.getCount();
        for (int i = 0; i < groupCount; i++) {
            listView.expandGroup(i);
        }
    }

    /**
     * 进度条上显示相应的分数
     */
    private void showStudentScore(ArcProgressbar scoreProgressBar,String score){
        scoreProgressBar.setOnCenterDraw(this);
        gradeShow=score;
        boolean flag=score.contains(".");
        if (flag){
            score=score.substring(0,score.indexOf("."));
            scoreProgressBar.setProgress(Integer.valueOf(score));
        }else {
            scoreProgressBar.setProgress(Integer.valueOf(score));
        }

    }
    /**
     * 在progressBar绘制显示的分数值
     */
    @Override
    public void draw(Canvas canvas, RectF rectF, float x, float y, float storkeWidth, int progress) {
        Paint textPaint = new Paint();
        textPaint.setStrokeWidth(35);
        textPaint.setTextSize(100);
        textPaint.setColor(getResources().getColor(R.color.text_green));
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
        float textX = x;
        float textY = y+10;
        canvas.drawText(gradeShow,textX,textY,textPaint);
        textPaint.setTextSize(40);
        textPaint.setStrokeWidth(5);
        textY=textY+60;
        canvas.drawText(getString(R.string.score),textX,textY,textPaint);
    }

    public static void setHasContentChanged(boolean hasContentChanged) {
        EnglishWritingCommentBySentenceFragment.hasContentChanged = hasContentChanged;
    }

    public static boolean hasContentChanged() {
        return hasContentChanged;
    }
}
