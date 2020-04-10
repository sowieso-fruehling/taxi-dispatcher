package de.br.aff.service.car;

import de.br.aff.dataaccessobject.CarRepository;
import de.br.aff.domainobject.CarDO;
import de.br.aff.exception.ConstraintsViolationException;
import de.br.aff.exception.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultCarService implements CarService
{

    private final CarRepository carRepository;


    @Override
    public Optional<CarDO> find(Long carId)
    {
        return carRepository.findById(carId);
    }


    @Override
    public List<CarDO> findAll()
    {
        return carRepository.findAll();
    }


    @Override
    public CarDO create(CarDO carDO) throws ConstraintsViolationException
    {
        if (carRepository.findByLicensePlate(carDO.getLicensePlate()).isPresent())
        {
            throw new ConstraintsViolationException("Car with this license plate number already exists");
        }

        return carRepository.save(carDO);
    }


    @Override
    public Optional<CarDO> findByLicensePlate(String licensePlate)
    {
        return carRepository.findByLicensePlate(licensePlate);
    }


    @Override
    public List<CarDO> findByRating(int rating)
    {
        return carRepository.findByRating(rating);
    }


    @Override
    public void update(Long carId, CarDO carDOSent) throws EntityNotFoundException, ConstraintsViolationException
    {
        CarDO existingCar = find(carId).orElseThrow(() -> new EntityNotFoundException("Car with this id not found"));

        if (!existingCar.getLicensePlate().equalsIgnoreCase(carDOSent.getLicensePlate()))
        {
            throw new ConstraintsViolationException("License plate has to match");
        }

        carDOSent.setId(existingCar.getId());

        carRepository.save(carDOSent);
    }


    @Override
    public void delete(Long carId) throws EntityNotFoundException
    {
        Optional<CarDO> carToDelete = carRepository.findById(carId);

        if (!carToDelete.isPresent())
        {
            throw new EntityNotFoundException("Car with this id doesn't exist");
        }

        carRepository.delete(carToDelete.get());
    }
}
