package com.decodex.br.testesunitarios.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.decodex.br.application.dto.categoria.CategoriaCreateDTO;
import com.decodex.br.application.dto.categoria.CategoriaResponseDTO;
import com.decodex.br.application.dto.categoria.CategoriaUpdateDTO;
import com.decodex.br.application.mapper.CategoriaDTOMapper;
import com.decodex.br.domain.model.Categoria;

class CategoriaDTOMapperTest {

    // toDomain(CreateDTO)

    @Test
    void toDomain_CreateDTO_ShouldConvertCreateDTOToCategoria() {
        // given
        CategoriaCreateDTO createDTO = new CategoriaCreateDTO("Alimentação");

        // when
        Categoria categoria = CategoriaDTOMapper.toDomain(createDTO);

        // then
        assertThat(categoria.getId()).isNull();
        assertThat(categoria.getNome()).isEqualTo("Alimentação");
    }

    @Test
    void toDomain_CreateDTO_ShouldReturnNull_WhenDTOIsNull() {
        // when
        Categoria categoria = CategoriaDTOMapper.toDomain((CategoriaCreateDTO) null);

        // then
        assertThat(categoria).isNull();
    }

    // toDomain(UpdateDTO)

    @Test
    void toDomain_UpdateDTO_ShouldReturnCategoriaComNovosDados() {
        // given
        CategoriaUpdateDTO updateDTO = new CategoriaUpdateDTO("Novo Nome");

        // when
        Categoria novaCategoria = CategoriaDTOMapper.toDomain(updateDTO);

        // then
        assertThat(novaCategoria.getId()).isNull();
        assertThat(novaCategoria.getNome()).isEqualTo("Novo Nome");
    }

    @Test
    void toDomain_UpdateDTO_ShouldReturnNull_WhenDTOIsNull() {
        // when
        Categoria categoria = CategoriaDTOMapper.toDomain((CategoriaUpdateDTO) null);

        // then
        assertThat(categoria).isNull();
    }

    @Test
    void update_ShouldAtualizarCategoria_QuandoToDomainEAtualizarCamposCombinados() {
        // given
        Categoria existing = new Categoria(1L, "Antigo Nome");
        CategoriaUpdateDTO updateDTO = new CategoriaUpdateDTO("Novo Nome");

        // when
        Categoria novosDados = CategoriaDTOMapper.toDomain(updateDTO);
        existing.atualizarCampos(novosDados);

        // then
        assertThat(existing.getId()).isEqualTo(1L); 
        assertThat(existing.getNome()).isEqualTo("Novo Nome");
    }

    // toDTO

    @Test
    void toDTO_ShouldConvertCategoriaToResponseDTO() {
        // given
        Categoria categoria = new Categoria(10L, "Transporte");

        // when
        CategoriaResponseDTO responseDTO = CategoriaDTOMapper.toDTO(categoria);

        // then
        assertThat(responseDTO.id()).isEqualTo(10L);
        assertThat(responseDTO.nome()).isEqualTo("Transporte");
    }

    @Test
    void toDTO_ShouldReturnNull_WhenCategoriaIsNull() {
        // when
        CategoriaResponseDTO responseDTO = CategoriaDTOMapper.toDTO(null);

        // then
        assertThat(responseDTO).isNull();
    }
    
    @Test
    void categoriaConstructor_ShouldThrow_WhenNomeIsNullOrBlank() {
        assertThatThrownBy(() -> new Categoria(null, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Nome");

        assertThatThrownBy(() -> new Categoria(null, "   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Nome");
    }
}