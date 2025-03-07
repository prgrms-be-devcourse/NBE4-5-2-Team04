package com.project2.global.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Getter
    public static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        AppConfig.objectMapper = objectMapper;
    }

    public static String getSiteFrontUrl() {
        return "http://localhost:3000";
    }
}
