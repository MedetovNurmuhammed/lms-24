package lms.enums;


import lombok.Getter;

@Getter
public enum Messages {
    DELETE_TRASH("Данные успешно удалены!"),
    NOT_FOUND_TRASH("Данные не найдены в корзине!"),
    RESTORE_TRASH("Данные успешно восстановлены!");

    private final String message;
    Messages(String message) {
        this.message = message;
    }
}
