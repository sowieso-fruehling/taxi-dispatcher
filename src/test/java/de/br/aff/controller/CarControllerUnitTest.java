package de.br.aff.controller;

import de.br.aff.controller.mapper.CarMapper;
import de.br.aff.datatransferobject.CarDTO;
import de.br.aff.domainobject.CarDO;
import de.br.aff.exception.EntityNotFoundException;
import de.br.aff.service.car.CarService;
import de.br.aff.utils.TestUtils;
import java.util.Arrays;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = CarController.class)
@WithMockUser
public class CarControllerUnitTest
{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;


    @Test
    public void thatGetCarEndpointWorksProperly() throws Exception
    {
        when(carService.find(1L)).thenReturn(Optional.of(TestUtils.TEST_CAR));

        mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/cars/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("licensePlate", Matchers.is(TestUtils.TEST_CAR.getLicensePlate())));
    }


    @Test
    public void thatGetAllCarsEndpointWorksProperly() throws Exception
    {
        when(carService.findAll()).thenReturn(Arrays.asList(TestUtils.TEST_CAR, TestUtils.TEST_CAR));

        mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/cars"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", Matchers.hasSize(2)))
            .andExpect(jsonPath("[*].licensePlate", Matchers.everyItem(Matchers.is(TestUtils.TEST_CAR.getLicensePlate()))));
    }


    @Test
    public void thatCreateEndpointWorksProperly() throws Exception
    {
        CarDO carDOReturned = CarMapper.makeCarDO(TestUtils.VALID_CAR_DTO);
        carDOReturned.setId(23L);

        when(carService.create(ArgumentMatchers.any(CarDO.class))).thenReturn(carDOReturned);

        mockMvc.perform(MockMvcRequestBuilders
            .post("/v1/cars").with(csrf())
            .content(TestUtils.toJson(TestUtils.VALID_CAR_DTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(header().string("LOCATION", "http://localhost/v1/cars/23"));
    }


    @Test
    public void thatCreateEndpointValidatesInput() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
            .post("/v1/cars").with(csrf())
            .content(TestUtils.toJson(TestUtils.CAR_DTO_WITHOUT_LICENSE_PLATE))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(carService, never()).create(ArgumentMatchers.any(CarDO.class));
    }


    @Test
    public void thatUpdateEndpointWorksProperly() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/cars/1").with(csrf())
            .content(TestUtils.toJson(TestUtils.VALID_CAR_DTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        verify(carService, times(1)).update(1L, CarMapper.makeCarDO(TestUtils.VALID_CAR_DTO));
    }


    @Test
    public void thatUpdateEndpointValidatesInput() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/cars/1").with(csrf())
            .content(TestUtils.toJson(TestUtils.CAR_DTO_WITHOUT_LICENSE_PLATE))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(carService, never()).update(ArgumentMatchers.anyLong(), ArgumentMatchers.any(CarDO.class));
    }


    @Test
    public void thatDeleteEndpointWorksProperly() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
            .delete("/v1/cars/1").with(csrf()))
            .andExpect(status().isNoContent());

        verify(carService, times(1)).delete(1L);
    }


    @Test
    public void thatEntityNotFoundExceptionIsGloballyHandled() throws Exception
    {
        Mockito.doThrow(new EntityNotFoundException("")).when(carService).delete(1L);

        mockMvc.perform(MockMvcRequestBuilders
            .delete("/v1/cars/1").with(csrf()))
            .andExpect(status().isNotFound());
    }


    @Test
    public void thatIllegalArgumentExceptionIsGloballyHandled() throws Exception
    {
        when(carService.create(ArgumentMatchers.any(CarDO.class))).thenThrow(IllegalArgumentException.class);

        CarDTO carDTOWithInvalidEngineType = TestUtils.VALID_CAR_DTO.toBuilder().build();
        carDTOWithInvalidEngineType.setEngineType("nonexisting");

        mockMvc.perform(MockMvcRequestBuilders
            .post("/v1/cars").with(csrf())
            .content(TestUtils.toJson(carDTOWithInvalidEngineType))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }
}
