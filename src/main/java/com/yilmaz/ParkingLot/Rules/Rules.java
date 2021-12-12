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

    @Value("${price.per.minute}")
    private double pricePerMinute;

    @Autowired
    SpotService spotService;

    public final Allocation run(Vehicle vehicle){
        Set<Spot> spots = spotService.findAll();
        Set<Spot> existingSpots = findByPlateNo(spots, vehicle.getPlateNumber());
        if(existingSpots.size() != 0)
            return leaveOperation(vehicle, existingSpots);
        else
            return findBestEmptySpot(vehicle, spots);

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

    protected abstract Allocation findBestSpotInGivenFloor(Set<Spot> spotsInTheFloor, Vehicle vehicle, int floorNumber);

    protected final Allocation leaveOperation(Vehicle vehicle, Set<Spot> existingSpots){
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

    protected final Allocation findBestEmptySpot(Vehicle vehicle, Set<Spot> allFilledSpots){
        for(int floorNumber = 0; floorNumber<numberOfFloors; floorNumber++){
            Set<Spot> spotsInTheFloor = findByFloor(allFilledSpots, floorNumber);

            //check weight
            if(!doesFloorSatisfyWeightRequirement(vehicle, spotsInTheFloor, floorNumber))
                continue;

            //check height
            if(!doesFloorSatisfyHeightRequirement(vehicle, floorNumber))
                continue;

            Allocation allocation = findBestSpotInGivenFloor(spotsInTheFloor, vehicle, floorNumber);

            if(allocation == null)
                continue;

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
    private final Set<Spot> findByFloor(Set<Spot> elems, int floorNumber){
        Set<Spot> result = new HashSet<Spot>();
        for(Spot s: elems)
            if(s.getFloorNumber() == floorNumber)
                result.add(s);
        return result;
    }

    //find spot given specific coordinates
    protected final Spot findByCoordinates(Set<Spot> elems, int x, int y){
        for(Spot s: elems) {
            if (s.getSpotXCoordinate() == x && s.getSpotYCoordinate() == y)
                return s;
        }
        return null;
    }

    //find spots filled given plate no
    protected final Set<Spot> findByPlateNo(Set<Spot> allFilledSpots, String plateNo){
        Set<Spot> res = new HashSet<Spot>();
        for(Spot s: allFilledSpots) {
            if (Objects.equals(s.getLicensePlateNumber(), plateNo))
                res.add(s);
        }
        return res;
    }
}
