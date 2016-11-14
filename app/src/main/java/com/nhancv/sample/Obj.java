package com.nhancv.sample;

/**
 * Created by nhancao on 11/14/16.
 */

public class Obj {
    public int id;
    public String name;

    public Obj(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String show() {
        return " id = " + id + " name = " + name;
    }
}
