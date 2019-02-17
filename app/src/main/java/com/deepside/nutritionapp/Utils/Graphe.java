package com.deepside.nutritionapp.Utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import com.deepside.nutritionapp.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by Samy on 16/02/2018.
 */

public class Graphe {
    public static final int GRAPHE_TYPE_CALORIES = 1, GRAPH_TYPE_MACRO = 0, GRAPH_TYPE_POIDS = 2;
    private GraphView graphView;
    private ArrayList<BaseSeries<DataPoint>> series;
    private ArrayList<ArrayList<Point>> values;


    public Graphe(Context context, GraphView graphView, Class graphViewType, ArrayList<ArrayList<Point>> values) {
        Context context1 = context;
        this.graphView = graphView;
        this.values = values;
        Class graphViewType1 = graphViewType;
        series = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            Collections.sort(values.get(i));
            if (graphViewType == LineGraphSeries.class) {
                series.add(new LineGraphSeries<DataPoint>());
            } else if (graphViewType == PointsGraphSeries.class) {
                series.add(new PointsGraphSeries<DataPoint>());
            } else {
                series.add(new BarGraphSeries<DataPoint>());
            }
        }
        series.get(0).setColor(ContextCompat.getColor(context, R.color.colorCalories));
        series.get(1).setColor(ContextCompat.getColor(context, R.color.colorProteines));
        series.get(2).setColor(ContextCompat.getColor(context, R.color.colorLipides));
        series.get(3).setColor(ContextCompat.getColor(context, R.color.colorGlucides));
        series.get(4).setColor(ContextCompat.getColor(context, R.color.colorPoids));
        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(context, DateUtils.formatterDay));
        graphView.getGridLabelRenderer().setTextSize(28);
        graphView.getGridLabelRenderer().setHumanRounding(true);
        graphView.getGridLabelRenderer().setNumHorizontalLabels(3);
        graphView.getViewport().setXAxisBoundsManual(true);
    }

    public void drawGraph(int graphType) {
        if (graphType == Graphe.GRAPHE_TYPE_CALORIES) {
            DataPoint[] data = new DataPoint[values.get(0).size()];
            for (int i = 0; i < values.get(0).size(); i++) {
                data[i] = new DataPoint(values.get(0).get(i).getDate(), values.get(0).get(i).getValue());
            }
            series.get(0).resetData(data);
            graphView.removeAllSeries();
            graphView.getViewport().setMinX(series.get(0).getLowestValueX());
            graphView.getViewport().setMaxX(series.get(0).getHighestValueX());
            graphView.addSeries(series.get(0));

        } else if (graphType == Graphe.GRAPH_TYPE_MACRO) {
            ArrayList<DataPoint[]> points = new ArrayList<>();
            for (int i = 1; i < values.size(); i++) {
                DataPoint[] data = new DataPoint[values.get(i).size()];
                for (int j = 0; j < values.get(i).size(); j++) {
                    data[j] = new DataPoint(values.get(i).get(j).getDate(), values.get(i).get(j).getValue());
                }
                points.add(data);
                series.get(i).resetData(data);
            }
            graphView.removeAllSeries();
            graphView.getViewport().setMinX(series.get(1).getLowestValueX());
            graphView.getViewport().setMaxX(series.get(1).getHighestValueX());
            graphView.addSeries(series.get(1));
            graphView.addSeries(series.get(2));
            graphView.addSeries(series.get(3));

        } else {
            DataPoint[] data = new DataPoint[values.get(4).size()];
            for (int i = 0; i < values.get(4).size(); i++) {
                data[i] = new DataPoint(values.get(4).get(i).getDate(), values.get(4).get(i).getValue());
            }
            series.get(4).resetData(data);
            graphView.removeAllSeries();
            graphView.getViewport().setMinX(series.get(4).getLowestValueX());
            graphView.getViewport().setMaxX(series.get(4).getHighestValueX());
            graphView.addSeries(series.get(4));
        }
    }

    public void drawGraph(int graphType, Date dateFrom, Date dateTo) {
        if (graphType == Graphe.GRAPHE_TYPE_CALORIES) {
            ArrayList<DataPoint> data = new ArrayList<>();
            for (int i = 0; i < values.get(0).size(); i++) {
                Date d = values.get(0).get(i).getDate();
                if (dateFrom.compareTo(d) * d.compareTo(dateTo) >= 0) {
                    data.add(new DataPoint(d, values.get(0).get(i).getValue()));
                }
            }
            DataPoint[] modifiedData = data.toArray(new DataPoint[data.size()]);
            series.get(0).resetData(modifiedData);
            graphView.removeAllSeries();
            graphView.getViewport().setMinX(series.get(0).getLowestValueX());
            graphView.getViewport().setMaxX(series.get(0).getHighestValueX());
            graphView.addSeries(series.get(0));

        } else if (graphType == Graphe.GRAPH_TYPE_MACRO) {
            ArrayList<DataPoint[]> points = new ArrayList<>();
            for (int i = 1; i < values.size(); i++) {
                ArrayList<DataPoint> data = new ArrayList<>();
                for (int j = 0; j < values.get(i).size(); j++) {
                    Date d = values.get(i).get(j).getDate();
                    if (dateFrom.compareTo(d) * d.compareTo(dateTo) >= 0) {
                        data.add(new DataPoint(d, values.get(i).get(j).getValue()));
                    }
                }
                DataPoint[] modifiedData = data.toArray(new DataPoint[data.size()]);
                points.add(modifiedData);
                series.get(i).resetData(modifiedData);
            }
            graphView.removeAllSeries();
            graphView.getViewport().setMinX(series.get(1).getLowestValueX());
            graphView.getViewport().setMaxX(series.get(1).getHighestValueX());
            graphView.addSeries(series.get(1));
            graphView.addSeries(series.get(2));
            graphView.addSeries(series.get(3));

        } else {
            ArrayList<DataPoint> data = new ArrayList<>();
            for (int i = 0; i < values.get(4).size(); i++) {
                Date d = values.get(4).get(i).getDate();
                if (dateFrom.compareTo(d) * d.compareTo(dateTo) >= 0) {
                    data.add(new DataPoint(d, values.get(4).get(i).getValue()));
                }
            }
            DataPoint[] modifiedData = data.toArray(new DataPoint[data.size()]);
            series.get(4).resetData(modifiedData);
            graphView.removeAllSeries();
            graphView.getViewport().setMinX(series.get(4).getLowestValueX());
            graphView.getViewport().setMaxX(series.get(4).getHighestValueX());
            graphView.addSeries(series.get(4));

        }
    }

    public void setValues(ArrayList<ArrayList<Point>> values) {
        this.values = values;
    }

    public void setVisibility(int macroType, boolean visible) {
        if (visible) {
            graphView.addSeries(series.get(macroType));
        } else {
            graphView.removeSeries(series.get(macroType));
        }
    }

}
