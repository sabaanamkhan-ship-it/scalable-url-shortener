package com.sabaanam.urlshortener.codegen;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ShortCodeProperties.class)
public class CodeGenConfig {

    @Bean
    public Base62Encoder base62Encoder() {
        return new Base62Encoder();
    }

    @Bean
    public CodeShuffler codeShuffler(ShortCodeProperties props) {
        return new CodeShuffler(
                props.multiplierA(),
                props.multiplierB(),
                props.multiplierC(),
                props.offset()
        );
    }
}