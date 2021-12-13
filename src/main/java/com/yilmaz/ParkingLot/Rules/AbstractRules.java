package com.yilmaz.ParkingLot.Rules;

import com.yilmaz.ParkingLot.Data.Services.SpotService;
import com.yilmaz.ParkingLot.Model.Allocation;
import com.yilmaz.ParkingLot.Model.Spot;
import com.yilmaz.ParkingLot.Model.Vehicle;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
public abstract class AbstractRules {

    @Value("${parking.lot.number.of.floors}")
    protected int numberOfFloors;

    @Value("${height.of.the.floors}")
    protected int[] heightsOfTheFloors;

    @Value("${weight.capacity.per.floor}")
    protected int[] weightCapacitiesOfTheFloors;

    @Value("${parking.lot.width}")
    protected int parkingLotX;

    @Value("${parking.lot.length}")
    protected int parkingLotY;

    @Value("${price.per.minute}")
    protected double pricePerMinute;

    @Autowired
    SpotService spotService;

    protected abstract Allocation registerBestSpotInGivenFloor(Set<Spot> spotsInTheFloor, Vehicle vehicle, int floorNumber);

    public final Allocation run(Vehicle vehicle){
        Set<Spot> existingSpots = spotService.findByPlateNo(vehicle.getPlateNumber());
        if(existingSpots.size() != 0)
            return leaveOperation(existingSpots);
        else
            return registerOperation(vehicle);

    }

    // override if needed
    protected double calculatePrice(Set<Spot> existingSpots){
        double price = 0;
        long milis = System.currentTimeMillis();
        for(Spot existingSpot: existingSpots){
            long minutes = ((milis - existingSpot.getEnterDate()) / 1000);// / 60
            price += minutes * pricePerMinute;
        }
        return price;
    }

    //find spot given specific coordinates
    protected final Spot findByCoordinates(Set<Spot> elems, int x, int y){
        for(Spot s: elems) {
            if (s.getSpotXCoordinate() == x && s.getSpotYCoordinate() == y)
                return s;
        }
        return null;
    }

    private final Allocation registerOperation(Vehicle vehicle){
        for(int floorNumber = 0; floorNumber<numberOfFloors; floorNumber++){
            Set<Spot> spotsInTheFloor = spotService.findByFloor(floorNumber);

            //check weight
            if(!doesFloorSatisfyWeightRequirement(vehicle, spotsInTheFloor, floorNumber))
                continue;

            //check height
            if(!doesFloorSatisfyHeightRequirement(vehicle, floorNumber))
                continue;

            Allocation allocation = registerBestSpotInGivenFloor(spotsInTheFloor, vehicle, floorNumber);

            //search next floor
            if(allocation == null)
                continue;

            return allocation;
        }
        return null;
    }

    private final Allocation leaveOperation(Set<Spot> existingSpots){
        double price = calculatePrice(existingSpots);

        //delete spots
        for(Spot existingSpot: existingSpots){
            spotService.delete(existingSpot);
        }

        //create result
        Allocation allocation = new Allocation();
        allocation.setPrice(price);
        allocation.setTitle("We have already missed you! :(");
        allocation.setExit(true);
        return allocation;
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
}
