package lms.service;

import lms.dto.request.SignInRequest;
import lms.dto.response.SignInResponse;

public interface UserService {
    void sendEmailSms(String to);
    void checkEmail(String email);
    SignInResponse signIn(SignInRequest signInRequest);
}
