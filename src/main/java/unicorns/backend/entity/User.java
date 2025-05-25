package unicorns.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import unicorns.backend.util.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(schema = Schema.BACKEND_APP)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity {
    String username;
    String password;
    String email;
    String name;
    LocalDateTime lastLogin;
    LocalDate dateOfBirth;
    LocalDateTime passExpireTime;
    Boolean changeFirstPass;
    Integer countFail;
    Integer status;
}
