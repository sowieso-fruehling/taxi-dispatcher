package de.br.aff.controller;

import de.br.aff.controller.mapper.CarMapper;
import de.br.aff.dataaccessobject.CarRepository;
import de.br.aff.dataaccessobject.DriverRepository;
import de.br.aff.datatransferobject.DriverDTO;
import de.br.aff.domainobject.CarDO;
import de.br.aff.domainobject.DriverDO;
import de.br.aff.domainvalue.GeoCoordinate;
import de.br.aff.domainvalue.OnlineStatus;
import de.br.aff.utils.TestUtils;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class InternalDriverControllerIntegrationTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private CarRepository carRepository;


    @Before
    public void init()
    {
        carRepository.deleteAll();
        driverRepository.deleteAll();
    }


    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void thatSearchingForDriversProperlyWorks() throws Exception
    {
        DriverDO offlineDriver = new DriverDO("xyz2", "zyx2", null);
        offlineDriver.setOnlineStatus(OnlineStatus.OFFLINE);

        driverRepository.save(TestUtils.VALID_ONLINE_DRIVER);
        driverRepository.save(offlineDriver);

        mockMvc.perform(MockMvcRequestBuilders
            .get("/internal/v1/drivers"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", Matchers.hasSize(2)))
            .andExpect(jsonPath("[*].username", Matchers.everyItem(Matchers.startsWith("xyz"))));

        mockMvc.perform(MockMvcRequestBuilders
            .get("/internal/v1/drivers?onlinestatus=offline"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", Matchers.hasSize(1)))
            .andExpect(jsonPath("[*].username", Matchers.everyItem(Matchers.is("xyz2"))));

        mockMvc.perform(MockMvcRequestBuilders
            .get("/internal/v1/drivers?username=xyz2"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", Matchers.hasSize(1)))
            .andExpect(jsonPath("[*].username", Matchers.everyItem(Matchers.is("xyz2"))));

        mockMvc.perform(MockMvcRequestBuilders
            .get("/internal/v1/drivers?username=xyz2&onlinestatus=online"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", Matchers.hasSize(0)));

    }


    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void thatCRUDOperationsOnDriverProperlyWork() throws Exception
    {
        DriverDTO driver = DriverDTO.builder()
            .id(12L)
            .username("user")
            .password("pass")
            .coordinate(new GeoCoordinate(1L, 2L))
            .build();

        String location = mockMvc.perform(MockMvcRequestBuilders
            .post("/internal/v1/drivers/").with(csrf())
            .content(TestUtils.toJson(driver))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(header().string("LOCATION", Matchers.startsWith("http://localhost/v1/drivers/")))
            .andReturn().getResponse().getHeader("LOCATION");

        String internalEndpoint = "/internal" + location.substring("http://localhost" .length());

        mockMvc.perform(MockMvcRequestBuilders
            .get("/internal/v1/drivers"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", Matchers.hasSize(1)))
            .andExpect(jsonPath("[*].username", Matchers.everyItem(Matchers.is("user"))))
            .andExpect(jsonPath("[*].password", Matchers.everyItem(Matchers.is("pass"))))
            .andExpect(jsonPath("[*].coordinate.point.y", Matchers.everyItem(Matchers.is(1L))));

        DriverDTO newDriverValues = DriverDTO.builder().username("patcheduser").coordinate(new GeoCoordinate(2L, 3L)).build();

        mockMvc.perform(MockMvcRequestBuilders
            .patch(internalEndpoint).with(csrf())
            .content(TestUtils.toJson(newDriverValues))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        mockMvc.perform(MockMvcRequestBuilders
            .get("/internal/v1/drivers"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", Matchers.hasSize(1)))
            .andExpect(jsonPath("[*].username", Matchers.everyItem(Matchers.is("patcheduser"))))
            .andExpect(jsonPath("[*].coordinate.point.y", Matchers.everyItem(Matchers.is(3L))));

        mockMvc.perform(MockMvcRequestBuilders
            .delete(internalEndpoint).with(csrf()))
            .andExpect(status().isNoContent());

        mockMvc.perform(MockMvcRequestBuilders
            .get("/internal/v1/drivers"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }


    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void thatSearchingDriversByCarCharacteristicWorks() throws Exception
    {
        CarDO D123_33 = CarMapper.makeCarDO(TestUtils.VALID_CAR_DTO);
        D123_33 = carRepository.save(D123_33);

        CarDO X111_33 = CarMapper.makeCarDO(TestUtils.VALID_CAR_DTO.toBuilder().licensePlate("X111").build());
        X111_33 = carRepository.save(X111_33);

        DriverDO firstImportedDriver = new DriverDO("1", "2", new GeoCoordinate(88L, 88L));
        firstImportedDriver = driverRepository.save(firstImportedDriver);
        firstImportedDriver.setCar(D123_33);
        firstImportedDriver = driverRepository.save(firstImportedDriver);

        DriverDO secondImportedDriver = new DriverDO("x", "y", new GeoCoordinate(1L, 1L));
        secondImportedDriver = driverRepository.save(secondImportedDriver);
        secondImportedDriver.setCar(X111_33);
        secondImportedDriver = driverRepository.save(secondImportedDriver);

        //by rating

        mockMvc.perform(MockMvcRequestBuilders
            .get("/internal/v1/cars/rating/" + D123_33.getRating() + "/drivers"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", Matchers.hasSize(2)))
            .andExpect((jsonPath("$[*].username", Matchers.containsInAnyOrder(firstImportedDriver.getUsername(), secondImportedDriver.getUsername()))));

        mockMvc.perform(MockMvcRequestBuilders
            .get("/internal/v1/cars/rating/44/drivers"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", Matchers.hasSize(0)));

        //by license plate
        mockMvc.perform(MockMvcRequestBuilders
            .get("/internal/v1/cars/licenseplate/X111/drivers"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect((jsonPath("username", Matchers.is(secondImportedDriver.getUsername()))));

        mockMvc.perform(MockMvcRequestBuilders
            .get("/internal/v1/cars/licenseplate/YTTT/drivers"))
            .andExpect(status().isNotFound()); //car dont exist

        CarDO XYZ_33 = CarMapper.makeCarDO(TestUtils.VALID_CAR_DTO.toBuilder().licensePlate("XYZ").build());
        XYZ_33 = carRepository.save(XYZ_33);

        mockMvc.perform(MockMvcRequestBuilders
            .get("/internal/v1/cars/licenseplate/XYZ/drivers"))
            .andExpect(status().isNotFound()); //driver doesnt exist

    }


    @Test
    public void thatInternalEndpointRequiresAuthentication() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
            .get("/internal/v1/drivers"))
            .andExpect(status().isUnauthorized());
    }


    @WithMockUser
    @Test
    public void thatInternalEndpointIsNotAccessibleWithUserRole() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
            .get("/internal/v1/drivers"))
            .andExpect(status().isForbidden());
    }


    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void thatInternalEndpointIsAccessibleAdminRole() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
            .get("/internal/v1/drivers"))
            .andExpect(status().isOk());
    }
}
