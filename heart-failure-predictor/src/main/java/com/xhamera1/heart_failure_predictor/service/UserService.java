package com.xhamera1.heart_failure_predictor.service;

import com.xhamera1.heart_failure_predictor.dto.UpdateUserDto;
import com.xhamera1.heart_failure_predictor.dto.UserDto;
import com.xhamera1.heart_failure_predictor.dto.UserRegistrationDto;
import com.xhamera1.heart_failure_predictor.exceptions.ResourceAlreadyExistsException;
import com.xhamera1.heart_failure_predictor.exceptions.ResourceNotFoundException;
import com.xhamera1.heart_failure_predictor.model.User;
import com.xhamera1.heart_failure_predictor.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto addUser(UserRegistrationDto registrationDto) throws ResourceAlreadyExistsException {
        log.debug("Attempting to add user.");
        if (userRepository.findByUsername(registrationDto.getUsername()).isPresent()) {
            throw new ResourceAlreadyExistsException("User with username: " + registrationDto.getUsername() + " already exists.");
        }

        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("User with email : " + registrationDto.getEmail() + " already exists.");
        }
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRoles("USER");
        User savedUser = userRepository.save(user);
        log.debug("User with id : {} added", user.getId());
        return mapUserToDto(savedUser);
    }


    public UserDto getUserByUsername(String username) throws ResourceNotFoundException {
        log.debug("Attempting to retrieve user by username: {}", username);
        User user =  userRepository.findByUsername(username).orElseThrow(() -> {
            log.warn("User not found with username: {}", username);
            return new ResourceNotFoundException("User not found with username: " + username);
        });
        return mapUserToDto(user);
    }

    public UserDto getUserByEmail(
            String email
    ) throws ResourceNotFoundException{
        log.debug("Attempting to retrieve user by email: {}", email);
        User user =  userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });
        return mapUserToDto(user);
    }


    public UserDto getUserById(
            Long id
    ) throws ResourceNotFoundException{
        log.debug("Attempting to retrieve user by ID: {}", id);
        User user =  userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });
        return mapUserToDto(user);
    }

    public UserDto updateUser(Long id, UpdateUserDto updateDto) throws ResourceNotFoundException, ResourceAlreadyExistsException{
        log.debug("Attempting to update user with id: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        if (updateDto.getEmail() != null && !updateDto.getEmail().isBlank() && !updateDto.getEmail().equals(existingUser.getEmail())) {
            log.debug("Attempting to update email for user ID: {}", id);
            Optional<User> userWithNewEmail = userRepository.findByEmail(updateDto.getEmail());
            if (userWithNewEmail.isPresent() && !userWithNewEmail.get().getId().equals(existingUser.getId())) {
                throw new ResourceAlreadyExistsException("Email: " + updateDto.getEmail() + " is already taken.");
            }
            existingUser.setEmail(updateDto.getEmail());
            log.info("Email updated for user ID: {}", id);
        }

        if (updateDto.getPassword() != null && !updateDto.getPassword().isBlank()) {
            log.debug("Attempting to update password for user ID: {}", id);
            existingUser.setPassword(passwordEncoder.encode(updateDto.getPassword()));
            log.info("Password updated for user ID: {}", id);
        }

        if (updateDto.getRoles() != null && !updateDto.getRoles().isBlank() && !updateDto.getRoles().equals(existingUser.getRoles())) {
            log.debug("Attempting to update roles for user ID: {}", id);
            existingUser.setRoles(updateDto.getRoles());
            log.info("Roles updated for user ID: {}", id);
        }

        User savedUser = userRepository.save(existingUser);
        log.info("Successfully updated user with ID: {}", savedUser.getId());
        return mapUserToDto(savedUser);


    }

    public void deleteUser(Long id) throws ResourceNotFoundException {
        log.debug("Attempting to delete user with ID: {}", id);
        if (!userRepository.existsById(id)) {
            log.warn("User not found with ID: {}. Cannot delete.", id);
            throw new ResourceNotFoundException("User not found for deletion with ID: " + id);
        }
        userRepository.deleteById(id);
        log.info("Successfully deleted user with ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        log.debug("Attempting to retrieve all users.");
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            log.warn("No users found in the repository.");
        }
        log.info("Retrieved {} users.", users.size());
        return users.stream().map((this::mapUserToDto)).collect(Collectors.toList());
    }

    private UserDto mapUserToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles());
        return userDto;
    }

    @Transactional(readOnly = true)
    public User findUserEntityByUsername(String username) throws ResourceNotFoundException {
        log.debug("Attempting to find user entity by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User entity not found with username: {}", username);
                    return new ResourceNotFoundException("User not found with username: " + username);
                });
    }



}
