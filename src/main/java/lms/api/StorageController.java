package lms.api;

import lms.config.aws.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/file")
@CrossOrigin(origins = "*",maxAge = 3600)
@RequiredArgsConstructor
public class StorageController {

    private static final Logger log = LoggerFactory.getLogger(StorageController.class);
    private final StorageService service;

    @Secured({"ADMIN","INSTRUCTOR","STUDENT"})
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file) {
        System.out.println(file.getOriginalFilename());
        String uploadFile = service.uploadFile(file);
        return new ResponseEntity<>(uploadFile, HttpStatus.OK);
    }

    @Secured({"ADMIN","INSTRUCTOR","STUDENT"})
    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadFile(@RequestParam String fileName){
        System.out.println("CHYKTYYY    "+ fileName);
        try {
            return service.downloadFile(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Secured({"ADMIN","INSTRUCTOR","STUDENT"})
    @DeleteMapping("/delete/")
    public ResponseEntity<String> deleteFile(@RequestParam String fileName) {
        System.out.printf("fileName: %s\n", fileName);
        return new ResponseEntity<>(service.deleteFile(fileName), HttpStatus.OK);
    }
}
