package de.br.aff.controller.mapper;

import de.br.aff.datatransferobject.CarDTO;
import de.br.aff.domainobject.CarDO;
import de.br.aff.domainvalue.EngineType;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CarMapper
{

    public static CarDTO makeCarDTO(CarDO carDO)
    {
        return CarDTO.builder().
            id(carDO.getId())
            .licensePlate(carDO.getLicensePlate())
            .seatCount(carDO.getSeatCount())
            .convertible(carDO.isConvertible())
            .rating(carDO.getRating())
            .engineType(carDO.getEngineType().name())
            .manufacturer(carDO.getManufacturer())
            .model(carDO.getModel())
            .build();

    }


    public static List<CarDTO> makeCarDTOList(Collection<CarDO> cars)
    {
        return cars.stream()
            .map(CarMapper::makeCarDTO)
            .collect(Collectors.toList());
    }


    public static CarDO makeCarDO(CarDTO carDTO)
    {
        return new CarDO(carDTO.getLicensePlate(), carDTO.getSeatCount(), carDTO.isConvertible(), carDTO.getRating(), EngineType.valueOf(carDTO.getEngineType().toUpperCase()),
            carDTO.getManufacturer(), carDTO.getModel());
    }
}
