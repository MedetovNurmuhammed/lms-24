package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import lms.dto.response.AllTrashResponse;
import lms.dto.response.SimpleResponse;
import lms.service.TrashService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/trash")
@RequiredArgsConstructor
@CrossOrigin(origins = "*",maxAge = 3600)
@PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
public class MyTrashApi {
    private final TrashService trashService;

    @Operation(summary = "Чтобы получить все удаленные, но не очищенные данные. Используйте токен пользователя, чтобы получить",
            description = " Авторизация: администратор и инструктор!")
    @GetMapping("/findAll")
    public AllTrashResponse findAll(@RequestParam(required = false, defaultValue = "1") int page,
                                    @RequestParam(required = false, defaultValue = "6") int size) {
        return trashService.findAll(page, size);
    }

    @Operation(summary = "Возвращать из корзины.", description = "Авторизация: администратор и инструктор!")
    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @DeleteMapping("/return/{trashID}")
    public SimpleResponse restoreData(@PathVariable Long trashID) {
        return trashService.restoreData(trashID);
    }

    @Operation(summary = "Удалить из корзины.")
    @DeleteMapping("{trashID}")
    public SimpleResponse deleteTrash(@PathVariable Long trashID){
        return trashService.delete(trashID);
    }
}
