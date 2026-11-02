package com.decodex.br.features.autenticacao.application.port.in;

import com.decodex.br.features.autenticacao.application.model.RegisterUserCommand;
import com.decodex.br.features.autenticacao.application.model.RegisteredUser;

public interface RegisterUserUseCase {

    RegisteredUser register(RegisterUserCommand command);
}
