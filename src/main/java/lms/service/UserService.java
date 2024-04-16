package lms.service;
import jakarta.mail.MessagingException;
import lms.dto.request.PasswordRequest;
import lms.dto.request.SignInRequest;
import lms.dto.response.SignInResponse;
import lms.dto.response.SimpleResponse;
import lms.exceptions.BadRequestException;

public interface UserService {
    void checkEmail(String email);
    SignInResponse signIn(SignInRequest signInRequest);
    public void sendEmail(String toEmail) throws MessagingException;

    SimpleResponse forgotPassword(String email) throws MessagingException;

    SimpleResponse checkCode(int code);

    SimpleResponse setPassword(PasswordRequest passwordRequest);
    SimpleResponse createPassword(String uuid, String password, String confirm) throws BadRequestException;
     void emailSender(String toEmail) throws MessagingException;
}
