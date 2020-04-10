package de.br.aff.controller;

import de.br.aff.dataaccessobject.CarRepository;
import de.br.aff.datatransferobject.CarDTO;
import de.br.aff.service.car.CarService;
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
public class CarControllerIntegrationTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarService carService;

    @Autowired
    private CarRepository carRepository;


    @Before
    public void emptyCarTable()
    {
        carRepository.deleteAll();
    }


    @WithMockUser
    @Test
    public void thatCarIsSuccessfullyCreatedUpdatedAndRetrieved() throws Exception
    {
        String resourceLocation = mockMvc.perform(MockMvcRequestBuilders
            .post("/v1/cars").with(csrf())
            .content(TestUtils.toJson(TestUtils.VALID_CAR_DTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(header().string("LOCATION", Matchers.startsWith("http://localhost/v1/cars/")))
            .andReturn().getResponse().getHeader("LOCATION");

        resourceLocation = resourceLocation.substring("http://localhost" .length());

        mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/cars"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("[*].licensePlate", Matchers.hasItem(Matchers.is(TestUtils.VALID_CAR_DTO.getLicensePlate()))));

        CarDTO updater = TestUtils.VALID_CAR_DTO.toBuilder().build();
        updater.setSeatCount(18);

        mockMvc.perform(MockMvcRequestBuilders
            .put(resourceLocation).with(csrf())
            .content(TestUtils.toJson(updater))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        mockMvc.perform(MockMvcRequestBuilders
            .get(resourceLocation))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("seatCount", Matchers.is(updater.getSeatCount())));

        mockMvc.perform(MockMvcRequestBuilders
            .delete(resourceLocation).with(csrf()))
            .andExpect(status().isNoContent());

        mockMvc.perform(MockMvcRequestBuilders
            .get(resourceLocation))
            .andExpect(status().isNotFound());

    }


    @Test
    public void thatPublicEndpointRequiresAuthentication() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/cars/1"))
            .andExpect(status().isUnauthorized());
    }


    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void thatPublicEndpointIsAccessibleByAdmin() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/cars"))
            .andExpect(status().isOk());
    }

}
