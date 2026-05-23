package com.decodex.br.testesunitarios.adapters.out.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
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

import com.decodex.br.adapters.out.persistence.adapter.PessoaRepositoryAdapter;
import com.decodex.br.adapters.out.persistence.entity.EnderecoEmbeddable;
import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;
import com.decodex.br.adapters.out.persistence.mapper.PessoaMapper;
import com.decodex.br.adapters.out.persistence.repository.PessoaRepository;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Pessoa;



@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários - PessoaRepositoryAdapter")
class PessoaRepositoryAdapterTest {

    @Mock
    private PessoaRepository pessoaRepository;

    @Mock
    private PessoaMapper pessoaMapper;

    @InjectMocks
    private PessoaRepositoryAdapter adapter;

    private Pessoa domainPessoa;
    private PessoaEntity pessoaEntity;

    @BeforeEach
    void setUp() {

        Endereco endereco = new Endereco(
            "Rua A", "10", null, "Centro", "00000-000", "São Paulo", "SP"
        );
        domainPessoa = new Pessoa(1L, "João Silva", endereco, true);

        EnderecoEmbeddable enderecoEmbeddable = new EnderecoEmbeddable();
        enderecoEmbeddable.setLogradouro("Rua A");
        enderecoEmbeddable.setNumero("10");
        enderecoEmbeddable.setBairro("Centro");
        enderecoEmbeddable.setCep("00000-000");
        enderecoEmbeddable.setCidade("São Paulo");
        enderecoEmbeddable.setEstado("SP");

        pessoaEntity = new PessoaEntity();
        pessoaEntity.setId(1L);
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
        assertThat(result.getId()).isEqualTo(1L);
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
        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoaEntity));
        when(pessoaMapper.toDomain(pessoaEntity)).thenReturn(domainPessoa);

        Optional<Pessoa> result = adapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getNome()).isEqualTo("João Silva");
        assertThat(result.get().getEndereco().getCidade()).isEqualTo("São Paulo");
        verify(pessoaRepository).findById(1L);
        verify(pessoaMapper).toDomain(pessoaEntity);
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar por ID inexistente")
    void findById_WhenNotExists_ShouldReturnEmpty() {
        when(pessoaRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Pessoa> result = adapter.findById(999L);

        assertThat(result).isEmpty();
        verify(pessoaRepository).findById(999L);
        verifyNoInteractions(pessoaMapper);
    }

    @Test
    @DisplayName("Deve listar todas as pessoas")
    void findAll_ShouldReturnAll() {
        when(pessoaRepository.findAll()).thenReturn(List.of(pessoaEntity));
        when(pessoaMapper.toDomain(pessoaEntity)).thenReturn(domainPessoa);

        List<Pessoa> result = adapter.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("João Silva");
        assertThat(result.get(0).getEndereco().getLogradouro()).isEqualTo("Rua A");
        verify(pessoaRepository).findAll();
        verify(pessoaMapper).toDomain(pessoaEntity);
    }

    @Test
    @DisplayName("Deve deletar pessoa por ID")
    void deleteById_ShouldCallRepository() {
        doNothing().when(pessoaRepository).deleteById(1L);

        adapter.deleteById(1L);

        verify(pessoaRepository).deleteById(1L);
        verifyNoMoreInteractions(pessoaRepository);
    }
}