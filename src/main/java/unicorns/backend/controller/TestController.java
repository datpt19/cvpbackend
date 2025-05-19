package unicorns.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import unicorns.backend.util.JwtUtil;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/generate")
    public String generateTestToken() {
        return jwtUtil.generateToken("testuser");
    }
}
