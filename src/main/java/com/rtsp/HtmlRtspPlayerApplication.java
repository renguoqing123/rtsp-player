package com.rtsp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableWebSocket
@SpringBootApplication
public class HtmlRtspPlayerApplication implements WebSocketConfigurer {

    @Bean
    public HtmlRtspPlayerWebSockerServer htmlRtspPlayerWebSockerServer() {
    	log.info("------info------");
    	log.warn("------warn------");
    	log.error("------error------");
    	log.debug("------debug------");
    	log.trace("------trace------");
        return new HtmlRtspPlayerWebSockerServer();
    }

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(htmlRtspPlayerWebSockerServer(), "/player").setAllowedOrigins("*").addInterceptors(new WebSocketHandshakeInterceptor());
    }

    public static void main(String[] args) {
        SpringApplication.run(HtmlRtspPlayerApplication.class, args);
    }
}
