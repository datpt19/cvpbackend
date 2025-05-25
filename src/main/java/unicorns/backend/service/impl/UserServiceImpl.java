package unicorns.backend.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import unicorns.backend.dto.UserDto;
import unicorns.backend.dto.request.BaseRequest;
import unicorns.backend.dto.request.CreateUserRequest;
import unicorns.backend.dto.request.ChangeProfileRequest;
import unicorns.backend.dto.response.BaseResponse;
import unicorns.backend.dto.response.ChangePasswordResponse;
import unicorns.backend.dto.response.CreateUserResponse;
import unicorns.backend.dto.response.ChangeProfileResponse;
import unicorns.backend.entity.User;
import unicorns.backend.repository.UserRepository;
import unicorns.backend.service.UserService;
import unicorns.backend.util.ApplicationCode;
import unicorns.backend.util.ApplicationException;
import unicorns.backend.util.Const;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import unicorns.backend.dto.request.ChangePasswordRequest;
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private     UserRepository userRepository;

    @Override
    public BaseResponse<CreateUserResponse> createUser(BaseRequest<CreateUserRequest> request) {
        CreateUserRequest createUserRequest = request.getWsRequest();
        Optional<User> userOptional = userRepository.findByUsername(createUserRequest.getUsername());
        if (userOptional.isPresent()) {
            User userExists = userOptional.get();
            if (Const.STATUS.DEACTIVATE.equals(userExists.getStatus())) {
                throw new ApplicationException(ApplicationCode.USER_DEACTIVATE);
            }
            throw new ApplicationException(ApplicationCode.USER_EXITS);
        }
        User user = new User();
        BeanUtils.copyProperties(createUserRequest, user);
        user.setStatus(Const.STATUS.ACTIVE);
        userRepository.save(user);
        BaseResponse baseResponse = BaseResponse.success();
        CreateUserResponse createUserResponse = new CreateUserResponse();
        BeanUtils.copyProperties(user, createUserResponse);
        baseResponse.setWsResponse(createUserResponse);
        return baseResponse;
    }
    @Override
    public BaseResponse<CreateUserResponse> getAllUser() {
        List<User> userList = userRepository.findAll();
        List<CreateUserResponse> createUserResponseList = new ArrayList<>();
        userList.forEach(
                i -> {
                    CreateUserResponse createUserResponse = new CreateUserResponse();
                    BeanUtils.copyProperties(i, createUserResponse);
                    createUserResponseList.add(createUserResponse);
                }
        );
        BaseResponse baseResponse = BaseResponse.success();
        baseResponse.setWsResponse(createUserResponseList);
        return baseResponse;
    }
    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";
        return password != null && password.matches(passwordRegex);
    }
        @Override
        public UserDto getUserByUsername(String username) {
            Optional<User> userOptional = userRepository.findByUsername(username);
            if(!userOptional.isPresent()){
                throw new ApplicationException(ApplicationCode.USER_NOT_FOUND);
            }
            UserDto response = new UserDto();
            BeanUtils.copyProperties(userOptional.get(), response);
            return response;
        }
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public BaseResponse<ChangePasswordResponse> changePassword(String username, BaseRequest<ChangePasswordRequest> request) {
        ChangePasswordRequest changePasswordRequest= request.getWsRequest();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationException(ApplicationCode.USER_NOT_FOUND));
        System.out.println( changePasswordRequest.getOldPassword());
        System.out.println( changePasswordRequest.getNewPassword());
        System.out.println(user.getPassword());
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
         //   System.out.println("Sai mật khẩu cũ");
            return new BaseResponse<>(ApplicationCode.INVALID_OLD_PASSWORD,"Mật khẩu cũ bị sai");
        }
        if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
          //  System.out.println("Mật khẩu mới trùng mật khẩu cũ");
            return new BaseResponse<>(ApplicationCode.WRONG_NEW_PASSWORD, "Mật khẩu mới phải khác mật khẩu cũ");
        }
        if (!isValidPassword(changePasswordRequest.getNewPassword())) {
            return new BaseResponse<>(ApplicationCode.INVALID_PASSWORD_FORMAT, "Mật khẩu mới không hợp lệ: phải có ít nhất 8 ký tự gồm chữ hoa, chữ thường, số và ký tự đặc biệt.");
        }
        String encodedNewPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
        user.setPassword(encodedNewPassword);
        userRepository.save(user);
        BaseResponse baseResponse = BaseResponse.success();
        baseResponse.setWsResponse("Mật khẩu đã được thay đổi");
        return baseResponse;
    }
    @Override
    public BaseResponse<ChangeProfileResponse>changeProfile(String username,BaseRequest<ChangeProfileRequest> request){
        ChangeProfileRequest changeProfileRequest= request.getWsRequest();
        User user= userRepository.findByUsername(username)
                .orElseThrow(()->new ApplicationException(ApplicationCode.USER_NOT_FOUND));
        if(changeProfileRequest.getFullName() != null &&!changeProfileRequest.getFullName().isEmpty()){
            user.setName(changeProfileRequest.getFullName());
        }
        if(changeProfileRequest.getEmail()!=null&&!changeProfileRequest.getEmail().isEmpty()){
            user.setEmail(changeProfileRequest.getEmail());
        }
        if(changeProfileRequest.getDateOfBirth()!=null){
            user.setDateOfBirth(changeProfileRequest.getDateOfBirth());
        }
        userRepository.save(user);
        BaseResponse baseResponse = BaseResponse.success();
        baseResponse.setWsResponse("Thông tin đã được cập nhật");
        System.out.println(changeProfileRequest.getFullName());
        System.out.println(changeProfileRequest.getEmail());
        return baseResponse;
    }
    @Override
    public Optional<UserDto> getUserDtoById(int id) {
        return Optional.empty();
    }
}
