package com.kevin.linebotdemo.controller;

import com.kevin.linebotdemo.model.WebUser;
import com.kevin.linebotdemo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author liyanting
 */
@RestController
@AllArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @GetMapping("/all")
    public List<WebUser> showUser() {
        return userRepository.findAll();
    }

    @GetMapping("/save/{name}")
    public void addUser(@PathVariable("name") String name) {
        WebUser user = new WebUser();
        user.setName(name);
        userRepository.save(user);
    }


}
