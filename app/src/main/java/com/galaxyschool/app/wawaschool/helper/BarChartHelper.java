package com.galaxyschool.app.wawaschool.helper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.alibaba.fastjson.JSONArray;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.pojo.BarChartDataInfo;
import com.galaxyschool.app.wawaschool.pojo.ExerciseItem;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.MyBarChartView;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.lqwawa.client.pojo.LearnTaskCardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 柱状图实现辅助类
 */
public class BarChartHelper {
    private Context mContext;
    private MyBarChartView barChartView;
    private TextView answerQuestionNumTextV;//答题人数
    private ExerciseItem exerciseItem;
    private List<BarChartDataInfo> barChartDataInfoList;
    private String[] checkList = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
    private int questionType;
    private int XCount;
    private int rightAnswerIndex = 0;
    private List<String> horizonalData = new ArrayList<>();
    private HashMap<String, Integer> commonAnswerMap = new HashMap<>();

    public BarChartHelper(Context context, ExerciseItem exerciseItem, List<BarChartDataInfo> infos) {
        this.mContext = context;
        this.exerciseItem = exerciseItem;
        this.barChartDataInfoList = infos;
        questionType = Integer.valueOf(exerciseItem.getType());
    }

    public void initView(View view) {
        barChartView = (MyBarChartView) view.findViewById(R.id.bar_chart);
        barChartView.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //柱状图的点击事件
//                RectF bounds = onValueSelectedRectF;
//                barChartView.getBarBounds((BarEntry) e, bounds);
//                MPPointF position = barChartView.getPosition(e, YAxis.AxisDependency.LEFT);
//                MPPointF.recycleInstance(position);
            }

