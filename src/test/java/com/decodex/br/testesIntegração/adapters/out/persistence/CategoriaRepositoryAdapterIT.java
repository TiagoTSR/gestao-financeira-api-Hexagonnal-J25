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

import com.decodex.br.adapters.out.persistence.adapter.CategoriaRepositoryAdapter;
import com.decodex.br.adapters.out.persistence.entity.CategoriaEntity;
import com.decodex.br.adapters.out.persistence.mapper.CategoriaMapper;
import com.decodex.br.adapters.out.persistence.repository.CategoriaRepository;
import com.decodex.br.adapters.out.persistence.repository.LancamentoRepository;
import com.decodex.br.application.dto.categoria.CategoriaFilter;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.testesIntegração.PostgresIntegrationBase;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({CategoriaRepositoryAdapter.class, CategoriaMapper.class})
@DisplayName("Testes de Integração - CategoriaRepositoryAdapter (Testcontainers)")
class CategoriaRepositoryAdapterIT extends PostgresIntegrationBase {

    @Autowired
    private CategoriaRepositoryAdapter adapter;

    @Autowired
    private CategoriaRepository repository;
    
    @Autowired
    private LancamentoRepository lancamentoRepository;

    @BeforeEach
    void setUp() {
        // Limpamos a tabela antes de cada teste para garantir isolamento e idempotência
    	lancamentoRepository.deleteAllInBatch();
        repository.deleteAll();

        // Inserimos a massa de dados real no PostgreSQL (ou no banco local via fallback)
        CategoriaEntity cat1 = new CategoriaEntity();
        cat1.setNome("Alimentação");
        
        CategoriaEntity cat2 = new CategoriaEntity();
        cat2.setNome("Lazer e Entretenimento");
        
        CategoriaEntity cat3 = new CategoriaEntity();
        cat3.setNome("Transporte");

        repository.save(cat1);
        repository.save(cat2);
        repository.save(cat3);
    }

    @Test
    @DisplayName("Deve retornar todas as categorias quando o filtro estiver vazio")
    void findAll_ComFiltroVazio_DeveRetornarTudo() {
        CategoriaFilter filtro = new CategoriaFilter();
        PageRequest pageRequest = new PageRequest(0, 10);

        PageResult<Categoria> resultado = adapter.findAll(filtro, pageRequest);

        assertThat(resultado.content()).hasSize(3);
        assertThat(resultado.totalElements()).isEqualTo(3L);
    }

    @Test
    @DisplayName("Deve filtrar corretamente pelo nome parcial ignorando case (ILIKE)")
    void findAll_ComFiltroPorNome_DeveRetornarCorrespondentes() {
        CategoriaFilter filtro = new CategoriaFilter();
        // Testando o ignore case (minúsculo) e o %LIKE% parcial
        filtro.setNome("laze"); 
        PageRequest pageRequest = new PageRequest(0, 10);

        PageResult<Categoria> resultado = adapter.findAll(filtro, pageRequest);

        assertThat(resultado.content()).hasSize(1);
        assertThat(resultado.content().get(0).getNome()).isEqualTo("Lazer e Entretenimento");
    }

    @Test
    @DisplayName("Deve filtrar corretamente pelo ID exato")
    void findAll_ComFiltroPorId_DeveRetornarCorrespondente() {
        // Pegamos um ID real que o banco acabou de gerar no setUp()
        Long idExistente = repository.findAll().get(0).getId();
        
        CategoriaFilter filtro = new CategoriaFilter();
        filtro.setId(idExistente);
        PageRequest pageRequest = new PageRequest(0, 10);

        PageResult<Categoria> resultado = adapter.findAll(filtro, pageRequest);

        assertThat(resultado.content()).hasSize(1);
        assertThat(resultado.content().get(0).getId()).isEqualTo(idExistente);
    }

    @Test
    @DisplayName("Deve escapar caractere especial '%' no filtro LIKE e não tratar como wildcard")
    void findAll_ComFiltroContendoPorcentagem_DeveEscaparCorretamente() {
        CategoriaEntity catComPercentual = new CategoriaEntity();
        catComPercentual.setNome("Desconto 10% Especial");
        CategoriaEntity catSemPercentual = new CategoriaEntity();
        catSemPercentual.setNome("Desconto 100 Especial");
        repository.save(catComPercentual);
        repository.save(catSemPercentual);

        CategoriaFilter filtro = new CategoriaFilter();
        filtro.setNome("10%");
        PageRequest pageRequest = new PageRequest(0, 10);

        PageResult<Categoria> resultado = adapter.findAll(filtro, pageRequest);

        assertThat(resultado.content()).hasSize(1);
        assertThat(resultado.content().get(0).getNome()).isEqualTo("Desconto 10% Especial");
    }

    @Test
    @DisplayName("Deve escapar caractere especial '_' no filtro LIKE e não tratar como caractere coringa")
    void findAll_ComFiltroContendoUnderscore_DeveEscaparCorretamente() {
        CategoriaEntity catComUnderscore = new CategoriaEntity();
        catComUnderscore.setNome("Conta_Corrente");
        CategoriaEntity catSemUnderscore = new CategoriaEntity();
        catSemUnderscore.setNome("Conta X Corrente");
        repository.save(catComUnderscore);
        repository.save(catSemUnderscore);

        CategoriaFilter filtro = new CategoriaFilter();
        filtro.setNome("Conta_");
        PageRequest pageRequest = new PageRequest(0, 10);

        PageResult<Categoria> resultado = adapter.findAll(filtro, pageRequest);

        assertThat(resultado.content()).hasSize(1);
        assertThat(resultado.content().get(0).getNome()).isEqualTo("Conta_Corrente");
    }

    @Test
    @DisplayName("Deve retornar todos os registros quando o filtro for null")
    void findAll_ComFiltroNull_DeveRetornarTodos() {
        PageRequest pageRequest = new PageRequest(0, 10);

        PageResult<Categoria> resultado = adapter.findAll(null, pageRequest);

        assertThat(resultado.content()).hasSize(3);
        assertThat(resultado.totalElements()).isEqualTo(3L);
    }

    @Test
    @DisplayName("Deve lançar RegraDeNegocioException ao tentar salvar categoria com nome duplicado no banco")
    void save_ComNomeDuplicado_DeveLancarRegraDeNegocioException() {
        Categoria categoriaDuplicada = new Categoria(null, "Alimentação");

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> adapter.save(categoriaDuplicada))
                .isInstanceOf(com.decodex.br.domain.exeption.RegraDeNegocioException.class)
                .hasMessageContaining("Já existe uma categoria cadastrada com o nome: Alimentação");
    }
}