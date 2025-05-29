package com.example.refoam;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.openai.api")
@Getter
@Setter
public class OpenAiProperties {
    private String key;
}
