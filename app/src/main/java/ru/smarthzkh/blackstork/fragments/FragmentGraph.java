package ru.smarthzkh.blackstork.fragments;

import android.animation.PropertyValuesHolder;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Display;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.db.chart.animation.Animation;
import com.db.chart.model.ChartSet;
import com.db.chart.model.LineSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.tooltip.Tooltip;
import com.db.chart.util.Tools;
import com.db.chart.view.LineChartView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import ru.smarthzkh.blackstork.R;
import ru.smarthzkh.blackstork.other.HoltWinters;
import ru.smarthzkh.blackstork.other.SaveLoadFile;

public class FragmentGraph extends Fragment {

    private OnFragmentInteractionListener mListener;
    private PieChart mChart;
    protected String[] mParties = new String[] {
            "ЖКХ", "Услуги", "Газ"
    };

    public FragmentGraph() {

    }

    final String[] mMonth = {"Янв", "Фев", "Мар", "Апр", "Май", "Июн", "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"};
    private JSONArray array;


    // TODO: Rename and change types and number of parameters
    public static FragmentGraph newInstance(String param1, String param2) {
        FragmentGraph fragment = new FragmentGraph();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Статистика");
        View fView = inflater.inflate(R.layout.fragment_graph, container, false);

        SaveLoadFile sl = new SaveLoadFile(Objects.requireNonNull(getContext()));
        array = sl.Read();

        initLineChart(fView, R.id.chart_line_water, R.id.textview_water, "1", "0");
        initLineChart(fView, R.id.chart_line_hot_water, R.id.textview_hotwater,"1", "1");
        initLineChart(fView, R.id.chart_line_energo, R.id.textview_energo,"1", "2");
        initLineChart(fView, R.id.chart_line_gas, R.id.textview_gas,"2", "0");


        Button btn[] = new Button[4];
        btn[0] = fView.findViewById(R.id.button_water);
        btn[1] = fView.findViewById(R.id.button_hotwater);
        btn[2] = fView.findViewById(R.id.button_energo);
        btn[3] = fView.findViewById(R.id.button_gas);


        btn[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle extras = new Bundle();
                extras.putSerializable("Number", "0");
                getActivity().getIntent().putExtras(extras);
                android.support.v4.app.FragmentTransaction fragTrans = getActivity().getSupportFragmentManager().beginTransaction();
                fragTrans.replace(R.id.container, new FragmentDetailLineChart());
                fragTrans.commitAllowingStateLoss();
            }
        });
        btn[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle extras = new Bundle();
                extras.putSerializable("Number", "1");
                getActivity().getIntent().putExtras(extras);
                android.support.v4.app.FragmentTransaction fragTrans = getActivity().getSupportFragmentManager().beginTransaction();
                fragTrans.replace(R.id.container, new FragmentDetailLineChart());
                fragTrans.commitAllowingStateLoss();
            }
        });
        btn[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle extras = new Bundle();
                extras.putSerializable("Number", "2");
                getActivity().getIntent().putExtras(extras);
                android.support.v4.app.FragmentTransaction fragTrans = getActivity().getSupportFragmentManager().beginTransaction();
                fragTrans.replace(R.id.container, new FragmentDetailLineChart());
                fragTrans.commitAllowingStateLoss();
            }
        });

        btn[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle extras = new Bundle();
                extras.putSerializable("Number", "3");
                getActivity().getIntent().putExtras(extras);
                android.support.v4.app.FragmentTransaction fragTrans = getActivity().getSupportFragmentManager().beginTransaction();
                fragTrans.replace(R.id.container, new FragmentDetailLineChart());
                fragTrans.commitAllowingStateLoss();
            }
        });

        return fView;
    }

    private void initLineChart(View fView, int Id, int TextId, String mode, String volumeNumber) {

        int startMonth = 0, max = 5;
        float[] startValues = new float[0];
        if(array != null) {
            try {
                int buf = 0;
                for (int i = array.length() - 1; i > 0; i--) {
                    if (array.getJSONObject(i).get("mode").equals(mode))
                        buf++;
                }
                if (buf == 0)
                    return;
                startValues = new float[buf];
                int buf2 = 0;
                for (int i = array.length() - 1; i > 0; i--) {
                    if (array.getJSONObject(i).get("mode").equals(mode)) {
                        startValues[buf2] = Float.valueOf(array.getJSONObject(i).get("volume" + volumeNumber).toString());
                        if ((int) startValues[buf2] > max)
                            max = (int) startValues[buf2];
                        buf2++;
                        if (buf2 == buf)
                            startMonth = Integer.parseInt(array.getJSONObject(i).get("paymperiod").toString().substring(0, 2)) - 1;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final Tooltip mTip = new Tooltip(getContext(), R.layout.linechart_three_tooltip, R.id.value33);
            mTip.setVerticalAlignment(Tooltip.Alignment.BOTTOM_TOP);
            mTip.setDimensions((int) Tools.fromDpToPx(58), (int) Tools.fromDpToPx(25));

            mTip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)).setDuration(200);

            mTip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 0f)).setDuration(200);

            mTip.setPivotX(Tools.fromDpToPx(65) / 2);
            mTip.setPivotY(Tools.fromDpToPx(25));

            int slen = 12, n_preds = 12;
            if (startValues.length < 14) {
                slen = 1;
                n_preds = 3;
                float[] buf = new float[14];
                buf[0] = 0;
                for (int i = 0; i < 14; i++) {
                    if (i < startValues.length)
                        buf[i] = startValues[i];
                    else
                        buf[i] = buf[i - 1];
                }
                startValues = buf;
            }

            HoltWinters model = new HoltWinters(startValues, slen, 0.9f, 0.05f, 0.9f, n_preds);
            model.triple_exponential_smoothing();
            ArrayList<Float> data = model.getResult();

            int size = startValues.length - 2;
            float[] mValues = new float[14];
            for (int i = 2; i < data.size() - startValues.length + 2; i++)
                mValues[i] = round(data.get(size + i), 1);
            mValues[0] = startValues[startValues.length - 2];
            mValues[1] = startValues[startValues.length - 1];

            final String[] mLabels = new String[14];
            for (int i = 0; i < 14; i++)
                mLabels[i] = mMonth[(startMonth + 11 + i) % 12]; // - 1 + 12. Отрицательное число не может быть

            LineSet dataset = new LineSet(mLabels, mValues);
            dataset.setColor(getResources().getColor(R.color.colorGraphPointLineAfter))
                    .setFill(getResources().getColor(R.color.colorGraphUnderline))
                    .setDotsColor(getResources().getColor(R.color.colorGraphPointAfter))
                    .setThickness(4)
                    .setSmooth(true)
                    .setDashed(new float[]{10f, 10f})
                    .beginAt(1);


            LineChartView lineChartView = fView.findViewById(Id);
            lineChartView.addData(dataset);
            dataset = new LineSet(mLabels, mValues);
            dataset.setColor(getResources().getColor(R.color.colorGraphPointLine))
                    .setFill(getResources().getColor(R.color.colorGraphUnderline))
                    .setDotsColor(getResources().getColor(R.color.colorGraphPoint))
                    .setSmooth(true)
                    .setThickness(4)
                    .endAt(2);
            lineChartView.addData(dataset);


            lineChartView.dismissAllTooltips();
            lineChartView.updateValues(0, mValues);
            lineChartView.updateValues(1, mValues);
            lineChartView.notifyDataUpdate();

            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            lineChartView.setGrid(5, 5, paint);

            lineChartView.setYLabels(AxisRenderer.LabelPosition.OUTSIDE)
                    .setAxisBorderValues(0, max * 2, max * 2 / 5)
                    .setTooltips(mTip)
                    .show(new Animation().setInterpolator(new BounceInterpolator())
                            .fromAlpha(0));

            TextView result = fView.findViewById(TextId);
            result.append("Текущее значение: " + startValues[startValues.length - 1] + "\n");
            result.append("Прогноз на следующий месяц: " + mValues[2] + "\n");
            result.append("Прирост потребления: ");
            float dif = round(mValues[2] - startValues[startValues.length - 1], 1);
            Spannable word = new SpannableString(String.valueOf(dif) + "\n");
            if (dif >= 0)
                word.setSpan(new ForegroundColorSpan(Color.RED), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            else
                word.setSpan(new ForegroundColorSpan(Color.GREEN), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            result.append(word);
        }
    }


    private void initPiechart(PieChart view) {
        mChart = view;
        mChart.setBackgroundColor(Color.WHITE);

        moveOffScreen();

        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);



        mChart.setMaxAngle(180f); // HALF CHART
        mChart.setRotationAngle(180f);
        mChart.setCenterTextOffset(0, -20);

        try {
            setData( 100);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        mChart.getLegend().setEnabled(false);

        // entry label styling
        mChart.setEntryLabelColor(Color.WHITE);
        mChart.setEntryLabelTextSize(12f);
    }

    private void setData(float range) throws JSONException {

        ArrayList<PieEntry> values = new ArrayList<PieEntry>();
        int zkh = 0, service = 0 , gas = 0;
        for(int j = 0; j < array.length(); j++) {
            switch (array.getJSONObject(j).get("mode").toString()) {
                case "0": zkh++; break;
                case "1": service++; break;
                case "2": gas++; break;
            }
        }

        values.add(new PieEntry(zkh, mParties[0]));
        values.add(new PieEntry(service, mParties[1]));
        values.add(new PieEntry(gas, mParties[2]));

        PieDataSet dataSet = new PieDataSet(values, "Election Results");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

        mChart.invalidate();
    }

    private void moveOffScreen() {

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int height = display.getHeight();  // deprecated

        int offset = (int)(height * 0.65); /* percent to move */

        RelativeLayout.LayoutParams rlParams =
                (RelativeLayout.LayoutParams)mChart.getLayoutParams();
        rlParams.setMargins(0, 0, 0, 0);
        mChart.setLayoutParams(rlParams);
    }


    private float round(float value, int precision) {
        float scale = (float) Math.pow(10, precision);
        return  Math.round(value * scale) / scale;
    }

    @Override
    public void onDetach() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Мобильный ЖКХ");
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
