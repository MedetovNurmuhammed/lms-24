package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import lms.dto.response.AllTrashResponse;
import lms.dto.response.SimpleResponse;
import lms.service.TrashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/trash")
@RequiredArgsConstructor
@Slf4j
public class TrashApi {
    private final TrashService trashService;

    @Operation(summary = "Получить все, кто в корзине!",
            description = "Метод для получение все, кто в корзине с пагинацией !" +
                    " Авторизация: администратор и инструктор!")
    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @GetMapping("/findAll")
    public AllTrashResponse findAll(@RequestParam(required = false, defaultValue = "1") int page,
                                    @RequestParam(required = false, defaultValue = "6") int size) {
        return trashService.findAll(page, size);
    }

    @Operation(summary = "Удалить из корзины.",
            description = "Метод для получение удаление из корзины!" +
                    " Авторизация: администратор и инструктор!")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/delete/{trashId}")
    public SimpleResponse delete(@PathVariable Long trashId){
        return trashService.delete(trashId);
    }

    @Operation(summary = "Удалить из корзины инструктора.",
            description = "Метод для получение удаление из корзины инструктора!" +
                    " Авторизация:  инструктор!")
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @DeleteMapping("/deleteInstructorTrash/{trashId}")
    public SimpleResponse deleteInstructorTrash(@PathVariable Long trashId){
        return trashService.deleteInstructorTrash(trashId);
    }

    @Operation(summary = "Возвращать из корзины.",
            description = "Метод для получение  из корзины и удаление из корзины!" +
                    " Авторизация: администратор и инструктор!")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/return/{trashId}")
    public SimpleResponse returnToBase(@PathVariable Long trashId) {
        return trashService.returnToBase(trashId);
    }

    @Operation(summary = "Возвращать из корзины инструктора.",
            description = "Метод для получение  из корзины инструктора и удаление из корзины инструктора!" +
                    " Авторизация:  инструктор!")
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR')")
    @DeleteMapping("/returnFromInstructorTrash/{trashId}")
    public SimpleResponse returnFromInstructorTrashToBase(@PathVariable Long trashId) {
        return trashService.returnFromInstructorTrashToBase(trashId);
    }
}
