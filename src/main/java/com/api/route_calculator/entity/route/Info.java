package com.api.route_calculator.entity.route;

import java.util.ArrayList;
import java.util.Date;

public class Info{
    private ArrayList<String> copyrights;
    private int took;
    private Date road_data_timestamp;

    public ArrayList<String> getCopyrights() {
        return copyrights;
    }

    public void setCopyrights(ArrayList<String> copyrights) {
        this.copyrights = copyrights;
    }

    public int getTook() {
        return took;
    }

    public void setTook(int took) {
        this.took = took;
    }

    public Date getRoad_data_timestamp() {
        return road_data_timestamp;
    }

    public void setRoad_data_timestamp(Date road_data_timestamp) {
        this.road_data_timestamp = road_data_timestamp;
    }
}