package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.*;
import org.example.service.UserService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(
            summary = "Создать нового пользователя",
            description = "Создает нового пользователя с указанными данными"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные пользователя"),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует")
    })
    public ResponseEntity<EntityModel<UserDto>> createUser(
            @Parameter(description = "Данные для создания пользователя", required = true)
            @Valid @RequestBody CreateUserDto createUserDto) {
        UserDto userDto = userService.createUser(createUserDto);
        EntityModel<UserDto> userModel = EntityModel.of(userDto);
        addUserLinks(userModel, userDto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(userModel);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает информацию о пользователе с указанным идентификатором"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<EntityModel<UserDto>> getUserById(
            @Parameter(description = "ID пользователя", required = true, example = "1")
            @PathVariable Long id) {
        UserDto userDto = userService.getUserById(id);
        EntityModel<UserDto> userModel = EntityModel.of(userDto);
        addUserLinks(userModel, id);
        return ResponseEntity.ok(userModel);
    }

    @GetMapping
    @Operation(
            summary = "Получить всех пользователей",
            description = "Возвращает список всех пользователей в системе"
    )
    @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен")
    public ResponseEntity<CollectionModel<EntityModel<UserDto>>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        
        List<EntityModel<UserDto>> userModels = users.stream()
                .map(userDto -> {
                    EntityModel<UserDto> model = EntityModel.of(userDto);
                    addUserLinks(model, userDto.getId());
                    return model;
                })
                .collect(Collectors.toList());
        
        CollectionModel<EntityModel<UserDto>> collectionModel = CollectionModel.of(userModels);

        Link selfLink = linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel();
        collectionModel.add(selfLink);

        Link createLink = linkTo(methodOn(UserController.class).createUser(null)).withRel("create");
        collectionModel.add(createLink);
        
        return ResponseEntity.ok(collectionModel);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить пользователя",
            description = "Обновляет информацию о пользователе с указанным идентификатором"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные для обновления"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "409", description = "Email уже используется другим пользователем")
    })
    public ResponseEntity<EntityModel<UserDto>> updateUser(
            @Parameter(description = "ID пользователя", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Данные для обновления пользователя", required = true)
            @Valid @RequestBody UpdateUserDto updateUserDto) {
        if (updateUserDto.isEmpty()) {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }
        UserDto userDto = userService.updateUser(id, updateUserDto);
        EntityModel<UserDto> userModel = EntityModel.of(userDto);
        addUserLinks(userModel, id);
        return ResponseEntity.ok(userModel);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя с указанным идентификатором"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь успешно удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID пользователя", required = true, example = "1")
            @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private void addUserLinks(EntityModel<UserDto> model, Long userId) {

        Link selfLink = linkTo(methodOn(UserController.class).getUserById(userId)).withSelfRel();
        model.add(selfLink);

        Link collectionLink = linkTo(methodOn(UserController.class).getAllUsers()).withRel("users");
        model.add(collectionLink);

        Link updateLink = linkTo(methodOn(UserController.class).updateUser(userId, null)).withRel("update");
        model.add(updateLink);

        Link deleteLink = linkTo(methodOn(UserController.class).deleteUser(userId)).withRel("delete");
        model.add(deleteLink);
    }
}
