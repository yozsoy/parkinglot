package com.yilmaz.ParkingLot.Data.Services;

import com.yilmaz.ParkingLot.Model.Spot;

import java.util.HashSet;
import java.util.Set;

public interface SpotService extends  CrudService<Spot, Long>{

    //find spots given plate no
    Set<Spot> findByPlateNo(String plateNo);

    //find spots given floor number
    Set<Spot> findByFloor(int floorNumber);
}
