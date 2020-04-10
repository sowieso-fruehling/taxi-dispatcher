package de.br.aff.controller;

import de.br.aff.controller.mapper.CarMapper;
import de.br.aff.datatransferobject.CarDTO;
import de.br.aff.domainobject.CarDO;
import de.br.aff.exception.ConstraintsViolationException;
import de.br.aff.exception.EntityNotFoundException;
import de.br.aff.service.car.CarService;
import de.br.aff.util.Utils;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("v1/cars")
@RequiredArgsConstructor
public class CarController
{
    private final CarService carService;


    @GetMapping("{carId}")
    public CarDTO getCar(@PathVariable long carId) throws EntityNotFoundException
    {
        return CarMapper.makeCarDTO(carService.find(carId).orElseThrow(() -> new EntityNotFoundException("Car with this id not found")));
    }


    @GetMapping
    public List<CarDTO> getAllCars()
    {
        return CarMapper.makeCarDTOList(carService.findAll());
    }


    @PostMapping
    public ResponseEntity create(@Valid @RequestBody CarDTO carDTO) throws ConstraintsViolationException
    {
        CarDO carDO = CarMapper.makeCarDO(carDTO);

        carDO = carService.create(carDO);

        return ResponseEntity.status(CREATED)
            .header(HttpHeaders.LOCATION, Utils.getCarResourceLocation(carDO)).build();
    }


    @PutMapping("{carId}")
    public ResponseEntity updateCar(@PathVariable Long carId, @Valid @RequestBody CarDTO carDTOSent) throws EntityNotFoundException, ConstraintsViolationException
    {
        CarDO carDOSent = CarMapper.makeCarDO(carDTOSent);

        carService.update(carId, carDOSent);

        return ResponseEntity.status(NO_CONTENT).build();
    }


    @DeleteMapping("{carId}")
    public ResponseEntity deleteCar(@PathVariable Long carId) throws EntityNotFoundException
    {
        carService.delete(carId);

        return ResponseEntity.status(NO_CONTENT).build();
    }
}
