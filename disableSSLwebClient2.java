import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.SslProvider;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
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
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, null);

        SslProvider sslProvider = SslProvider.builder()
                .sslContext(sslContext)
                .defaultConfiguration()
                .build();

        return HttpClient.create().secure(sslProvider);
    }
}
