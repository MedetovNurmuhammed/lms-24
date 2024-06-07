package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lms.dto.request.SignInRequest;
import lms.dto.response.SignInResponse;
import lms.dto.response.SimpleResponse;
import lms.service.UserService;
import lms.validation.password.PasswordValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
@CrossOrigin(origins = "*",maxAge = 3600)
public class AuthApi {
    private final UserService userService;

    @Operation(summary = "Войти")
    @PostMapping("/signIn")
    public SignInResponse signIn(@RequestBody @Valid SignInRequest signInRequest) throws AccessDeniedException {
        return userService.signIn(signInRequest);
    }

    @Operation(summary = "забыли пароль")
    @PutMapping("/forgotPassword")
    public SimpleResponse forgotPassword(@RequestParam String email, @RequestParam String link) throws MessagingException {
        return userService.emailSender(email , link);
    }

    @Operation(summary = "создать пароль")
    @PostMapping("/createPassword")
    public SimpleResponse createPassword(@RequestParam @PasswordValidation String password,
                                         @RequestParam @PasswordValidation String confirm,
                                         @RequestParam String uuid) {
        return userService.createPassword(uuid, password, confirm);
    }
}
