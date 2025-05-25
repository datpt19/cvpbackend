package unicorns.backend.service;

import unicorns.backend.dto.UserDto;
import unicorns.backend.dto.request.BaseRequest;
import unicorns.backend.dto.request.CreateUserRequest;
import unicorns.backend.dto.response.BaseResponse;
import unicorns.backend.dto.response.CreateUserResponse;

import java.util.Optional;

public interface UserService {
    BaseResponse<CreateUserResponse> createUser(BaseRequest<CreateUserRequest> request);
    BaseResponse<CreateUserResponse> getAllUser();
    UserDto getUserByUsername(String username);
   Optional<UserDto> getUserDtoById(int id);
}
