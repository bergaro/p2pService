package ru.netology.p2p.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.netology.p2p.objectsDTO.RsTransferDTO;

@Configuration
public class AppConfig {

    @Bean
    @Scope("prototype")
    public RsTransferDTO appResponse() {
        return new RsTransferDTO();
    }

}
