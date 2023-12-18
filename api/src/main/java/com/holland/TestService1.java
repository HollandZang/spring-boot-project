package com.holland;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class TestService1 {
    @Cacheable("status")
    public String test() {
        System.err.println("no cache");
        return "OK";
    }
}
