package unicorns.backend.service;

import unicorns.backend.dto.UserDto;
import unicorns.backend.dto.request.BaseRequest;
import unicorns.backend.dto.request.ChangeProfileRequest;
import unicorns.backend.dto.request.CreateUserRequest;
import unicorns.backend.dto.request.ChangePasswordRequest;
import unicorns.backend.dto.response.BaseResponse;
import unicorns.backend.dto.response.ChangePasswordResponse;
import unicorns.backend.dto.response.CreateUserResponse;
import unicorns.backend.dto.response.ChangeProfileResponse;

import java.util.Optional;

public interface UserService {
    BaseResponse<CreateUserResponse> createUser(BaseRequest<CreateUserRequest> request);
    BaseResponse<CreateUserResponse> getAllUser();
    UserDto getUserByUsername(String username);
   Optional<UserDto> getUserDtoById(int id);
    BaseResponse<ChangePasswordResponse> changePassword(String username, BaseRequest<ChangePasswordRequest> request);
    BaseResponse<ChangeProfileResponse>changeProfile(String username,BaseRequest<ChangeProfileRequest> request);

   // BaseResponse<String> changeProfile(String username, ChangeProfileRequest request);
}
