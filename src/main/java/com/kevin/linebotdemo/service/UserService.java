package com.kevin.linebotdemo.service;

import com.kevin.linebotdemo.config.LineBotConfig;
import com.kevin.linebotdemo.model.Users;
import com.kevin.linebotdemo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;

/**
 * @author liyanting
 */
@Service
@AllArgsConstructor
public class UserService {

    UserRepository userRepository;

    @Transactional(rollbackOn = {Exception.class})
    public Users findUser(String userId) {
        Optional<Users> optionalUser = userRepository.findById(userId);
        Users user;
        if (!optionalUser.isPresent()) {
            user = userRepository.save(new Users(userId, Instant.now(), Instant.now()));
        } else {
            user = optionalUser.get();
        }
        return user;
    }
}
