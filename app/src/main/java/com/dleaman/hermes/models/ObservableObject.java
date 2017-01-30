package com.dleaman.hermes.models;

import java.util.Observable;

/**
 * Created by dleam on 1/28/2017.
 */

public class ObservableObject extends Observable {
    private static ObservableObject instance = new ObservableObject();

    private ObservableObject() {
    }

    public static ObservableObject getInstance() {
        return instance;
    }

    public void updateValue(Object data) {
        synchronized (this) {
            setChanged();
            notifyObservers(data);
        }
    }
}
