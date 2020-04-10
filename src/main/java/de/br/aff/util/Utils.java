package de.br.aff.util;

import de.br.aff.domainobject.CarDO;
import de.br.aff.domainobject.DriverDO;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class Utils
{
    public static String getCarResourceLocation(CarDO createdCar)
    {
        return ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdCar.getId())
            .toUriString();
    }


    public static String getDriverResourceLocation(DriverDO driver)
    {
        return ServletUriComponentsBuilder
            .fromHttpUrl("http://localhost/v1/drivers")
            .path("/{id}")
            .buildAndExpand(driver.getId())
            .toUriString();
    }
}
