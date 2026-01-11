package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.CreateUserDto;
import org.example.dto.UpdateUserDto;
import org.example.dto.UserDto;
import org.example.exception.UserNotFoundException;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;
    private CreateUserDto createUserDto;
    private UpdateUserDto updateUserDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "John Doe", "john@example.com", 25, LocalDateTime.now());
        createUserDto = new CreateUserDto("John Doe", "john@example.com", 25);
        updateUserDto = new UpdateUserDto("Jane Doe", "jane@example.com", 30);
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        when(userService.createUser(any(CreateUserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.age").value(25));

        verify(userService, times(1)).createUser(any(CreateUserDto.class));
    }

    @Test
    void createUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        CreateUserDto invalidDto = new CreateUserDto("", "", null);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any());
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.age").value(25));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void getUserById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        when(userService.getUserById(999L))
                .thenThrow(new UserNotFoundException(999L));

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id 999 not found"));

        verify(userService, times(1)).getUserById(999L);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        UserDto userDto2 = new UserDto(2L, "Jane Doe", "jane@example.com", 30, LocalDateTime.now());
        List<UserDto> users = Arrays.asList(userDto, userDto2);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Jane Doe"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        UserDto updatedUserDto = new UserDto(1L, "Jane Doe", "jane@example.com", 30, LocalDateTime.now());
        when(userService.updateUser(eq(1L), any(UpdateUserDto.class))).thenReturn(updatedUserDto);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"))
                .andExpect(jsonPath("$.age").value(30));

        verify(userService, times(1)).updateUser(eq(1L), any(UpdateUserDto.class));
    }

    @Test
    void updateUser_WithEmptyDto_ShouldReturnBadRequest() throws Exception {
        UpdateUserDto emptyDto = new UpdateUserDto(null, null, null);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("At least one field must be provided for update"));

        verify(userService, never()).updateUser(anyLong(), any());
    }

    @Test
    void updateUser_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        when(userService.updateUser(eq(999L), any(UpdateUserDto.class)))
                .thenThrow(new UserNotFoundException(999L));

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id 999 not found"));

        verify(userService, times(1)).updateUser(eq(999L), any(UpdateUserDto.class));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void deleteUser_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        doThrow(new UserNotFoundException(999L))
                .when(userService).deleteUser(999L);

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id 999 not found"));

        verify(userService, times(1)).deleteUser(999L);
    }
}
