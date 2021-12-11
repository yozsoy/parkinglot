package com.yilmaz.ParkingLot.Model;

import com.yilmaz.ParkingLot.Enums.SpotType;
import lombok.Data;

@Data
public class Vehicle {
    private double height;
    private double weight;
    private SpotType size;
}
