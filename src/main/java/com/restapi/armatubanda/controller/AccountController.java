package com.restapi.armatubanda.controller;


import com.restapi.armatubanda.model.Musician;
import com.restapi.armatubanda.services.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AccountController {

    private AccountService accountService;

    private PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Musician musician){
        Musician savedMusician = new Musician();
        ResponseEntity response = null;
        try{
            String hashedPassword = passwordEncoder.encode(musician.getPassword());
            musician.setPassword(hashedPassword);
            savedMusician = accountService.create(musician);
            if(savedMusician != null){
                response = ResponseEntity.status(HttpStatus.CREATED).body("User successfully registered");
            }else{
                response = ResponseEntity.status(HttpStatus.CONFLICT).body("User cannot be created");
            }

        }catch(Exception ex){
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An exception was occurred due to "+ex.getMessage());
        }
        return response;
    }

    @GetMapping("/myAccount")
    public String getAccountDetails(){
        return "Acc Details";
    }


}
