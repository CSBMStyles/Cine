package com.unicine.transfer.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Par de tokens tras login, refresh o registro futuro.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParTokensResponse {

    private String accessToken;

    private String refreshToken;

    @Builder.Default
    private String tipoToken = "Bearer";
}
