package lms.service.impl;

import lms.config.jwt.JwtService;
import lms.dto.request.SignInRequest;
import lms.dto.response.SignInResponse;
import lms.entities.User;
import lms.exceptions.AlreadyExistsException;
import lms.exceptions.NotFoundException;
import lms.repository.UserRepository;
import lms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@RequiredArgsConstructor
@Service
@Validated
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void checkEmail(String email) {
        boolean exists = userRepository.existsByEmail(email);
        if (exists) throw new AlreadyExistsException("User with email: " + email + " already have");

    }

    @Override
    public SignInResponse signIn(SignInRequest signInRequest) {
        User user = userRepository.getByEmail(signInRequest.getLogin());
        boolean matches = passwordEncoder.matches(signInRequest.getPassword(), user.getPassword());
        if (!matches) throw new NotFoundException("Invalid Password");
        return SignInResponse.builder()
                .token(jwtService.createToken(user))
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .httpStatus(HttpStatus.OK)
                .message("Successful login")
                .build();
    }

}


