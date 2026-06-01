package com.svalero.transportFleet.service;

import com.svalero.transportFleet.domain.Station;
import com.svalero.transportFleet.dto.StationOutDto;
import com.svalero.transportFleet.exception.StationNotFoundException;
import com.svalero.transportFleet.repository.StationRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private ModelMapper modelMapper;

    public Station add(Station station) {
        return stationRepository.save(station);
    }

    public void delete(long id) throws StationNotFoundException {
        Station station = stationRepository.findById(id)
                .orElseThrow(StationNotFoundException::new);
        stationRepository.delete(station);
    }

    public List<StationOutDto> findAll(String category, Boolean disabledAccess, Integer postalCode) {
        List<Station> allStations;

        if (category != null && !category.isEmpty()) {
            allStations = stationRepository.findByCategory(category);
        } else if (disabledAccess != null && disabledAccess) {
            allStations = stationRepository.findByDisabledAccessTrue();
        } else if (postalCode != null) {
            allStations = stationRepository.findByPostalCode(postalCode);
        } else {
            allStations = stationRepository.findAll();
        }

        return modelMapper.map(allStations, new TypeToken<List<StationOutDto>>() {}.getType());
    }

    public Station findById(long id) throws StationNotFoundException {
        return stationRepository.findById(id)
                .orElseThrow(StationNotFoundException::new);
    }

    public Station modify(long id, Station station) throws StationNotFoundException {
        Station existingStation = stationRepository.findById(id)
                .orElseThrow(StationNotFoundException::new);

        modelMapper.map(station, existingStation);
        existingStation.setId(id);

        return stationRepository.save(existingStation);
    }
}
