package com.ninetwozero.bf3droid.service;

public abstract class Restorer<T> {

    public abstract <T> T fetch();

    public abstract void save(T object);
}
