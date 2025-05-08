package com.api.route_calculator.entity.route;

import com.api.route_calculator.entity.route.Hints;
import com.api.route_calculator.entity.route.Info;
import com.api.route_calculator.entity.route.Path;

import java.util.ArrayList;

public class Route{
    private Hints hints;

    public Hints getHints() {
        return hints;
    }

    public void setHints(Hints hints) {
        this.hints = hints;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public ArrayList<Path> getPaths() {
        return paths;
    }

    public void setPaths(ArrayList<Path> paths) {
        this.paths = paths;
    }

    private Info info;
    private ArrayList<Path> paths;
}
