package com.yilmaz.ParkingLot.Data.Services.map;

import com.yilmaz.ParkingLot.Data.Services.SpotService;
import com.yilmaz.ParkingLot.Model.Spot;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
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

    //find spots filled given plate no
    @Override
    public Set<Spot> findByPlateNo(String plateNo){
        Set<Spot> allFilledSpots = findAll();
        Set<Spot> res = new HashSet<Spot>();
        for(Spot s: allFilledSpots) {
            if (Objects.equals(s.getLicensePlateNumber(), plateNo))
                res.add(s);
        }
        return res;
    }

    //find spots among given set of floors given floor number
    @Override
    public final Set<Spot> findByFloor(int floorNumber){
        Set<Spot> elems = findAll();
        Set<Spot> result = new HashSet<Spot>();
        for(Spot s: elems)
            if(s.getFloorNumber() == floorNumber)
                result.add(s);
        return result;
    }
}
