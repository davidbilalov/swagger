package org.example.service;

import org.example.User;
import org.example.dto.CreateUserDto;
import org.example.dto.UpdateUserDto;
import org.example.dto.UserDto;
import org.example.exception.UserAlreadyExistsException;
import org.example.exception.UserNotFoundException;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;

    public UserService(UserRepository userRepository, KafkaProducerService kafkaProducerService) {
        this.userRepository = userRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    public UserDto createUser(CreateUserDto createUserDto) {
        if (userRepository.existsByEmail(createUserDto.getEmail())) {
            throw new UserAlreadyExistsException(createUserDto.getEmail());
        }

        User user = new User();
        user.setName(createUserDto.getName());
        user.setEmail(createUserDto.getEmail());
        user.setAge(createUserDto.getAge());

        User savedUser = userRepository.save(user);
        
        // Отправка сообщения в Kafka о создании пользователя
        kafkaProducerService.sendUserEvent("CREATE", savedUser.getEmail());
        
        return convertToDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return convertToDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto updateUser(Long id, UpdateUserDto updateUserDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (updateUserDto.getName() != null && !updateUserDto.getName().trim().isEmpty()) {
            user.setName(updateUserDto.getName().trim());
        }
        if (updateUserDto.getEmail() != null && !updateUserDto.getEmail().trim().isEmpty()) {
            String newEmail = updateUserDto.getEmail().trim();
            if (userRepository.existsByEmail(newEmail) && !user.getEmail().equals(newEmail)) {
                throw new UserAlreadyExistsException(newEmail);
            }
            user.setEmail(newEmail);
        }
        if (updateUserDto.getAge() != null && updateUserDto.getAge() > 0) {
            user.setAge(updateUserDto.getAge());
        }

        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        
        String email = user.getEmail();
        userRepository.deleteById(id);
        
        // Отправка сообщения в Kafka об удалении пользователя
        kafkaProducerService.sendUserEvent("DELETE", email);
    }

    private UserDto convertToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }
}
