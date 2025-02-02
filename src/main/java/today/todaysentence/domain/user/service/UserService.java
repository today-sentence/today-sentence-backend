package today.todaysentence.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import today.todaysentence.domain.user.User;
import today.todaysentence.domain.user.repository.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
