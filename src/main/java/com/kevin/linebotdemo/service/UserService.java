package com.kevin.linebotdemo.service;

import com.kevin.linebotdemo.model.Users;
import com.kevin.linebotdemo.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * @author liyanting
 */
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // TODO refactor
    public Users getUser(String userId) {
        if (hasUser(userId)) {
            return userRepository.findById(userId).get();
        } else {
            return createUser(userId);
        }
    }

    @Transactional(readOnly = true)
    public Boolean hasUser(String userId) {
        Optional<Users> optionalUser = userRepository.findById(userId);
        return optionalUser.isPresent();
    }

    @Transactional(rollbackFor = Exception.class)
    public Users createUser(String userId) {
        val user = new Users(userId, Instant.now(), Instant.now());
        return userRepository.save(user);
    }
}
