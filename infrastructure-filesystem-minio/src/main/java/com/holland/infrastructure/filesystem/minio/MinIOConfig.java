package com.holland.infrastructure.filesystem.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class MinIOConfig {

    @Resource
    private MinIOProperties properties;

    @Bean
    public MinioClient minioClient() throws Exception {
        final MinioClient minioClient = MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .httpClient(getUnsafeOkHttpClient())
                .build();

        // init buckets
        for (MinIOBuckets bucket : MinIOBuckets.values()) {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket.name).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket.name).build());
                MinIOPolicy.toPublic(minioClient, bucket);
            }
        }

        return minioClient;
    }

    @Bean
    public List<String> cacheBuckets() {
        final List<String> buckets = new ArrayList<>();
        for (MinIOBuckets anEnum : MinIOBuckets.values()) {
            buckets.add(anEnum.name);
        }

        return buckets;
    }

    private OkHttpClient getUnsafeOkHttpClient() throws KeyManagementException, NoSuchAlgorithmException {
        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new SecureRandom());

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);

        builder.setHostnameVerifier$okhttp((s, sslSession) -> true);
        return builder.build();
    }
}
