package lms.config.aws.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lms.config.aws.AwsProperties;
import lms.dto.response.AwsResponse;
import lms.dto.response.SimpleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Service
@Slf4j
public class StorageService {
    private final AwsProperties.Bucket bucket;

    private final AmazonS3 s3Client;

    public StorageService(AmazonS3 s3Client,
                          AwsProperties awsProperties) {
        this.s3Client = s3Client;
        this.bucket = awsProperties.getBucket();
    }

    public AwsResponse uploadFile(MultipartFile file) {
        File fileObj = convertMultiPartFileToFile(file);
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        log.info("Uploading file: {}", fileName);
        s3Client.putObject(new PutObjectRequest(bucket.getName(), fileName, fileObj));
        fileObj.delete();
        return AwsResponse.builder()
                .fileName(fileName)
                .urlFile(bucket.getProfilePath() + fileName)
                .build();
    }

    public ResponseEntity<ByteArrayResource> downloadFile(String fileName) throws IOException {
        S3Object s3Object = s3Client.getObject(bucket.getName(), fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        byte[] content;
        try {
            content = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
        ByteArrayResource resource = new ByteArrayResource(content);
        return ResponseEntity
                .ok()
                .contentLength(content.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    public SimpleResponse deleteFile(String fileName) {
        log.info("Deleting file: {}", fileName);
        s3Client.deleteObject(bucket.getName(), fileName);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message(fileName + " успешно удалено из Amazon S3")
                .build();
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Ошибка при преобразовании MultipartFile в файл", e);
        }
        return convertedFile;
    }
}
