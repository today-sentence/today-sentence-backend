package today.todaysentence.domain.user.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저")
@RestController
public class UserController {

//    @Operation(summary = "회원 가입")
//    @PostMapping("/join")
//    public ResponseEntity<?> join() {
//        return ResponseEntity.ok().build();
//    }
}
