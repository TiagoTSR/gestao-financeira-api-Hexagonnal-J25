package com.decodex.br.testesunitarios.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.decodex.br.application.dto.categoria.CategoriaCreateDTO;
import com.decodex.br.application.dto.categoria.CategoriaResponseDTO;
import com.decodex.br.application.dto.categoria.CategoriaUpdateDTO;
import com.decodex.br.domain.exception.RegraDeNegocioException;
import com.decodex.br.domain.model.Categoria;

class CategoriaDTOMapperTest {

    // toDomain(Create)

    @Test
    void toDomain_CreateDTO_ShouldConvertCreateDTOToCategoria() {
        // given
        CategoriaCreateDTO createDTO = new CategoriaCreateDTO("Alimentação");

        // when
        Categoria categoria = createDTO.toDomain();

        // then
        assertThat(categoria.getId()).isNull();
        assertThat(categoria.getNome()).isEqualTo("Alimentação");
    }

    // toDomain(Update)

    @Test
    void toDomain_UpdateDTO_ShouldReturnCategoriaComNovosDados() {
        // given
        CategoriaUpdateDTO updateDTO = new CategoriaUpdateDTO("Novo Nome");

        // when
        Categoria novaCategoria = updateDTO.toDomain();

        // then
        assertThat(novaCategoria.getId()).isNull();
        assertThat(novaCategoria.getNome()).isEqualTo("Novo Nome");
    }

    @Test
    void update_ShouldAtualizarCategoria_QuandoToDomainEAtualizarCamposCombinados() {
        // given
        java.util.UUID id = java.util.UUID.randomUUID();
        Categoria existing = new Categoria(id, "Antigo Nome");
        CategoriaUpdateDTO updateDTO = new CategoriaUpdateDTO("Novo Nome");

        // when
        Categoria novosDados = updateDTO.toDomain();
        existing.atualizarCampos(novosDados);

        // then
        assertThat(existing.getId()).isEqualTo(id); 
        assertThat(existing.getNome()).isEqualTo("Novo Nome");
    }

    // from (Response)

    @Test
    void from_ShouldConvertCategoriaToResponseDTO() {
        // given
        java.util.UUID catId = java.util.UUID.randomUUID();
        Categoria categoria = new Categoria(catId, "Transporte");

        // when
        CategoriaResponseDTO responseDTO = CategoriaResponseDTO.from(categoria);

        // then
        assertThat(responseDTO.id()).isEqualTo(catId);
        assertThat(responseDTO.nome()).isEqualTo("Transporte");
    }

    @Test
    void from_ShouldReturnNull_WhenCategoriaIsNull() {
        // when
        CategoriaResponseDTO responseDTO = CategoriaResponseDTO.from(null);

        // then
        assertThat(responseDTO).isNull();
    }
    
    @Test
    void categoriaConstructor_ShouldThrow_WhenNomeIsNullOrBlank() {
        assertThatThrownBy(() -> new Categoria(null, null))
            .isInstanceOf(RegraDeNegocioException.class)
            .hasMessageContaining("Nome");

        assertThatThrownBy(() -> new Categoria(null, "   "))
            .isInstanceOf(RegraDeNegocioException.class)
            .hasMessageContaining("Nome");
    }
}