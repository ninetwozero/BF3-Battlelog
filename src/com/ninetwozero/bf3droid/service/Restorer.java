package com.ninetwozero.bf3droid.service;

import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.model.User;

public abstract class Restorer<T> {

    public abstract T  fetch();

    public abstract void save(T object);

    protected User userBy(String type) {
        return BF3Droid.getUserBy(type);
    }
}
