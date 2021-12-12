package com.yilmaz.ParkingLot.Rules;

import com.yilmaz.ParkingLot.Data.Services.SpotService;
import com.yilmaz.ParkingLot.Model.Allocation;
import com.yilmaz.ParkingLot.Model.Spot;
import com.yilmaz.ParkingLot.Model.Vehicle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
public class BusRules extends Rules {
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
    public Allocation findBestSpot(Set<Spot> spotsInTheFloor, Vehicle vehicle, int floorNumber){
        Allocation allocation = new Allocation();

        for(int xNumber = 1; xNumber <= parkingLotX; xNumber++){
            for(int yNumber = 1; yNumber <= parkingLotY; yNumber++){
                Spot filledSpot = findByCoordinates(spotsInTheFloor, xNumber, yNumber);
                if(filledSpot == null){
                    allocation = new Allocation();
                    allocation.setYCoordinate(yNumber);
                    allocation.setXCoordinate(xNumber);
                    allocation.setFloor(floorNumber);
                    return allocation;
                }
            }
        }

        return null;
    }

    @Override
    public Allocation leaveOperation(Set<Spot> existingSpots){
        double price = 0;
        long milis = System.currentTimeMillis();
        for(Spot existingSpot: existingSpots){
            long minutes = ((milis - existingSpot.getEnterDate()) / 1000);// / 60
            price += minutes * pricePerMinute;
            spotService.delete(existingSpot);
        }
        Allocation allocation = new Allocation();
        allocation.setPrice(price);
        allocation.setTitle("We have already missed you! :(");
        allocation.setExit(true);
        return allocation;
    }
}
