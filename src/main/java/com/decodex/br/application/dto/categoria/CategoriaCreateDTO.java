package com.decodex.br.application.dto.categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaCreateDTO(
		
		@NotBlank
        @Size(min = 3, max = 50)
		String nome
) {}
