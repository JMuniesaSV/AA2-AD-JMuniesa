package com.svalero.transportFleet.service;

import com.svalero.transportFleet.domain.Driver;
import com.svalero.transportFleet.domain.Route;
import com.svalero.transportFleet.domain.Station;
import com.svalero.transportFleet.dto.RouteInDto;
import com.svalero.transportFleet.dto.RouteModifyInDto;
import com.svalero.transportFleet.dto.RouteOutDto;
import com.svalero.transportFleet.exception.RouteNotFoundException;
import com.svalero.transportFleet.repository.RouteRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {

    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private ModelMapper modelMapper;

    public Route add(Station station, RouteInDto routeInDto, List<Driver> drivers) {
        Route route = new Route();
        route.setName(routeInDto.getName());
        route.setDescription(routeInDto.getDescription());
        route.setRouteDate(routeInDto.getRouteDate());
        route.setCategory(routeInDto.getCategory());
        route.setCapacity(routeInDto.getCapacity());
        route.setPrice(routeInDto.getPrice());
        route.setStation(station);
        route.setDrivers(drivers);
        route.setAvailability(true);

        return routeRepository.save(route);
    }

    public void delete(long id) throws RouteNotFoundException {
        Route route = routeRepository.findById(id)
                .orElseThrow(RouteNotFoundException::new);
        routeRepository.delete(route);
    }

    public List<RouteOutDto> findAll(String category, String stationName, Float price) {
        List<Route> allRoutes;

        if (category != null && !category.isEmpty()) {
            allRoutes = routeRepository.findByCategory(category);
        } else if (stationName != null && !stationName.isEmpty()) {
            allRoutes = routeRepository.findByStation_Name(stationName);
        } else if (price != null) {
            allRoutes = routeRepository.findByPriceLessThanEqualOrderByPriceAsc(price);
        } else {
            allRoutes = routeRepository.findAll();
        }

        return modelMapper.map(allRoutes, new TypeToken<List<RouteOutDto>>() {}.getType());
    }

    public Route findById(long id) throws RouteNotFoundException {
        return routeRepository.findById(id)
                .orElseThrow(RouteNotFoundException::new);
    }

    public Route modify(long id, RouteModifyInDto routeInDto, Station station, List<Driver> drivers) throws RouteNotFoundException {
        Route existingRoute = routeRepository.findById(id)
                .orElseThrow(RouteNotFoundException::new);

        modelMapper.map(routeInDto, existingRoute);
        existingRoute.setId(id);
        existingRoute.setDrivers(drivers);
        existingRoute.setStation(station);

        return routeRepository.save(existingRoute);
    }
}
