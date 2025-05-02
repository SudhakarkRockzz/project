package main.GitAutomation;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TelegramIntegration {
	
	private final static HttpClient CLIENT = HttpClient.newBuilder()
	        .version(HttpClient.Version.HTTP_2)
	        .connectTimeout(Duration.ofSeconds(10))
	        .build();
	
	public void sendMessage(String urlMessage) throws IOException, InterruptedException {
	
		String botUrl = "https://api.telegram.org/bot7840693968:AAEfuD3BOHYFAJLs9LQ2PbTQwZzqJDOzZ80/sendMessage?chat_id=-4703508678&text="+urlMessage;
	
		HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(botUrl)) 
                .GET()
                .build();
		
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
	}
	
}

