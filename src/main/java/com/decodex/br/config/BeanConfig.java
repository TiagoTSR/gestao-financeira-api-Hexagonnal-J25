package com.decodex.br.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.decodex.br.domain.port.out.CategoriaRepositoryPort;
import com.decodex.br.domain.port.out.PessoaRepositoryPort;
import com.decodex.br.domain.port.out.RefreshTokenRepositoryPort;
import com.decodex.br.domain.port.out.UsuarioRepositoryPort;
import com.decodex.br.features.autenticacao.application.RefreshTokenApplicationService;
import com.decodex.br.features.autenticacao.application.RegisterUserApplicationService;
import com.decodex.br.features.autenticacao.application.port.in.RefreshTokenUseCase;
import com.decodex.br.features.autenticacao.application.port.in.RegisterUserUseCase;
import com.decodex.br.features.autenticacao.application.port.out.PasswordHasher;
import com.decodex.br.features.categoria.application.CategoriaApplicationService;
import com.decodex.br.features.categoria.application.port.in.CategoriaUseCase;
import com.decodex.br.features.categoria.application.port.out.CategoriaQueryPort;
import com.decodex.br.features.pessoa.application.PessoaApplicationService;
import com.decodex.br.features.pessoa.application.port.in.PessoaUseCase;
import com.decodex.br.features.pessoa.application.port.out.PessoaQueryPort;
import com.decodex.br.features.lancamentos.application.LancamentoApplicationService;
import com.decodex.br.features.lancamentos.application.port.in.LancamentoUseCase;
import com.decodex.br.features.lancamentos.application.port.out.LancamentoStore;
import com.decodex.br.features.relatorios.application.RelatorioAsyncApplicationService;
import com.decodex.br.features.relatorios.application.RelatorioProcessingApplicationService;
import com.decodex.br.features.relatorios.application.port.in.ProcessarRelatorioUseCase;
import com.decodex.br.features.relatorios.application.port.out.RelatorioFileGenerator;
import com.decodex.br.features.relatorios.application.port.out.RelatorioRequestPublisher;
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
            LancamentoStore lancamentoStore,
            CategoriaRepositoryPort categoriaRepositoryPort,
            PessoaRepositoryPort pessoaRepositoryPort) {
        return new LancamentoApplicationService(
                lancamentoStore,
                categoriaRepositoryPort,
                pessoaRepositoryPort);
    }

    @Bean
    public RelatorioAsyncApplicationService relatorioAsyncApplicationService(
            RelatorioSolicitacaoStore relatorioSolicitacaoStore,
            RelatorioRequestPublisher relatorioRequestPublisher) {
        return new RelatorioAsyncApplicationService(relatorioSolicitacaoStore, relatorioRequestPublisher);
    }

    @Bean
    public ProcessarRelatorioUseCase processarRelatorioUseCase(
            RelatorioSolicitacaoStore relatorioSolicitacaoStore,
            RelatorioFileGenerator relatorioFileGenerator) {
        return new RelatorioProcessingApplicationService(relatorioSolicitacaoStore, relatorioFileGenerator);
    }

    @Bean
    public RefreshTokenUseCase refreshTokenUseCase(
            RefreshTokenRepositoryPort refreshTokenRepositoryPort,
            UsuarioRepositoryPort usuarioRepositoryPort) {
        return new RefreshTokenApplicationService(refreshTokenRepositoryPort, usuarioRepositoryPort);
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UsuarioRepositoryPort usuarioRepositoryPort,
            PasswordHasher passwordHasher) {
        return new RegisterUserApplicationService(usuarioRepositoryPort, passwordHasher);
    }

}
