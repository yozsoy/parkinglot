package com.yilmaz.ParkingLot.Rules;

import com.yilmaz.ParkingLot.Enums.VehicleType;
import com.yilmaz.ParkingLot.Model.Vehicle;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class RuleFactory {

    ApplicationContext ctx;

    RuleFactory(ApplicationContext ctx){
        this.ctx = ctx;
    }

    public Rules get(Vehicle vehicle){
        Rules rules = (Rules)ctx.getBean(vehicle.getVehicleType().getRuleClass());
        return rules;
    }
}
