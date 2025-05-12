@Data
public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}