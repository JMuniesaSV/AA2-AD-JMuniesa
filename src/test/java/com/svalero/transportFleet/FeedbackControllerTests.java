package com.svalero.transportFleet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.transportFleet.controller.FeedbackController;
import com.svalero.transportFleet.domain.Feedback;
import com.svalero.transportFleet.domain.Route;
import com.svalero.transportFleet.domain.User;
import com.svalero.transportFleet.dto.FeedbackInDto;
import com.svalero.transportFleet.dto.FeedbackModifyInDto;
import com.svalero.transportFleet.dto.FeedbackOutDto;
import com.svalero.transportFleet.exception.FeedbackNotFoundException;
import com.svalero.transportFleet.exception.RouteNotFoundException;
import com.svalero.transportFleet.exception.UserNotFoundException;
import com.svalero.transportFleet.service.FeedbackService;
import com.svalero.transportFleet.service.RouteService;
import com.svalero.transportFleet.service.UserService;
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

@WebMvcTest(FeedbackController.class)
public class FeedbackControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FeedbackService feedbackService;
    @MockitoBean
    private RouteService routeService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAll() throws Exception {
        List<FeedbackOutDto> feedbacks = List.of(
            new FeedbackOutDto(1, 4.0f, "Bueno", LocalDate.now(), true, 5, true)
        );

        when(feedbackService.findAll(null, null, null)).thenReturn(feedbacks);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/feedbacks")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<FeedbackOutDto> response = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(1, response.size());
    }

    @Test
    public void testGetAllByUsername() throws Exception {
        List<FeedbackOutDto> feedbacks = List.of(new FeedbackOutDto());
        when(feedbackService.findAll("jorge123", null, null)).thenReturn(feedbacks);

        mockMvc.perform(MockMvcRequestBuilders.get("/feedbacks")
                .queryParam("username", "jorge123")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllByRouteName() throws Exception {
        List<FeedbackOutDto> feedbacks = List.of(new FeedbackOutDto());
        when(feedbackService.findAll(null, "Ruta A", null)).thenReturn(feedbacks);

        mockMvc.perform(MockMvcRequestBuilders.get("/feedbacks")
                .queryParam("routeName", "Ruta A")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllByRating() throws Exception {
        List<FeedbackOutDto> feedbacks = List.of(new FeedbackOutDto());
        when(feedbackService.findAll(null, null, 4.0f)).thenReturn(feedbacks);

        mockMvc.perform(MockMvcRequestBuilders.get("/feedbacks")
                .queryParam("rating", "4.0")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetFeedbackById() throws Exception {
        Feedback feedback = new Feedback();
        feedback.setComment("Genial");

        when(feedbackService.getFeedbackById(1L)).thenReturn(feedback);

        mockMvc.perform(MockMvcRequestBuilders.get("/feedbacks/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk());
    }

    @Test
    public void testGetFeedbackByIdNotFound() throws Exception {
        when(feedbackService.getFeedbackById(99L)).thenThrow(new FeedbackNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/feedbacks/99")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isNotFound());
    }

    @Test
    public void testAddFeedback() throws Exception {
        FeedbackInDto inDto = new FeedbackInDto(5.0f, "OK", LocalDate.now(), true, 0, true, 1L, 1L);
        when(routeService.findById(1L)).thenReturn(new Route());
        when(userService.findUserById(1L)).thenReturn(new User());
        when(feedbackService.add(any(FeedbackInDto.class), any(Route.class), any(User.class))).thenReturn(new Feedback());

        mockMvc.perform(MockMvcRequestBuilders.post("/feedbacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                        .andExpect(status().isCreated());
    }

    @Test
    public void testAddFeedbackValidationError400() throws Exception {
        FeedbackInDto inDto = new FeedbackInDto();

        mockMvc.perform(MockMvcRequestBuilders.post("/feedbacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                        .andExpect(status().isBadRequest());
    }

    @Test
    public void testModifyFeedback() throws Exception {
        FeedbackModifyInDto modifyDto = new FeedbackModifyInDto(4.0f, "Mod", LocalDate.now(), true, 1, true, 1L, 1L);
        when(routeService.findById(1L)).thenReturn(new Route());
        when(userService.findUserById(1L)).thenReturn(new User());
        when(feedbackService.modify(eq(1L), any(FeedbackModifyInDto.class), any(Route.class), any(User.class))).thenReturn(new Feedback());

        mockMvc.perform(MockMvcRequestBuilders.put("/feedbacks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyDto)))
                        .andExpect(status().isOk());
    }

    @Test
    public void testModifyFeedbackNotFound() throws Exception {
        FeedbackModifyInDto modifyDto = new FeedbackModifyInDto(4.0f, "Mod", LocalDate.now(), true, 1, true, 1L, 1L);
        when(routeService.findById(1L)).thenReturn(new Route());
        when(userService.findUserById(1L)).thenReturn(new User());
        when(feedbackService.modify(eq(99L), any(FeedbackModifyInDto.class), any(Route.class), any(User.class))).thenThrow(new FeedbackNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put("/feedbacks/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyDto)))
                        .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteFeedback() throws Exception {
        doNothing().when(feedbackService).delete(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/feedbacks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteFeedbackNotFound() throws Exception {
        doThrow(new FeedbackNotFoundException()).when(feedbackService).delete(99L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/feedbacks/99"))
                .andExpect(status().isNotFound());
    }
}
