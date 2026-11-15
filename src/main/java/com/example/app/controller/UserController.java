package com.example.app.controller;

import com.example.app.model.User;
import com.example.app.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ユーザー関連のリクエストを受け付けるクラス。
 *
 * <p>このサンプルは Spring Boot として起動しないため、アノテーションは「業務コードと同じ見た目」にするためのものです。
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public String register(@RequestParam String name, @RequestParam int age) {
        User user = userService.register(name, age);
        return "OK: " + user.name() + " (" + user.ageGroup() + ")";
    }
}
