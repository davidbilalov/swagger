package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Информация о пользователе")
public class UserDto {
    @Schema(description = "Уникальный идентификатор пользователя", example = "1")
    private Long id;
    
    @Schema(description = "Имя пользователя", example = "Иван Иванов")
    private String name;
    
    @Schema(description = "Email адрес пользователя", example = "ivan@example.com")
    private String email;
    
    @Schema(description = "Возраст пользователя", example = "25")
    private int age;
    
    @Schema(description = "Дата и время создания записи", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
}
