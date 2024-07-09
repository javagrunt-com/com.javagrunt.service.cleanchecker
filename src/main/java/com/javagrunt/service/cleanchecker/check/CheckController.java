package com.javagrunt.service.cleanchecker.check;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
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
import java.util.List;

@RestController
class CheckController {

    private final ChatClient chatClient;
    private final CleanClient cleanClient;
    
    public CheckController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
        WebClient webClient = WebClient.builder().baseUrl("http://satellite:8080").build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        this.cleanClient = factory.createClient(CleanClient.class);
    }

    @GetMapping("/check")
    public ImageAnalysis check() {
        //todo make call to get image data (bin or base64)
        byte[] capture = cleanClient.getCapture();

//        var userMessage = new UserMessage(
//                "Is this room clean and why or why not?",
//                List.of(new Media(MimeTypeUtils.IMAGE_JPEG, capture))
//        );

        return chatClient.prompt()
                .user(userSpec -> {
                    userSpec
                            .text("Is this room clean and why or why not?")
                            .media(new Media(MimeTypeUtils.IMAGE_JPEG, capture));
                })
                .call()
                .entity(ImageAnalysis.class);
    }
    
}
