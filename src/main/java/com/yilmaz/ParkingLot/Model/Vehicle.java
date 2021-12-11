package com.yilmaz.ParkingLot.Model;

import com.yilmaz.ParkingLot.Enums.SpotType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Vehicle {
    private String plateNumber;
    private double height;
    private double weight;
    private SpotType size;
    private long enterDate;
    private boolean agree;
}
