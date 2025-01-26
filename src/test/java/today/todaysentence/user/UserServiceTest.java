package today.todaysentence.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import today.todaysentence.domain.user.entity.User;
import today.todaysentence.domain.user.repository.UserRepository;
import today.todaysentence.domain.user.service.UserService;
import today.todaysentence.domain.user.util.JwtUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("local")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password123")); // 암호화된 비밀번호 저장
        userRepository.save(user);
    }

    @Test
    void authenticate_ValidCredentials_ReturnsEmail() {
        String email = userService.authenticate("test@example.com", "password123");
        assertThat(email).isEqualTo("test@example.com");
    }

    @Test
    void authenticate_InvalidPassword_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.authenticate("test@example.com", "wrongpassword"));
    }

    @Test
    void authenticate_InvalidEmail_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.authenticate("wrong@example.com", "password123"));
    }

    @Test
    void generateToken_ValidEmail_ReturnsToken() {
        String token = jwtUtil.generateToken("test@example.com");
        assertThat(token).isNotEmpty();
    }

    @Test
    void validateToken_ValidToken_ReturnsEmail() {
        String token = jwtUtil.generateToken("test@example.com");
        String email = jwtUtil.validateToken(token);
        assertThat(email).isEqualTo("test@example.com");
    }
}
