package org.example.dto;

import jakarta.validation.constraints.*;

public class UpdateUserDto {
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Min(value = 1, message = "Age must be at least 1")
    private Integer age;

    public UpdateUserDto() {
    }

    public UpdateUserDto(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public boolean isEmpty() {
        return name == null && email == null && age == null;
    }
}
