package com.company;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

class Nest implements Serializable, Cloneable {

    private int hosesNum; //αριθμός σφηκών
    //συντεταγμένες x και y
    private int x;
    private int y;
    private int z;

    //Constructor
    Nest(int hosesNum, int x, int y) {
        this.hosesNum = hosesNum;
        this.x = x;
        this.y = y;
    }

    Nest(int hosesNum, int x, int y, int z) {
        this.hosesNum = hosesNum;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    //Setters & Getters
    int getHosesNum() {
        return this.hosesNum;
    }

    void setHosesNum(int hosesNum) {
        this.hosesNum = hosesNum;
    }

    int getX() {
        return this.x;
    }

    int getY() {
        return this.y;
    }

    int getZ() {
        return this.z;
    }

    @Override
    public String toString() {
        return hosesNum + " " + x + " " + y + " " + z;
    }

    @Override
    public Nest clone() {
        try {
            return (Nest) super.clone();
        }catch(CloneNotSupportedException e){
            throw new RuntimeException(e); // won't happen
        }
    }

    static Nest[] cloneArray() {
        List<Nest> list = new ArrayList<>();
        for (Nest nest : Main.nests) {
            Nest clone = nest.clone();
            list.add(clone);
        }
        return list.toArray(new Nest[0]);
    }
}
