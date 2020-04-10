package de.br.aff.service.driver;

import de.br.aff.domainobject.DriverDO;
import de.br.aff.domainvalue.OnlineStatus;
import de.br.aff.exception.CarAlreadyInUseException;
import de.br.aff.exception.ConstraintsViolationException;
import de.br.aff.exception.EntityNotFoundException;
import java.util.List;

public interface DriverService
{

    DriverDO find(Long driverId) throws EntityNotFoundException;

    List<DriverDO> find(String username, OnlineStatus onlineStatus);

    DriverDO create(DriverDO driverDO) throws ConstraintsViolationException;

    void delete(Long driverId) throws EntityNotFoundException;

    void selectCar(long driverId, long carId) throws EntityNotFoundException, ConstraintsViolationException, CarAlreadyInUseException;

    void deselectCar(long driverId) throws EntityNotFoundException;

    void updatePartially(long driverId, DriverDO driverDO) throws EntityNotFoundException;

    List<DriverDO> findByCarRating(int carRating);

    DriverDO findByLicensePlate(String licensePlate) throws EntityNotFoundException;
}
