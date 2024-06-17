package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lms.dto.response.AllTrashResponse;
import lms.dto.response.SimpleResponse;
import lms.service.TrashService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trash")
@Tag(name = "TRASH MANAGEMENT!")
@RequiredArgsConstructor
@CrossOrigin(origins = "*",maxAge = 3600)
@PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
public class TrashApi {
    private final TrashService trashService;

    @Operation(summary = "Получить все объекты из корзины.", description = " Авторизация: администратор и инструктор!")
    @GetMapping("/findAll")
    public AllTrashResponse findAll(@RequestParam(required = false, defaultValue = "1") int page,
                                    @RequestParam(required = false, defaultValue = "6") int size) {
        return trashService.findAll(page, size);
    }

    @Operation(summary = "Восстановить объект из корзины.", description = "Авторизация: администратор и инструктор!")
    @DeleteMapping("/restore/{trashID}")
    public SimpleResponse restoreData(@PathVariable Long trashID) {
        return trashService.restoreData(trashID);
    }

    @Operation(summary = "Удалить объект из корзины.", description = "Авторизация: администратор и инструктор!")
    @DeleteMapping("{trashID}")
    public SimpleResponse deleteTrash(@PathVariable Long trashID){
        return trashService.deleteData(trashID);
    }
}
