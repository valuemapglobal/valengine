package com.value.decision.common.utils.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * RestTemplate配置类
 */
@Configuration
public class RestTemplateConfiguration {

   /* @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }*/

    @Bean
    public RestTemplate httpsRestTemplate(SimpleClientHttpRequestFactory httpsFactory){
        RestTemplate restTemplate = new RestTemplate(httpsFactory);
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
			
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void handleError(ClientHttpResponse response) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
        return restTemplate;
    }

    @Bean(name = "httpsFactory")
    public SimpleClientHttpRequestFactory httpsFactory() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new SecureRandom());
        HostnameVerifier hostnameVerifier = (hostname, session) -> true;

        SimpleClientHttpRequestFactory httpsFactory = new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
                if (connection instanceof HttpsURLConnection httpsConnection) {
                    httpsConnection.setSSLSocketFactory(sslContext.getSocketFactory());
                    httpsConnection.setHostnameVerifier(hostnameVerifier);
                }
                super.prepareConnection(connection, httpMethod);
            }
        };
        httpsFactory.setConnectTimeout(20000);
        httpsFactory.setReadTimeout(20000);
        return httpsFactory;
    }
}
