import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.SslProvider;

public class MainClass {
    
    public static void main(String[] args) {
        WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .build();
        
        // Use the webClient for further operations
    }
    
    private static HttpClient createHttpClient() {
        return HttpClient.create()
                .secure(sslContextSpec -> sslContextSpec.sslProvider(disableSslVerification()));
    }
    
    private static SslProvider disableSslVerification() {
        return SslProvider.builder()
                .sslContextCustomizer(sslContext -> sslContext
                        .setProtocol("TLS")
                        .setTrustManager(io.netty.handler.ssl.util.InsecureTrustManagerFactory.INSTANCE))
                .build();
    }
}
