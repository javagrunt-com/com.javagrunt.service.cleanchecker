package com.javagrunt.service.cleanchecker.check;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
class CheckController {

    private final ChatClient chatClient;
    
    public CheckController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/check")
    public String check() {
        //todo make call to get image data (bin or base64)
        return chatClient.prompt()
                .user(userSpec -> {
                    try {
                        userSpec
                                .text("Is this room clean and why or why not?")
                                .media(MimeTypeUtils.IMAGE_JPEG, new URL("http://satellite:8080/encoded"));
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .call()
                .content();
//                .entity(ImageAnalysis.class);
    }
    
}
