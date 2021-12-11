package com.yilmaz.ParkingLot.Controllers;

import com.yilmaz.ParkingLot.Model.PriceRequest;
import com.yilmaz.ParkingLot.Model.PriceResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ResourceBundle;

@RestController
public class PriceController {
    //private static final ResourceBundle resource = ResourceBundle.getBundle("config");

    @GetMapping("/price")
    public PriceResponse getPrice(PriceRequest req){
        PriceResponse resp = new PriceResponse();
        resp.setPrice(req.getHeight() + req.getWeight());
        return resp;
    }
}
