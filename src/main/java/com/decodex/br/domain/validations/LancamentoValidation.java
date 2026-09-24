package com.decodex.br.domain.validations;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.decodex.br.domain.exeption.RegraDeNegocioException;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.model.TipoLancamento;

public final class LancamentoValidation {

    private LancamentoValidation() {
    }

    public static String validarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new RegraDeNegocioException("Descrição não pode ser nula ou vazia");
        }
        return descricao;
    }

    public static LocalDate validarDataVencimento(LocalDate dataVencimento) {
        if (dataVencimento == null) {
            throw new RegraDeNegocioException("Data de vencimento não pode ser nula");
        }
        return dataVencimento;
    }

    public static BigDecimal validarValor(BigDecimal valor) {
        if (valor == null) {
            throw new RegraDeNegocioException("Valor não pode ser nulo");
        }
        return valor;
    }

    public static TipoLancamento validarTipo(TipoLancamento tipo) {
        if (tipo == null) {
            throw new RegraDeNegocioException("Tipo não pode ser nulo");
        }
        return tipo;
    }

    public static Categoria validarCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new RegraDeNegocioException("Categoria não pode ser nula");
        }
        return categoria;
    }

    public static Pessoa validarPessoa(Pessoa pessoa) {
        if (pessoa == null) {
            throw new RegraDeNegocioException("Pessoa não pode ser nula");
        }
        return pessoa;
    }
}
