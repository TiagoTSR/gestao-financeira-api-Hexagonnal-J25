package com.decodex.br.testesunitarios.config.rabbitmq;

import com.decodex.br.config.rabbitmq.RabbitMqConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("Testes Unitários - RabbitMqConfig")
class RabbitMqConfigTest {

    private final RabbitMqConfig config = new RabbitMqConfig();

    @Test
    @DisplayName("Deve configurar exchange principal de relatórios como Direct e durável")
    void deveConfigurarExchangePrincipal() {
        DirectExchange exchange = config.relatoriosExchange();
        assertNotNull(exchange);
        assertEquals(RabbitMqConfig.EXCHANGE_RELATORIOS, exchange.getName());
        assertTrue(exchange.isDurable());
    }

    @Test
    @DisplayName("Deve configurar dead letter exchange (DLX) como Direct e durável")
    void deveConfigurarDlx() {
        DirectExchange dlx = config.relatoriosDlx();
        assertNotNull(dlx);
        assertEquals(RabbitMqConfig.DLX_RELATORIOS, dlx.getName());
        assertTrue(dlx.isDurable());
    }

    @Test
    @DisplayName("Deve configurar fila principal com argumentos de Dead Letter Queue")
    void deveConfigurarFilaPrincipalComDlx() {
        Queue queue = config.relatoriosQueue();
        assertNotNull(queue);
        assertEquals(RabbitMqConfig.QUEUE_RELATORIOS, queue.getName());
        assertTrue(queue.isDurable());
        assertEquals(RabbitMqConfig.DLX_RELATORIOS, queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals(RabbitMqConfig.ROUTING_KEY_DLQ, queue.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    @DisplayName("Deve configurar fila DLQ durável")
    void deveConfigurarDlq() {
        Queue dlq = config.relatoriosDlq();
        assertNotNull(dlq);
        assertEquals(RabbitMqConfig.DLQ_RELATORIOS, dlq.getName());
        assertTrue(dlq.isDurable());
    }

    @Test
    @DisplayName("Deve configurar binding da fila principal e da DLQ")
    void deveConfigurarBindings() {
        Queue queue = config.relatoriosQueue();
        DirectExchange exchange = config.relatoriosExchange();
        Binding binding = config.bindingRelatorios(queue, exchange);
        assertNotNull(binding);
        assertEquals(RabbitMqConfig.ROUTING_KEY_RELATORIOS, binding.getRoutingKey());

        Queue dlq = config.relatoriosDlq();
        DirectExchange dlx = config.relatoriosDlx();
        Binding bindingDlq = config.bindingDlq(dlq, dlx);
        assertNotNull(bindingDlq);
        assertEquals(RabbitMqConfig.ROUTING_KEY_DLQ, bindingDlq.getRoutingKey());
    }

    @Test
    @DisplayName("Deve instanciar Jackson2JsonMessageConverter e RabbitTemplate")
    void deveConfigurarMessageConverterETemplate() {
        MessageConverter converter = config.jacksonMessageConverter();
        assertNotNull(converter);
        assertInstanceOf(Jackson2JsonMessageConverter.class, converter);

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        RabbitTemplate template = config.rabbitTemplate(connectionFactory, converter);
        assertNotNull(template);
        assertSame(converter, template.getMessageConverter());
    }
}
