package org.example.model.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class JwtResponse {
    private String username;
    private String accessToken;
    private String refreshToken;
}
