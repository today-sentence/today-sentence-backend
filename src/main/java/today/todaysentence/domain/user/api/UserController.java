package today.todaysentence.domain.user.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import today.todaysentence.domain.user.service.UserService;
import today.todaysentence.domain.user.util.JwtUtil;

@Tag(name = "유저")
@RestController("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final JwtUtil jwtUtil;

    @Operation(summary = "회원 가입")
    @PostMapping("/join")
    public ResponseEntity<?> join() {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {

        String userEmail = userService.authenticate(email, password);
        String token = jwtUtil.generateToken(userEmail);
        return ResponseEntity.ok(token);
    }
}
