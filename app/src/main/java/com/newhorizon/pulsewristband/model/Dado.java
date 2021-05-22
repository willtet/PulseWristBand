package com.newhorizon.pulsewristband.model;

public class Dado {
    int cardio;
    int queda;
    double latitude;
    double longitude;

    public Dado(int cardio, int queda, double latitude, double longitude) {
        this.cardio = cardio;
        this.queda = queda;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getCardio() {
        return cardio;
    }

    public void setCardio(int cardio) {
        this.cardio = cardio;
    }

    public int getQueda() {
        return queda;
    }

    public void setQueda(int queda) {
        this.queda = queda;
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
}
