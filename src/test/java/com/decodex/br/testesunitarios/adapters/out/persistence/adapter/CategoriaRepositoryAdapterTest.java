package com.decodex.br.testesunitarios.adapters.out.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.decodex.br.adapters.out.persistence.adapter.CategoriaRepositoryAdapter;
import com.decodex.br.adapters.out.persistence.entity.CategoriaEntity;
import com.decodex.br.adapters.out.persistence.mapper.CategoriaMapper;
import com.decodex.br.adapters.out.persistence.repository.CategoriaRepository;
import com.decodex.br.domain.filter.CategoriaFilter;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários - CategoriaRepositoryAdapter")
class CategoriaRepositoryAdapterTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CategoriaMapper categoriaMapper;

    @InjectMocks
    private CategoriaRepositoryAdapter adapter;

    private Categoria domainCategoria;
    private CategoriaEntity categoriaEntity;

    @BeforeEach
    void setUp() {
        domainCategoria = new Categoria(1L, "Alimentação");
        categoriaEntity = new CategoriaEntity();
        categoriaEntity.setId(1L);
        categoriaEntity.setNome("Alimentação");
    }

    @Test
    @DisplayName("Deve salvar e retornar domínio")
    void save_ShouldPersistAndReturnDomain() {
        when(categoriaMapper.toEntity(domainCategoria)).thenReturn(categoriaEntity);
        when(categoriaRepository.save(categoriaEntity)).thenReturn(categoriaEntity);
        when(categoriaMapper.toDomain(categoriaEntity)).thenReturn(domainCategoria);

        Categoria result = adapter.save(domainCategoria);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNome()).isEqualTo("Alimentação");

        verify(categoriaMapper).toEntity(domainCategoria);
        verify(categoriaRepository).save(categoriaEntity);
        verify(categoriaMapper).toDomain(categoriaEntity);
    }

    @Test
    @DisplayName("Deve retornar domínio ao buscar por ID existente")
    void findById_WhenExists_ShouldReturnDomain() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaEntity));
        when(categoriaMapper.toDomain(categoriaEntity)).thenReturn(domainCategoria);

        Optional<Categoria> result = adapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getNome()).isEqualTo("Alimentação");
        verify(categoriaRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar por ID inexistente")
    void findById_WhenNotExists_ShouldReturnEmpty() {
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Categoria> result = adapter.findById(999L);

        assertThat(result).isEmpty();
        verify(categoriaRepository).findById(999L);
        verifyNoInteractions(categoriaMapper);
    }

    @SuppressWarnings("unchecked")
	@Test
    @DisplayName("Deve listar categorias com paginação e filtro")
    void findAll_ShouldReturnPageResult() {
        CategoriaFilter filter = new CategoriaFilter();
        PageRequest pageRequest = new PageRequest(0, 10);
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        Page<CategoriaEntity> page = new PageImpl<>(List.of(categoriaEntity), pageable, 1);

        when(categoriaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(categoriaMapper.toDomain(categoriaEntity)).thenReturn(domainCategoria);

        PageResult<Categoria> result = adapter.findAll(filter, pageRequest);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).getNome()).isEqualTo("Alimentação");
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.totalPages()).isEqualTo(1);
        
        verify(categoriaRepository).findAll(any(Specification.class), eq(pageable));
        verify(categoriaMapper).toDomain(categoriaEntity);
    }

    @Test
    @DisplayName("Deve deletar categoria por ID")
    void deleteById_ShouldCallRepository() {
        doNothing().when(categoriaRepository).deleteById(1L);

        adapter.deleteById(1L);

        verify(categoriaRepository).deleteById(1L);
        verifyNoMoreInteractions(categoriaRepository);
    }
}