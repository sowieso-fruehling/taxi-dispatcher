package de.br.aff.service.car;

import de.br.aff.controller.mapper.CarMapper;
import de.br.aff.dataaccessobject.CarRepository;
import de.br.aff.domainobject.CarDO;
import de.br.aff.exception.ConstraintsViolationException;
import de.br.aff.exception.EntityNotFoundException;
import de.br.aff.utils.TestUtils;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = DefaultCarService.class)
public class CarServiceTest
{
    @MockBean
    private CarRepository carRepository;

    @Autowired
    private CarService carService;


    @Test(expected = ConstraintsViolationException.class)
    public void thatExceptionIsThrownIfCreatingCarWithExistingLicencePlateIsAttempted() throws ConstraintsViolationException
    {
        when(carRepository.findByLicensePlate(TestUtils.TEST_CAR.getLicensePlate())).thenReturn(Optional.of(TestUtils.TEST_CAR));

        carService.create(TestUtils.TEST_CAR);
    }


    @Test(expected = ConstraintsViolationException.class)
    public void thatWhenUpdatingLicensePlateHasToMatch() throws ConstraintsViolationException, EntityNotFoundException
    {
        CarDO carDOWithNonMatchingLicensePlates = CarMapper.makeCarDO(TestUtils.VALID_CAR_DTO);

        when(carRepository.findById(TestUtils.TEST_CAR.getId())).thenReturn(Optional.of(carDOWithNonMatchingLicensePlates));

        carService.update(TestUtils.TEST_CAR.getId(), TestUtils.TEST_CAR);
    }


    @Test(expected = EntityNotFoundException.class)
    public void thatWhenUpdatingCarHaveToExist() throws EntityNotFoundException, ConstraintsViolationException
    {
        carService.update(123L, TestUtils.TEST_CAR);
    }


    @Test(expected = EntityNotFoundException.class)
    public void thatDeleteThrowsAnExceptionIfEntityDOesntExist() throws EntityNotFoundException
    {
        carService.delete(123L);
    }
}
