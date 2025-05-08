package com.api.route_calculator.entity.route;

import java.util.ArrayList;

public class Instruction{
    private double distance;
    private double heading;
    private int sign;
    private ArrayList<Integer> interval;
    private String text;
    private int time;
    private String street_name;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public int getSign() {
        return sign;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }

    public ArrayList<Integer> getInterval() {
        return interval;
    }

    public void setInterval(ArrayList<Integer> interval) {
        this.interval = interval;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getStreet_name() {
        return street_name;
    }

    public void setStreet_name(String street_name) {
        this.street_name = street_name;
    }

    public double getLast_heading() {
        return last_heading;
    }

    public void setLast_heading(double last_heading) {
        this.last_heading = last_heading;
    }

    public double last_heading;
}
