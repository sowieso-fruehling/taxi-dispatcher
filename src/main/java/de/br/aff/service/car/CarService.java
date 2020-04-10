package de.br.aff.service.car;

import de.br.aff.domainobject.CarDO;
import de.br.aff.exception.ConstraintsViolationException;
import de.br.aff.exception.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

public interface CarService
{

    Optional<CarDO> find(Long carId);

    List<CarDO> findAll();

    CarDO create(CarDO carDO) throws ConstraintsViolationException;

    void update(Long carId, CarDO carDOSent) throws EntityNotFoundException, ConstraintsViolationException;

    void delete(Long carId) throws EntityNotFoundException;

    Optional<CarDO> findByLicensePlate(String licensePlate);

    List<CarDO> findByRating(int rating);
}
