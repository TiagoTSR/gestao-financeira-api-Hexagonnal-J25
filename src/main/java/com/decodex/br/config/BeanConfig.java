package com.decodex.br.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.decodex.br.domain.port.in.CategoriaInputPort;
import com.decodex.br.domain.port.in.LancamentoInputPort;
import com.decodex.br.domain.port.in.PessoaInputPort;
import com.decodex.br.domain.port.in.RefreshTokenInputPort;
import com.decodex.br.domain.port.out.CategoriaRepositoryPort;
import com.decodex.br.domain.port.out.LancamentoRepositoryPort;
import com.decodex.br.domain.port.out.PessoaRepositoryPort;
import com.decodex.br.domain.service.CategoriaService;
import com.decodex.br.domain.service.LancamentoService;
import com.decodex.br.domain.service.PessoaService;

@Configuration
public class BeanConfig {

    @Bean
    public CategoriaInputPort categoriaInputPort(CategoriaRepositoryPort categoriaRepositoryPort) {
        return new CategoriaService(categoriaRepositoryPort);
    }

    @Bean
    public PessoaInputPort pessoaInputPort(PessoaRepositoryPort pessoaRepositoryPort) {
        return new PessoaService(pessoaRepositoryPort);
    }

    @Bean
    public LancamentoInputPort lancamentoInputPort(
            LancamentoRepositoryPort lancamentoRepositoryPort,
            CategoriaRepositoryPort categoriaRepositoryPort,
            PessoaRepositoryPort pessoaRepositoryPort) {
        return new LancamentoService(lancamentoRepositoryPort, categoriaRepositoryPort, pessoaRepositoryPort);
    }

    @Bean
    public RefreshTokenInputPort refreshTokenInputPort(
            com.decodex.br.domain.port.out.RefreshTokenRepositoryPort refreshTokenRepositoryPort,
            com.decodex.br.domain.port.out.UsuarioRepositoryPort usuarioRepositoryPort) {
        return new com.decodex.br.domain.service.RefreshTokenService(refreshTokenRepositoryPort, usuarioRepositoryPort);
    }

}
