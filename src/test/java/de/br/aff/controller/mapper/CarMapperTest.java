package de.br.aff.controller.mapper;

import de.br.aff.datatransferobject.CarDTO;
import de.br.aff.domainobject.CarDO;
import de.br.aff.utils.TestUtils;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class CarMapperTest
{
    @Test
    public void thatCarDTOIsProperlyCreated()
    {
        CarDTO resultingCarDTO = CarMapper.makeCarDTO(TestUtils.TEST_CAR);

        assert resultingCarDTO.getId() == TestUtils.TEST_CAR.getId();
        assert resultingCarDTO.getLicensePlate().equals(TestUtils.TEST_CAR.getLicensePlate());
        assert resultingCarDTO.getSeatCount() == TestUtils.TEST_CAR.getSeatCount();
        assert resultingCarDTO.isConvertible() == TestUtils.TEST_CAR.isConvertible();
        assert resultingCarDTO.getRating() == TestUtils.TEST_CAR.getRating();
        assert resultingCarDTO.getEngineType().equals(TestUtils.TEST_CAR.getEngineType().name());
        assert resultingCarDTO.getManufacturer().equals(TestUtils.TEST_CAR.getManufacturer());
        assert resultingCarDTO.getModel().equals(TestUtils.TEST_CAR.getModel());

    }


    @Test
    public void thatCarDOIsProperlyCreated()
    {
        CarDO carDO = CarMapper.makeCarDO(TestUtils.VALID_CAR_DTO);

        assert carDO.getLicensePlate().equals(TestUtils.VALID_CAR_DTO.getLicensePlate());
        assert carDO.getSeatCount() == TestUtils.VALID_CAR_DTO.getSeatCount();
        assert carDO.isConvertible() == TestUtils.VALID_CAR_DTO.isConvertible();
        assert carDO.getRating() == TestUtils.VALID_CAR_DTO.getRating();
        assert carDO.getEngineType().name().equals(TestUtils.VALID_CAR_DTO.getEngineType().toUpperCase());
        assert carDO.getManufacturer().equals(TestUtils.VALID_CAR_DTO.getManufacturer());
        assert carDO.getModel().equals(TestUtils.VALID_CAR_DTO.getModel());
    }


    @Test(expected = IllegalArgumentException.class)
    public void thatExceptionIsThrownIfNonExistingEngineTypeIsSent()
    {
        CarDTO carDTOWithInvalidEngineType = TestUtils.VALID_CAR_DTO.toBuilder().build();
        carDTOWithInvalidEngineType.setEngineType("nonexisting");

        CarMapper.makeCarDO(carDTOWithInvalidEngineType);
    }


    @Test
    public void thatCarDTOListIsProperlyCreated()
    {
        List<CarDO> carList = Arrays.asList(TestUtils.TEST_CAR, TestUtils.TEST_CAR);

        List<CarDTO> resultingList = CarMapper.makeCarDTOList(carList);

        assert resultingList.size() == 2;
        assert resultingList.get(0).getManufacturer().equals(TestUtils.TEST_CAR.getManufacturer());
        assert resultingList.get(1).getRating() == TestUtils.TEST_CAR.getRating();

    }
}
