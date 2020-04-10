package de.br.aff.controller;

import de.br.aff.exception.CarAlreadyInUseException;
import de.br.aff.exception.ConstraintsViolationException;
import de.br.aff.exception.EntityNotFoundException;
import de.br.aff.service.driver.DriverService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = DriverController.class)
@WithMockUser
public class DriverControllerUnitTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DriverService driverService;


    @Test
    public void thatSelectingCarWorksProperly() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/drivers/1/car/1").with(csrf()))
            .andExpect(status().isNoContent());

        verify(driverService, times(1)).selectCar(1L, 1L);
    }


    @Test
    public void thatDriverIdAndCarIdHaveToBeNumbers() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/drivers/qw/car/qw").with(csrf()))
            .andExpect(status().isBadRequest());

        verify(driverService, never()).selectCar(anyLong(), anyLong());
    }


    @Test
    public void thatDeselectingCarWorksProperly() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
            .delete("/v1/drivers/1/car").with(csrf()))
            .andExpect(status().isNoContent());

        verify(driverService, times(1)).deselectCar(1L);
    }


    @Test
    public void thatConstraintsViolationExceptionIsMappedToBadRequest() throws Exception
    {
        Mockito.doThrow(new ConstraintsViolationException("")).when(driverService).selectCar(anyLong(), anyLong());

        mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/drivers/4/car/11").with(csrf()))
            .andExpect(status().isBadRequest());
    }


    @Test
    public void thatCarAlreadyInUseExceptionIsMappedToBadRequest() throws Exception
    {
        Mockito.doThrow(new CarAlreadyInUseException("")).when(driverService).selectCar(anyLong(), anyLong());

        mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/drivers/4/car/11").with(csrf()))
            .andExpect(status().isBadRequest());
    }


    @Test
    public void thatEntityNotFoundExceptionIsMappedToNotFound() throws Exception
    {
        Mockito.doThrow(new EntityNotFoundException("")).when(driverService).selectCar(anyLong(), anyLong());

        mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/drivers/4/car/11").with(csrf()))
            .andExpect(status().isNotFound());
    }
}
