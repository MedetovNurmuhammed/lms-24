package lms.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lms.config.jwt.JwtService;
import lms.dto.request.SignInRequest;
import lms.dto.response.SignInResponse;
import lms.dto.response.SimpleResponse;
import lms.entities.User;
import lms.exceptions.AlreadyExistsException;
import lms.exceptions.NotFoundException;
import lms.repository.UserRepository;
import lms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

    @Override
    public void checkEmail(String email) {
        boolean exists = userRepository.existsByEmail(email);
        if (exists) throw new AlreadyExistsException("User with email: " + email + " already have");
    }

    @Override
    public SignInResponse signIn(SignInRequest signInRequest) throws AccessDeniedException {
        User user = userRepository.findByEmail(signInRequest.getLogin()).orElseThrow(()
                -> new AccessDeniedException("Пользователь с электронной почтой " + signInRequest.getLogin() + " не найден"));
        boolean matches = passwordEncoder.matches(signInRequest.getPassword(), user.getPassword());
        if (!matches) throw new NotFoundException("Неверный пароль");
        if (user.getBlock()) throw new AccessDeniedException("Вам закрыли доступ");
        return SignInResponse.builder()
                .token(jwtService.createToken(user))
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .httpStatus(HttpStatus.OK)
                .message("Успешный вход")
                .build();
    }

    public SimpleResponse emailSender(String toEmail) throws MessagingException {
        String uuid = UUID.randomUUID().toString();
        User user = userRepository.getByEmail(toEmail);
        user.setUuid(uuid);
        String link = "http://192.168.0.14:9090/createPassword.html?uuid=" + uuid; // Constructing the link
        String htmlContent = String.format("""
                <html>
                <body>
                    <p>Для установки пароля, перейдите по следующей ссылке:</p>
                    <a href="%s">УСТАНОВИТЬ ПАРОЛЬ</a>
                </body>
                </html>
                """, link);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom("PEAKSOFT");
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setText(uuid);
        mimeMessageHelper.setText(htmlContent, true);
        mimeMessageHelper.setSubject("КУРСЫ ПРОГРАММИРОВАНИЕ PEAKSOFT!");
        javaMailSender.send(mimeMessage);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Success!")
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse createPassword(String uuid, String password, String confirm) {
        if (!password.equals(confirm)) {
            throw new IllegalArgumentException("Пароли не совпадают");
        } else {
            User user = userRepository.findByUuid(uuid).orElseThrow(() -> new NoSuchElementException("User not found"));
            user.setPassword(passwordEncoder.encode(password));
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Пароль успешно создан!")
                    .build();
        }
    }
}


