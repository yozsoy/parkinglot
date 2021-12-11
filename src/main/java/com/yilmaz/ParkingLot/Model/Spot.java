package com.yilmaz.ParkingLot.Model;

import com.yilmaz.ParkingLot.Enums.SpotType;
import lombok.Data;

@Data
public class Spot extends BaseEntity{
    private int floorNumber;
    private int spotXCoordinate;
    private int spotYCoordinate;
    private double weight;
    private SpotType spotType;//small or large
    private String licensePlateNumber;
}
