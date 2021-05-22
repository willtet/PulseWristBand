package com.newhorizon.pulsewristband.model;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class Dado {
    int cardio;
    int queda;
    double latitude;
    double longitude;
    String horarioQueda;

    public Dado(int cardio, int queda) {
        this.cardio = cardio;
        this.queda = queda;
    }

    public Dado(int cardio, int queda, double latitude, double longitude, String horarioQueda) {
        this.cardio = cardio;
        this.queda = queda;
        this.latitude = latitude;
        this.longitude = longitude;
        this.horarioQueda = horarioQueda;;
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

    public String getHorarioQueda() {
        return horarioQueda;
    }

    public void setHorarioQueda(String horarioQueda) {
        this.horarioQueda = horarioQueda;
    }
}
