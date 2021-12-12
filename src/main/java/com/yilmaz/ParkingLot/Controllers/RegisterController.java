package com.yilmaz.ParkingLot.Controllers;

import com.yilmaz.ParkingLot.Data.Services.SpotService;
import com.yilmaz.ParkingLot.Model.Allocation;
import com.yilmaz.ParkingLot.Model.Spot;
import com.yilmaz.ParkingLot.Model.Vehicle;
import com.yilmaz.ParkingLot.Rules.RuleFactory;
import com.yilmaz.ParkingLot.Rules.AbstractRules;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
        AbstractRules rule = ruleFactory.get(vehicle);
        Allocation allocation = rule.run(vehicle);
        if(allocation == null){
            throw new RuntimeException("We don't have vacancy! :(");
        }
        return allocation;
    }

    @GetMapping("/get-all")
    public Set<Spot> getAll(){
        Set<Spot> spots = spotService.findAll();
        return spots;
    }




}
