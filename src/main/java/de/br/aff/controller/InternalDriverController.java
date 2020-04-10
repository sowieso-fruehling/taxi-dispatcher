package de.br.aff.controller;

import de.br.aff.controller.mapper.DriverMapper;
import de.br.aff.datatransferobject.DriverDTO;
import de.br.aff.domainobject.DriverDO;
import de.br.aff.domainvalue.OnlineStatus;
import de.br.aff.exception.ConstraintsViolationException;
import de.br.aff.exception.EntityNotFoundException;
import de.br.aff.service.driver.DriverService;
import de.br.aff.util.Utils;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("internal/v1")
@RequiredArgsConstructor
public class InternalDriverController
{
    private final DriverService driverService;


    @GetMapping("/drivers")
    public List<DriverDTO> findDrivers(
        @RequestParam(value = "onlinestatus", required = false) String onlineStatus,
        @RequestParam(value = "username", required = false) String username)
    {
        List<DriverDO> drivers = driverService.find(username, onlineStatus != null ? OnlineStatus.valueOf(onlineStatus.toUpperCase()) : null);

        drivers = filterDeletedDriversOut(drivers);

        return DriverMapper.makeDriverDTOList(drivers);
    }


    @GetMapping("/cars/rating/{carRating}/drivers")
    public List<DriverDTO> findDriversByCarRatings(@PathVariable int carRating)
    {
        List<DriverDO> drivers = driverService.findByCarRating(carRating);

        drivers = filterDeletedDriversOut(drivers);

        return DriverMapper.makeDriverDTOList(drivers);
    }


    @GetMapping("/cars/licenseplate/{licensePlate}/drivers")
    public DriverDTO findDriverByLicensePlate(@PathVariable String licensePlate) throws EntityNotFoundException
    {
        DriverDO driver = driverService.findByLicensePlate(licensePlate);

        return DriverMapper.makeDriverDTO(driver);
    }


    private List<DriverDO> filterDeletedDriversOut(List<DriverDO> drivers)
    {
        return drivers.stream().filter(driverDO ->
            !driverDO.getDeleted()
        ).collect(Collectors.toList());
    }


    @PostMapping("/drivers")
    public ResponseEntity createDriver(@Valid @RequestBody DriverDTO driverDTO) throws ConstraintsViolationException
    {
        DriverDO driverDO = DriverMapper.makeDriverDO(driverDTO);

        return ResponseEntity.status(CREATED)
            .header(HttpHeaders.LOCATION, Utils.getDriverResourceLocation(driverService.create(driverDO))).build();
    }


    @DeleteMapping("/drivers/{driverId}")
    public ResponseEntity deleteDriver(@PathVariable long driverId) throws EntityNotFoundException
    {
        driverService.delete(driverId);
        return ResponseEntity.status(NO_CONTENT).build();
    }


    @PatchMapping("drivers/{driverId}")
    public ResponseEntity patchDriver(@PathVariable long driverId, @RequestBody DriverDTO driverDTO) throws EntityNotFoundException
    {
        DriverDO driverDO = DriverMapper.makeDriverDO(driverDTO);
        driverService.updatePartially(driverId, driverDO);

        return ResponseEntity.status(NO_CONTENT).build();
    }


}
