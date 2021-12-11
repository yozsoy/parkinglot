package com.yilmaz.ParkingLot.Controllers;

import com.yilmaz.ParkingLot.Data.Services.SpotService;
import com.yilmaz.ParkingLot.Model.Allocation;
import com.yilmaz.ParkingLot.Model.PriceResponse;
import com.yilmaz.ParkingLot.Model.Spot;
import com.yilmaz.ParkingLot.Model.Vehicle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class RegisterController {

    SpotService spotService;

    @Value("${parking.lot.number.of.floors}")
    private int numberOfFloors;

    @Value("${parking.lot.width}")
    private int parkingLotWidth;

    @Value("${parking.lot.length}")
    private int parkingLotLength;

    public RegisterController(SpotService spotService) {
        this.spotService = spotService;
    }

    @GetMapping("/register")
    public Allocation registerVehicle(Vehicle vehicle){
        Spot spot = new Spot();
        spot.setFloorNumber((int)vehicle.getHeight());
        spot.setWeight(vehicle.getWeight());
        spot.setLicensePlateNumber(vehicle.getPlateNumber());
        spot.setSpotType(vehicle.getSize());
        spot.setSpotXCoordinate(numberOfFloors + parkingLotWidth + parkingLotLength);
        spot.setSpotYCoordinate(numberOfFloors + parkingLotWidth + parkingLotLength);
        spotService.save(spot);
        Allocation allocation = new Allocation();
        allocation.setXCoordinate(10);
        allocation.setXCoordinate(20);
        return allocation;
    }

    @GetMapping("/get-all")
    public Set<Spot> getAll(){
        Set<Spot> spots = spotService.findAll();
        return spots;
    }

}
