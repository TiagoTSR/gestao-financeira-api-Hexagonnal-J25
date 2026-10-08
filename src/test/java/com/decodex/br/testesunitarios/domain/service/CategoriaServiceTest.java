package com.decodex.br.testesunitarios.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.decodex.br.application.dto.categoria.CategoriaFilter;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.out.CategoriaRepositoryPort;
import com.decodex.br.domain.service.CategoriaService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para CategoriaService")
class CategoriaServiceTest {

    @Mock
    private CategoriaRepositoryPort repository;

    @InjectMocks
    private CategoriaService service;

    private final UUID categoriaId = UUID.randomUUID();
    private final UUID idInexistente = UUID.randomUUID();
    private Categoria categoria;
    private PageRequest pageRequest;

    @BeforeEach
    void setUp() {
        categoria = new Categoria(categoriaId, "Alimentação");
        pageRequest = new PageRequest(0, 10);
    }

    @Test
    @DisplayName("Deve listar categorias paginadas")
    void findAll_ShouldReturnPageResult() {
        CategoriaFilter filter = new CategoriaFilter();
        PageResult<Categoria> pageResult = new PageResult<>(
            List.of(categoria), 0, 10, 1L, 1
        );
        when(repository.findAll(any(CategoriaFilter.class), eq(pageRequest))).thenReturn(pageResult);

        PageResult<Categoria> result = service.findAll(filter, pageRequest);

        assertThat(result.content()).hasSize(1).contains(categoria);
        assertThat(result.page()).isZero();
        assertThat(result.totalElements()).isEqualTo(1L);
        verify(repository, times(1)).findAll(any(CategoriaFilter.class), eq(pageRequest));
    }

    @Test
    @DisplayName("Deve buscar categoria por ID com sucesso")
    void findById_WhenExists_ShouldReturnCategoria() {
        when(repository.findById(categoriaId)).thenReturn(Optional.of(categoria));

        Categoria found = service.findById(categoriaId);

        assertThat(found).isEqualTo(categoria);
        verify(repository, times(1)).findById(categoriaId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar categoria por ID inexistente")
    void findById_WhenNotExists_ShouldThrowException() {
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(idInexistente))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Categoria não encontrado: " + idInexistente);
        verify(repository, times(1)).findById(idInexistente);
    }

    @Test
    @DisplayName("Deve criar nova categoria")
    void create_ShouldSaveAndReturn() {
        when(repository.save(any(Categoria.class))).thenReturn(categoria);

        Categoria created = service.create(categoria);

        assertThat(created).isEqualTo(categoria);
        verify(repository, times(1)).save(categoria);
    }

    @Test
    @DisplayName("Deve atualizar categoria existente")
    void update_ShouldUpdateNomeAndSave() {
        Categoria existing = new Categoria(categoriaId, "Alimentação");
        Categoria updatedDetails = new Categoria(null, "Comida Saudável");
        Categoria expectedUpdated = new Categoria(categoriaId, "Comida Saudável");

        when(repository.findById(categoriaId)).thenReturn(Optional.of(existing));
        when(repository.save(any(Categoria.class))).thenReturn(expectedUpdated);

        Categoria result = service.update(categoriaId, updatedDetails);

        assertThat(result.getNome()).isEqualTo("Comida Saudável");
        verify(repository, times(1)).findById(categoriaId);
        verify(repository, times(1)).save(argThat(c -> 
            c.getId().equals(categoriaId) && c.getNome().equals("Comida Saudável")
        ));
    }

    @Test
    @DisplayName("Deve deletar categoria existente")
    void delete_ShouldDeleteById() {
        when(repository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        doNothing().when(repository).deleteById(categoriaId);

        service.delete(categoriaId);

        verify(repository, times(1)).findById(categoriaId);
        verify(repository, times(1)).deleteById(categoriaId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar categoria inexistente")
    void delete_WhenNotExists_ShouldThrowException() {
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(idInexistente))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Categoria não encontrado: " + idInexistente);

        verify(repository, times(1)).findById(idInexistente);
        verify(repository, never()).deleteById(any());
    }
}