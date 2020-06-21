package com.danielchwh.macaubus;

import java.util.List;

public class MyRouteInfo {
    public String staCode;
    public String staName;
    public String busAtStation;
    public String busOnRoad;

    public MyRouteInfo(RouteInfo routeInfo) {
        staCode = routeInfo.staCode;
        staName = routeInfo.staName;
        busAtStation = "";
        busOnRoad = "";
    }

    public boolean refresh(List<BusInfo> busInfo) {
        if (busInfo == null) {
            busAtStation = null;
            busOnRoad = null;
            return false;
        }
        String preBusAtStation = busAtStation;
        String preBusOnRoad = busOnRoad;
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
        return !busAtStation.equals(preBusAtStation) || !busOnRoad.equals(preBusOnRoad);
    }
}