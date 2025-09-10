package com.example.warehousesystem.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "app.cookie")
public class CookieProperties {
    private boolean secure;
    private String sameSite;

}
