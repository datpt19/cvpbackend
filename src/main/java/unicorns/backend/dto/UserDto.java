package unicorns.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserDto {
    private String username;
    private String name;
    private LocalDateTime dateOfBirth;
    private String email;
}

