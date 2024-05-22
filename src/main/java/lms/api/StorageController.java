package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import lms.config.aws.service.StorageService;
import lms.dto.response.AwsResponse;
import lms.dto.response.SimpleResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class StorageController {

    private static final Logger log = LoggerFactory.getLogger(StorageController.class);
    private final StorageService service;

    @Secured({"ADMIN", "INSTRUCTOR", "STUDENT"})
    @Operation(summary = "Загрузить файл")
    @PostMapping( consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AwsResponse uploadFile(@RequestParam MultipartFile file) {
        log.info(file.getOriginalFilename());
        return service.uploadFile(file);
    }

    @Secured({"ADMIN", "INSTRUCTOR", "STUDENT"})
    @Operation(summary = "Удалить файл")
    @DeleteMapping("/{fileName}")
    public SimpleResponse deleteFile(@PathVariable String fileName) {
        log.info("fileName:{}", fileName);
        return service.deleteFile(fileName);
    }
}
