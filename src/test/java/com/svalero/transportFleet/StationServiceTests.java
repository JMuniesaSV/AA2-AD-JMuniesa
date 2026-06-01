package com.svalero.transportFleet;

import com.svalero.transportFleet.domain.Station;
import com.svalero.transportFleet.dto.StationOutDto;
import com.svalero.transportFleet.exception.StationNotFoundException;
import com.svalero.transportFleet.repository.StationRepository;
import com.svalero.transportFleet.service.StationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StationServiceTests {

    @InjectMocks
    private StationService stationService;
    @Mock
    private StationRepository stationRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testFindAll() {
        List<Station> mockStationList = List.of(new Station());
        List<StationOutDto> mockStationOutDto = List.of(new StationOutDto());

        when(stationRepository.findAll()).thenReturn(mockStationList);
        when(modelMapper.map(mockStationList, new TypeToken<List<StationOutDto>>() {}.getType())).thenReturn(mockStationOutDto);

        List<StationOutDto> actualStationList = stationService.findAll(null, null, null);
        assertEquals(1, actualStationList.size());

        verify(stationRepository, times(1)).findAll();
    }

    @Test
    public void testFindByCategory() {
        List<Station> mockStationList = List.of(new Station());
        List<StationOutDto> mockStationOutDto = List.of(new StationOutDto());

        when(stationRepository.findByCategory("Logística")).thenReturn(mockStationList);
        when(modelMapper.map(mockStationList, new TypeToken<List<StationOutDto>>() {}.getType())).thenReturn(mockStationOutDto);

        List<StationOutDto> actualStationList = stationService.findAll("Logística", null, null);
        assertEquals(1, actualStationList.size());

        verify(stationRepository, times(1)).findByCategory("Logística");
    }

    @Test
    public void testFindByPostalCode() {
        List<Station> mockStationList = List.of(new Station());
        List<StationOutDto> mockStationOutDto = List.of(new StationOutDto());

        when(stationRepository.findByPostalCode(50001)).thenReturn(mockStationList);
        when(modelMapper.map(mockStationList, new TypeToken<List<StationOutDto>>() {}.getType())).thenReturn(mockStationOutDto);

        List<StationOutDto> actualStationList = stationService.findAll(null, null, 50001);
        assertEquals(1, actualStationList.size());

        verify(stationRepository, times(1)).findByPostalCode(50001);
    }

    @Test
    public void testFindByDisabledAccessTrue() {
        List<Station> mockStationList = List.of(new Station());
        List<StationOutDto> mockStationOutDto = List.of(new StationOutDto());

        when(stationRepository.findByDisabledAccessTrue()).thenReturn(mockStationList);
        when(modelMapper.map(mockStationList, new TypeToken<List<StationOutDto>>() {}.getType())).thenReturn(mockStationOutDto);

        List<StationOutDto> actualStationList = stationService.findAll(null, true, null);
        assertEquals(1, actualStationList.size());

        verify(stationRepository, times(1)).findByDisabledAccessTrue();
    }

    @Test
    public void testFindById() throws StationNotFoundException {
        Station mockStation = new Station();
        mockStation.setName("Estación Central");

        when(stationRepository.findById(1L)).thenReturn(Optional.of(mockStation));

        Station station = stationService.findById(1L);
        assertEquals("Estación Central", station.getName());
    }

    @Test
    public void testFindByIdNotFound() {
        when(stationRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(StationNotFoundException.class, () -> stationService.findById(99L));
    }

    @Test
    public void testAdd() {
        Station station = new Station();
        when(stationRepository.save(any(Station.class))).thenReturn(station);

        Station result = stationService.add(station);
        assertNotNull(result);

        verify(stationRepository, times(1)).save(station);
    }

    @Test
    public void testModify() throws StationNotFoundException {
        Station existing = new Station();
        existing.setId(1L);
        Station updated = new Station();
        updated.setName("Nuevo Nombre");

        when(stationRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(stationRepository.save(any(Station.class))).thenReturn(existing);

        stationService.modify(1L, updated);

        verify(modelMapper).map(updated, existing);
        verify(stationRepository).save(existing);
    }

    @Test
    public void testModifyNotFound() {
        Station updated = new Station();
        when(stationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(StationNotFoundException.class, () -> stationService.modify(99L, updated));
        verify(stationRepository, never()).save(any(Station.class));
    }

    @Test
    public void testDelete() throws StationNotFoundException {
        Station station = new Station();
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));

        stationService.delete(1L);

        verify(stationRepository).delete(station);
    }

    @Test
    public void testDeleteNotFound() {
        when(stationRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(StationNotFoundException.class, () -> stationService.delete(99L));
        verify(stationRepository, never()).delete(any(Station.class));
    }
}
