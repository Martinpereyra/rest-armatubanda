package com.restapi.armatubanda.services;


import com.restapi.armatubanda.model.Musician;
import com.restapi.armatubanda.model.PasswordResetToken;
import com.restapi.armatubanda.repository.MusicianRepository;
import com.restapi.armatubanda.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JavaMailSender mailSender;
    private final MusicianRepository musicianRepository;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;


    public HttpStatus createPasswordResetToken(String email){
        Optional<Musician> musicianOptional = this.musicianRepository.findByEmail(email);
        if(musicianOptional.isEmpty()){
            throw new RuntimeException();
        }
        Musician musicianToReset = musicianOptional.get();
        String token = UUID.randomUUID().toString();
        PasswordResetToken myToken = new PasswordResetToken(token, LocalDateTime.now(),LocalDateTime.now().plusHours(6),musicianToReset);
        passwordResetTokenRepository.save(myToken);
        sendPasswordResetMail(email,token);
        return HttpStatus.OK;
    }

    public void sendPasswordResetMail(String userEmail, String token){
        // TODO: Este link que se envia por email tiene que redireccionar a una vista del front junto con el parametro token para luego enviar el token y la password al backend
        String resetLink = "http://PATH-DE-LA-VISTA-FRONTEND/api/auth/reset/" + token;
        emailService.sendEmailReset(userEmail,buildEmail(userEmail,resetLink));
    }

    private String buildEmail(String name, String link) {
        return "<div>Hola, " + name + "! Por favor clickea en el siguiente link para reinciar tu contraseña: <a href=\"" + link + "\">Reiniciar contraseña</a></div>";
    }

    public boolean validatePasswordResetToken(PasswordResetToken token){
        return token.getExpiresAt().isAfter(LocalDateTime.now());
    }

    public String changeUserPassword(String token, String newPassword){
        Optional<PasswordResetToken> passwordResetTokenOptional = this.passwordResetTokenRepository.findByToken(token);
        if(passwordResetTokenOptional.isPresent()){
            PasswordResetToken passwordResetToken = passwordResetTokenOptional.get();
            if(validatePasswordResetToken(passwordResetToken)){
                Musician musician = passwordResetToken.getMusician();
                musician.setPassword(passwordEncoder.encode(newPassword));
                musicianRepository.save(musician);
                return "Password changed";
            }else{
                return "Token expired";
            }
        }else{
            return "There is no token";
        }
    }



}
