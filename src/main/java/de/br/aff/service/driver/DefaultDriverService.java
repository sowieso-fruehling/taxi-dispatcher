package de.br.aff.service.driver;

import de.br.aff.dataaccessobject.DriverRepository;
import de.br.aff.domainobject.CarDO;
import de.br.aff.domainobject.DriverDO;
import de.br.aff.domainvalue.GeoCoordinate;
import de.br.aff.domainvalue.OnlineStatus;
import de.br.aff.exception.CarAlreadyInUseException;
import de.br.aff.exception.ConstraintsViolationException;
import de.br.aff.exception.EntityNotFoundException;
import de.br.aff.service.car.CarService;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to encapsulate the link between DAO and controller and to have business logic for some driver specific things.
 * <p/>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultDriverService implements DriverService
{

    private final DriverRepository driverRepository;
    private final CarService carService;


    /**
     * Selects a driver by id.
     *
     * @param driverId
     * @return found driver
     * @throws EntityNotFoundException if no driver with the given id was found.
     */
    @Override
    public DriverDO find(Long driverId) throws EntityNotFoundException
    {
        return findDriverChecked(driverId);
    }


    /**
     * Find all drivers by online status or username or both.
     */
    @Override
    public List<DriverDO> find(String username, OnlineStatus onlineStatus)
    {
        if (username != null && onlineStatus != null)
        {
            return driverRepository.findByUsernameAndOnlineStatus(username, onlineStatus);
        }

        if (onlineStatus != null)
        {
            return driverRepository.findByOnlineStatus(onlineStatus);
        }

        if (username != null)
        {
            return driverRepository.findByUsername(username);
        }

        return (List<DriverDO>) driverRepository.findAll();

    }


    /**
     * Creates a new driver.
     *
     * @param driverDO
     * @return
     * @throws ConstraintsViolationException if a driver already exists with the given username, ... .
     */
    @Override
    public DriverDO create(DriverDO driverDO) throws ConstraintsViolationException
    {
        DriverDO driver;
        try
        {
            driver = driverRepository.save(driverDO);
        }
        catch (DataIntegrityViolationException e)
        {
            log.warn("ConstraintsViolationException while creating a driver: {}", driverDO, e);
            throw new ConstraintsViolationException(e.getMessage());
        }
        return driver;
    }


    /**
     * Deletes an existing driver by id.
     *
     * @param driverId
     * @throws EntityNotFoundException if no driver with the given id was found.
     */
    @Override
    @Transactional
    public void delete(Long driverId) throws EntityNotFoundException
    {
        DriverDO driverDO = findDriverChecked(driverId);
        driverDO.setDeleted(true);
    }


    @Override
    public void selectCar(long driverId, long carId) throws EntityNotFoundException, ConstraintsViolationException, CarAlreadyInUseException
    {
        DriverDO driver = find(driverId);

        if (OnlineStatus.OFFLINE == driver.getOnlineStatus())
        {
            throw new ConstraintsViolationException("Only ONLINE drivers can select cars");
        }

        CarDO carToSelect = carService.find(carId).orElseThrow(() -> new ConstraintsViolationException("Car to be assigned not found"));

        if (carToSelect.getDriver() != null)
        {
            if (carToSelect.getDriver().getId() == driverId)
            {
                return; //put is idempotent
            }
            else
            {
                throw new CarAlreadyInUseException("This car is already taken by another driver");
            }
        }

        driver.setCar(carToSelect);

        driverRepository.save(driver);
    }


    @Override
    public void deselectCar(long driverId) throws EntityNotFoundException
    {
        DriverDO driver = find(driverId);

        if (driver.getCar() == null)
        {
            return;
        }

        driver.setCar(null);

        driverRepository.save(driver);
    }


    @Override
    public void updatePartially(long driverId, DriverDO newDriverData) throws EntityNotFoundException
    {
        DriverDO existingDriver = find(driverId);

        if (newDriverData.getUsername() != null)
        {
            existingDriver.setUsername(newDriverData.getUsername());
        }

        if (newDriverData.getPassword() != null)
        {
            existingDriver.setPassword(newDriverData.getPassword());
        }

        if (newDriverData.getOnlineStatus() != null)
        {
            existingDriver.setOnlineStatus(newDriverData.getOnlineStatus());
        }

        if (newDriverData.getCoordinate() != null)
        {
            updateLocation(existingDriver, newDriverData.getCoordinate());
        }

        driverRepository.save(existingDriver);
    }


    @Override
    public List<DriverDO> findByCarRating(int carRating)
    {
        List<CarDO> cars = carService.findByRating(carRating);

        List<DriverDO> drivers = new ArrayList<>();

        cars.forEach(car -> {
            if (car.getDriver() != null)
            {
                drivers.add(car.getDriver());
            }

        });

        return drivers;
    }


    @Override
    public DriverDO findByLicensePlate(String licensePlate) throws EntityNotFoundException
    {

        CarDO car = carService.findByLicensePlate(licensePlate).orElseThrow(() -> new EntityNotFoundException("car with license plate submitted doesnt exist"));

        if (car.getDriver() == null)
        {
            throw new EntityNotFoundException("car with license plate submitted doesnt have driver assigned");
        }

        return car.getDriver();
    }


    private DriverDO findDriverChecked(Long driverId) throws EntityNotFoundException
    {
        return driverRepository.findById(driverId)
            .orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: " + driverId));
    }


    private void updateLocation(DriverDO driver, GeoCoordinate coordinate)
    {
        driver.setCoordinate(coordinate);
        driver.setDateCoordinateUpdated(ZonedDateTime.now());
    }

}
