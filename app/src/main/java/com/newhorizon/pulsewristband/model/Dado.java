package com.newhorizon.pulsewristband.model;

public class Dado {
    int cardio;
    int queda;

    public Dado(int cardio, int queda) {
        this.cardio = cardio;
        this.queda = queda;
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
}
