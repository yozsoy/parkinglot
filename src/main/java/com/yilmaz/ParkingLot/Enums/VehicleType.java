package com.yilmaz.ParkingLot.Enums;


import java.util.HashMap;
import java.util.Map;

public enum VehicleType {
    CAR(1, "carRules"),
    BUS(2, "busRules");

    private int value;
    private String className;
    private static Map map = new HashMap<>();

    VehicleType(int val, String className){
        value = val;
        this.className = className;
    }

    static {
        for (VehicleType t : VehicleType.values()) {
            map.put(t.value, t);
        }
    }

    public static VehicleType valueOf(int t) {
        return (VehicleType) map.get(t);
    }

    public int getValue() {
        return value;
    }
    public String getRuleClass() {
        return className;
    }
}
