package com.decodex.br.adapters.in.web.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import com.decodex.br.application.dto.auth.LoginRequestDTO;
import com.decodex.br.application.dto.auth.TokenResponseDTO;
import jakarta.validation.Valid;

@Tag(name = "Autenticação", description = "Endpoints para autenticação de usuários e geração de tokens JWT")
public interface AuthControllerDoc {

    @Operation(summary = "Autenticar usuário", description = "Realiza a autenticação com usuário e senha cadastrados no banco de dados e retorna um token JWT de acesso.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autenticado com sucesso, retorna o token JWT",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados de login vazios ou inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas (usuário ou senha incorretos)", content = @Content)
    })
    ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequest);
}
