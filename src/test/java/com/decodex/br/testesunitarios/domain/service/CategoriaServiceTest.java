package com.decodex.br.testesunitarios.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.port.out.CategoriaRepositoryPort;
import com.decodex.br.domain.service.CategoriaService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para CategoriaService")
class CategoriaServiceTest {

    @Mock
    private CategoriaRepositoryPort repository;

    @InjectMocks
    private CategoriaService service;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria(1L, "Alimentação");
    }

    @Test
    @DisplayName("Deve listar todas as categorias")
    void findAll_ShouldReturnList() {
        when(repository.findAll()).thenReturn(List.of(categoria));

        List<Categoria> result = service.findAll();

        assertThat(result).hasSize(1).contains(categoria);
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar categoria por ID com sucesso")
    void findById_WhenExists_ShouldReturnCategoria() {
        when(repository.findById(1L)).thenReturn(Optional.of(categoria));

        Categoria found = service.findById(1L);

        assertThat(found).isEqualTo(categoria);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar categoria por ID inexistente")
    void findById_WhenNotExists_ShouldThrowException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Categoria não encontrado: 99");
        verify(repository, times(1)).findById(99L);
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
        Categoria existing = new Categoria(1L, "Alimentação");
        Categoria updatedDetails = new Categoria(null, "Comida Saudável");
        Categoria expectedUpdated = new Categoria(1L, "Comida Saudável");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Categoria.class))).thenReturn(expectedUpdated);

        Categoria result = service.update(1L, updatedDetails);

        assertThat(result.getNome()).isEqualTo("Comida Saudável");
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(argThat(c -> 
            c.getId().equals(1L) && c.getNome().equals("Comida Saudável")
        ));
    }

    @Test
    @DisplayName("Deve deletar categoria existente")
    void delete_ShouldDeleteById() {
        when(repository.findById(1L)).thenReturn(Optional.of(categoria));
        doNothing().when(repository).deleteById(1L);

        service.delete(1L);

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar categoria inexistente")
    void delete_WhenNotExists_ShouldThrowException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Categoria não encontrado: 99");

        verify(repository, times(1)).findById(99L);
        verify(repository, never()).deleteById(any());
    }
}