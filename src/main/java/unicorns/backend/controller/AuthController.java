package unicorns.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import unicorns.backend.dto.request.BaseRequest;
import unicorns.backend.dto.request.LoginRequest;
import unicorns.backend.dto.response.BaseResponse;
import unicorns.backend.dto.response.LoginResponse;
import unicorns.backend.entity.User;
import unicorns.backend.repository.UserRepository;
import unicorns.backend.util.ApplicationCode;
import unicorns.backend.util.JwtUtil;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RequestMapping("/api/auth")
@RestController
public class AuthController {
    private final Set<String> blacklistedTokens = new HashSet<>();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestBody BaseRequest<LoginRequest> baseRequest) {
        LoginRequest request = baseRequest.getWsRequest();
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());
        if (optionalUser.isEmpty()) {
            BaseResponse<LoginResponse> response = new BaseResponse<>(401, "Xác thực không thành công");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        User user = optionalUser.get();
        if (!user.getPassword().equals(request.getPassword())) {
            BaseResponse<LoginResponse> response = new BaseResponse<>(401, "Xác thực không thành công");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        String token = jwtUtil.generateToken(user.getUsername());
        LoginResponse loginResponse = new LoginResponse(token);
        BaseResponse<LoginResponse> response = new BaseResponse<>(ApplicationCode.SUCCESS);
        response.setWsResponse(loginResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<String>> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            blacklistedTokens.add(token);
            BaseResponse<String> response = new BaseResponse<>(ApplicationCode.SUCCESS);
            response.setWsResponse("Đăng xuất thành công");
            return ResponseEntity.ok(response);
        }
        BaseResponse<String> response = new BaseResponse<>(400, "Không tìm thấy token");
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/check")
    public ResponseEntity<BaseResponse<String>> hello(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            BaseResponse<String> response = new BaseResponse<>(401, "Thiếu token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validToken(token)) {
            BaseResponse<String> response = new BaseResponse<>(401, "Token không hợp lệ");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        if (blacklistedTokens.contains(token)) {
            BaseResponse<String> response = new BaseResponse<>(401, "Token đã bị đăng xuất");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        String username = jwtUtil.getUsernameFromToken(token);
        BaseResponse<String> response = new BaseResponse<>(ApplicationCode.SUCCESS);
        response.setWsResponse("hello " + username + "!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/generate")
    public ResponseEntity<BaseResponse<String>> generateTestToken() {
        String token = jwtUtil.generateToken("light");
        BaseResponse<String> response = new BaseResponse<>(ApplicationCode.SUCCESS);
        response.setWsResponse(token);
        return ResponseEntity.ok(response);
    }
}
