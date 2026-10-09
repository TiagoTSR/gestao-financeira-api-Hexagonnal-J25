package com.decodex.br.application.dto.pessoa;

import jakarta.validation.constraints.NotNull;

public record PessoaAtivoDTO(
    @NotNull Boolean ativo
) {}
