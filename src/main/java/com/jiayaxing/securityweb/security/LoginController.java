package com.jiayaxing.securityweb.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public Object login(@RequestParam String username,@RequestParam String password,@RequestParam String code,@RequestParam String uuid)
    {
        // ็ๆไปค็
        String token = loginService.login(username, password, code, uuid);
        return token;
    }
}
