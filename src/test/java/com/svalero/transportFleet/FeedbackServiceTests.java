package com.svalero.transportFleet;

import com.svalero.transportFleet.domain.Feedback;
import com.svalero.transportFleet.domain.Route;
import com.svalero.transportFleet.domain.User;
import com.svalero.transportFleet.dto.FeedbackInDto;
import com.svalero.transportFleet.dto.FeedbackModifyInDto;
import com.svalero.transportFleet.dto.FeedbackOutDto;
import com.svalero.transportFleet.exception.FeedbackNotFoundException;
import com.svalero.transportFleet.repository.FeedbackRepository;
import com.svalero.transportFleet.service.FeedbackService;
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
public class FeedbackServiceTests {

    @InjectMocks
    private FeedbackService feedbackService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private FeedbackRepository feedbackRepository;

    @Test
    public void testFindAll() {
        List<Feedback> mockFeedbackList = List.of(new Feedback());
        List<FeedbackOutDto> mockFeedbackOutDto = List.of(new FeedbackOutDto());

        when(feedbackRepository.findAll()).thenReturn(mockFeedbackList);
        when(modelMapper.map(mockFeedbackList, new TypeToken<List<FeedbackOutDto>>() {}.getType())).thenReturn(mockFeedbackOutDto);

        List<FeedbackOutDto> actualList = feedbackService.findAll(null, null, null);
        assertEquals(1, actualList.size());

        verify(feedbackRepository, times(1)).findAll();
    }

    @Test
    public void testFindByUserUsername() {
        List<Feedback> mockFeedbackList = List.of(new Feedback());
        List<FeedbackOutDto> mockFeedbackOutDto = List.of(new FeedbackOutDto());

        when(feedbackRepository.findByUserUsername("jorge123")).thenReturn(mockFeedbackList);
        when(modelMapper.map(mockFeedbackList, new TypeToken<List<FeedbackOutDto>>() {}.getType())).thenReturn(mockFeedbackOutDto);

        List<FeedbackOutDto> actualList = feedbackService.findAll("jorge123", null, null);
        assertEquals(1, actualList.size());

        verify(feedbackRepository, times(1)).findByUserUsername("jorge123");
    }

    @Test
    public void testFindByRouteName() {
        List<Feedback> mockFeedbackList = List.of(new Feedback());
        List<FeedbackOutDto> mockFeedbackOutDto = List.of(new FeedbackOutDto());

        when(feedbackRepository.findByRoute_Name("Ruta A")).thenReturn(mockFeedbackList);
        when(modelMapper.map(mockFeedbackList, new TypeToken<List<FeedbackOutDto>>() {}.getType())).thenReturn(mockFeedbackOutDto);

        List<FeedbackOutDto> actualList = feedbackService.findAll(null, "Ruta A", null);
        assertEquals(1, actualList.size());

        verify(feedbackRepository, times(1)).findByRoute_Name("Ruta A");
    }

    @Test
    public void testFindByRatingGreaterThan() {
        List<Feedback> mockFeedbackList = List.of(new Feedback());
        List<FeedbackOutDto> mockFeedbackOutDto = List.of(new FeedbackOutDto());

        when(feedbackRepository.findByRatingGreaterThan(3.0f)).thenReturn(mockFeedbackList);
        when(modelMapper.map(mockFeedbackList, new TypeToken<List<FeedbackOutDto>>() {}.getType())).thenReturn(mockFeedbackOutDto);

        List<FeedbackOutDto> actualList = feedbackService.findAll(null, null, 3.0f);
        assertEquals(1, actualList.size());

        verify(feedbackRepository, times(1)).findByRatingGreaterThan(3.0f);
    }

    @Test
    public void testFindById() throws FeedbackNotFoundException {
        Feedback mockFeedback = new Feedback();
        mockFeedback.setComment("Comentario");

        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(mockFeedback));

        Feedback feedback = feedbackService.getFeedbackById(1L);
        assertEquals("Comentario", feedback.getComment());
    }

    @Test
    public void testFindByIdNotFound() {
        when(feedbackRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(FeedbackNotFoundException.class, () -> feedbackService.getFeedbackById(99L));
    }

    @Test
    public void testAddFeedback() {
        Route route = new Route();
        User user = new User();
        FeedbackInDto feedbackInDto = new FeedbackInDto(5.0f, "Perfecto", LocalDate.now(), true, 0, true, 1L, 1L);
        Feedback registerFeedback = new Feedback();

        when(feedbackRepository.save(any(Feedback.class))).thenReturn(registerFeedback);

        Feedback result = feedbackService.add(feedbackInDto, route, user);
        assertNotNull(result);

        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    public void testModifyFeedback() throws FeedbackNotFoundException {
        Route route = new Route();
        User user = new User();
        Feedback existingFeedback = new Feedback();
        existingFeedback.setId(1);

        FeedbackModifyInDto modifyDto = new FeedbackModifyInDto();
        modifyDto.setComment("Modificado");

        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(existingFeedback));
        when(feedbackRepository.save(existingFeedback)).thenReturn(existingFeedback);

        feedbackService.modify(1L, modifyDto, route, user);

        verify(modelMapper).map(modifyDto, existingFeedback);
        verify(feedbackRepository).save(existingFeedback);
    }

    @Test
    public void testModifyFeedbackNotFound() {
        FeedbackModifyInDto modifyDto = new FeedbackModifyInDto();
        when(feedbackRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(FeedbackNotFoundException.class, () -> feedbackService.modify(99L, modifyDto, new Route(), new User()));
        verify(feedbackRepository, never()).save(any(Feedback.class));
    }

    @Test
    public void testDeleteFeedback() throws FeedbackNotFoundException {
        Feedback feedback = new Feedback();
        feedback.setId(1L);

        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));

        feedbackService.delete(1L);

        verify(feedbackRepository, times(1)).delete(feedback);
    }

    @Test
    public void testDeleteFeedbackNotFound() {
        when(feedbackRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(FeedbackNotFoundException.class, () -> feedbackService.delete(99L));
        verify(feedbackRepository, never()).delete(any(Feedback.class));
    }
}
