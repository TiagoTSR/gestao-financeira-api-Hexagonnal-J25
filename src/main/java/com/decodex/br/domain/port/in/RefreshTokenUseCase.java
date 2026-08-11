package com.decodex.br.domain.port.in;

import com.decodex.br.domain.model.RefreshToken;

public interface RefreshTokenUseCase {

    RefreshToken create(String username);

    RefreshToken verifyExpiration(RefreshToken token);

    void deleteByToken(String token);

    RefreshToken verifyAndGet(String token);
}
