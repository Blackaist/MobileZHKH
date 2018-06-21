package ru.smarthzkh.blackstork.fragments;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;

import ru.smarthzkh.blackstork.R;
import ru.smarthzkh.blackstork.other.MyMarkerView;
import ru.smarthzkh.blackstork.other.SaveLoadFile;

public class FragmentDetailLineChart extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener {

    String mode;
    float min, max;
    private LineChart mChart;

    public FragmentDetailLineChart() { }


    // TODO: Rename and change types and number of parameters
    public static FragmentDetailLineChart newInstance(String param1, String param2) {
        return new FragmentDetailLineChart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Полный график");
        View fView = inflater.inflate(R.layout.fragment_detail_line_chart, container, false);

        mChart = fView.findViewById(R.id.chartline);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        mChart.getDescription().setEnabled(false); // no description text
        mChart.setTouchEnabled(true); // enable touch gestures

        mChart.setDragEnabled(true); // enable scaling and dragging
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);

        MyMarkerView mv = new MyMarkerView(getContext(), R.layout.custom_marker_view);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);

        setData(fView);

        LimitLine ll1 = new LimitLine(max, "Максимум");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(min, "Минимум");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(max);
        leftAxis.setAxisMinimum(min);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);


        mChart.animateX(2500);
        mChart.getLegend().setForm(Legend.LegendForm.LINE);

        return fView;
    }

    private void setData(View fView) {
        mode = (String)getActivity().getIntent().getSerializableExtra("Number");
        String volumeNumber = "0";
        switch (mode) {
            case "0":
                volumeNumber = "0";
                break;
            case "1":
                volumeNumber = "1";
                break;
            case "2":
                volumeNumber = "2";
                break;
        }
        mode = mode.equals("3") ? "2" : "1";
        ArrayList<Entry> values = new ArrayList<Entry>();

        SaveLoadFile sl = new SaveLoadFile(Objects.requireNonNull(getContext()));
        JSONArray array = sl.Read();
        min = 10000;
        int startMonth = 0;

        float[] startValues = new float[0];
        if(array != null) {
            try {
                int buf = 0, buf2 = 0;
                for (int i = array.length() - 1; i > 0; i--) {
                    if (array.getJSONObject(i).get("mode").equals(mode))
                        buf++;
                }
                if (buf == 0) return;
                startValues = new float[buf];
                for (int i = array.length() - 1; i > 0; i--) {
                    if (array.getJSONObject(i).get("mode").equals(mode)) {
                        startValues[buf2] = Float.valueOf(array.getJSONObject(i).get("volume" + volumeNumber).toString());
                        if ((int) startValues[buf2] > max)
                            max = (int) startValues[buf2];
                        if ((int) startValues[buf2] < min)
                            min = (int) startValues[buf2];
                        if (buf2 == 0)
                            startMonth = Integer.parseInt(array.getJSONObject(i).get("paymperiod").toString().substring(0, 2)) - 1;
                        buf2++;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < startValues.length; i++) {
                values.add(new Entry(startMonth + i, startValues[i]));
            }

            LineDataSet set1;
            if (mChart.getData() != null &&
                    mChart.getData().getDataSetCount() > 0) {
                set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
                set1.setValues(values);
                mChart.getData().notifyDataChanged();
                mChart.notifyDataSetChanged();
            } else {
                // create a dataset and give it a type
                set1 = new LineDataSet(values, "Данные за все время");

                set1.setDrawIcons(false);

                // set the line to be drawn like this "- - - - - -"
                set1.enableDashedLine(10f, 5f, 0f);
                set1.enableDashedHighlightLine(10f, 5f, 0f);
                set1.setColor(Color.BLACK);
                set1.setCircleColor(Color.BLACK);
                set1.setLineWidth(1f);
                set1.setCircleRadius(3f);
                set1.setDrawCircleHole(false);
                set1.setValueTextSize(9f);
                set1.setDrawFilled(true);
                set1.setFormLineWidth(1f);
                set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                set1.setFormSize(15.f);

                if (Utils.getSDKInt() >= 18) {
                    // fill drawable only supported on api level 18 and above
                    Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.side_nav_bar);
                    set1.setFillDrawable(drawable);
                } else {
                    set1.setFillColor(Color.BLACK);
                }

                ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                dataSets.add(set1); // add the datasets

                // create a data object with the datasets
                LineData data = new LineData(dataSets);

                // set data
                mChart.setData(data);
            }
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            mChart.highlightValues(null);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }
}
