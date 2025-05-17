package unicorns.backend.controller;

import io.jsonwebtoken.Jwt;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import unicorns.backend.dto.request.LoginRequest;
import unicorns.backend.dto.response.LoginResponse;
import unicorns.backend.entity.User;
import unicorns.backend.repository.UserRepository;
import unicorns.backend.util.JwtUtil;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;

@RequestMapping("/api/auth")
@RestController
//@RequestMapping("/test")
public class AuthController {
    private final Set<String> blacklistedTokens = new HashSet<>();
    @Autowired
    private UserRepository userRepository;
@Autowired
    private JwtUtil jwtUtil;
@PostMapping("/login")
@Transactional
    public ResponseEntity<?>login(@RequestBody LoginRequest request) {
    Optional<User> optionalUser = userRepository.findByUsername(request.username);
    if(optionalUser.isEmpty()){
        return ResponseEntity.status(401).body("xac thuc ko thanh cong");
    }
    User user = optionalUser.get();
    if(!user.getPassword().equals(request.password)){
        return ResponseEntity.status(401).body("xac thuc ko thanh cong");
    }

    String token = jwtUtil.generateToken(user.getUsername());
    return ResponseEntity.ok(new LoginResponse(token));
}
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            blacklistedTokens.add(token);
            return ResponseEntity.ok("Dang xuat thanh cong");
        }
        return ResponseEntity.badRequest().body("Ko tim thay token");
    }
    @GetMapping("/check")
    public ResponseEntity<String> hello(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Thiếu token");
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validToken(token)) {
            return ResponseEntity.status(401).body("Token không hợp lệ");
        }

        if (blacklistedTokens.contains(token)) {
            return ResponseEntity.status(401).body("Token đã bị đăng xuất");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        return ResponseEntity.ok("hello " + username + "!");
    }
}
//    @Autowired
//    private JwtUtil jwtUtil;
//    @GetMapping("/hehe/haha/generate")
//    public String generateTestToken() {
//        return jwtUtil.generateToken("testuser");
//    }


