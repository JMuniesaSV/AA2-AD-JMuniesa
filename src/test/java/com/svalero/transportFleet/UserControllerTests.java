package com.svalero.transportFleet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.transportFleet.controller.UserController;
import com.svalero.transportFleet.domain.User;
import com.svalero.transportFleet.dto.UserInDto;
import com.svalero.transportFleet.dto.UserOutDto;
import com.svalero.transportFleet.exception.UserNotFoundException;
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

@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAll() throws Exception {
        List<UserOutDto> users = List.of(
            new UserOutDto(1, "jorge123", "Jorge", "Ruiz", LocalDate.now(), 615987325, true)
        );

        when(userService.findAll(null, null, null)).thenReturn(users);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<UserOutDto> response = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(1, response.size());
    }

    @Test
    public void testGetAllByName() throws Exception {
        List<UserOutDto> users = List.of(new UserOutDto());
        when(userService.findAll("Jorge", null, null)).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .queryParam("name", "Jorge")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllByDate() throws Exception {
        LocalDate date = LocalDate.of(2000, 1, 1);
        List<UserOutDto> users = List.of(new UserOutDto());
        when(userService.findAll(null, date, null)).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .queryParam("date", date.toString())
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllByActive() throws Exception {
        List<UserOutDto> users = List.of(new UserOutDto());
        when(userService.findAll(null, null, true)).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .queryParam("active", "true")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetUserById() throws Exception {
        User user = new User();
        user.setUsername("jorge123");

        when(userService.findUserById(1L)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk());
    }

    @Test
    public void testGetUserByIdNotFound() throws Exception {
        when(userService.findUserById(99L)).thenThrow(new UserNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/users/99")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isNotFound());
    }

    @Test
    public void testAddUser() throws Exception {
        UserInDto inDto = new UserInDto("jorge123", "Jorge", "Ruiz", LocalDate.now(), 615987325, true);
        when(userService.add(any(UserInDto.class))).thenReturn(new User());

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                        .andExpect(status().isCreated());
    }

    @Test
    public void testAddUserValidationError400() throws Exception {
        UserInDto inDto = new UserInDto();

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                        .andExpect(status().isBadRequest());
    }

    @Test
    public void testModifyUser() throws Exception {
        User userRequest = new User();
        userRequest.setName("Jorge Modificado");

        when(userService.modify(eq(1L), any(User.class))).thenReturn(userRequest);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                        .andExpect(status().isOk());
    }

    @Test
    public void testModifyUserNotFound() throws Exception {
        User userRequest = new User();
        when(userService.modify(eq(99L), any(User.class))).thenThrow(new UserNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put("/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                        .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteUser() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteUserNotFound() throws Exception {
        doThrow(new UserNotFoundException()).when(userService).delete(99L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/99"))
                .andExpect(status().isNotFound());
    }
}
