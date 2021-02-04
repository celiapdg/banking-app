package com.ironhack.securitydemo.controller.impl;

import com.ironhack.securitydemo.controller.interfaces.IHelloWorldController;
import com.ironhack.securitydemo.model.User;
import com.ironhack.securitydemo.utils.PasswordUtil;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController implements IHelloWorldController {
    @GetMapping("/hello-world")
    public String helloWorld() {
        return "Hello World :D";
    }

    @GetMapping("/goodbye-world")
    public String goodbyeWorld() {
        return "Goodbye World D:";
    }

    @GetMapping("/say-hello")
    public String sayHello(@AuthenticationPrincipal UserDetails userDetails) {
        return "Welcome, " + userDetails.getUsername();
    }


}
