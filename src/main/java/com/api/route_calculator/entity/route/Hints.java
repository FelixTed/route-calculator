package com.api.route_calculator.entity.route;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Hints{
    @JsonProperty("visited_nodes.sum")
    private int visitedNodesSum;
    @JsonProperty("visited_nodes.average")
    private int visitedNodesAverage;

    public int getVisitedNodesSum() {
        return visitedNodesSum;
    }

    public void setVisitedNodesSum(int visitedNodesSum) {
        this.visitedNodesSum = visitedNodesSum;
    }

    public int getVisitedNodesAverage() {
        return visitedNodesAverage;
    }

    public void setVisitedNodesAverage(int visitedNodesAverage) {
        this.visitedNodesAverage = visitedNodesAverage;
    }
}