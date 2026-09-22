package com.decodex.br.domain.validations;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.model.TipoLancamento;

public class LancamentoValidation {

    public String validarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição não pode ser nula ou vazia");
        }
        return descricao;
    }

    public LocalDate validarDataVencimento(LocalDate dataVencimento) {
        if (dataVencimento == null) {
            throw new IllegalArgumentException("Data de vencimento não pode ser nula");
        }
        return dataVencimento;
    }

    public BigDecimal validarValor(BigDecimal valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Valor não pode ser nulo");
        }
        return valor;
    }

    public TipoLancamento validarTipo(TipoLancamento tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo não pode ser nulo");
        }
        return tipo;
    }

    public Categoria validarCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula");
        }
        return categoria;
    }

    public Pessoa validarPessoa(Pessoa pessoa) {
        if (pessoa == null) {
            throw new IllegalArgumentException("Pessoa não pode ser nula");
        }
        return pessoa;
    }
}
