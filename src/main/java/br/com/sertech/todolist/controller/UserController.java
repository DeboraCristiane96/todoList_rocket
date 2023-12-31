package br.com.sertech.todolist.controller;


import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.sertech.todolist.model.UserModel;
import br.com.sertech.todolist.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel userModel){
        var user = this.userRepository.findByUsername(userModel.getName());
        if(user != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário Já Existe!");
        }
        var password = BCrypt.withDefaults().
                hashToString(12,userModel.getPassword().toCharArray());
        userModel.setPassword(password);
        var userCreated = this.userRepository.save(userModel);
            return  ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }
}
