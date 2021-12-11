package com.yilmaz.ParkingLot.Data.Services.map;

import com.yilmaz.ParkingLot.Data.Services.SpotService;
import com.yilmaz.ParkingLot.Model.Spot;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Profile({"default", "map"})
public class SpotMapService extends AbstractMapService<Spot, Long> implements SpotService {

    @Override
    public Set<Spot> findAll() {
        Set<Spot> x = super.findAll();
        return x;
    }

    @Override
    public Spot findById(Long id) {
        return super.findById(id);
    }

    @Override
    public Spot save(Spot spot) {
        return super.save(spot);
    }

    @Override
    public void delete(Spot spot) {
        super.delete(spot);
    }

    @Override
    public void deleteById(Long id) {
        super.deleteById(id);
    }
}
