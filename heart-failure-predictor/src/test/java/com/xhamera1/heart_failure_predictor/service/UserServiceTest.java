package com.xhamera1.heart_failure_predictor.service;

import com.xhamera1.heart_failure_predictor.dto.UpdateUserDto;
import com.xhamera1.heart_failure_predictor.dto.UserDto;
import com.xhamera1.heart_failure_predictor.dto.UserRegistrationDto;
import com.xhamera1.heart_failure_predictor.exceptions.ResourceAlreadyExistsException;
import com.xhamera1.heart_failure_predictor.exceptions.ResourceNotFoundException;
import com.xhamera1.heart_failure_predictor.model.User;
import com.xhamera1.heart_failure_predictor.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRegistrationDto registrationDto;
    private User user;
    private UserDto userDto;
    private UpdateUserDto updateUserDto;

    @BeforeEach
    void setUp() {
        registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("testuser");
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("hashedPassword");
        user.setRoles("USER");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testuser");
        userDto.setEmail("test@example.com");
        userDto.setRoles("USER");

        updateUserDto = new UpdateUserDto();
    }

    @Test
    @DisplayName("addUser should save user successfully when username and email are unique")
    void addUser_Success() throws ResourceAlreadyExistsException { // Dodaj throws, jeśli metoda serwisu deklaruje
        when(userRepository.findByUsername(registrationDto.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(registrationDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registrationDto.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            userToSave.setId(1L);
            return userToSave;
        });

        UserDto resultDto = userService.addUser(registrationDto);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getId()).isEqualTo(1L);
        assertThat(resultDto.getUsername()).isEqualTo(registrationDto.getUsername());
        verify(passwordEncoder, times(1)).encode(registrationDto.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("addUser should throw ResourceAlreadyExistsException when username exists")
    void addUser_UsernameExists_ThrowsException() {
        when(userRepository.findByUsername(registrationDto.getUsername())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.addUser(registrationDto))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("User with username: " + registrationDto.getUsername());

        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("addUser should throw ResourceAlreadyExistsException when email exists")
    void addUser_EmailExists_ThrowsException() {
        when(userRepository.findByUsername(registrationDto.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(registrationDto.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.addUser(registrationDto))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("User with email : " + registrationDto.getEmail());

        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    @DisplayName("getUserByUsername should return UserDto when user exists")
    void getUserByUsername_UserExists_ReturnsDto() throws ResourceNotFoundException { // Dodaj throws
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDto resultDto = userService.getUserByUsername(username);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getId()).isEqualTo(user.getId());
        assertThat(resultDto.getUsername()).isEqualTo(user.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("getUserByUsername should throw ResourceNotFoundException when user does not exist")
    void getUserByUsername_UserNotFound_ThrowsException() {
        String username = "nonexistentuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByUsername(username))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with username: " + username);

        verify(userRepository, times(1)).findByUsername(username);
    }


    @Test
    @DisplayName("updateUser should update email successfully")
    void updateUser_UpdateEmail_Success() throws ResourceNotFoundException, ResourceAlreadyExistsException { // Dodaj throws
        Long userId = 1L;
        String newEmail = "newemail@example.com";
        updateUserDto.setEmail(newEmail);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(newEmail)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto resultDto = userService.updateUser(userId, updateUserDto);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getEmail()).isEqualTo(newEmail);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail()).isEqualTo(newEmail);
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("hashedPassword");
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("updateUser should update password successfully")
    void updateUser_UpdatePassword_Success() throws ResourceNotFoundException, ResourceAlreadyExistsException { // Dodaj throws
        Long userId = 1L;
        String newPassword = "newPassword123";
        String newHashedPassword = "newHashedPassword";
        updateUserDto.setPassword(newPassword);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn(newHashedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto resultDto = userService.updateUser(userId, updateUserDto);

        assertThat(resultDto).isNotNull();
        verify(passwordEncoder, times(1)).encode(newPassword);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo(newHashedPassword);
        assertThat(userCaptor.getValue().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("updateUser should throw ResourceNotFoundException when user not found")
    void updateUser_UserNotFound_ThrowsException() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(userId, updateUserDto))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("updateUser should throw ResourceAlreadyExistsException when new email is taken")
    void updateUser_NewEmailTaken_ThrowsException() {
        Long userId = 1L;
        String newEmail = "taken@example.com";
        updateUserDto.setEmail(newEmail);
        User anotherUser = new User(); // Inny użytkownik z tym samym emailem
        anotherUser.setId(2L);
        anotherUser.setEmail(newEmail);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(newEmail)).thenReturn(Optional.of(anotherUser)); // Nowy email jest zajęty przez kogoś innego

        assertThatThrownBy(() -> userService.updateUser(userId, updateUserDto))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Email: " + newEmail);

        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    @DisplayName("deleteUser should call repository delete when user exists")
    void deleteUser_UserExists_DeletesUser() throws ResourceNotFoundException { // Dodaj throws
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        // Mockito domyślnie nie robi nic dla metod void, więc nie trzeba konfigurować deleteById
        // doNothing().when(userRepository).deleteById(userId); // Opcjonalne, jeśli chcesz być jawny

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("deleteUser should throw ResourceNotFoundException when user does not exist")
    void deleteUser_UserNotFound_ThrowsException() {
        Long userId = 99L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found for deletion with ID: " + userId);

        verify(userRepository, never()).deleteById(anyLong());
    }


    @Test
    @DisplayName("getAllUsers should return list of UserDto when users exist")
    void getAllUsers_UsersExist_ReturnsDtoList() {
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setRoles("USER");
        when(userRepository.findAll()).thenReturn(List.of(user, user2));

        List<UserDto> resultList = userService.getAllUsers();

        assertThat(resultList).isNotNull().hasSize(2);
        assertThat(resultList.get(0).getUsername()).isEqualTo(user.getUsername());
        assertThat(resultList.get(1).getUsername()).isEqualTo(user2.getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllUsers should return empty list when no users exist")
    void getAllUsers_NoUsers_ReturnsEmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> resultList = userService.getAllUsers();

        assertThat(resultList).isNotNull().isEmpty();
        verify(userRepository, times(1)).findAll();
    }
}