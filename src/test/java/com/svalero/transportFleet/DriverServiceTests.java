package com.svalero.transportFleet;

import com.svalero.transportFleet.domain.Driver;
import com.svalero.transportFleet.dto.DriverOutDto;
import com.svalero.transportFleet.exception.DriverNotFoundException;
import com.svalero.transportFleet.repository.DriverRepository;
import com.svalero.transportFleet.service.DriverService;
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
public class DriverServiceTests {

    @InjectMocks
    private DriverService driverService;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testFindAll() {
        List<Driver> mockDriverList = List.of(new Driver());
        List<DriverOutDto> mockDriverOutDto = List.of(new DriverOutDto());

        when(driverRepository.findAll()).thenReturn(mockDriverList);
        when(modelMapper.map(mockDriverList, new TypeToken<List<DriverOutDto>>() {}.getType())).thenReturn(mockDriverOutDto);

        List<DriverOutDto> actualDriverList = driverService.findAll(null, null, null);
        assertEquals(1, actualDriverList.size());

        verify(driverRepository, times(1)).findAll();
    }

    @Test
    public void testFindByType() {
        List<Driver> mockDriverList = List.of(new Driver());
        List<DriverOutDto> mockDriverOutDto = List.of(new DriverOutDto());

        when(driverRepository.findByType("F1")).thenReturn(mockDriverList);
        when(modelMapper.map(mockDriverList, new TypeToken<List<DriverOutDto>>() {}.getType())).thenReturn(mockDriverOutDto);

        List<DriverOutDto> actualDriverList = driverService.findAll("F1", null, null);
        assertEquals(1, actualDriverList.size());

        verify(driverRepository, times(1)).findByType("F1");
    }

    @Test
    public void testFindByActiveTrue() {
        List<Driver> mockDriverList = List.of(new Driver());
        List<DriverOutDto> mockDriverOutDto = List.of(new DriverOutDto());

        when(driverRepository.findByActiveTrue()).thenReturn(mockDriverList);
        when(modelMapper.map(mockDriverList, new TypeToken<List<DriverOutDto>>() {}.getType())).thenReturn(mockDriverOutDto);

        List<DriverOutDto> actualDriverList = driverService.findAll(null, true, null);
        assertEquals(1, actualDriverList.size());

        verify(driverRepository, times(1)).findByActiveTrue();
    }

    @Test
    public void testFindAllByOrderByExperienceDesc() {
        List<Driver> mockDriverList = List.of(new Driver());
        List<DriverOutDto> mockDriverOutDto = List.of(new DriverOutDto());

        when(driverRepository.findAllByOrderByExperienceDesc()).thenReturn(mockDriverList);
        when(modelMapper.map(mockDriverList, new TypeToken<List<DriverOutDto>>() {}.getType())).thenReturn(mockDriverOutDto);

        List<DriverOutDto> actualDriverList = driverService.findAll(null, null, true);
        assertEquals(1, actualDriverList.size());

        verify(driverRepository, times(1)).findAllByOrderByExperienceDesc();
    }

    @Test
    public void testFindDriverById() throws DriverNotFoundException {
        Driver mockDriver = new Driver();
        mockDriver.setName("Carlos");

        when(driverRepository.findById(1L)).thenReturn(Optional.of(mockDriver));

        Driver driver = driverService.findDriverById(1L);
        assertEquals("Carlos", driver.getName());
    }

    @Test
    public void testFindDriverByIdNotFound() {
        when(driverRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(DriverNotFoundException.class, () -> driverService.findDriverById(99L));
    }

    @Test
    public void testFindAllDriversById() {
        List<Driver> mockDrivers = List.of(new Driver(), new Driver());

        when(driverRepository.findAllById(List.of(1L, 2L))).thenReturn(mockDrivers);

        List<Driver> driverList = driverService.findAllDriversById(List.of(1L, 2L));
        assertEquals(2, driverList.size());
    }

    @Test
    public void testAddDriver() {
        Driver driver = new Driver();
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        Driver result = driverService.add(driver);
        assertNotNull(result);

        verify(driverRepository, times(1)).save(any(Driver.class));
    }

    @Test
    public void testModifyDriver() throws DriverNotFoundException {
        Driver existing = new Driver();
        existing.setId(1);
        Driver updated = new Driver();
        updated.setName("Modificado");

        when(driverRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(driverRepository.save(any(Driver.class))).thenReturn(existing);

        driverService.modify(1L, updated);

        verify(modelMapper).map(updated, existing);
        verify(driverRepository).save(existing);
    }

    @Test
    public void testModifyDriverNotFound() {
        Driver updated = new Driver();
        when(driverRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class, () -> driverService.modify(99L, updated));
        verify(driverRepository, never()).save(any(Driver.class));
    }

    @Test
    public void testDeleteDriver() throws DriverNotFoundException {
        Driver driver = new Driver();
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));

        driverService.delete(1L);

        verify(driverRepository, times(1)).delete(driver);
    }

    @Test
    public void testDeleteDriverNotFound() {
        when(driverRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class, () -> driverService.delete(99L));
        verify(driverRepository, never()).delete(any(Driver.class));
    }
}
