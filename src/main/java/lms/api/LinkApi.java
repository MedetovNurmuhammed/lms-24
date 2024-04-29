package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import lms.dto.response.SimpleResponse;
import lms.service.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RequestMapping("/api/link")
@RestController
@RequiredArgsConstructor
public class LinkApi {
    private final LinkService linkService;

    @Operation(summary = "Удалить ссылку",
            description = "Метод для удаления ссылку по его идентификатору." +
                    " Авторизация:  инструктор!")
    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @PostMapping("/delete/{linkId}")
    public SimpleResponse delete(@PathVariable Long linkId ){
        return linkService.delete(linkId);
    }

}
