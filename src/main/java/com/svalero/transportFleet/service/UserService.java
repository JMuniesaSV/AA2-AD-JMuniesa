package com.svalero.transportFleet.service;

import com.svalero.transportFleet.domain.User;
import com.svalero.transportFleet.dto.UserInDto;
import com.svalero.transportFleet.dto.UserOutDto;
import com.svalero.transportFleet.exception.UserNotFoundException;
import com.svalero.transportFleet.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public User add(UserInDto userInDto) {
        User user = new User();
        modelMapper.map(userInDto, user);
        return userRepository.save(user);
    }

    public void delete(long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);
    }

    public List<UserOutDto> findAll(String name, LocalDate date, Boolean active) {
        List<User> allUsers;

        if (name != null && !name.isEmpty()) {
            allUsers = userRepository.findUserByName(name);
        } else if (date != null) {
            allUsers = userRepository.findByBirthDateBefore(date);
        } else if (active != null && active) {
            allUsers = userRepository.findByActiveFalse();
        } else {
            allUsers = userRepository.findAll();
        }

        return modelMapper.map(allUsers, new TypeToken<List<UserOutDto>>() {}.getType());
    }

    public User findUserById(long id) throws UserNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    public User modify(long id, User user) throws UserNotFoundException {
        User userExisting = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        modelMapper.map(user, userExisting);
        userExisting.setId(id);

        return userRepository.save(userExisting);
    }
}
