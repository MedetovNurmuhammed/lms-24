package lms.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lms.config.jwt.JwtService;
import lms.dto.request.PasswordRequest;
import lms.dto.request.SignInRequest;
import lms.dto.response.SignInResponse;
import lms.dto.response.SimpleResponse;
import lms.entities.User;
import lms.exceptions.AlreadyExistsException;
import lms.exceptions.BadRequestException;
import lms.exceptions.NotFoundException;
import lms.repository.UserRepository;
import lms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    int num;
    String email;

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


    public void sendEmail(String toEmail) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        email = toEmail;
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom("nurkamilkamchiev@gmail.com");
        mimeMessageHelper.setTo(toEmail);
        Random r = new Random();
        num = r.nextInt(+9000) + 1000;
        mimeMessageHelper.setText("""
                 <div>
                          <a href="http://localhost:8080/verify-account?email=%s" target="_blank">CREATE NEW PASSWORD</a>
                        </div>
                """.formatted(email), true);
        mimeMessageHelper.setSubject("PEAKSOFT PROGRAMMING COURSES");
        FileSystemResource file = new FileSystemResource(new File("C:\\Users\\asus\\OneDrive\\Изображения\\peaksoft.jpg"));
        mimeMessageHelper.addInline("image", file);
        javaMailSender.send(mimeMessage);
    }
    public void emailSender(String toEmail) throws MessagingException {
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
        mimeMessageHelper.setSubject("PEAKSOFT PROGRAMMING COURSES");
        javaMailSender.send(mimeMessage);
    }
    @Override
    public SimpleResponse forgotPassword(String email) throws MessagingException {
        User foundEmail = userRepository.getByEmail(email);
        if (foundEmail == null) {
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .message("User with the provided email does not exist.")
                    .build();
        } else {
            sendEmail(email);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Please check your email to set password!!!")
                    .build();
        }
    }

    @Override
    public SimpleResponse checkCode(int code) {
        if (code != num) {
            throw new BadRequestException("Not equal code!!!");
        } else {
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("OTP is true!")
                    .build();
        }
    }

    @Override
    public SimpleResponse setPassword(PasswordRequest passwordRequest) {
        User user = userRepository.getByEmail(email);
        if (!passwordRequest.code().equals(passwordRequest.confirmCode())) {
            throw new BadRequestException("Not equal to confirm password");
        }
        user.setPassword(passwordEncoder.encode(passwordRequest.code()));
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Success updated password!").
                build();
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
                    .message("Created password!")
                    .build();
        }
    }


}


