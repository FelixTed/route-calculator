package com.api.route_calculator.entity.route;

import com.api.route_calculator.entity.route.Details;
import com.api.route_calculator.entity.route.Instruction;

import java.util.ArrayList;

public class Path{
    public double distance;
    public double weight;
    public int time;
    public int transfers;
    public boolean points_encoded;
    public int points_encoded_multiplier;
    public ArrayList<Double> bbox;
    public String points;
    public ArrayList<Instruction> instructions;
    public ArrayList<Object> legs;
    public Details details;
    public double ascend;
    public double descend;
    public String snapped_waypoints;
}