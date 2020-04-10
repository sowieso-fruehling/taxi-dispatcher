package de.br.aff.controller;

import de.br.aff.domainvalue.OnlineStatus;
import de.br.aff.service.driver.DriverService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = InternalDriverController.class)
@WithMockUser(username = "admin")
public class InternalDriverControllerUnitTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DriverService driverService;


    @Test
    public void thatGettingDriversByOnlineStatusWorks() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
            .get("/internal/v1/drivers?onlinestatus=ONLINE"))
            .andExpect(status().isOk());

        verify(driverService, times(1)).find(null, OnlineStatus.ONLINE);
    }


    @Test
    public void thatGettingDriversByOnlineStatusWhenONlineStatusIsWrittenLowercaseWorks() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
            .get("/internal/v1/drivers?onlinestatus=online"))
            .andExpect(status().isOk());

        verify(driverService, times(1)).find(null, OnlineStatus.ONLINE);
    }


    @Test
    public void thatNonExistingOnlineStatusResultsWithBedRequest() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
            .get("/internal/v1/drivers?onlinestatus=smthnonexisting"))
            .andExpect(status().isBadRequest());

        verify(driverService, never()).find(eq(null), any(OnlineStatus.class));
    }


    @Test
    public void thatGettingDriversByUsernameWorks() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
            .get("/internal/v1/drivers?username=smth"))
            .andExpect(status().isOk());

        verify(driverService, times(1)).find("smth", null);
    }


    @Test
    public void thatGettingDriversByUsernameAndOnlineStatusWorks() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
            .get("/internal/v1/drivers?username=smth&onlinestatus=online"))
            .andExpect(status().isOk());

        verify(driverService, times(1)).find("smth", OnlineStatus.ONLINE);
    }


    @Test
    public void thatGettingDriversWorks() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
            .get("/internal/v1/drivers"))
            .andExpect(status().isOk());

        verify(driverService, times(1)).find(null, null);
    }


}
