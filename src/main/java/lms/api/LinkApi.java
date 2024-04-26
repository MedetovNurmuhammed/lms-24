package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lms.dto.request.LinkRequest;
import lms.dto.response.AllLinkResponse;
import lms.dto.response.LinkResponse;
import lms.dto.response.SimpleResponse;
import lms.service.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/api/links")
@RequiredArgsConstructor
public class LinkApi {
    private final LinkService linkService;
    @Secured("INSTRUCTOR")
    @Operation(summary = "добавляет ссылку.(Авторизация: инструктор)")
    @PostMapping("/addLink/{lessonId}")
    public SimpleResponse addLesson(@RequestBody @Valid LinkRequest linkRequest, @PathVariable Long lessonId) throws MessagingException {
        return linkService.addLink(linkRequest, lessonId);
    }

    @Secured("INSTRUCTOR")
    @Operation(summary = "Возвращает пагинированный список всех ссылок урока.(Авторизация: инструктор)")
    @GetMapping("/findAll/{lessonId}")
    public AllLinkResponse findAll(@RequestParam(required = false, defaultValue = "1") int page,
                                   @RequestParam(required = false, defaultValue = "6") int size, @PathVariable Long lessonId) {
        return linkService.findAll(page, size,lessonId);
    }

    @PreAuthorize("hasAnyAuthority('STUDENT','INSTRUCTOR')")
    @Operation(summary = "Возвращает ссылку.(Авторизация: инструктор, студент)")
    @GetMapping("/findById/{linkId}")
    public LinkResponse findById(@PathVariable Long linkId) {
        return linkService.findById(linkId);
    }

    @Secured("INSTRUCTOR")
    @PutMapping("/updateLink/{linkId}")
    @Operation(summary = "Обновляет информацию о ссылке.(Авторизация: инструктор)")
    public SimpleResponse update(
            @RequestBody @Valid LinkRequest linkRequest, @PathVariable Long linkId) {
        return linkService.update(linkRequest, linkId);
    }
    @Secured("INSTRUCTOR")
    @DeleteMapping("/delete/{linkId}")
    @Operation(summary = "Удаляет текущую ссылку.(Авторизация: инструктор)")
    public SimpleResponse delete(@PathVariable Long linkId) {
        return linkService.delete(linkId);
    }
}
