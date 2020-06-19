package com.danielchwh.macaubus;

import java.util.Random;

public class MyRouteInfo {
    public String staCode;
    public String staName;
    public String busAtStation;
    public String busOnRoad;

    public MyRouteInfo(RouteInfo routeInfo) {
        staCode = routeInfo.staCode;
        staName = routeInfo.staName;
        if (new Random().nextInt(2) == 0) {
            busAtStation = "TT1234";
        }
        if (new Random().nextInt(2) == 0) {
            busOnRoad = "TT1234";
        }
    }
}