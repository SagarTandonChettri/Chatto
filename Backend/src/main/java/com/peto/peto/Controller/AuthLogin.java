package com.peto.peto.Controller;

import com.peto.peto.ApiResponse.ApiResponse;
import com.peto.peto.Dto.LoginRequest;
import com.peto.peto.Dto.RefreshToken;
import com.peto.peto.Entity.User;
import com.peto.peto.JwtFile.JwtUtil;
import com.peto.peto.Service.RefreshTokenService;
import com.peto.peto.Service.UserService;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthLogin {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletResponse response){
        log.info("LOGIN ATTEMPT: {}", req.getUsername());

        // ⛔ First check username + password with your method
        User user = userService.loginByUsername(req.getUsername(), req.getPassword());

        if (user == null) {
            log.warn("LOGIN FAILED: Invalid credentials -> {}", req.getUsername());
            return ResponseEntity.status(401).body(
                    ApiResponse.builder()
                            .success(false)
                            .message("Invalid username or password")
                            .data(null)
                            .build()
            );
        }

        // ✔ Generate access token
        String accessToken = jwtUtil.generateToken(user.getUsername());

        // ✔ Create refresh token (or update existing one)
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        Map<String, Object> data = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "accessToken", accessToken
        );

        // ✔ Set refresh token inside HttpOnly cookie
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken.getToken())
                .httpOnly(true)
                .secure(false) // true IN PRODUCTION
                .sameSite("Strict")
                .path("/")
                .maxAge(30 * 24 * 60 * 60) // 30 days
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.builder()
                        .success(true)
                        .message("Login successful")
                        .data(data)
                        .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@CookieValue("refresh_token") String token) {

        return refreshTokenService.validateToken(token)
                .map(rt -> {
                    String newAccessToken = jwtUtil.generateToken(rt.getUsername());
                    return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
                })
                .orElseGet(() -> ResponseEntity.status(403).body(Map.of("error", "Invalid refresh token")));
    }
}
