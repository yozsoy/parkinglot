package com.yilmaz.ParkingLot.Model;

import lombok.Data;

@Data
public class Allocation {
    private int xCoordinate;
    private int yCoordinate;
    private int floor;
    private String title;
    private double price;
    private boolean isExit;
}
