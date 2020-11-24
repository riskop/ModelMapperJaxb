package pack;

import static org.junit.Assert.*;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import namespacea.CityData;
import namespacea.DistrictData;

public class Test {

    @org.junit.Test
    public void testCopyCityData() {
        CityData cityData = new CityData();
        DistrictData districtData = new DistrictData();
        districtData.setPopulation(1234);
        cityData.getDistrictData().add(districtData);

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        namespaceb.CityData dest = modelMapper.map(cityData, namespaceb.CityData.class);
        assertEquals(1, dest.getDistrictData().size());
    }

}
