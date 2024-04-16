package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lms.dto.request.PasswordRequest;
import lms.dto.request.SignInRequest;
import lms.dto.response.SignInResponse;
import lms.dto.response.SimpleResponse;
import lms.service.UserService;
import lms.validation.password.PasswordValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthApi {
    private final UserService userService;

    @PostMapping("/signIn")
    @Operation(description = "SignIn")
    public SignInResponse signIn(@RequestBody @Valid SignInRequest signInRequest) {
        return userService.signIn(signInRequest);
    }

    @PutMapping("/forgotPassword")
    public SimpleResponse forgotPassword (@RequestParam String email) throws  MessagingException {
        return userService.forgotPassword(email);
    }

    @PutMapping("/checkCode")
    public SimpleResponse checking(@RequestParam int code){
        return userService.checkCode(code);
    }

    @PutMapping("/setPassword")
    public SimpleResponse setPassword(@RequestBody PasswordRequest passwordRequest){
        return userService.setPassword(passwordRequest);
    }
    @PostMapping("/createPassword")
    public SimpleResponse createPassword(@RequestParam @PasswordValidation String password,
                                         @RequestParam @PasswordValidation String confirm,
                                         @RequestParam String uuid) {
        log.error("Password created for !");
        return userService.createPassword(uuid,password,confirm);
    }
}
