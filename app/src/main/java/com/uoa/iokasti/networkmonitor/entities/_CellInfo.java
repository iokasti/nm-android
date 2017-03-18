package com.uoa.iokasti.networkmonitor.entities;

public class _CellInfo {

    private int cellId;
    private int lac;
    private int tac;
    private int rsrp;
    private int rssi;
    private int rsrq;
    private int rssnr;
    private int cqi;
    private String rat;
    private int mcc;
    private int mnc;
    private double latitude;
    private double longitude;
    private boolean connected;

    public _CellInfo(int cellId, int lac, int tac, int rssi, int rsrp, int rsrq, int rssnr, int cqi, String rat, int mcc, int mnc, double latitude, double longitude, boolean connected) {
        this.cellId = cellId;
        this.lac = lac;
        this.tac = tac;
        this.rssi = rssi;
        this.rsrp = rsrp;
        this.rsrq = rsrq;
        this.rssnr = rssnr;
        this.cqi = cqi;
        this.rat = rat;
        this.mcc = mcc;
        this.mnc = mnc;
        this.latitude = latitude;
        this.longitude = longitude;
        this.connected = connected;
    }

    public _CellInfo() {
        this.cellId = Integer.MAX_VALUE;
        this.lac = Integer.MAX_VALUE;
        this.tac = Integer.MAX_VALUE;
        this.rssi = Integer.MAX_VALUE;
        this.rsrp = Integer.MAX_VALUE;
        this.rsrq = Integer.MAX_VALUE;
        this.rssnr = Integer.MAX_VALUE;
        this.cqi = Integer.MAX_VALUE;
        this.rat = "";
        this.mcc = Integer.MAX_VALUE;
        this.mnc = Integer.MAX_VALUE;
        this.latitude = Double.MAX_VALUE;
        this.longitude = Double.MAX_VALUE;
        this.connected = false;
    }

    public int getCellId() {
        return cellId;
    }

    public void setCellId(int cellId) {
        this.cellId = cellId;
    }

    public int getLac() {
        return lac;
    }

    public void setLac(int lac) {
        this.lac = lac;
    }

    public int getTac() {
        return tac;
    }

    public void setTac(int tac) {
        this.tac = tac;
    }

    public int getRsrp() {
        return rsrp;
    }

    public void setRsrp(int rsrp) {
        this.rsrp = rsrp;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getRsrq() {
        return rsrq;
    }

    public void setRsrq(int rsrq) {
        this.rsrq = rsrq;
    }

    public int getRssnr() {
        return rssnr;
    }

    public void setRssnr(int rssnr) {
        this.rssnr = rssnr;
    }

    public int getCqi() {
        return cqi;
    }

    public void setCqi(int cqi) {
        this.cqi = cqi;
    }

    public String getRat() {
        return rat;
    }

    public void setRat(String rat) {
        this.rat = rat;
    }

    public int getMcc() {
        return mcc;
    }

    public void setMcc(int mcc) {
        this.mcc = mcc;
    }

    public int getMnc() {
        return mnc;
    }

    public void setMnc(int mnc) {
        this.mnc = mnc;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean getConnected() {
        return connected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        _CellInfo cellInfo = (_CellInfo) o;

        return cellId == cellInfo.cellId;
    }

    @Override
    public int hashCode() {
        return cellId;
    }

}
