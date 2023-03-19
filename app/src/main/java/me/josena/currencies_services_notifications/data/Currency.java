package me.josena.currencies_services_notifications.data;

import androidx.annotation.NonNull;

public class Currency {

    private String name;
    private double value;

    public Currency(String name, double value) {

        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {

        return name + ": " + value + "\n";
    }
}
