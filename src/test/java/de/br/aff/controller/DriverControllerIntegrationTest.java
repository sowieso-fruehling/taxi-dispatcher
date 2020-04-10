package de.br.aff.controller;

import de.br.aff.controller.mapper.CarMapper;
import de.br.aff.dataaccessobject.CarRepository;
import de.br.aff.dataaccessobject.DriverRepository;
import de.br.aff.domainobject.CarDO;
import de.br.aff.domainobject.DriverDO;
import de.br.aff.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DriverControllerIntegrationTest
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
        driverRepository.deleteAll();
    }


    @WithMockUser
    @Test
    public void thatSelectingAndDeselectingCarProperlyWorks() throws Exception
    {
        CarDO savedCar = carRepository.save(CarMapper.makeCarDO(TestUtils.VALID_CAR_DTO));

        DriverDO savedDriver = driverRepository.save(TestUtils.VALID_ONLINE_DRIVER);

        mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/drivers/" + savedDriver.getId() + "/car/" + savedCar.getId()).with(csrf()))
            .andExpect(status().isNoContent());

        assert driverRepository.findById(savedDriver.getId()).get().getCar().getLicensePlate().equalsIgnoreCase(TestUtils.VALID_CAR_DTO.getLicensePlate());

        mockMvc.perform(MockMvcRequestBuilders
            .delete("/v1/drivers/" + savedDriver.getId() + "/car").with(csrf()))
            .andExpect(status().isNoContent());

        assert driverRepository.findById(savedDriver.getId()).get().getCar() == null;

    }


    @Test
    public void thatPublicEndpointRequiresAuthentication() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/drivers/1"))
            .andExpect(status().isUnauthorized());
    }

}
