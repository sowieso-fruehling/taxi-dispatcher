package de.br.aff.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.br.aff.datatransferobject.CarDTO;
import de.br.aff.domainobject.CarDO;
import de.br.aff.domainobject.DriverDO;
import de.br.aff.domainvalue.EngineType;
import de.br.aff.domainvalue.OnlineStatus;
import java.time.ZonedDateTime;

public class TestUtils
{

    public static final CarDTO VALID_CAR_DTO = CarDTO.builder()
        .id(203L)
        .licensePlate("D123")
        .seatCount(3)
        .convertible(true)
        .rating(33)
        .engineType("petrol")
        .manufacturer("Mercedes")
        .model("CLS").build();
    public static final CarDTO CAR_DTO_WITHOUT_LICENSE_PLATE = CarDTO.builder()
        .id(204L)
        .seatCount(3)
        .convertible(true)
        .rating(33)
        .engineType("nonexisting")
        .model("Mercedes")
        .model("CLS").build();
    public static final CarDO TEST_CAR = new CarDO(1L, ZonedDateTime.now(),
        "B123", 5, false, 12, EngineType.DIESEL, "BMW", "M2", null);

    public static final DriverDO VALID_ONLINE_DRIVER;


    static
    {
        DriverDO driver = new DriverDO("xyz", "zyx", null);
        driver.setOnlineStatus(OnlineStatus.ONLINE);
        VALID_ONLINE_DRIVER = driver;
    }


    public static String toJson(Object object) throws JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

}
