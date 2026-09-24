package com.decodex.br.domain.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para indicar a construtores/métodos padrão a serem utilizados
 * por ferramentas de mapeamento (como MapStruct) em objetos de domínio
 * que possuem múltiplos construtores.
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface Default {
}
