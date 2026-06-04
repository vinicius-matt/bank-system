package com.Bank.bank_system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank System API")
                        .description("API bancária utilizando Java, Spring Boot e PostgreSQL" +
                                " com gerenciamento de clientes, contas, extrato, depósitos, saques, transferências e suporte a limite de crédito " +
                                "(cheque especial)")
                        .version("1.0"));
    }
}
