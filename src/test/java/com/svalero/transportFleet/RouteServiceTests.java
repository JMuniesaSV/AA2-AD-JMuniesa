package com.svalero.transportFleet;

import com.svalero.transportFleet.domain.Driver;
import com.svalero.transportFleet.domain.Route;
import com.svalero.transportFleet.domain.Station;
import com.svalero.transportFleet.dto.RouteInDto;
import com.svalero.transportFleet.dto.RouteModifyInDto;
import com.svalero.transportFleet.dto.RouteOutDto;
import com.svalero.transportFleet.exception.RouteNotFoundException;
import com.svalero.transportFleet.repository.RouteRepository;
import com.svalero.transportFleet.service.RouteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RouteServiceTests {

    @InjectMocks
    private RouteService routeService;
    @Mock
    private RouteRepository routeRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testFindAll() {
        List<Route> mockRouteList = List.of(new Route());
        List<RouteOutDto> mockRouteOutDto = List.of(new RouteOutDto());

        when(routeRepository.findAll()).thenReturn(mockRouteList);
        when(modelMapper.map(mockRouteList, new TypeToken<List<RouteOutDto>>() {}.getType())).thenReturn(mockRouteOutDto);

        List<RouteOutDto> actualRouteList = routeService.findAll(null, null, null);
        assertEquals(1, actualRouteList.size());

        verify(routeRepository, times(1)).findAll();
    }

    @Test
    public void testFindAllByCategory() {
        List<Route> mockRouteList = List.of(new Route());
        List<RouteOutDto> mockRouteOutDto = List.of(new RouteOutDto());

        when(routeRepository.findByCategory("Express")).thenReturn(mockRouteList);
        when(modelMapper.map(mockRouteList, new TypeToken<List<RouteOutDto>>() {}.getType())).thenReturn(mockRouteOutDto);

        List<RouteOutDto> actualRouteList = routeService.findAll("Express", null, null);
        assertEquals(1, actualRouteList.size());

        verify(routeRepository, times(1)).findByCategory("Express");
    }

    @Test
    public void testFindAllByStationName() {
        List<Route> mockRouteList = List.of(new Route());
        List<RouteOutDto> mockRouteOutDto = List.of(new RouteOutDto());

        when(routeRepository.findByStation_Name("Estación Central")).thenReturn(mockRouteList);
        when(modelMapper.map(mockRouteList, new TypeToken<List<RouteOutDto>>() {}.getType())).thenReturn(mockRouteOutDto);

        List<RouteOutDto> actualRouteList = routeService.findAll(null, "Estación Central", null);
        assertEquals(1, actualRouteList.size());

        verify(routeRepository, times(1)).findByStation_Name("Estación Central");
    }

    @Test
    public void testFindAllByPrice() {
        List<Route> mockRouteList = List.of(new Route());
        List<RouteOutDto> mockRouteOutDto = List.of(new RouteOutDto());

        when(routeRepository.findByPriceLessThanEqualOrderByPriceAsc(100.0f)).thenReturn(mockRouteList);
        when(modelMapper.map(mockRouteList, new TypeToken<List<RouteOutDto>>() {}.getType())).thenReturn(mockRouteOutDto);

        List<RouteOutDto> actualRouteList = routeService.findAll(null, null, 100.0f);
        assertEquals(1, actualRouteList.size());

        verify(routeRepository, times(1)).findByPriceLessThanEqualOrderByPriceAsc(100.0f);
    }

    @Test
    public void testFindById() throws RouteNotFoundException {
        Route mockRoute = new Route();
        mockRoute.setName("Ruta 1");

        when(routeRepository.findById(1L)).thenReturn(Optional.of(mockRoute));

        Route route = routeService.findById(1L);
        assertEquals("Ruta 1", route.getName());
    }

    @Test
    public void testFindByIdNotFound() {
        when(routeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RouteNotFoundException.class, () -> routeService.findById(99L));
    }

    @Test
    public void testAddRoute() {
        Station station = new Station();
        RouteInDto routeInDto = new RouteInDto("Nueva", "Desc", LocalDate.now(), "Cat", 10, 100.0f, 1L, List.of(1L));
        List<Driver> drivers = new ArrayList<>();

        Route savedRoute = new Route();
        savedRoute.setName("Nueva");

        when(routeRepository.save(any(Route.class))).thenReturn(savedRoute);

        Route result = routeService.add(station, routeInDto, drivers);
        assertEquals("Nueva", result.getName());

        verify(routeRepository, times(1)).save(any(Route.class));
    }

    @Test
    public void testModifyRoute() throws RouteNotFoundException {
        Station station = new Station();
        List<Driver> drivers = new ArrayList<>();
        Route existingRoute = new Route();
        existingRoute.setId(1);

        RouteModifyInDto updatingDto = new RouteModifyInDto();
        updatingDto.setName("Actualizada");

        when(routeRepository.findById(1L)).thenReturn(Optional.of(existingRoute));
        when(routeRepository.save(existingRoute)).thenReturn(existingRoute);

        routeService.modify(1L, updatingDto, station, drivers);

        verify(modelMapper).map(updatingDto, existingRoute);
        verify(routeRepository).save(existingRoute);
    }

    @Test
    public void testModifyRouteNotFound() {
        RouteModifyInDto updatingDto = new RouteModifyInDto();
        when(routeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RouteNotFoundException.class, () -> routeService.modify(99L, updatingDto, new Station(), new ArrayList<>()));
        verify(routeRepository, never()).save(any(Route.class));
    }

    @Test
    public void testDeleteRoute() throws RouteNotFoundException {
        Route route = new Route();
        route.setId(1L);

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));

        routeService.delete(1L);

        verify(routeRepository, times(1)).delete(route);
    }

    @Test
    public void testDeleteRouteNotFound() {
        when(routeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RouteNotFoundException.class, () -> routeService.delete(99L));
        verify(routeRepository, never()).delete(any(Route.class));
    }
}
