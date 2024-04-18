package lms.aws.controller;

import lms.aws.service.StorageService;
import org.springdoc.core.service.GenericParameterService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class StorageController {

    @Autowired
    private StorageService service;
    @Autowired
    private GenericParameterService parameterBuilder;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, params = "file")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        service.uploadFile(file);
        return new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);
    }


    //    @Secured("ADMIN")
    @GetMapping("/download/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) throws IOException {
        return service.downloadFile(fileName);
    }

    @Secured("ADMIN")
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        return new ResponseEntity<>(service.deleteFile(fileName), HttpStatus.OK);
    }
}
