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

import com.decodex.br.adapters.out.persistence.adapter.PessoaRepositoryAdapter;
import com.decodex.br.adapters.out.persistence.entity.EnderecoEmbeddable;
import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;
import com.decodex.br.adapters.out.persistence.mapper.PessoaMapper;
import com.decodex.br.adapters.out.persistence.repository.PessoaRepository;
import com.decodex.br.application.dto.pessoa.PessoaFilter;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários - PessoaRepositoryAdapter")
class PessoaRepositoryAdapterTest {

    @Mock
    private PessoaRepository pessoaRepository;

    @Mock
    private PessoaMapper pessoaMapper;

    @InjectMocks
    private PessoaRepositoryAdapter adapter;

    private UUID pessoaId;
    private Pessoa domainPessoa;
    private PessoaEntity pessoaEntity;

    @BeforeEach
    void setUp() {
        pessoaId = UUID.randomUUID();
        Endereco endereco = new Endereco(
            "Rua A", "10", null, "Centro", "00000-000", "São Paulo", "SP"
        );
        domainPessoa = new Pessoa(pessoaId, "João Silva", endereco, true);

        EnderecoEmbeddable enderecoEmbeddable = new EnderecoEmbeddable();
        enderecoEmbeddable.setLogradouro("Rua A");
        enderecoEmbeddable.setNumero("10");
        enderecoEmbeddable.setBairro("Centro");
        enderecoEmbeddable.setCep("00000-000");
        enderecoEmbeddable.setCidade("São Paulo");
        enderecoEmbeddable.setEstado("SP");

        pessoaEntity = new PessoaEntity();
        pessoaEntity.setId(pessoaId);
        pessoaEntity.setNome("João Silva");
        pessoaEntity.setEndereco(enderecoEmbeddable);
        pessoaEntity.setAtivo(true);
    }

    @Test
    @DisplayName("Deve salvar e retornar domínio")
    void save_ShouldPersistAndReturnDomain() {
        when(pessoaMapper.toEntity(domainPessoa)).thenReturn(pessoaEntity);
        when(pessoaRepository.save(pessoaEntity)).thenReturn(pessoaEntity);
        when(pessoaMapper.toDomain(pessoaEntity)).thenReturn(domainPessoa);

        Pessoa result = adapter.save(domainPessoa);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(pessoaId);
        assertThat(result.getNome()).isEqualTo("João Silva");
        assertThat(result.getEndereco().getLogradouro()).isEqualTo("Rua A");
        assertThat(result.getAtivo()).isTrue();

        verify(pessoaMapper).toEntity(domainPessoa);
        verify(pessoaRepository).save(pessoaEntity);
        verify(pessoaMapper).toDomain(pessoaEntity);
    }

    @Test
    @DisplayName("Deve retornar domínio ao buscar por ID existente")
    void findById_WhenExists_ShouldReturnDomain() {
        when(pessoaRepository.findById(pessoaId)).thenReturn(Optional.of(pessoaEntity));
        when(pessoaMapper.toDomain(pessoaEntity)).thenReturn(domainPessoa);

        Optional<Pessoa> result = adapter.findById(pessoaId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(pessoaId);
        assertThat(result.get().getNome()).isEqualTo("João Silva");
        assertThat(result.get().getEndereco().getCidade()).isEqualTo("São Paulo");
        verify(pessoaRepository).findById(pessoaId);
        verify(pessoaMapper).toDomain(pessoaEntity);
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar por ID inexistente")
    void findById_WhenNotExists_ShouldReturnEmpty() {
        UUID nonExistentId = UUID.randomUUID();
        when(pessoaRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Optional<Pessoa> result = adapter.findById(nonExistentId);

        assertThat(result).isEmpty();
        verify(pessoaRepository).findById(nonExistentId);
        verifyNoInteractions(pessoaMapper);
    }

    @SuppressWarnings("unchecked")
	@Test
    @DisplayName("Deve listar pessoas com paginação e filtro")
    void findAll_ShouldReturnPageResult() {
        PessoaFilter filter = new PessoaFilter();
        PageRequest pageRequest = new PageRequest(0, 10);
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                0, 10, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "nome")
        );
        Page<PessoaEntity> page = new PageImpl<>(List.of(pessoaEntity), pageable, 1);

        when(pessoaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(pessoaMapper.toDomain(pessoaEntity)).thenReturn(domainPessoa);

        PageResult<Pessoa> result = adapter.findAll(filter, pageRequest);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).getNome()).isEqualTo("João Silva");
        assertThat(result.content().get(0).getEndereco().getLogradouro()).isEqualTo("Rua A");
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.totalPages()).isEqualTo(1);
        
        verify(pessoaRepository).findAll(any(Specification.class), eq(pageable));
        verify(pessoaMapper).toDomain(pessoaEntity);
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Deve resolver ordenação com resolverSort respeitando campos permitidos e direção descendente")
    void findAll_ComOrdenacaoDescendente_DeveResolverSortCorretamente() {
        PessoaFilter filter = new PessoaFilter();
        PageRequest pageRequest = PageRequest.of(0, 10, "ativo", "desc");
        Pageable pageableEsperado = org.springframework.data.domain.PageRequest.of(
                0, 10, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "ativo")
        );
        Page<PessoaEntity> page = new PageImpl<>(List.of(pessoaEntity), pageableEsperado, 1);

        when(pessoaRepository.findAll(any(Specification.class), eq(pageableEsperado))).thenReturn(page);
        when(pessoaMapper.toDomain(pessoaEntity)).thenReturn(domainPessoa);

        PageResult<Pessoa> result = adapter.findAll(filter, pageRequest);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar pessoa por ID")
    void deleteById_ShouldCallRepository() {
        doNothing().when(pessoaRepository).deleteById(pessoaId);

        adapter.deleteById(pessoaId);

        verify(pessoaRepository).deleteById(pessoaId);
        verifyNoMoreInteractions(pessoaRepository);
    }
}