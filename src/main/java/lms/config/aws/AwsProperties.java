package lms.config.aws;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("aws.s3")
public class AwsProperties {
    private Credentials credentials;
    private Bucket bucket;

    @Getter
    @Setter
    public static class Credentials {
        private String awsAccessKeyId;
        private String awsAccessSecretKey;
    }

    @Getter
    @Setter
    public static class Bucket {
        private String name;
        private String profilePath;
        private String region;
    }
}
