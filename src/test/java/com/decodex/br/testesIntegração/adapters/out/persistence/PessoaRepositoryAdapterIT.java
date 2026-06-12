package com.decodex.br.testesIntegração.adapters.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.decodex.br.adapters.out.persistence.adapter.PessoaRepositoryAdapter;
import com.decodex.br.adapters.out.persistence.entity.EnderecoEmbeddable;
import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;
import com.decodex.br.adapters.out.persistence.mapper.PessoaMapper;
import com.decodex.br.adapters.out.persistence.repository.LancamentoRepository;
import com.decodex.br.adapters.out.persistence.repository.PessoaRepository;
import com.decodex.br.domain.filter.PessoaFilter;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.testesIntegração.PostgresIntegrationBase;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({PessoaRepositoryAdapter.class, PessoaMapper.class})
@DisplayName("Testes de Integração - PessoaRepositoryAdapter (Testcontainers)")
class PessoaRepositoryAdapterIT extends PostgresIntegrationBase {

    @Autowired
    private PessoaRepositoryAdapter adapter;

    @Autowired
    private PessoaRepository repository;
    
    @Autowired
    private LancamentoRepository lancamentoRepository;

    @BeforeEach
    void setUp() {
      
        lancamentoRepository.deleteAll();
        
        repository.deleteAll();

        PessoaEntity p1 = new PessoaEntity();
        p1.setNome("Tiago Silva");
        p1.setAtivo(true);
        p1.setEndereco(criarEnderecoCompleto("Rua A", "123", "Apto 1", "Centro", "29100000", "Vila Velha", "ES"));

        PessoaEntity p2 = new PessoaEntity();
        p2.setNome("Bruna Oliveira");
        p2.setAtivo(false);
        p2.setEndereco(criarEnderecoCompleto("Rua B", "456", null, "Jardim Camburi", "29100111", "Vitória", "ES"));

        PessoaEntity p3 = new PessoaEntity();
        p3.setNome("Carlos Souza");
        p3.setAtivo(true);
        p3.setEndereco(criarEnderecoCompleto("Rua C", "789", "Casa 2", "Copacabana", "22000000", "Rio de Janeiro", "RJ"));

        repository.save(p1);
        repository.save(p2);
        repository.save(p3);
    }

    @Test
    @DisplayName("Deve filtrar pessoas pelo nome parcial e status ativo")
    void findAll_PorNomeEAtivo_DeveRetornarCorrespondente() {
        PessoaFilter filtro = new PessoaFilter();
        filtro.setNome("tiago");
        filtro.setAtivo(true);
        PageRequest pageRequest = new PageRequest(0, 10);

        PageResult<Pessoa> resultado = adapter.findAll(filtro, pageRequest);

        assertThat(resultado.content()).hasSize(1);
        assertThat(resultado.content().get(0).getNome()).isEqualTo("Tiago Silva");
    }

    @Test
    @DisplayName("Deve filtrar pessoas pela cidade contida no endereço")
    void findAll_PorCidade_DeveRetornarPessoasDaquelaCidade() {
        PessoaFilter filtro = new PessoaFilter();
        filtro.setCidade("vitória");
        PageRequest pageRequest = new PageRequest(0, 10);

        PageResult<Pessoa> resultado = adapter.findAll(filtro, pageRequest);

        assertThat(resultado.content()).hasSize(1);
        assertThat(resultado.content().get(0).getNome()).isEqualTo("Bruna Oliveira");
    }

    @Test
    @DisplayName("Deve retornar vazio se o CEP não bater exatamente")
    void findAll_PorCepInexistente_DeveRetornarVazio() {
        PessoaFilter filtro = new PessoaFilter();
        filtro.setCep("00000000");
        PageRequest pageRequest = new PageRequest(0, 10);

        PageResult<Pessoa> resultado = adapter.findAll(filtro, pageRequest);

        assertThat(resultado.content()).isEmpty();
    }
    
    private EnderecoEmbeddable criarEnderecoCompleto(String logradouro, String numero, String complemento,
                                                     String bairro, String cep, String cidade, String estado) {
        EnderecoEmbeddable end = new EnderecoEmbeddable();
        end.setLogradouro(logradouro);
        end.setNumero(numero);
        end.setComplemento(complemento);  // pode ser null
        end.setBairro(bairro);
        end.setCep(cep);
        end.setCidade(cidade);
        end.setEstado(estado);
        return end;
    }
}