package org.example.classworkwebwk3.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/redis")
public class RedisController {

    private final StringRedisTemplate redisTemplate;

    public RedisController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/set")
    public String setValue(@RequestParam String key, @RequestParam String value) {
        redisTemplate.opsForValue().set(key, value);
        return "Successfully stored " + key + " = " + value;
    }

    @GetMapping("/get")
    public String getValue(@RequestParam String key) {
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? value : "Key not found";
    }
}