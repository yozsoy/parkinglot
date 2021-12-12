package com.yilmaz.ParkingLot.Controllers;

import com.yilmaz.ParkingLot.Data.Services.SpotService;
import com.yilmaz.ParkingLot.Model.Allocation;
import com.yilmaz.ParkingLot.Model.Spot;
import com.yilmaz.ParkingLot.Model.Vehicle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@RestController
public class RegisterController {

    SpotService spotService;

    @Value("${parking.lot.number.of.floors}")
    private int numberOfFloors;

    @Value("${parking.lot.width}")
    private int parkingLotX;

    @Value("${parking.lot.length}")
    private int parkingLotY;

    @Value("${price.per.minute}")
    private double pricePerMinute;

    @Value("${initial.price}")
    private double initialPrice;

    @Value("${weight.capacity.per.floor}")
    private int[] weightCapacitiesOfTheFloors;

    @Value("${height.of.the.floors}")
    private int[] heightsOfTheFloors;

    public RegisterController(SpotService spotService) {
        this.spotService = spotService;
    }

    @GetMapping("/register")
    public Allocation registerVehicle(Vehicle vehicle){
        Spot spot = new Spot();
        spot.setWeight(vehicle.getWeight());
        spot.setLicensePlateNumber(vehicle.getPlateNumber());
        spot.setSpotType(vehicle.getSize());
        Allocation allocation;

        Set spots = spotService.findAll();
        Set<Spot> existingSpots = findByPlateNo(spots, vehicle.getPlateNumber());
        if(existingSpots.size() != 0){
            double price = 0;
            long milis = System.currentTimeMillis();
            for(Spot existingSpot: existingSpots){
                long minutes = ((milis - existingSpot.getEnterDate()) / 1000);// / 60
                price += minutes * pricePerMinute;
                spotService.delete(existingSpot);
            }
            allocation = new Allocation();
            allocation.setPrice(price);
            allocation.setTitle("We have already missed you! :(");
            allocation.setExit(true);
            return allocation;
        }
        allocation = findEmptySpot(spots, vehicle);
        if(allocation == null){
            throw new RuntimeException("We don't have vacancy! :(");
        }
        spot.setSpotYCoordinate(allocation.getYCoordinate());
        spot.setSpotXCoordinate(allocation.getXCoordinate());
        spot.setFloorNumber(allocation.getFloor());
        spot.setEnterDate(System.currentTimeMillis());
        spotService.save(spot);
        allocation.setTitle("We have vacancy!");
        return allocation;
    }

    @GetMapping("/get-all")
    public Set<Spot> getAll(){
        Set<Spot> spots = spotService.findAll();
        return spots;
    }

    private Set<Spot> findByFloor(Set<Spot> elems, int floorNumber){
        Set<Spot> result = new HashSet<Spot>();
        for(Spot s: elems)
            if(s.getFloorNumber() == floorNumber)
                result.add(s);
        return result;
    }
    private Spot findByCoordinates(Set<Spot> elems, int x, int y){
        for(Spot s: elems) {
            if (s.getSpotXCoordinate() == x && s.getSpotYCoordinate() == y)
                return s;
        }
        return null;
    }
    private Set<Spot> findByPlateNo(Set<Spot> allFilledSpots, String plateNo){
        Set<Spot> res = new HashSet<Spot>();
        for(Spot s: allFilledSpots) {
            if (Objects.equals(s.getLicensePlateNumber(), plateNo))
                res.add(s);
        }
        return res;
    }
    private Allocation findEmptySpot(Set<Spot> allFilledSpots, Vehicle vehicle){
        for(int floorNumber = 0; floorNumber<numberOfFloors; floorNumber++){
            Set<Spot> spotsInTheFloor = findByFloor(allFilledSpots, floorNumber);

            //check weight
            if(!doesFloorSatisfyWeightRequirement(vehicle, spotsInTheFloor, floorNumber))
                continue;

            //check height
            if(!doesFloorSatisfyHeightRequirement(vehicle, floorNumber))
                continue;

            //find an empty spot
            for(int xNumber = 1; xNumber <= parkingLotX; xNumber++){
                for(int yNumber = 1; yNumber <= parkingLotY; yNumber++){
                    Spot filledSpot = findByCoordinates(spotsInTheFloor, xNumber, yNumber);
                    if(filledSpot == null){
                        Allocation allocation = new Allocation();
                        allocation.setYCoordinate(yNumber);
                        allocation.setXCoordinate(xNumber);
                        allocation.setFloor(floorNumber);
                        return allocation;
                    }
                }
            }
        }
        return null;
    }
    private boolean doesFloorSatisfyWeightRequirement(Vehicle incomingVehicle, Set<Spot> existingSpots, int floorNumber){
        double remainingWeight = weightCapacitiesOfTheFloors[floorNumber] - incomingVehicle.getWeight();
        for(Spot spot: existingSpots){
            remainingWeight -= spot.getWeight();
        }
        return remainingWeight > 0;
    }

    private boolean doesFloorSatisfyHeightRequirement(Vehicle incomingVehicle, int floorNumber){
        return heightsOfTheFloors[floorNumber] > incomingVehicle.getHeight();
    }
}
