package unicorns.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicorns.backend.dto.UserDto;
import unicorns.backend.dto.request.BaseRequest;
import unicorns.backend.dto.request.ChangeProfileRequest;
import unicorns.backend.dto.request.CreateUserRequest;
import unicorns.backend.dto.request.ChangePasswordRequest;
import unicorns.backend.dto.response.BaseResponse;
import unicorns.backend.dto.response.ChangePasswordResponse;
import unicorns.backend.dto.response.ChangeProfileResponse;
import unicorns.backend.dto.response.CreateUserResponse;
import unicorns.backend.service.UserService;
import unicorns.backend.util.ApplicationCode;
import unicorns.backend.util.Const;

import java.util.Optional;

@RestController
@RequestMapping(Const.PREFIX_USER_V1)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserController {

    UserService userService;

    @Operation(summary = "get all user", description = "Get all user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully")
    })
    @GetMapping(value = "getAll")
    public BaseResponse<CreateUserResponse> getAllUsers() {
        return userService.getAllUser();
    }

    @Operation(summary = "Create new user", description = "Creates a new user using the provided information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
        @PostMapping(value = "createUser")
    public BaseResponse<CreateUserResponse> createUser(@Valid @RequestBody BaseRequest<CreateUserRequest> request) {
        return userService.createUser(request);
    }
       @GetMapping("/{id}")
    public ResponseEntity<?>getUserById(@PathVariable int id){
           Optional<UserDto> userOpt = userService.getUserDtoById(id);
           return userOpt.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
       }
    @GetMapping("/getProfileUser/{username}")
    public ResponseEntity<BaseResponse<UserDto>> getUserByUsername(@PathVariable String username) {
        Optional<UserDto> userOpt = Optional.ofNullable(userService.getUserByUsername(username));

        return userOpt
                .map(user -> {
                    BaseResponse<UserDto> response = new BaseResponse<>(ApplicationCode.SUCCESS);
                    response.setWsResponse(user);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    BaseResponse<UserDto> response = new BaseResponse<>(ApplicationCode.USER_NOT_FOUND, "User not found");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }
    @PutMapping("/changePassword/{username}")
    public ResponseEntity<?> changePassword(@PathVariable String username,
                                            @RequestBody BaseRequest<ChangePasswordRequest> request) {
        BaseResponse<ChangePasswordResponse> response = userService.changePassword(username, request);
        if (!response.getCode().equals(ApplicationCode.SUCCESS)) {
            return ResponseEntity.badRequest().body(response.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    @PutMapping("/changeProfile/{username}")
    public ResponseEntity<?>changeProfile(@PathVariable String username, @RequestBody BaseRequest<ChangeProfileRequest> request){
        BaseResponse<ChangeProfileResponse> response=userService.changeProfile(username, request);
     //   BaseResponse<String> response = userService.changeProfile(username, request);
        if (!response.getCode().equals(ApplicationCode.SUCCESS)) {
            return ResponseEntity.badRequest().body(response.getMessage());
        }
        return ResponseEntity.ok(response);
    }

}

