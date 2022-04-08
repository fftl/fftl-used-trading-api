package fftl.usedtradingapi.commons.config;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3Config {

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public AmazonS3 amazonS3(){
        InstanceProfileCredentialsProvider provider
          = new InstanceProfileCredentialsProvider(true);
        return AmazonS3ClientBuilder.standard()
            .withRegion(region)
          .withCredentials(provider)
          .build();
    }
}
