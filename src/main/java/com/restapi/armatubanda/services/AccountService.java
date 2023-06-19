package com.restapi.armatubanda.services;


import com.restapi.armatubanda.model.Musician;
import com.restapi.armatubanda.repository.MusicianRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;



@Service
@AllArgsConstructor
public class AccountService{

    private MusicianRepository musicianRepository;

    public Musician create(Musician musician){
        return musicianRepository.save(musician);
    }

    public Musician getMusicianByEmail(String email){
        return musicianRepository.findByEmail(email);
    }
}
