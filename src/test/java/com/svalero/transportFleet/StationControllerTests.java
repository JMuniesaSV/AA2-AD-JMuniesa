package com.svalero.transportFleet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.transportFleet.controller.StationController;
import com.svalero.transportFleet.domain.Station;
import com.svalero.transportFleet.dto.StationOutDto;
import com.svalero.transportFleet.exception.StationNotFoundException;
import com.svalero.transportFleet.service.StationService;
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

@WebMvcTest(StationController.class)
public class StationControllerTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ModelMapper modelMapper;

    @MockitoBean
    private StationService stationService;

    @Test
    public void testGetAll() throws Exception {
        List<StationOutDto> stationOutDto = List.of(
                new StationOutDto(1, "Estación Central", "Base principal", "Logística", "Calle 1", 50001,
                        LocalDate.now(), true, 0.0, 0.0),
                new StationOutDto(2, "Punto Norte", "Transbordo", "Carga", "Calle 2", 50002, LocalDate.now(), true, 0.0,
                        0.0));

        when(stationService.findAll(null, null, null)).thenReturn(stationOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/stations")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<StationOutDto> stationsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        assertEquals(2, stationsListResponse.size());
    }

    @Test
    public void testGetAllByCategory() throws Exception {
        List<StationOutDto> stationOutDto = List.of(
                new StationOutDto(1, "Estación Central", "Base principal", "Logística", "Calle 1", 50001,
                        LocalDate.now(), true, 0.0, 0.0));

        when(stationService.findAll("Logística", null, null)).thenReturn(stationOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/stations")
                .queryParam("category", "Logística")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<StationOutDto> response = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        assertEquals(1, response.size());
    }

    @Test
    public void testGetAllByPostalCode() throws Exception {
        List<StationOutDto> stationOutDto = List.of(new StationOutDto());
        when(stationService.findAll(null, null, 50001)).thenReturn(stationOutDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/stations")
                .queryParam("postalCode", "50012")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllByDisabledAccess() throws Exception {
        List<StationOutDto> stationOutDto = List.of(new StationOutDto());
        when(stationService.findAll(null, true, null)).thenReturn(stationOutDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/stations")
                .queryParam("disabledAccess", "true")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetStationById() throws Exception {
        Station station = new Station();
        station.setId(3);
        station.setName("Estación Especial");

        when(stationService.findById(3L)).thenReturn(station);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/stations/3")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Station response = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        assertEquals("Estación Especial", response.getName());
    }

    @Test
    public void testGetStationByIdNotFound() throws Exception {
        when(stationService.findById(99L)).thenThrow(new StationNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/stations/99")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddStation() throws Exception {
        Station stationBody = new Station();
        stationBody.setName("Nueva Estación");
        stationBody.setCategory("Carga");
        stationBody.setPostalCode(50001);

        when(stationService.add(any(Station.class))).thenReturn(stationBody);

        mockMvc.perform(MockMvcRequestBuilders.post("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stationBody)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testAddStationValidationError400() throws Exception {
        Station notValidStation = new Station(); // Vacío, fallará validación @NotNull

        mockMvc.perform(MockMvcRequestBuilders.post("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notValidStation)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testModifyStation() throws Exception {
        Station stationBody = new Station();
        stationBody.setName("Nombre Modificado");
        stationBody.setCategory("Carga");
        stationBody.setPostalCode(50001);

        when(stationService.modify(eq(1L), any(Station.class))).thenReturn(stationBody);

        mockMvc.perform(MockMvcRequestBuilders.put("/stations/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(stationBody)))
                .andExpect(status().isOk());
    }

    @Test
    public void testModifyStationNotFound() throws Exception {
        Station stationBody = new Station();
        stationBody.setName("Nombre");
        stationBody.setCategory("Carga");
        stationBody.setPostalCode(50001);

        when(stationService.modify(eq(99L), any(Station.class))).thenThrow(new StationNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put("/stations/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stationBody)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteStation() throws Exception {
        doNothing().when(stationService).delete(5L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/stations/5"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteStationNotFound() throws Exception {
        doThrow(new StationNotFoundException()).when(stationService).delete(5L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/stations/5"))
                .andExpect(status().isNotFound());
    }
}
