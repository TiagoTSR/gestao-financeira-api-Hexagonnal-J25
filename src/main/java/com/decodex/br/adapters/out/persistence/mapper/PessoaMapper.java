package com.decodex.br.adapters.out.persistence.mapper;

import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;
import com.decodex.br.domain.model.Pessoa;

public class PessoaMapper {
 public Pessoa toDomain(PessoaEntity entity) {
     if (entity == null) return null;
     return new Pessoa(
         entity.getId(),
         entity.getNome(),
         entity.getEndereco(),
         entity.getAtivo()
     );
 }

 public PessoaEntity toEntity(Pessoa pessoa) {
     if (pessoa == null) return null;
     PessoaEntity entity = new PessoaEntity();
     entity.setId(pessoa.getId());
     entity.setNome(pessoa.getNome());
     entity.setEndereco(pessoa.getEndereco());
     entity.setAtivo(pessoa.getAtivo());
     return entity;
 }
}