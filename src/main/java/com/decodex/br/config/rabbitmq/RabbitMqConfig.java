package com.decodex.br.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_RELATORIOS = "relatorios.exchange";
    public static final String QUEUE_RELATORIOS = "relatorios.solicitacao.queue";
    public static final String ROUTING_KEY_RELATORIOS = "relatorios.solicitacao";

    public static final String DLX_RELATORIOS = "relatorios.dlx";
    public static final String DLQ_RELATORIOS = "relatorios.dlq";
    public static final String ROUTING_KEY_DLQ = "relatorios.solicitacao.dlq";

    @Bean
    public DirectExchange relatoriosExchange() {
        return new DirectExchange(EXCHANGE_RELATORIOS, true, false);
    }

    @Bean
    public DirectExchange relatoriosDlx() {
        return new DirectExchange(DLX_RELATORIOS, true, false);
    }

    @Bean
    public Queue relatoriosQueue() {
        return QueueBuilder.durable(QUEUE_RELATORIOS)
                .withArgument("x-dead-letter-exchange", DLX_RELATORIOS)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY_DLQ)
                .build();
    }

    @Bean
    public Queue relatoriosDlq() {
        return QueueBuilder.durable(DLQ_RELATORIOS).build();
    }

    @Bean
    public Binding bindingRelatorios(Queue relatoriosQueue, DirectExchange relatoriosExchange) {
        return BindingBuilder.bind(relatoriosQueue)
                .to(relatoriosExchange)
                .with(ROUTING_KEY_RELATORIOS);
    }

    @Bean
    public Binding bindingDlq(Queue relatoriosDlq, DirectExchange relatoriosDlx) {
        return BindingBuilder.bind(relatoriosDlq)
                .to(relatoriosDlx)
                .with(ROUTING_KEY_DLQ);
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jacksonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jacksonMessageConverter);
        return template;
    }
}
