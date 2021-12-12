package com.yilmaz.ParkingLot.Rules;

import com.yilmaz.ParkingLot.Data.Services.SpotService;
import com.yilmaz.ParkingLot.Model.Allocation;
import com.yilmaz.ParkingLot.Model.Spot;
import com.yilmaz.ParkingLot.Model.Vehicle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
public class BusRules extends AbstractRules {
    @Value("${parking.lot.width}")
    private int parkingLotX;

    @Value("${parking.lot.length}")
    private int parkingLotY;

    @Value("${price.per.minute}")
    private double pricePerMinute;

    SpotService spotService;

    BusRules(SpotService spotService){
        this.spotService = spotService;
    }

    @Override
    public Allocation registerBestSpotInGivenFloor(Set<Spot> spotsInTheFloor, Vehicle vehicle, int floorNumber){

        for(int xNumber = 1; xNumber <= parkingLotX; xNumber++){
            for(int yNumber = 1; yNumber <= parkingLotY - 1; yNumber++){
                Spot filledSpot = findByCoordinates(spotsInTheFloor, xNumber, yNumber);
                Spot nextFilledSpot = findByCoordinates(spotsInTheFloor, xNumber, yNumber + 1);
                if(filledSpot == null && nextFilledSpot == null){
                    Allocation allocation = new Allocation();
                    allocation.setYCoordinate(yNumber + 1);
                    allocation.setXCoordinate(xNumber);
                    allocation.setFloor(floorNumber);
                    allocation.setTitle("We have vacancy!");

                    //save registration
                    saveSpot(vehicle, allocation);

                    //save the second spot
                    allocation.setYCoordinate(yNumber);
                    saveSpot(vehicle, allocation);

                    String message = "Fields " + xNumber + "-" + getSuffix(yNumber)
                            + " and " + xNumber + "-" + getSuffix(yNumber+1)
                            + " are reserved for you in the " +  getSuffix(floorNumber) + " floor! :)";
                    allocation.setMessage(message);

                    return allocation;
                }
            }
        }
        // no suitable spot
        return null;
    }

    private void saveSpot(Vehicle vehicle, Allocation allocation){
        Spot spot = new Spot();
        spot.setWeight(vehicle.getWeight());
        spot.setLicensePlateNumber(vehicle.getPlateNumber());
        spot.setSpotType(vehicle.getSize());
        spot.setSpotYCoordinate(allocation.getYCoordinate());
        spot.setSpotXCoordinate(allocation.getXCoordinate());
        spot.setFloorNumber(allocation.getFloor());
        spot.setEnterDate(System.currentTimeMillis());
        spotService.save(spot);
    }

    private String getSuffix(int i){
        int j = i % 10;
        int k = i % 100;
        if (j == 1 && k != 11) {
            return i + "st";
        }
        if (j == 2 && k != 12) {
            return i + "nd";
        }
        if (j == 3 && k != 13) {
            return i + "rd";
        }
        return i + "th";
    }
}
