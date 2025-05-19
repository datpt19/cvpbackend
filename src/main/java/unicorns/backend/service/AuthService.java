package unicorns.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import unicorns.backend.entity.User;
import unicorns.backend.repository.UserRepository;
import unicorns.backend.util.ApplicationCode;
import unicorns.backend.util.ApplicationException;
import unicorns.backend.util.JwtUtil;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public AuthService(JwtUtil jwtUtil, BCryptPasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public String login(String username, String password) throws ApplicationException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationException(ApplicationCode.USER_NOT_FOUND));


        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ApplicationException(ApplicationCode.INVALID_PASSWORD);
        }
        return jwtUtil.generateToken(username);
    }
}