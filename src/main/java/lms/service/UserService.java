package lms.service;
import jakarta.mail.MessagingException;
import lms.dto.request.PasswordRequest;
import lms.dto.request.SignInRequest;
import lms.dto.response.SignInResponse;
import lms.dto.response.SimpleResponse;

public interface UserService {
    void checkEmail(String email);
    SignInResponse signIn(SignInRequest signInRequest);

    SimpleResponse forgotPassword(String email) throws MessagingException;

    SimpleResponse checkCode(int code);

    SimpleResponse setPassword(PasswordRequest passwordRequest);
}
