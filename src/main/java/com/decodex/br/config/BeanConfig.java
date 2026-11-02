package com.decodex.br.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.decodex.br.domain.port.in.RefreshTokenInputPort;
import com.decodex.br.domain.port.out.CategoriaRepositoryPort;
import com.decodex.br.domain.port.out.LancamentoRepositoryPort;
import com.decodex.br.domain.port.out.PessoaRepositoryPort;
import com.decodex.br.features.categoria.application.CategoriaApplicationService;
import com.decodex.br.features.categoria.application.port.in.CategoriaUseCase;
import com.decodex.br.features.categoria.application.port.out.CategoriaQueryPort;
import com.decodex.br.features.pessoa.application.PessoaApplicationService;
import com.decodex.br.features.pessoa.application.port.in.PessoaUseCase;
import com.decodex.br.features.pessoa.application.port.out.PessoaQueryPort;
import com.decodex.br.features.lancamentos.application.LancamentoApplicationService;
import com.decodex.br.features.lancamentos.application.port.in.LancamentoUseCase;
import com.decodex.br.domain.port.out.RelatorioProducerPort;
import com.decodex.br.features.relatorios.application.RelatorioAsyncApplicationService;
import com.decodex.br.features.relatorios.application.port.out.RelatorioSolicitacaoStore;

@Configuration
public class BeanConfig {

    @Bean
    public CategoriaUseCase categoriaUseCase(
            CategoriaRepositoryPort categoriaRepositoryPort,
            CategoriaQueryPort categoriaQueryPort) {
        return new CategoriaApplicationService(categoriaRepositoryPort, categoriaQueryPort);
    }

    @Bean
    public PessoaUseCase pessoaUseCase(
            PessoaRepositoryPort pessoaRepositoryPort,
            PessoaQueryPort pessoaQueryPort) {
        return new PessoaApplicationService(pessoaRepositoryPort, pessoaQueryPort);
    }

    @Bean
    public LancamentoUseCase lancamentoUseCase(
            LancamentoRepositoryPort lancamentoRepositoryPort,
            CategoriaRepositoryPort categoriaRepositoryPort,
            PessoaRepositoryPort pessoaRepositoryPort) {
        return new LancamentoApplicationService(
                lancamentoRepositoryPort,
                categoriaRepositoryPort,
                pessoaRepositoryPort);
    }

    @Bean
    public RelatorioAsyncApplicationService relatorioAsyncApplicationService(
            RelatorioSolicitacaoStore relatorioSolicitacaoStore,
            RelatorioProducerPort relatorioProducerPort) {
        return new RelatorioAsyncApplicationService(relatorioSolicitacaoStore, relatorioProducerPort);
    }

    @Bean
    public RefreshTokenInputPort refreshTokenInputPort(
            com.decodex.br.domain.port.out.RefreshTokenRepositoryPort refreshTokenRepositoryPort,
            com.decodex.br.domain.port.out.UsuarioRepositoryPort usuarioRepositoryPort) {
        return new com.decodex.br.domain.service.RefreshTokenService(refreshTokenRepositoryPort, usuarioRepositoryPort);
    }

}
