package lms.api;

import com.amazonaws.services.dynamodbv2.document.internal.ScanImpl;
import io.swagger.v3.oas.annotations.Operation;
import lms.dto.response.SimpleResponse;
import lms.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestApi {
    private final TestService testService;

    @Operation(summary = "Удалить теста",
            description = "Метод для удаления теста по его идентификатору." +
                    " Авторизация: администратор и инструктор!")
    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @DeleteMapping("/delete/{testId}")
    public SimpleResponse delete(@PathVariable Long testId){
        return testService.delete(testId);
    }
}
