import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class MainClass {

    public static void main(String[] args) throws NoSuchAlgorithmException, KeyManagementException {
        WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .build();

        // Use the webClient for further operations
    }

    private static HttpClient createHttpClient() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, getTrustManagers(), new java.security.SecureRandom());

        return HttpClient.create()
                .secure(ssl -> ssl.sslContext(sslContextSpec -> sslContextSpec.configure(sslContext)));
    }

    private static TrustManager[] getTrustManagers() {
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };
    }
}
