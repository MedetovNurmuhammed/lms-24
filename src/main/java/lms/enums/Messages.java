package lms.enums;


import lombok.Getter;

@Getter
public enum Messages {
    DELETE("Данные успешно удалены!"),
    NOT_FOUND("Данные не найдены!"),
    RESTORE("Данные успешно восстановлены!");

    private final String message;
    Messages(String message) {
        this.message = message;
    }
}
