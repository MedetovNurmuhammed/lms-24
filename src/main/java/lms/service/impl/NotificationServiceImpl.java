//package lms.service.impl;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import jakarta.transaction.Transactional;
//import lms.dto.response.NotificationResponse;
//import lms.dto.response.SimpleResponse;
//import lms.entities.User;
//import lms.repository.NotificationRepository;
//import lms.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.NoSuchElementException;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class NotificationServiceImpl implements NotificationService {
//    private final NotificationRepository notificationRepository;
//    private final JavaMailSender javaMailSender;
//    private final UserRepository userRepository;
//
//    @Override
//    public void emailMessage(String message, String toEmail) throws MessagingException {
//        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
//        mimeMessageHelper.setFrom("PEAKSOFT");
//        mimeMessageHelper.setTo("gulumkanusonkyzy@gmail.com");
//        mimeMessageHelper.setText(message);
//        mimeMessageHelper.setSubject("КУРСЫ ПРОГРАММИРОВАНИЕ PEAKSOFT!");
//        javaMailSender.send(mimeMessage);
//        SimpleResponse build = SimpleResponse.builder()
//                .httpStatus(HttpStatus.OK)
//                .message("Успешно отправлено!")
//                .build();
//        System.out.println(build);
//    }
//
//    @Override
//    public List<NotificationResponse> findAll() {
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        User currentUser = userRepository.findByEmail(email)
//                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
//       return userRepository.findAllByUserId(currentUser.getId());
//
//    }
//}
