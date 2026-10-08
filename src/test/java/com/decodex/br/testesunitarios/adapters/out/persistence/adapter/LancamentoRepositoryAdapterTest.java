package com.decodex.br.testesunitarios.adapters.out.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.decodex.br.adapters.out.persistence.adapter.LancamentoRepositoryAdapter;
import com.decodex.br.adapters.out.persistence.entity.LancamentoEntity;
import com.decodex.br.adapters.out.persistence.mapper.LancamentoMapper;
import com.decodex.br.adapters.out.persistence.repository.LancamentoRepository;
import com.decodex.br.application.dto.lancamento.LancamentoFilter;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.model.TipoLancamento;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários - LancamentoRepositoryAdapter")
class LancamentoRepositoryAdapterTest {

    @Mock
    private LancamentoRepository lancamentoRepository;

    @Mock
    private LancamentoMapper lancamentoMapper;

    @InjectMocks
    private LancamentoRepositoryAdapter adapter;

    private UUID lancamentoId;
    private Lancamento domainLancamento;
    private LancamentoEntity lancamentoEntity;

    @BeforeEach
    void setUp() {
        lancamentoId = UUID.randomUUID();
        Categoria categoria = new Categoria(UUID.randomUUID(), "Alimentação");
        Endereco endereco = new Endereco("Rua A", "10", null, "Centro", "00000-000", "São Paulo", "SP");
        Pessoa pessoa = new Pessoa(UUID.randomUUID(), "João Silva", endereco, true);

        domainLancamento = new Lancamento(
            lancamentoId, "Conta de luz", LocalDate.of(2025, 6, 10), null,
            new BigDecimal("350.00"), "Observação teste", TipoLancamento.DESPESA, categoria, pessoa
        );

        lancamentoEntity = new LancamentoEntity();
        lancamentoEntity.setId(lancamentoId);
        lancamentoEntity.setDescricao("Conta de luz");
    }

    @Test
    @DisplayName("Deve salvar e retornar domínio")
    void save_ShouldPersistAndReturnDomain() {
        when(lancamentoMapper.toEntity(domainLancamento)).thenReturn(lancamentoEntity);
        when(lancamentoRepository.save(lancamentoEntity)).thenReturn(lancamentoEntity);
        when(lancamentoMapper.toDomain(lancamentoEntity)).thenReturn(domainLancamento);

        Lancamento result = adapter.save(domainLancamento);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(lancamentoId);
        assertThat(result.getDescricao()).isEqualTo("Conta de luz");
        verify(lancamentoMapper).toEntity(domainLancamento);
        verify(lancamentoRepository).save(lancamentoEntity);
        verify(lancamentoMapper).toDomain(lancamentoEntity);
    }

    @Test
    @DisplayName("Deve retornar domínio ao buscar por ID existente")
    void findById_WhenExists_ShouldReturnDomain() {
        when(lancamentoRepository.findById(lancamentoId)).thenReturn(Optional.of(lancamentoEntity));
        when(lancamentoMapper.toDomain(lancamentoEntity)).thenReturn(domainLancamento);

        Optional<Lancamento> result = adapter.findById(lancamentoId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(lancamentoId);
        verify(lancamentoRepository).findById(lancamentoId);
        verify(lancamentoMapper).toDomain(lancamentoEntity);
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar por ID inexistente")
    void findById_WhenNotExists_ShouldReturnEmpty() {
        UUID nonExistentId = UUID.randomUUID();
        when(lancamentoRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Optional<Lancamento> result = adapter.findById(nonExistentId);

        assertThat(result).isEmpty();
        verify(lancamentoRepository).findById(nonExistentId);
        verifyNoInteractions(lancamentoMapper);
    }

    @SuppressWarnings("unchecked")
	@Test
    @DisplayName("Deve listar todos os lançamentos com paginação e filtro")
    void findAll_ShouldReturnPageResult() {
        LancamentoFilter filter = new LancamentoFilter();
        PageRequest pageRequest = new PageRequest(0, 10);
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                0, 10, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "dataVencimento")
        );
        Page<LancamentoEntity> page = new PageImpl<>(List.of(lancamentoEntity), pageable, 1);

        when(lancamentoRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(lancamentoMapper.toDomain(lancamentoEntity)).thenReturn(domainLancamento);

        PageResult<Lancamento> result = adapter.findAll(filter, pageRequest);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).getDescricao()).isEqualTo("Conta de luz");
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.totalPages()).isEqualTo(1);
        
        verify(lancamentoRepository).findAll(any(Specification.class), eq(pageable));
        verify(lancamentoMapper).toDomain(lancamentoEntity);
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Deve resolver ordenação com resolverSort respeitando campos permitidos e direção descendente")
    void findAll_ComOrdenacaoDescendente_DeveResolverSortCorretamente() {
        LancamentoFilter filter = new LancamentoFilter();
        PageRequest pageRequest = PageRequest.of(0, 10, "valor", "desc");
        Pageable pageableEsperado = org.springframework.data.domain.PageRequest.of(
                0, 10, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "valor")
        );
        Page<LancamentoEntity> page = new PageImpl<>(List.of(lancamentoEntity), pageableEsperado, 1);

        when(lancamentoRepository.findAll(any(Specification.class), eq(pageableEsperado))).thenReturn(page);
        when(lancamentoMapper.toDomain(lancamentoEntity)).thenReturn(domainLancamento);

        PageResult<Lancamento> result = adapter.findAll(filter, pageRequest);

        assertThat(result).isNotNull();
        verify(lancamentoRepository).findAll(any(Specification.class), eq(pageableEsperado));
    }

    @Test
    @DisplayName("Deve deletar lançamento por ID")
    void deleteById_ShouldCallRepository() {
        doNothing().when(lancamentoRepository).deleteById(lancamentoId);

        adapter.deleteById(lancamentoId);

        verify(lancamentoRepository).deleteById(lancamentoId);
        verifyNoMoreInteractions(lancamentoRepository);
    }
}