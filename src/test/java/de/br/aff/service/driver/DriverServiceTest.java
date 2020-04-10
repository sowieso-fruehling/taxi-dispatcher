package de.br.aff.service.driver;

import de.br.aff.dataaccessobject.DriverRepository;
import de.br.aff.domainobject.CarDO;
import de.br.aff.domainobject.DriverDO;
import de.br.aff.domainvalue.EngineType;
import de.br.aff.domainvalue.OnlineStatus;
import de.br.aff.exception.CarAlreadyInUseException;
import de.br.aff.exception.ConstraintsViolationException;
import de.br.aff.exception.EntityNotFoundException;
import de.br.aff.service.car.CarService;
import de.br.aff.utils.TestUtils;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = DefaultDriverService.class)
public class DriverServiceTest
{
    @MockBean
    private DriverRepository driverRepository;

    @MockBean
    private CarService carService;

    @Autowired
    private DriverService driverService;


    @Test(expected = EntityNotFoundException.class)
    public void thatDriverHasToExistWhenSelectingCar() throws EntityNotFoundException, ConstraintsViolationException, CarAlreadyInUseException
    {
        when(driverRepository.findById(1L)).thenReturn(Optional.empty());
        driverService.selectCar(1L, 1L);
    }


    @Test(expected = EntityNotFoundException.class)
    public void thatDriverHasToExistWhenDeselectingCar() throws EntityNotFoundException
    {
        when(driverRepository.findById(1L)).thenReturn(Optional.empty());
        driverService.deselectCar(1L);
    }


    @Test(expected = ConstraintsViolationException.class)
    public void thatCarWithProvidedCarIdHasToExist() throws ConstraintsViolationException, EntityNotFoundException, CarAlreadyInUseException
    {

        when(driverRepository.findById(1L)).thenReturn(Optional.of(TestUtils.VALID_ONLINE_DRIVER));

        when(carService.find(1L)).thenReturn(Optional.empty());

        driverService.selectCar(1L, 1L);
    }


    @Test
    public void thatIfDriverDoesntHaveCarSelectedNoIoOperationOccurs() throws EntityNotFoundException
    {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(TestUtils.VALID_ONLINE_DRIVER));
        driverService.deselectCar(1L);

        verify(driverRepository, never()).save(any(DriverDO.class));

    }


    @Test(expected = ConstraintsViolationException.class)
    public void thatCarCanBeSelectedOnlyByOnlineDrivers() throws EntityNotFoundException, ConstraintsViolationException, CarAlreadyInUseException
    {
        DriverDO offlineDriver = new DriverDO("xyz", "zyx", null);

        when(driverRepository.findById(1L)).thenReturn(Optional.of(offlineDriver));

        driverService.selectCar(1L, 10L);

        verify(driverRepository, never()).save(any(DriverDO.class));
    }


    @Test
    public void thatCarSelectionWorksProperly() throws EntityNotFoundException, ConstraintsViolationException, CarAlreadyInUseException
    {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(TestUtils.VALID_ONLINE_DRIVER));

        when(carService.find(10L)).thenReturn(Optional.of(TestUtils.TEST_CAR));

        when(driverRepository.findByCar(TestUtils.TEST_CAR)).thenReturn(Optional.empty());

        driverService.selectCar(1L, 10L);

        verify(driverRepository, times(1)).save(any(DriverDO.class));
    }


    @Test(expected = CarAlreadyInUseException.class)
    public void thatExceptionIsThrownIfCarIsAlreadyTaken() throws ConstraintsViolationException, EntityNotFoundException, CarAlreadyInUseException
    {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(TestUtils.VALID_ONLINE_DRIVER));

        DriverDO driverPossessingCar = new DriverDO();
        driverPossessingCar.setId(2L);
        CarDO carAlreadyTakenByAnotherDrivernew = new CarDO(1L, ZonedDateTime.now(),
            "B123", 5, false, 12, EngineType.DIESEL, "BMW", "M2", driverPossessingCar);

        when(carService.find(10L)).thenReturn(Optional.of(carAlreadyTakenByAnotherDrivernew));

        driverService.selectCar(1L, 10L);

        verify(driverRepository, never()).save(any(DriverDO.class));
    }


    @Test
    public void thatCarAreNotSelectedAgainIfTheyAreAlreadyTakenByThisDriver() throws EntityNotFoundException, ConstraintsViolationException, CarAlreadyInUseException
    {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(TestUtils.VALID_ONLINE_DRIVER));

        DriverDO driverPossessingCar = new DriverDO();
        driverPossessingCar.setId(1L);
        CarDO carAlreadyTakenByThisDriver = new CarDO(1L, ZonedDateTime.now(),
            "B123", 5, false, 12, EngineType.DIESEL, "BMW", "M2", driverPossessingCar);

        when(carService.find(10L)).thenReturn(Optional.of(carAlreadyTakenByThisDriver));

        driverService.selectCar(1L, 10L);

        verify(driverRepository, never()).save(any(DriverDO.class));
    }


    @Test
    public void thatSearchingOnlyByUsernameWorks()
    {
        driverService.find("username", null);

        verify(driverRepository, times(1)).findByUsername("username");
    }


    @Test
    public void thatSearchingOnlyByOnlineStatusWorks()
    {
        driverService.find(null, OnlineStatus.ONLINE);

        verify(driverRepository, times(1)).findByOnlineStatus(OnlineStatus.ONLINE);
    }


    @Test
    public void thatSearchingByUsernameAndOnlineStatusWorks()
    {
        driverService.find("username", OnlineStatus.OFFLINE);

        verify(driverRepository, times(1)).findByUsernameAndOnlineStatus("username", OnlineStatus.OFFLINE);
    }


    @Test
    public void thatFindingAllDriversWorks()
    {
        driverService.find(null, null);

        verify(driverRepository, times(1)).findAll();
    }


    @Test(expected = EntityNotFoundException.class)
    public void thatDeleteDriverThrowsEntityNotFoundWhenDriverWithProvidedIdDoesntExist() throws EntityNotFoundException
    {
        when(driverRepository.findById(1L)).thenReturn(Optional.empty());

        driverService.delete(1L);
    }


    @Test(expected = EntityNotFoundException.class)
    public void thatUpdateDriverPartiallyThrowsEntityNotFoundWhenDriverWithProvidedIdDoesntExist() throws EntityNotFoundException
    {
        when(driverRepository.findById(1L)).thenReturn(Optional.empty());

        driverService.updatePartially(1L, null);
    }


    @Test(expected = EntityNotFoundException.class)
    public void thatSearchingDriverByLicensePlateOfUnexistingCarThrowsException() throws EntityNotFoundException
    {
        when(carService.findByLicensePlate("123")).thenReturn(Optional.empty());

        driverService.findByLicensePlate("123");
    }


    @Test(expected = EntityNotFoundException.class)
    public void thatExceptionIsThrownIfDriverForDoesntExistWhenSearchedByCarsLicensePlate() throws EntityNotFoundException
    {
        when(carService.findByLicensePlate("123")).thenReturn(Optional.of(TestUtils.TEST_CAR));

        driverService.findByLicensePlate("123");

    }
}
