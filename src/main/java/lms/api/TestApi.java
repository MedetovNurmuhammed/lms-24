package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import lms.dto.request.TestRequest;
import lms.dto.response.SimpleResponse;
import lms.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestApi {
     private final TestService testService;

     @Secured("ADMIN")
     @PostMapping("/save/{lessonId}")
     @Operation(summary = "Сохранить студента",
             description = "Метод для сохранение студента и отправка сообщение почту чтобы создать студент создал себе пароль! " +
                     " Авторизация: администратор!")
     public SimpleResponse createTest(@PathVariable Long lessonId,
                                      @RequestBody TestRequest testRequest){
          return testService.createTest();
     }
}
