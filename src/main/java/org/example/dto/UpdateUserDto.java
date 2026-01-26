package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Данные для обновления пользователя (все поля опциональны)")
public class UpdateUserDto {
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    @Schema(description = "Имя пользователя", example = "Иван Иванов", minLength = 1, maxLength = 100)
    private String name;

    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Schema(description = "Email адрес пользователя", example = "ivan@example.com", maxLength = 255)
    private String email;

    @Min(value = 1, message = "Age must be at least 1")
    @Schema(description = "Возраст пользователя", example = "25", minimum = "1")
    private Integer age;

    public boolean isEmpty() {
        return name == null && email == null && age == null;
    }
}
