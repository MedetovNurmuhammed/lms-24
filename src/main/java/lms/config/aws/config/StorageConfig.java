package lms.config.aws.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lms.config.aws.AwsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StorageConfig {

    private final AwsProperties awsProperties;

    @Bean
    public AmazonS3 s3Client() {
        var credentials = awsProperties.getCredentials();
        var bucket = awsProperties.getBucket();

        AWSCredentials awsCredentials = new BasicAWSCredentials(
                credentials.getAwsAccessKeyId(),
                credentials.getAwsAccessSecretKey()
        );

        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(bucket.getRegion())
                .build();
    }
}