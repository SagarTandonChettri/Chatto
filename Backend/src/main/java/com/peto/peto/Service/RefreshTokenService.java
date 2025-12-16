package com.peto.peto.Service;

import com.peto.peto.Dto.RefreshToken;
import com.peto.peto.Repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository repo;

    private final long refreshExpiration = 1000L * 60 * 60 * 24 * 7;

    public RefreshToken createRefreshToken (String username){
        repo.deleteByUsername(username);

        RefreshToken token = RefreshToken.builder()
                .username(username)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshExpiration))
                .build();

        return repo.save(token);
    }

    public Optional<RefreshToken> validateToken(String token){
        return repo.findByToken(token)
                .filter(rt -> rt.getExpiryDate().isAfter(Instant.now()));
    }
}
