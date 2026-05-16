package com.decodex.br.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.decodex.br.domain.port.in.CategoriaUseCase;
import com.decodex.br.domain.port.in.LancamentoUseCase;
import com.decodex.br.domain.port.in.PessoaUseCase;
import com.decodex.br.domain.port.out.CategoriaRepositoryPort;
import com.decodex.br.domain.port.out.LancamentoRepositoryPort;
import com.decodex.br.domain.port.out.PessoaRepositoryPort;
import com.decodex.br.domain.service.CategoriaService;
import com.decodex.br.domain.service.LancamentoService;
import com.decodex.br.domain.service.PessoaService;

@Configuration
public class BeanConfig {
	
	@Bean
    public CategoriaUseCase categoriaUseCase(CategoriaRepositoryPort categoriaRepositoryPort) {
        return new CategoriaService(categoriaRepositoryPort);
    }

    @Bean
    public PessoaUseCase pessoaUseCase(PessoaRepositoryPort pessoaRepositoryPort) {
        return new PessoaService(pessoaRepositoryPort);
    }
    
    @Bean
    public LancamentoUseCase lancamentoUseCase(LancamentoRepositoryPort lancamentoRepositoryPort) {
        return new LancamentoService(lancamentoRepositoryPort);
    }

}
