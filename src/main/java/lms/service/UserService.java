package lms.service;

import jakarta.mail.MessagingException;
import lms.dto.request.SignInRequest;
import lms.dto.response.SignInResponse;
import lms.dto.response.SimpleResponse;
import lms.exceptions.BadRequestException;
import java.nio.file.AccessDeniedException;

public interface UserService {
    void checkEmail(String email);

    SignInResponse signIn(SignInRequest signInRequest) throws AccessDeniedException;

    SimpleResponse createPassword(String uuid, String password, String confirm) throws BadRequestException;

    SimpleResponse emailSender(String toEmail) throws MessagingException;
}
