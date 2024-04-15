package lms.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lms.dto.request.PasswordRequest;
import lms.dto.request.SignInRequest;
import lms.dto.response.SignInResponse;
import lms.dto.response.SimpleResponse;
import lms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthApi {
    private final UserService userService;

    @PostMapping("/SignIn")
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
}
