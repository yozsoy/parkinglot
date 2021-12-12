package com.yilmaz.ParkingLot.Rules;

import com.yilmaz.ParkingLot.Data.Services.SpotService;
import com.yilmaz.ParkingLot.Model.Allocation;
import com.yilmaz.ParkingLot.Model.Spot;
import com.yilmaz.ParkingLot.Model.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class CarRules extends AbstractRules {

    @Value("${parking.lot.width}")
    private int parkingLotX;

    @Value("${parking.lot.length}")
    private int parkingLotY;

    @Autowired
    SpotService spotService;

    @Override
    public Allocation registerBestSpotInGivenFloor(Set<Spot> spotsInTheFloor, Vehicle vehicle, int floorNumber){

        for(int xNumber = 1; xNumber <= parkingLotX; xNumber++){
            for(int yNumber = 1; yNumber <= parkingLotY; yNumber++){
                Spot filledSpot = findByCoordinates(spotsInTheFloor, xNumber, yNumber);
                if(filledSpot == null){
                    Allocation allocation = new Allocation();
                    allocation.setYCoordinate(yNumber);
                    allocation.setXCoordinate(xNumber);
                    allocation.setFloor(floorNumber);
                    allocation.setTitle("We have vacancy!");

                    //save registration
                    saveSpot(vehicle, allocation);
                    return allocation;
                }
            }
        }

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
}
