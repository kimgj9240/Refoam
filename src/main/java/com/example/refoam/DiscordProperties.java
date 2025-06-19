package com.example.refoam;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "discord.webhook")
public class DiscordProperties {
    private String url;
}
