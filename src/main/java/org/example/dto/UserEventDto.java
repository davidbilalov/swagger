package org.example.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class UserEventDto {
    private String operation;
    private String email;

    @JsonCreator
    public UserEventDto(@JsonProperty("operation") String operation,
                       @JsonProperty("email") String email) {
        this.operation = operation;
        this.email = email;
    }

}
