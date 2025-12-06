package com.kirtasth.gamevault.helloworld;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HelloWorldController {

    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello World! - Made by David Soto - Testing");
    }


}
