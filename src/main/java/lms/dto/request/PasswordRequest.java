package lms.dto.request;

public record PasswordRequest(
        String code,
        String confirmCode
){}
