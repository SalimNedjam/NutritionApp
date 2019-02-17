package com.deepside.nutritionapp.Utils;

import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by Samy on 16/02/2018.
 */

public class Point implements Comparable<Point> {
    private Date date;
    private double value;

    public Point(Date date, double value) {
        this.date = date;
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public int compareTo(@NonNull Point o) {
        return getDate().compareTo(o.getDate());
    }
}
