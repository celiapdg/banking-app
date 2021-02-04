package com.ironhack.securitydemo.controller.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

public interface IHelloWorldController {

    String helloWorld();
    String goodbyeWorld();

    String sayHello(UserDetails userDetails);

}
