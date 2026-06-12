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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.decodex.br.domain.filter.PessoaFilter;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.out.PessoaRepositoryPort;
import com.decodex.br.domain.service.PessoaService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para PessoaService")
class PessoaServiceTest {

    @Mock
    private PessoaRepositoryPort repository;

    @InjectMocks
    private PessoaService service;

    private Endereco endereco;
    private Pessoa pessoa;
    private PageRequest pageRequest;

    @BeforeEach
    void setUp() {
        endereco = new Endereco(
            "Rua A", "123", null, "Centro", "12345-678", "São Paulo", "SP"
        );
        pessoa = new Pessoa(1L, "João Silva", endereco, true);
        pageRequest = new PageRequest(0, 10);
    }

    @Test
    @DisplayName("Deve listar pessoas paginadas")
    void findAll_ShouldReturnPageResult() {
        PessoaFilter filter = new PessoaFilter();
        PageResult<Pessoa> pageResult = new PageResult<>(
            List.of(pessoa), 0, 10, 1L, 1
        );
        when(repository.findAll(any(PessoaFilter.class), eq(pageRequest))).thenReturn(pageResult);

        PageResult<Pessoa> result = service.findAll(filter, pageRequest);

        assertThat(result.content()).hasSize(1).contains(pessoa);
        assertThat(result.page()).isZero();
        assertThat(result.totalElements()).isEqualTo(1L);
        verify(repository, times(1)).findAll(any(PessoaFilter.class), eq(pageRequest));
    }

    @Test
    @DisplayName("Deve buscar pessoa por ID com sucesso")
    void findById_WhenExists_ShouldReturnPessoa() {
        when(repository.findById(1L)).thenReturn(Optional.of(pessoa));

        Pessoa found = service.findById(1L);

        assertThat(found).isEqualTo(pessoa);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar pessoa por ID inexistente")
    void findById_WhenNotExists_ShouldThrowException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Pessoa não encontrada: 99");

        verify(repository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve criar nova pessoa")
    void create_ShouldSaveAndReturn() {
        when(repository.save(any(Pessoa.class))).thenReturn(pessoa);

        Pessoa created = service.create(pessoa);

        assertThat(created).isEqualTo(pessoa);
        verify(repository, times(1)).save(pessoa);
    }

    @Test
    @DisplayName("Deve atualizar pessoa existente")
    void update_ShouldUpdateFieldsAndSave() {
        Pessoa existing = new Pessoa(1L, "João Silva", endereco, true);

        Endereco novoEndereco = new Endereco(
            "Rua B", "456", null, "Bairro Novo", "11111-111", "Rio", "RJ"
        );
        Pessoa updatedDetails = new Pessoa(null, "João Silva Atualizado", novoEndereco, false);
        Pessoa expectedUpdated = new Pessoa(1L, "João Silva Atualizado", novoEndereco, false);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Pessoa.class))).thenReturn(expectedUpdated);

        Pessoa result = service.update(1L, updatedDetails);

        assertThat(result.getNome()).isEqualTo("João Silva Atualizado");
        assertThat(result.getAtivo()).isFalse();
        assertThat(result.getEndereco().getLogradouro()).isEqualTo("Rua B");
        assertThat(result.getEndereco().getCidade()).isEqualTo("Rio");

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(argThat(p ->
            p.getId().equals(1L) &&
            p.getNome().equals("João Silva Atualizado") &&
            p.getEndereco().getLogradouro().equals("Rua B") &&
            p.getAtivo().equals(false)
        ));
    }

    @Test
    @DisplayName("Deve deletar pessoa existente")
    void delete_ShouldDeleteById() {
        when(repository.findById(1L)).thenReturn(Optional.of(pessoa));
        doNothing().when(repository).deleteById(1L);

        service.delete(1L);

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar pessoa inexistente")
    void delete_WhenNotExists_ShouldThrowException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Pessoa não encontrada: 99");

        verify(repository, times(1)).findById(99L);
        verify(repository, never()).deleteById(any());
    }
}