package de.br.aff.controller;

import de.br.aff.controller.mapper.DriverMapper;
import de.br.aff.datatransferobject.DriverDTO;
import de.br.aff.exception.CarAlreadyInUseException;
import de.br.aff.exception.ConstraintsViolationException;
import de.br.aff.exception.EntityNotFoundException;
import de.br.aff.service.driver.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * All operations with a driver will be routed by this controller.
 * <p/>
 */
@RestController
@RequestMapping("v1/drivers")
@RequiredArgsConstructor
public class DriverController
{
    private final DriverService driverService;


    @GetMapping("/{driverId}")
    public DriverDTO findDriver(@PathVariable long driverId) throws EntityNotFoundException
    {
        return DriverMapper.makeDriverDTO(driverService.find(driverId));
    }


    @PutMapping("/{driverId}/car/{carId}")
    public ResponseEntity selectCar(@PathVariable long driverId, @PathVariable long carId) throws EntityNotFoundException, CarAlreadyInUseException, ConstraintsViolationException
    {
        driverService.selectCar(driverId, carId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @DeleteMapping("/{driverId}/car")
    public ResponseEntity deselectCar(@PathVariable long driverId) throws EntityNotFoundException
    {
        driverService.deselectCar(driverId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
