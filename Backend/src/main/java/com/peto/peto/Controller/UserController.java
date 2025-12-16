package com.peto.peto.Controller;

import com.peto.peto.ApiResponse.ApiResponse;
import com.peto.peto.Dto.LoginRequest;
import com.peto.peto.Dto.UserCheck;
import com.peto.peto.Entity.User;
import com.peto.peto.JwtFile.JwtUtil;
import com.peto.peto.Service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/profile")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody User user){

        log.info("REGISTER REQUEST: {}", user);

        if(userService.existsByEmail(user.getEmail())){

            log.warn("REGISTER FAILED: Email already exists -> {}", user.getEmail());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Email Already registered")
                            .data(null)
                            .build());
        }
        User savedUser = userService.registerUser(user);

        log.info("REGISTER SUCCESS: New user created -> id={}, username={}",
                savedUser.getId(), savedUser.getUsername());

        Map<String,Object> userData = Map.of(
                "id",savedUser.getId(),
                "username",savedUser.getUsername(),
                "email",savedUser.getEmail()
        );

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("User register successfully")
                        .data(userData)
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {

        log.info("LOGIN ATTEMPT: {}", request.getUsername());

        User user = userService.loginByUsername(request.getUsername(), request.getPassword());

        if (user == null) {
            log.warn("LOGIN FAILED: Invalid credentials -> {}", request.getUsername());
            return ResponseEntity.status(401).body(
                    ApiResponse.builder()
                            .success(false)
                            .message("Invalid username or password")
                            .data(null)
                            .build()
            );
        }

        log.info("LOGIN SUCCESS: {}", user.getUsername());
        String token = jwtUtil.generateToken(user.getUsername());

        Map<String, Object> data = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "token",token
        );

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Login successful")
                        .data(data)
                        .build()
        );
    }

    @PostMapping("/checkUser")
    public ResponseEntity<ApiResponse> homeCheck(@RequestBody UserCheck userCheck){
        log.info("User Check ATTEMPT: {}", userCheck.getUserName());
        User user = userService.checkUser(userCheck.getUserName());

        if (user == null) {
            log.warn("Wrong UseName FAILED: Invalid credentials -> {}", userCheck.getUserName());
            return ResponseEntity.status(401).body(
                    ApiResponse.builder()
                            .success(false)
                            .message("Invalid username")
                            .data(null)
                            .build()
            );
        }
        log.info("User Found SUCCESS Found UserName: {}", user.getUsername());
        Map<String, Object> data = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail()
        );

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Login successful")
                        .data(data)
                        .build()
        );
    }
}
