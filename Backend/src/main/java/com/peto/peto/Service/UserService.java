package com.peto.peto.Service;

import com.peto.peto.Entity.User;
import com.peto.peto.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User registerUser(User user) {
        log.info("Register start: {}", user.getEmail());
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    //loginByEMail
    public User loginByEmail(String email, String password) {
        log.info("login service start: User Data email:{},password:{}", email,password);
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return null; // email not found
        }

        if (!user.getPassword().equals(password)) {
            return null; // password wrong
        }

        return user; // success
    }

    public User loginByUsername(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public User checkUser(String username){
        return userRepository.findByUsername(username);
    }
}
