package pack;

import static org.junit.Assert.assertEquals;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;

import namespacea.CityData;
import namespacea.CountryData;
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
        // fails, because ModelMapper does not map districtData because Jaxb had not generated setter for it.
        assertEquals(1, dest.getDistrictData().size());
    }
    
    /**
     * should succeed because ModelMapper is instructed to use fields if setter is not present
     */
    @org.junit.Test
    public void testCopyCityDataWithFieldAccess() {
        CityData cityData = new CityData();
        DistrictData districtData = new DistrictData();
        districtData.setPopulation(1234);
        cityData.getDistrictData().add(districtData);

        ModelMapper modelMapper = new ModelMapper();

        // use fields
        modelMapper.getConfiguration()
        .setMatchingStrategy(MatchingStrategies.STRICT)
        .setFieldMatchingEnabled(true)
        .setFieldAccessLevel(AccessLevel.PROTECTED);
        
        modelMapper.addConverter(new Converter<CityData, namespaceb.CityData>() {
            @Override
            public namespaceb.CityData convert(MappingContext<CityData, namespaceb.CityData> context) {
                namespaceb.CityData result = new namespaceb.CityData();
                if(context.getSource() != null) {
                    for(DistrictData districtData : context.getSource().getDistrictData()) {
                        namespaceb.DistrictData mapped = modelMapper.map(districtData, namespaceb.DistrictData.class);
                        result.getDistrictData().add(mapped);
                    }
                }
                return result;
            }
        });

        namespaceb.CityData dest = modelMapper.map(cityData, namespaceb.CityData.class);
        assertEquals(1, dest.getDistrictData().size());
    }
    
    /**
     * clumsy: do the conversion manually for jaxb generated lists without setter
     */
    @org.junit.Test
    public void testCopyCityDataWithConverter() {
        
        CountryData countryData = new CountryData();
        CityData cityData = new CityData();
        DistrictData districtData = new DistrictData();
        districtData.setPopulation(1234);
        cityData.getDistrictData().add(districtData);
        countryData.getCityData().add(cityData);
        
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        
        modelMapper.addConverter(new Converter<CountryData, namespaceb.CountryData>() {
            @Override
            public namespaceb.CountryData convert(MappingContext<CountryData, namespaceb.CountryData> context) {
                namespaceb.CountryData result = new namespaceb.CountryData();
                if(context.getSource() != null) {
                    for(CityData cityData : context.getSource().getCityData()) {
                        namespaceb.CityData mapped = modelMapper.map(cityData, namespaceb.CityData.class);
                        result.getCityData().add(mapped);
                    }
                }
                return result;
            }
        });
        
        modelMapper.addConverter(new Converter<CityData, namespaceb.CityData>() {
            @Override
            public namespaceb.CityData convert(MappingContext<CityData, namespaceb.CityData> context) {
                namespaceb.CityData result = new namespaceb.CityData();
                if(context.getSource() != null) {
                    for(DistrictData districtData : context.getSource().getDistrictData()) {
                        namespaceb.DistrictData mapped = modelMapper.map(districtData, namespaceb.DistrictData.class);
                        result.getDistrictData().add(mapped);
                    }
                }
                return result;
            }
        });

        namespaceb.CountryData destCountryData = modelMapper.map(countryData, namespaceb.CountryData.class);
        assertEquals(1, destCountryData.getCityData().size());
        namespaceb.CityData destCityData = destCountryData.getCityData().get(0);
        assertEquals(1, destCityData.getDistrictData().size());
        namespaceb.DistrictData destDistrictData = destCityData.getDistrictData().get(0);
        assertEquals(1234, destDistrictData.getPopulation());
    }
    

}
