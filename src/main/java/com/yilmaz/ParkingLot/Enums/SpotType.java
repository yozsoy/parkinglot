package com.yilmaz.ParkingLot.Enums;


import java.util.HashMap;
import java.util.Map;

public enum SpotType {
    SMALL(1),
    BIG(2);

    private int value;
    private static Map map = new HashMap<>();

    SpotType(int val){
        value = val;
    }

    static {
        for (SpotType t : SpotType.values()) {
            map.put(t.value, t);
        }
    }

    public static SpotType valueOf(int t) {
        return (SpotType) map.get(t);
    }

    public int getValue() {
        return value;
    }
}
