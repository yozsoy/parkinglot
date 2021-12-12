package com.yilmaz.ParkingLot.Rules;

import com.yilmaz.ParkingLot.Model.Vehicle;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class RuleFactory {

    ApplicationContext ctx;

    RuleFactory(ApplicationContext ctx){
        this.ctx = ctx;
    }

    public AbstractRules get(Vehicle vehicle){
        AbstractRules rules = (AbstractRules)ctx.getBean(vehicle.getVehicleType().getRuleClass());
        return rules;
    }
}
