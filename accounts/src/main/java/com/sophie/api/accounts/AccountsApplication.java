package com.sophie.api.accounts;

import com.sophie.api.accounts.dto.AccountsContactInfoDto;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.links.Link;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@OpenAPIDefinition(info=@Info(
        title="Accounts MicroService REST API Documentation",
        description = "Bank Account MicroService Rest API Documentation",
        version="v1",
        contact=@Contact(
                name="Sophie Sung",
                email="sophie@service.com",
                url="https://bankms.com"
        ),
        license = @License(
                name="Apache 2.0",
                url="https://bankms.com"
        )
),
externalDocs = @ExternalDocumentation(
        description = "Accounts MicroService REST API Documentation",
        url="https://bankms.com/swagger-ui.html"
))
@EnableConfigurationProperties(value = {AccountsContactInfoDto.class})
public class AccountsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountsApplication.class, args);
    }

}
