package com.decodex.br.adapters.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.decodex.br.adapters.out.persistence.entity.EnderecoEmbeddable;
import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Pessoa;

@Component
public class PessoaMapper {

    public Pessoa toDomain(PessoaEntity entity) {
        if (entity == null) return null;

        Endereco endereco = new Endereco(
            entity.getEndereco().getLogradouro(),
            entity.getEndereco().getNumero(),
            entity.getEndereco().getComplemento(),
            entity.getEndereco().getBairro(),
            entity.getEndereco().getCep(),
            entity.getEndereco().getCidade(),
            entity.getEndereco().getEstado()
        );

        return new Pessoa(
            entity.getId(),
            entity.getNome(),
            endereco,
            entity.getAtivo()
        );
    }

    public PessoaEntity toEntity(Pessoa pessoa) {
        if (pessoa == null) return null;

        EnderecoEmbeddable enderecoEmbeddable = new EnderecoEmbeddable();
        enderecoEmbeddable.setLogradouro(pessoa.getEndereco().getLogradouro());
        enderecoEmbeddable.setNumero(pessoa.getEndereco().getNumero());
        enderecoEmbeddable.setComplemento(pessoa.getEndereco().getComplemento());
        enderecoEmbeddable.setBairro(pessoa.getEndereco().getBairro());
        enderecoEmbeddable.setCep(pessoa.getEndereco().getCep());
        enderecoEmbeddable.setCidade(pessoa.getEndereco().getCidade());
        enderecoEmbeddable.setEstado(pessoa.getEndereco().getEstado());
        

        PessoaEntity entity = new PessoaEntity();
        entity.setId(pessoa.getId());
        entity.setNome(pessoa.getNome());
        entity.setEndereco(enderecoEmbeddable);
        entity.setAtivo(pessoa.getAtivo());

        return entity;
    }
}