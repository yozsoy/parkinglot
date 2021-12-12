package com.yilmaz.ParkingLot.Controllers;

import com.yilmaz.ParkingLot.Data.Services.SpotService;
import com.yilmaz.ParkingLot.Model.Allocation;
import com.yilmaz.ParkingLot.Model.Spot;
import com.yilmaz.ParkingLot.Model.Vehicle;
import com.yilmaz.ParkingLot.Rules.RuleFactory;
import com.yilmaz.ParkingLot.Rules.Rules;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@RestController
public class RegisterController {

    SpotService spotService;
    RuleFactory ruleFactory;

    @Value("${price.per.minute}")
    private double pricePerMinute;

    public RegisterController(SpotService spotService, RuleFactory ruleFactory) {
        this.spotService = spotService;
        this.ruleFactory = ruleFactory;
    }

    @GetMapping("/register")
    public Allocation registerVehicle(Vehicle vehicle){
        Spot spot = new Spot();
        spot.setWeight(vehicle.getWeight());
        spot.setLicensePlateNumber(vehicle.getPlateNumber());
        spot.setSpotType(vehicle.getSize());
        Allocation allocation;

        Set allSpots = spotService.findAll();
        Set<Spot> existingSpots = findByPlateNo(allSpots, vehicle.getPlateNumber());
        Rules rule = ruleFactory.get(vehicle);
        if(existingSpots.size() != 0){
            allocation = rule.leaveOperation(existingSpots);
            return allocation;
        }


        allocation = rule.findBestEmptySpot(allSpots, vehicle);
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

    //find spot filled by given plate no
    private Set<Spot> findByPlateNo(Set<Spot> allFilledSpots, String plateNo){
        Set<Spot> res = new HashSet<Spot>();
        for(Spot s: allFilledSpots) {
            if (Objects.equals(s.getLicensePlateNumber(), plateNo))
                res.add(s);
        }
        return res;
    }


}
