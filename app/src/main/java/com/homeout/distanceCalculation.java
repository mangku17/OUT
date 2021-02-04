package com.homeout;

public class distanceCalculation {

    //위도 계산 0.00044966051
    public boolean latitudeCalculation(double latitude, double homeX){

        boolean result = false;
        double x = 0.00044966051;

        //집x에서 50m 더하고 빼는 범위

        if((homeX - x <= latitude) && (latitude <= homeX +x)){
            result = true;
        }

        return result;

    }

    //경도 0.00037500938
    public boolean longitudeCalculation(double longitude, double homeY){

        boolean result = false;
        double y = 0.00044966051;

        if((homeY - y <= longitude) && (longitude <= homeY + y)){
            result = true;
        }
        return result;
    }
}
