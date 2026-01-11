package org.example.service;

import org.example.User;
import org.example.dto.CreateUserDto;
import org.example.dto.UpdateUserDto;
import org.example.dto.UserDto;
import org.example.exception.UserAlreadyExistsException;
import org.example.exception.UserNotFoundException;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private CreateUserDto createUserDto;
    private UpdateUserDto updateUserDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setAge(25);
        user.setCreatedAt(LocalDateTime.now());

        createUserDto = new CreateUserDto("John Doe", "john@example.com", 25);
        updateUserDto = new UpdateUserDto("Jane Doe", "jane@example.com", 30);
    }

    @Test
    void createUser_ShouldReturnUserDto() {
        when(userRepository.existsByEmail(createUserDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(createUserDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getAge(), result.getAge());

        verify(userRepository, times(1)).existsByEmail(createUserDto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_WithExistingEmail_ShouldThrowException() {
        when(userRepository.existsByEmail(createUserDto.getEmail())).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userService.createUser(createUserDto));

        assertNotEquals("User with email " + createUserDto.getEmail() + " already exists", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail(createUserDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_ShouldReturnUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getAge(), result.getAge());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_WithNonExistentId_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(999L));

        assertEquals("User with id 999 not found", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUserDtos() {
        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Doe");
        user2.setEmail("jane@example.com");
        user2.setAge(30);
        user2.setCreatedAt(LocalDateTime.now());

        List<User> users = Arrays.asList(user, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(user.getId(), result.get(0).getId());
        assertEquals(user2.getId(), result.get(1).getId());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateUser_ShouldReturnUpdatedUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(updateUserDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUser(1L, updateUserDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_WithNonExistentId_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(999L, updateUserDto));

        assertEquals("User with id 999 not found", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WithExistingEmail_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(updateUserDto.getEmail())).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userService.updateUser(1L, updateUserDto));

        assertNotEquals("User with email " + updateUserDto.getEmail() + " already exists", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmail(updateUserDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_WithNonExistentId_ShouldThrowException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(999L));

        assertEquals("User with id 999 not found", exception.getMessage());
        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }
}
