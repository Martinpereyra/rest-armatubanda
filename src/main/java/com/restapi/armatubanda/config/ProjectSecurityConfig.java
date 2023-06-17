package com.restapi.armatubanda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ProjectSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests((requests)->requests
                .requestMatchers("/myAccount").authenticated()
                        .requestMatchers("/bands").permitAll())
                        .formLogin(Customizer.withDefaults())
                                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

}
