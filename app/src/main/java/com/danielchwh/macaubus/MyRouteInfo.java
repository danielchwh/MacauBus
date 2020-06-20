package com.danielchwh.macaubus;

import java.util.List;
import java.util.Random;

public class MyRouteInfo {
    public String staCode;
    public String staName;
    public String busAtStation;
    public String busOnRoad;

    public MyRouteInfo(RouteInfo routeInfo) {
        staCode = routeInfo.staCode;
        staName = routeInfo.staName;
    }

    public void refresh(List<BusInfo> busInfo) {
        if (busInfo == null) {
            busAtStation = null;
            busOnRoad = null;
            return;
        }
        busAtStation = "";
        busOnRoad = "";
        for (int i = 0; i < busInfo.size(); i++) {
            if (busInfo.get(i).status == 1)
                busAtStation += busInfo.get(i).busPlate + "\n";
            else
                busOnRoad += busInfo.get(i).busPlate + "\n";
        }
        busAtStation = busAtStation.trim();
        busOnRoad = busOnRoad.trim();
        if (busAtStation.equals(""))
            busAtStation = null;
        if (busOnRoad.equals(""))
            busOnRoad = null;
    }
}