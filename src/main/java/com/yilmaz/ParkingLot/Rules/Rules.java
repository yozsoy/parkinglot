package com.yilmaz.ParkingLot.Rules;

import com.yilmaz.ParkingLot.Model.Allocation;
import com.yilmaz.ParkingLot.Model.Spot;
import com.yilmaz.ParkingLot.Model.Vehicle;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class Rules {

    @Value("${parking.lot.number.of.floors}")
    private int numberOfFloors;

    @Value("${height.of.the.floors}")
    private int[] heightsOfTheFloors;

    @Value("${weight.capacity.per.floor}")
    private int[] weightCapacitiesOfTheFloors;

    @Value("${parking.lot.width}")
    private int parkingLotX;

    @Value("${parking.lot.length}")
    private int parkingLotY;

    public abstract Allocation leaveOperation(Set<Spot> existingSpots);

    public abstract Allocation findBestSpot(Set<Spot> allFilledSpots, Vehicle vehicle, int floorNumber);

    public final Allocation findBestEmptySpot(Set<Spot> allFilledSpots, Vehicle vehicle){
        for(int floorNumber = 0; floorNumber<numberOfFloors; floorNumber++){
            Set<Spot> spotsInTheFloor = findByFloor(allFilledSpots, floorNumber);

            //check weight
            if(!doesFloorSatisfyWeightRequirement(vehicle, spotsInTheFloor, floorNumber))
                continue;

            //check height
            if(!doesFloorSatisfyHeightRequirement(vehicle, floorNumber))
                continue;

            Allocation allocation = findBestSpot(allFilledSpots, vehicle, floorNumber);
            return allocation;
        }
        return null;
    }

    //check weight
    private final boolean doesFloorSatisfyWeightRequirement(Vehicle incomingVehicle, Set<Spot> existingSpots, int floorNumber){
        double remainingWeight = weightCapacitiesOfTheFloors[floorNumber] - incomingVehicle.getWeight();
        for(Spot spot: existingSpots){
            remainingWeight -= spot.getWeight();
        }
        return remainingWeight > 0;
    }

    // check height
    private final  boolean doesFloorSatisfyHeightRequirement(Vehicle incomingVehicle, int floorNumber){
        return heightsOfTheFloors[floorNumber] > incomingVehicle.getHeight();
    }

    //find spots among given set of floors given floor number
    private Set<Spot> findByFloor(Set<Spot> elems, int floorNumber){
        Set<Spot> result = new HashSet<Spot>();
        for(Spot s: elems)
            if(s.getFloorNumber() == floorNumber)
                result.add(s);
        return result;
    }

    //find spot given specific coordinates
    protected Spot findByCoordinates(Set<Spot> elems, int x, int y){
        for(Spot s: elems) {
            if (s.getSpotXCoordinate() == x && s.getSpotYCoordinate() == y)
                return s;
        }
        return null;
    }

}