            @Override
            public void onNothingSelected() {

            }
        });
        answerQuestionNumTextV = (TextView) view.findViewById(R.id.tv_answer_question_num);
        initXCountData();
        initViewData();
        initBarChartData();
    }

    private void initXCountData() {
        if (questionType == LearnTaskCardType.SINGLE_CHOICE_QUESTION
                || questionType == LearnTaskCardType.LISTEN_SINGLE_SELECTION) {
            //单选题
            XCount = Integer.valueOf(exerciseItem.getItem_count());
            int rightAnswer = Integer.valueOf(exerciseItem.getRight_answer());
            for (int i = 0; i < XCount; i++) {
                String selectData = mContext.getString(R.string.str_single_select_data, checkList[i]);
                if (i == rightAnswer - 1) {
                    rightAnswerIndex = i;
                    selectData = selectData + "\n" + mContext.getString(R.string.str_right_answer);
                }
                horizonalData.add(selectData);
            }
        } else if (questionType == LearnTaskCardType.SUBJECTIVE_PROBLEM) {
            //主观题
            XCount = 2;
            horizonalData.add(mContext.getString(R.string.str_average_up));
            horizonalData.add(mContext.getString(R.string.str_average_down));
        } else if (questionType == LearnTaskCardType.LISTEN_FILL_CONTENT
                || questionType == LearnTaskCardType.FILL_CONTENT) {
            //填空题
            String rightAnswer = exerciseItem.getRight_answer();
            if (!TextUtils.isEmpty(rightAnswer)) {
                JSONArray jsonArray = JSONArray.parseArray(rightAnswer);
                if (jsonArray != null && jsonArray.size() > 0) {
                    XCount = jsonArray.size();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        String info = mContext.getString(R.string.str_space) + (i + 1);
                        horizonalData.add(info);
                    }
                }
            }
        } else if (questionType == LearnTaskCardType.JUDGMENT_PROBLEM
                || questionType == LearnTaskCardType.HEARING_JUDGMENT) {
            //判断题
            XCount = 2;
            String rightData = mContext.getString(R.string.str_single_select_data, mContext
                    .getString(R.string.str_right));
            String wrongData = mContext.getString(R.string.str_single_select_data, mContext
                    .getString(R.string.str_wrong));
            if (TextUtils.equals(exerciseItem.getRight_answer(), "1")) {
                rightAnswerIndex = 0;
                rightData = rightData + "\n" + mContext.getString(R.string.str_right_answer);
                wrongData = wrongData + "\n" + mContext.getString(R.string.str_wrong_answer);
            } else {
                rightAnswerIndex = 1;
                rightData = rightData + "\n" + mContext.getString(R.string.str_wrong_answer);
                wrongData = wrongData + "\n" + mContext.getString(R.string.str_right_answer);
            }
            horizonalData.add(rightData);
            horizonalData.add(wrongData);
        } else {
            //多选、单选改错
            XCount = 2;
            horizonalData.add(mContext.getString(R.string.str_answer_right));
            horizonalData.add(mContext.getString(R.string.str_answer_wrong));
        }
    }

    private void initBarChartData() {
        if (questionType == LearnTaskCardType.FILL_CONTENT
                || questionType == LearnTaskCardType.LISTEN_FILL_CONTENT) {
            //填空题和听力填空题
            initMultBarChart();
        } else {
            initSingleBarChart();
        }
    }

    private void initSingleBarChart() {
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 1; i < XCount + 1; i++) {
            float index = (float) (i - 0.5);
            //添加相应的数据
            float rate = 0;
            if (questionType == LearnTaskCardType.SINGLE_CHOICE_QUESTION
                    || questionType == LearnTaskCardType.LISTEN_SINGLE_SELECTION) {
                //选择题
                if (commonAnswerMap.containsKey(i + "")) {
                    rate = Utils.getNumberDivData((commonAnswerMap.get(i + "") * 100),
                            barChartDataInfoList.size());
                }
            } else if (questionType == LearnTaskCardType.JUDGMENT_PROBLEM
                    || questionType == LearnTaskCardType.HEARING_JUDGMENT){
                //答错的人数(判断题)
                rate = exerciseItem.getErrorRate();
                if (i == rightAnswerIndex + 1){
                    rate = 100 - rate;
                }
            } else {
                //答错的人数
                rate = exerciseItem.getErrorRate();
                if (i == 1) {
                    //平均分以上
                    rate = 100 - rate;
                }
            }
            values.add(new BarEntry(index, rate));
        }
        BarDataSet dataSet;
        if (barChartView.getData() != null &&
                barChartView.getData().getDataSetCount() > 0) {
            dataSet = (BarDataSet) barChartView.getData().getDataSetByIndex(0);
            dataSet.setValues(values);
            barChartView.getData().notifyDataChanged();
            barChartView.notifyDataSetChanged();
        } else {
            dataSet = new BarDataSet(values, "柱形图");
            dataSet.setDrawIcons(false);
            int[] colors = new int[XCount];
            for (int m = 0; m < XCount; m++) {
                if (rightAnswerIndex == m) {
                    //正确答案
                    colors[m] = R.color.text_green;
                } else {
                    //错误答案
                    colors[m] = R.color.red;
                }
            }
            dataSet.setColors(colors, mContext);
            dataSet.setForm(Legend.LegendForm.LINE);
            //设置柱状图的点击的颜色处理
            dataSet.setHighLightAlpha(0);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(dataSet);
            BarData data = new BarData(dataSets);
            data.setValueFormatter(new PercentFormatter());
            //设置柱状图文本的size
            data.setValueTextSize(10f);
            data.setBarWidth(0.3f);
            barChartView.setData(data);
        }
        showBarChart(barChartView, barChartView.getBarData());
        barChartView.invalidate();
    }

    private void initMultBarChart() {
        float groupSpace = 0.3f;
        float barSpace = 0f;
        float barWidth = 0.35f;
        ArrayList<BarEntry> values = new ArrayList<>();
        ArrayList<BarEntry> values1 = new ArrayList<>();
        for (int i = 0; i < XCount; i++) {
            float rate = exerciseItem.getSubErrorRate().get(i);
            //正确的
            values.add(new BarEntry(i, 100 - rate));
            //错误的
            values1.add(new BarEntry(i, rate));
        }
        BarDataSet set1, set2;
        if (barChartView.getData() != null &&
                barChartView.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChartView.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set2 = (BarDataSet) barChartView.getData().getDataSetByIndex(1);
            set2.setValues(values1);
            barChartView.getData().notifyDataChanged();
            barChartView.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, mContext.getString(R.string.str_answer_right));
            set1.setColors(ContextCompat.getColor(mContext, R.color.text_green));
            set1.setDrawIcons(false);
            set2 = new BarDataSet(values1, mContext.getString(R.string.str_answer_wrong));
            set2.setDrawIcons(false);
            set2.setColors(ContextCompat.getColor(mContext, R.color.red));
            //设置柱状图的点击的颜色处理
            set1.setHighLightAlpha(0);
            set2.setHighLightAlpha(0);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            dataSets.add(set2);
            BarData data = new BarData(dataSets);
            data.setValueFormatter(new PercentFormatter());
            //设置柱状图文本的size
            data.setValueTextSize(6f);
            barChartView.setData(data);
        }
        showBarChart(barChartView, barChartView.getBarData());
        barChartView.getBarData().setBarWidth(barWidth);
        barChartView.groupBars(0, groupSpace, barSpace);
        barChartView.invalidate();
    }

    private void initViewData() {
        //答题人数
        answerQuestionNumTextV.setText(mContext.getString(R.string.str_answer_question_personal,
                exerciseItem.getSubimtNum()));
        for (int i = 0; i < barChartDataInfoList.size(); i++) {
            BarChartDataInfo dataInfo = barChartDataInfoList.get(i);
            String studentAnswer = dataInfo.getEQAnswer();
            if (!TextUtils.isEmpty(studentAnswer)) {
                if (questionType == LearnTaskCardType.SINGLE_CHOICE_QUESTION
                        || questionType == LearnTaskCardType.LISTEN_SINGLE_SELECTION) {
                    if (commonAnswerMap.containsKey(studentAnswer)) {
                        int num = commonAnswerMap.get(studentAnswer) + 1;
                        commonAnswerMap.put(studentAnswer, num);
                    } else {
                        commonAnswerMap.put(studentAnswer, 1);
                    }
                }
            }
        }
    }

    public void showBarChart(BarChart barChart, BarData barData) {
        //绘制高亮箭头
//        barChart.setDrawHighlightArrow(false);
//        barChart.set
        //设置值显示在柱状图的上边或者下边
        barChart.setDrawValueAboveBar(true);
        //设置绘制网格背景
        barChart.setDrawGridBackground(true);
        //设置双击缩放功能
        barChart.setDoubleTapToZoomEnabled(false);
        //设置规模Y启用
        barChart.setScaleYEnabled(false);
        //设置规模X启用
        barChart.setScaleXEnabled(false);
        //启用设置阻力
        barChart.setScaleEnabled(true);
        //设置每个拖动启用的高亮显示
        barChart.setHighlightPerDragEnabled(false);
        // 设置硬件加速功能
        barChart.setHardwareAccelerationEnabled(true);
        // 设置绘制标记视图
//        barChart.setDrawMarkerViews(true);
        barChart.setDrawMarkers(true);
        // 设置启用日志
        barChart.setLogEnabled(false);
        // 设置突出功能
//        barChart.setHighlightEnabled(true);
//        barChart.sethighlight
        // 设置拖动减速功能
        barChart.setDragDecelerationEnabled(true);
        // 数据描述
//        barChart.setDescription("");
        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
//        barChart.setNoDataTextDescription("没有数据了");
        barChart.getDescription().setText("没有数据了");
        barChart.getDescription().setEnabled(false);
        barChart.setNoDataText("没有数据");
        // 是否显示表格颜色
//        barChart.setDrawGridBackground(false);
//        barChart.setVisibleXRangeMaximum(10f);

        // 设置是否可以触摸
        barChart.setTouchEnabled(true);
        // 是否可以拖拽
        barChart.setDragEnabled(true);//放大可拖拽
        // 是否可以缩放
        barChart.setScaleEnabled(false);
        // 集双指缩放
        barChart.setPinchZoom(false);
        barChart.getDescription().setEnabled(false);
        // 设置背景
//        barChart.setBackgroundColor(Color.parseColor("#01000000"));
        // 如果打开，背景矩形将出现在已经画好的绘图区域的后边。
        barChart.setDrawGridBackground(false);
        // 集拉杆阴影
        barChart.setDrawBarShadow(false);
        // 图例
        barChart.getLegend().setEnabled(true);
        // 设置数据
        barChart.setData(barData);
        // 隐藏右边的坐标轴 (就是右边的0 - 100 - 200 - 300 ... 和图表中横线)
        barChart.getAxisRight().setEnabled(false);
        // 隐藏左边的左边轴 (同上)
        // 网格背景颜色
//        barChart.setGridBackgroundColor(Color.parseColor("#00000000"));
        // 是否显示表格颜色
        barChart.setDrawGridBackground(false);
        barChart.setBorderColor(Color.parseColor("#ff0000"));
        // 说明颜色
//        barChart.setDescriptionColor(Color.parseColor("#00000000"));
        barChart.getDescription().setTextColor(Color.parseColor("#42afff"));
        barChart.getAxisLeft().setZeroLineColor(Color.parseColor("#00000000"));
        //设置X轴显示柱形图的个数
        barChart.setVisibleXRangeMaximum(4);
        // 拉杆阴影
        barChart.setDrawBarShadow(false);
        // 打开或关闭绘制的图表边框。（环绕图表的线）
        barChart.setDrawBorders(false);
        //过滤柱形图上超过X值之后不显示相应的数据
        barChart.setMaxVisibleValueCount(110);

        //距离底部的距离
        barChart.setExtraBottomOffset(20f);

        //设置x轴的数据
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(XCount);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(XCount);
        xAxis.setTextSize(10f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(horizonalData.toArray(new
                String[horizonalData.size()])));

        //设置y轴的数据
        IAxisValueFormatter custom = new PercentFormatter();
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(100f);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

//        YAxis rightAxis = barChart.getAxisRight();
//        rightAxis.setDrawGridLines(false);
////        rightAxis.setTypeface(tfLight);
//        rightAxis.setLabelCount(8, false);
//        rightAxis.setValueFormatter(custom);
//        rightAxis.setSpaceTop(15f);
//        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        //点击item的弹框
//        XYMarkerView mv = new XYMarkerView(activity, xAxisFormatter);
//        mv.setChartView(barChart); // For bounds control
//        barChart.setMarker(mv); // Set the marker to the chart

        Legend mLegend = barChart.getLegend(); // 设置比例图标示
        // 字体颜色
        mLegend.setTextColor(ContextCompat.getColor(mContext,R.color.text_black));
        mLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        mLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        mLegend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        mLegend.setDrawInside(false);
        //设置图例形状:如SQUARE、CIRCLE、LINE、DEFAULT
        mLegend.setForm(Legend.LegendForm.SQUARE);
        //y轴顶部图标的大小
        mLegend.setFormSize(9f);
        //y轴顶部字体的大小
        mLegend.setTextSize(10f);
        mLegend.setTextColor(Color.BLACK);
        //设置图例标签到文字之间的距离
        mLegend.setFormToTextSpace(10f);
        if (questionType == LearnTaskCardType.LISTEN_FILL_CONTENT
                || questionType == LearnTaskCardType.FILL_CONTENT) {
            //是否开启图例
            mLegend.setEnabled(true);
        } else {
            mLegend.setEnabled(false);
        }
    }

    private final RectF onValueSelectedRectF = new RectF();
}
