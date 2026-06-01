package com.svalero.transportFleet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.transportFleet.controller.RouteController;
import com.svalero.transportFleet.domain.Driver;
import com.svalero.transportFleet.domain.Route;
import com.svalero.transportFleet.domain.Station;
import com.svalero.transportFleet.dto.RouteInDto;
import com.svalero.transportFleet.dto.RouteModifyInDto;
import com.svalero.transportFleet.dto.RouteOutDto;
import com.svalero.transportFleet.exception.RouteNotFoundException;
import com.svalero.transportFleet.exception.StationNotFoundException;
import com.svalero.transportFleet.service.DriverService;
import com.svalero.transportFleet.service.RouteService;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RouteController.class)
public class RouteControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RouteService routeService;
    @MockitoBean
    private DriverService driverService;
    @MockitoBean
    private StationService stationService;
    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAll() throws Exception {
        List<RouteOutDto> routes = List.of(
                new RouteOutDto(1, "Ruta A", "Desc", LocalDate.now(), "Express", 50, 100.0f, true),
                new RouteOutDto(2, "Ruta B", "Desc", LocalDate.now(), "Nacional", 40, 80.0f, true)
        );

        when(routeService.findAll(null, null, null)).thenReturn(routes);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/routes")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<RouteOutDto> response = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(2, response.size());
    }

    @Test
    public void testGetAllByCategory() throws Exception {
        List<RouteOutDto> routes = List.of(new RouteOutDto());
        when(routeService.findAll("Express", null, null)).thenReturn(routes);

        mockMvc.perform(MockMvcRequestBuilders.get("/routes")
                .queryParam("category", "Express")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllByStationName() throws Exception {
        List<RouteOutDto> routes = List.of(new RouteOutDto());
        when(routeService.findAll(null, "Estación Central", null)).thenReturn(routes);

        mockMvc.perform(MockMvcRequestBuilders.get("/routes")
                .queryParam("stationName", "Estación Central")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllByPrice() throws Exception {
        List<RouteOutDto> routes = List.of(new RouteOutDto());
        when(routeService.findAll(null, null, 100.0f)).thenReturn(routes);

        mockMvc.perform(MockMvcRequestBuilders.get("/routes")
                .queryParam("price", "100.0")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetRouteById() throws Exception {
        Route route = new Route();
        route.setName("Ruta 1");

        when(routeService.findById(1L)).thenReturn(route);

        mockMvc.perform(MockMvcRequestBuilders.get("/routes/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk());
    }

    @Test
    public void testGetRouteByIdNotFound() throws Exception {
        when(routeService.findById(99L)).thenThrow(new RouteNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/routes/99")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isNotFound());
    }

    @Test
    public void testAddRoute() throws Exception {
        RouteInDto routeInDto = new RouteInDto("Nueva", "Desc", LocalDate.now(), "Cat", 10, 100.0f, 1L, List.of(1L));
        when(stationService.findById(1L)).thenReturn(new Station());
        when(driverService.findAllDriversById(anyList())).thenReturn(new ArrayList<>());
        when(routeService.add(any(Station.class), any(RouteInDto.class), anyList())).thenReturn(new Route());

        mockMvc.perform(MockMvcRequestBuilders.post("/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(routeInDto)))
                        .andExpect(status().isCreated());
    }

    @Test
    public void testAddRouteValidationError400() throws Exception {
        RouteInDto notValidDto = new RouteInDto(); // Fallará por @NotNull en Name y Category

        mockMvc.perform(MockMvcRequestBuilders.post("/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValidDto)))
                        .andExpect(status().isBadRequest());
    }

    @Test
    public void testModifyRoute() throws Exception {
        RouteModifyInDto modifyDto = new RouteModifyInDto("Mod", "Desc", LocalDate.now(), "Cat", 10, 100.0f, true, 1L, List.of(1L));
        when(stationService.findById(1L)).thenReturn(new Station());
        when(driverService.findAllDriversById(anyList())).thenReturn(new ArrayList<>());
        when(routeService.modify(eq(1L), any(RouteModifyInDto.class), any(Station.class), anyList())).thenReturn(new Route());

        mockMvc.perform(MockMvcRequestBuilders.put("/routes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyDto)))
                        .andExpect(status().isOk());
    }

    @Test
    public void testModifyRouteNotFound() throws Exception {
        RouteModifyInDto modifyDto = new RouteModifyInDto("Mod", "Desc", LocalDate.now(), "Cat", 10, 100.0f, true, 1L, List.of(1L));
        when(stationService.findById(1L)).thenReturn(new Station());
        when(driverService.findAllDriversById(anyList())).thenReturn(new ArrayList<>());
        when(routeService.modify(eq(99L), any(RouteModifyInDto.class), any(Station.class), anyList())).thenThrow(new RouteNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put("/routes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyDto)))
                        .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteRoute() throws Exception {
        doNothing().when(routeService).delete(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/routes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteRouteNotFound() throws Exception {
        doThrow(new RouteNotFoundException()).when(routeService).delete(99L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/routes/99"))
                .andExpect(status().isNotFound());
    }
}
