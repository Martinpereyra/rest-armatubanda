package com.restapi.armatubanda.config;

import com.restapi.armatubanda.repository.MusicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final MusicianRepository musicianRepository;


    // Implementacion de la interfaz que carga los datos especificos del usuario (CRUD de UserDetails)
    @Bean
    public UserDetailsService userDetailsService(){
        return username -> musicianRepository.findByEmail(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }


    // Es un DAO(data access object) responsable de buscar los userdetails, codificar la contrasenia etc
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }


    // Recibe la request del filtro y delega la validacion del userDetails a los authProviders que existan
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();

    }

    // Setea el tipo de password enconder que sera usado
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
