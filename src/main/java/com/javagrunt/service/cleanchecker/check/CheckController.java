package com.javagrunt.service.cleanchecker.check;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Media;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@SuppressWarnings("removal")
@RestController
class CheckController {

    private final ChatClient chatClient;
    private final CleanClient cleanClient;
    private static final String USER_TEXT = "Is the room in this picture clean?";
    private static final String SYSTEM_TEXT = "You are in charge of making sure the children keep their rooms clean.  Clutter is not allowed.  Beds need to be made.  Doors and drawers should be closed.";

    public CheckController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
        WebClient webClient = WebClient.builder().baseUrl("http://satellite:8080").build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        this.cleanClient = factory.createClient(CleanClient.class);
    }

    @GetMapping("/check")
    public ImageAnalysis check() {
        byte[] capture = cleanClient.getCapture();
        return chatClient.prompt()
                .user(userSpec -> {
                    userSpec
                            .text(USER_TEXT)
                            .media(new Media(MimeTypeUtils.IMAGE_JPEG, capture));
                })
                .system(SYSTEM_TEXT)
                .call()
                .entity(ImageAnalysis.class);
    }

    @GetMapping("/unstructured")
    public String unstructured() {
        byte[] capture = cleanClient.getCapture();
        return chatClient.prompt()
                .user(userSpec -> {
                    userSpec
                            .text(USER_TEXT)
                            .media(new Media(MimeTypeUtils.IMAGE_JPEG, capture));
                })
                .system(SYSTEM_TEXT)
                .call()
                .content();
    }

}
