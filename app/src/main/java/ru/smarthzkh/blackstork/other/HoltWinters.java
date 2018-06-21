package ru.smarthzkh.blackstork.other;

import android.util.SparseArray;
import java.util.ArrayList;

public class HoltWinters {

    float[] series;
    ArrayList<Float> result;
    int slen, n_preds;
    float alpha, beta, gamma;


    public HoltWinters(float[] series, int slen, float alpha, float beta, float gamma, int n_preds){
        this.series = series;
        this.slen = slen;
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
        this.n_preds = n_preds;
    }


    public void triple_exponential_smoothing() {
        result = new ArrayList<>();
        ArrayList<Integer> Smooth = new ArrayList<>();
        ArrayList<Float> Season = new ArrayList<>();
        ArrayList<Float> Trend = new ArrayList<>();

        SparseArray<Float> seasonals = initial_seasonal_components();

        double smooth = 0;
        double trend = 0;
        for(int i = 0; i < series.length + n_preds; i++) {
            if (i == 0) {
                smooth = series[0];
                trend = initial_trend();
                result.add(series[0]);
                Smooth.add((int)smooth);
                Trend.add((float)trend);
                Season.add(seasonals.get(i % slen));
                continue;
            }

            if(i >= series.length) { //прогноз
                int m = i - series.length + 1;
                result.add((float)(smooth + m * trend) + seasonals.get(i % slen));
            }
            else {
                float val = series[i];
                double last_smooth = smooth;
                smooth = alpha * (val - seasonals.get(i % slen)) + (1 - alpha) * (smooth + trend);
                trend = beta * (smooth - last_smooth) + (1 - beta) * trend;
                seasonals.setValueAt(i % slen, (float) (gamma * (val - smooth) + (1 - gamma) * seasonals.get(i % slen)));
                result.add((float)(smooth + trend + seasonals.get(i % slen)));
            }
        }
    }


    public final ArrayList<Float> getResult() {
        return result;
    }


    private float initial_trend() {
        float sum = 0.0f;
        for(int i = 0; i < slen; i++) {
            sum += (series[i+slen] - series[i]) / slen;
        }
        return sum / slen;
    }


    private SparseArray<Float> initial_seasonal_components() {
        SparseArray<Float> seasonals = new SparseArray<>();
        ArrayList<Float> season_averages = new ArrayList<>();
        int n_seasons = series.length / slen;
        // вычисляем сезонные средние
        for (int j = 0; j < n_seasons; j++) {
            double sum = 0.0;
            int val = slen * j + slen < series.length ? slen * j + slen : series.length;
            if (slen*j < series.length)
                for (int k = slen * j; k < val; k++)
                    sum += series[k];
            season_averages.add((float)sum / slen);
        }
        // вычисляем начальные значения
        for(int i = 0; i < slen; i++) {
            double sum_of_vals_over_avg = 0.0;
            for(int j = 0; j < n_seasons; j++)
                sum_of_vals_over_avg += series[slen * j + i] - season_averages.get(j);
            seasonals.put(i, (float) sum_of_vals_over_avg / n_seasons);
        }
        return seasonals;
    }



}
