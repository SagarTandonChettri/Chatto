package com.peto.peto.Dto;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "refresh_tokens")
@Builder
public class RefreshToken {

    @Id
    private Long id;

    private String username;

    private String token;

    private Instant expiryDate;
}
