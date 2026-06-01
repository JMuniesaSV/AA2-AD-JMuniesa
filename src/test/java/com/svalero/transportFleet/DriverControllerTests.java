package com.svalero.transportFleet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.transportFleet.controller.DriverController;
import com.svalero.transportFleet.domain.Driver;
import com.svalero.transportFleet.dto.DriverOutDto;
import com.svalero.transportFleet.exception.DriverNotFoundException;
import com.svalero.transportFleet.service.DriverService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DriverController.class)
public class DriverControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DriverService driverService;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAll() throws Exception {
        List<DriverOutDto> driverOutDto = List.of(
                new DriverOutDto(1, "Carlos", "Sainz", "C", LocalDate.now(), "F1", 20, 1.75f, true),
                new DriverOutDto(2, "Fernando", "Alonso", "C", LocalDate.now(), "F1", 22, 1.70f, true)
        );

        when(driverService.findAll(null, null, null)).thenReturn(driverOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/drivers")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<DriverOutDto> response = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(2, response.size());
    }

    @Test
    public void testGetAllByType() throws Exception {
        List<DriverOutDto> driverOutDto = List.of(new DriverOutDto());
        when(driverService.findAll("F1", null, null)).thenReturn(driverOutDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/drivers")
                        .queryParam("type", "F1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk());
    }

    @Test
    public void testGetAllByActiveTrue() throws Exception {
        List<DriverOutDto> driverOutDto = List.of(new DriverOutDto());
        when(driverService.findAll(null, true, null)).thenReturn(driverOutDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/drivers")
                        .queryParam("active", "true")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk());
    }

    @Test
    public void testGetAllByOrderByExperienceDesc() throws Exception {
        List<DriverOutDto> driverOutDto = List.of(new DriverOutDto());
        when(driverService.findAll(null, null, true)).thenReturn(driverOutDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/drivers")
                        .queryParam("orderByExperience", "true")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk());
    }

    @Test
    public void testGetDriverById() throws Exception {
        Driver driver = new Driver();
        driver.setName("Carlos");

        when(driverService.findDriverById(1L)).thenReturn(driver);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/drivers/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Driver response = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals("Carlos", response.getName());
    }

    @Test
    public void testGetDriverByIdNotFound() throws Exception {
        when(driverService.findDriverById(99L)).thenThrow(new DriverNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/drivers/99")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isNotFound());
    }

    @Test
    public void testAddDriver() throws Exception {
        Driver driver = new Driver();
        driver.setName("Fernando");
        driver.setSurname("Alonso");
        driver.setLicenseCategory("C");
        driver.setType("Nacional");

        when(driverService.add(any(Driver.class))).thenReturn(driver);

        mockMvc.perform(MockMvcRequestBuilders.post("/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driver)))
                        .andExpect(status().isCreated());
    }

    @Test
    public void testAddDriverValidationError400() throws Exception {
        Driver notValidDriver = new Driver();

        mockMvc.perform(MockMvcRequestBuilders.post("/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValidDriver)))
                        .andExpect(status().isBadRequest());
    }

    @Test
    public void testModifyDriver() throws Exception {
        Driver driverRequest = new Driver();
        driverRequest.setName("Carlos");
        driverRequest.setSurname("Sainz");
        driverRequest.setLicenseCategory("C");
        driverRequest.setType("Nacional");

        when(driverService.modify(eq(1L), any(Driver.class))).thenReturn(driverRequest);

        mockMvc.perform(MockMvcRequestBuilders.put("/drivers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverRequest)))
                        .andExpect(status().isOk());
    }

    @Test
    public void testModifyDriverNotFound() throws Exception {
        Driver driverRequest = new Driver();
        driverRequest.setName("Nombre");
        driverRequest.setSurname("Apellido");
        driverRequest.setLicenseCategory("C");
        driverRequest.setType("Nacional");

        when(driverService.modify(eq(99L), any(Driver.class))).thenThrow(new DriverNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put("/drivers/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverRequest)))
                        .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteDriver() throws Exception {
        doNothing().when(driverService).delete(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/drivers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteDriverNotFound() throws Exception {
        doThrow(new DriverNotFoundException()).when(driverService).delete(99L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/drivers/99"))
                .andExpect(status().isNotFound());
    }
}
