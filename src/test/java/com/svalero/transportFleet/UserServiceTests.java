package com.svalero.transportFleet;

import com.svalero.transportFleet.domain.User;
import com.svalero.transportFleet.dto.UserInDto;
import com.svalero.transportFleet.dto.UserOutDto;
import com.svalero.transportFleet.exception.UserNotFoundException;
import com.svalero.transportFleet.repository.UserRepository;
import com.svalero.transportFleet.service.UserService;
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
public class UserServiceTests {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testFindAll() {
        List<User> mockUserList = List.of(new User());
        List<UserOutDto> mockUserOutDto = List.of(new UserOutDto());

        when(userRepository.findAll()).thenReturn(mockUserList);
        when(modelMapper.map(mockUserList, new TypeToken<List<UserOutDto>>() {}.getType())).thenReturn(mockUserOutDto);

        List<UserOutDto> actualUserList = userService.findAll(null, null, null);
        assertEquals(1, actualUserList.size());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testFindUserByName() {
        List<User> mockUserList = List.of(new User());
        List<UserOutDto> mockUserOutDto = List.of(new UserOutDto());

        when(userRepository.findUserByName("Jorge")).thenReturn(mockUserList);
        when(modelMapper.map(mockUserList, new TypeToken<List<UserOutDto>>() {}.getType())).thenReturn(mockUserOutDto);

        List<UserOutDto> actualUserList = userService.findAll("Jorge", null, null);
        assertEquals(1, actualUserList.size());

        verify(userRepository, times(1)).findUserByName("Jorge");
    }

    @Test
    public void testFindByBirthDateBefore() {
        LocalDate date = LocalDate.of(2000, 1, 1);
        List<User> mockUserList = List.of(new User());
        List<UserOutDto> mockUserOutDto = List.of(new UserOutDto());

        when(userRepository.findByBirthDateBefore(date)).thenReturn(mockUserList);
        when(modelMapper.map(mockUserList, new TypeToken<List<UserOutDto>>() {}.getType())).thenReturn(mockUserOutDto);

        List<UserOutDto> actualUserList = userService.findAll(null, date, null);
        assertEquals(1, actualUserList.size());

        verify(userRepository, times(1)).findByBirthDateBefore(date);
    }

    @Test
    public void testFindByActiveTrue() {
        List<User> mockUserList = List.of(new User());
        List<UserOutDto> mockUserOutDto = List.of(new UserOutDto());

        when(userRepository.findByActiveFalse()).thenReturn(mockUserList);
        when(modelMapper.map(mockUserList, new TypeToken<List<UserOutDto>>() {}.getType())).thenReturn(mockUserOutDto);

        List<UserOutDto> actualUserList = userService.findAll(null, null, true);
        assertEquals(1, actualUserList.size());

        verify(userRepository, times(1)).findByActiveFalse();
    }

    @Test
    public void testFindUserById() throws UserNotFoundException {
        User mockUser = new User();
        mockUser.setUsername("jorge123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        User user = userService.findUserById(1L);
        assertEquals("jorge123", user.getUsername());
    }

    @Test
    public void testFindUserByIdNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findUserById(99L));
    }

    @Test
    public void testAddUser() {
        UserInDto userInDto = new UserInDto("jorge123", "Jorge", "Ruiz", LocalDate.now(), 615987325, true);
        User registerUser = new User();

        when(userRepository.save(any(User.class))).thenReturn(registerUser);

        User resultUser = userService.add(userInDto);
        assertNotNull(resultUser);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testModifyUser() throws UserNotFoundException {
        User existingUser = new User();
        existingUser.setId(1);
        User updatingUser = new User();
        updatingUser.setName("Sergio");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        userService.modify(1L, updatingUser);

        verify(modelMapper).map(updatingUser, existingUser);
        verify(userRepository).save(existingUser);
    }

    @Test
    public void testModifyUserNotFound() {
        User updated = new User();
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.modify(99L, updated));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDeleteUser() throws UserNotFoundException {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testDeleteUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.delete(99L));
        verify(userRepository, never()).delete(any(User.class));
    }
}
