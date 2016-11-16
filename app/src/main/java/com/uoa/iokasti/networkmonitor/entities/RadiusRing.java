package com.uoa.iokasti.networkmonitor.entities;

public class RadiusRing {
    private double phoneLat;
    private double phoneLong;
    private double minLat;
    private double maxLat;
    private double minLong;
    private double maxLong;

    public RadiusRing() {
        this.phoneLat = phoneLat;
        this.phoneLong = phoneLong;
    }

    public void calculateRing(double radiusInKm){
        double kmInLongitudeDegree = 111.320 * Math.cos( this.phoneLat / 180.0 * Math.PI);
        double deltaLat = radiusInKm / 111.1;
        double deltaLong = radiusInKm / kmInLongitudeDegree;

        minLat = this.phoneLat - deltaLat;
        maxLat = this.phoneLat + deltaLat;
        minLong = this.phoneLong - deltaLong;
        maxLong = this.phoneLong + deltaLong;
    }


    public double getPhoneLat() {
        return phoneLat;
    }

    public void setPhoneLat(double phoneLat) {
        this.phoneLat = phoneLat;
    }

    public double getPhoneLong() {
        return phoneLong;
    }

    public void setPhoneLong(double phoneLong) {
        this.phoneLong = phoneLong;
    }

    public double getMinLat() {
        return minLat;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public double getMinLong() {
        return minLong;
    }

    public double getMaxLong() {
        return maxLong;
    }
}
