package de.br.aff.controller.mapper;

import de.br.aff.datatransferobject.DriverDTO;
import de.br.aff.domainobject.DriverDO;
import de.br.aff.domainvalue.GeoCoordinate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DriverMapper
{
    public static DriverDO makeDriverDO(DriverDTO driverDTO)
    {
        return new DriverDO(driverDTO.getUsername(), driverDTO.getPassword(), driverDTO.getCoordinate());
    }


    public static DriverDTO makeDriverDTO(DriverDO driverDO)
    {
        DriverDTO.DriverDTOBuilder driverDTOBuilder = DriverDTO.builder()
            .id(driverDO.getId())
            .password(driverDO.getPassword()) // TODO why returning password ??
            .username(driverDO.getUsername())
            .carId(driverDO.getCar() != null ? driverDO.getCar().getId() : null);

        GeoCoordinate coordinate = driverDO.getCoordinate();
        if (coordinate != null)
        {
            driverDTOBuilder.coordinate(coordinate);
        }

        return driverDTOBuilder.build();
    }


    public static List<DriverDTO> makeDriverDTOList(Collection<DriverDO> drivers)
    {
        return drivers.stream()
            .map(DriverMapper::makeDriverDTO)
            .collect(Collectors.toList());
    }
}
