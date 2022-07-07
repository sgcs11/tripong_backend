package java.tripong.backend.service.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tripong.backend.dto.authentication.EmailAuthRequestDto;
import tripong.backend.entity.authentication.EmailValidLink;
import tripong.backend.entity.user.User;
import tripong.backend.repository.authentication.EmailAuthRepository;
import tripong.backend.repository.authentication.UserAuthRepository;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private final JavaMailSender mailSender;
    private final EmailAuthRepository emailAuthRepository;
    private final UserAuthRepository userAuthRepository;

    // 이메일 인증
    public void createEmailValidLik(EmailAuthRequestDto dto) throws MessagingException {

        // 이메일 유효링크 저장
        EmailValidLink validLink = EmailValidLink.createEmailValidLink(dto.getUserId());
        emailAuthRepository.save(validLink);

        //이메일 발송 서비스 호출
        sendEmail(dto, validLink);
    }

    // 이메일 인증: 비동기식 JavaMailSender
    @Async
    public void sendEmail(EmailAuthRequestDto dto, EmailValidLink validLink) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        String text = "";
        String link = "http://localhost:8089/users/auth/email/confirm?emailValidLink=" + validLink.getId();


        message.setRecipients(RecipientType.TO, dto.getEmail());
        message.setSubject("회원가입 이메일 인증");

        text += "<html><body>";
        text += "<div style = 'margin:100px; border-top: 3px solid #0DC5D6; border-bottom: 1px solid #7A7A7A; padding: 30px 0 30px 0;'>";
        text += "<br>";
        text += "<h1><span style='color: #0DC5D6;'>메일 인증</span> 안내입니다.</h1>";
        text += "<br>";
        text += "<p style='font-size: 14px'>여행 커뮤니티 서비스, <span style='color: #0DC5D6; font-weight: bold;'>Tripong</span>에 가입하신것을 환영합니다.</p>";
        text += "<p style='font-size: 14px'>아래 <span style='color: #0DC5D6; font-weight: bold;'>링크</span>를 클릭하시면 인증이 정상적으로 완료됩니다.</p>";
        text += "<p style='font-size: 11px'>인증링크는 메일이 발송된 시점부터 5분간만 유효합니다.</p>";
        text += "<br>";
        text += "<div align='center' style='background-color: #F7F7F7;padding: 40px;'>";
        text += link;
        text += "</div>";
        text += "<br>";
        text += "<p style='font-size: 14px'>감사합니다.</p>";
        text += "<br>";
        text += "</div>";
        text += "</body></html>";

        message.setText(text, "utf-8", "html");

        mailSender.send(message);
    }

    // 이메일 인증: 유효 링크 확인
    public EmailValidLink findByIdAndExpirationDateAfterAndExpired(String emailValidLink) {

        Optional<EmailValidLink> validLink  = emailAuthRepository.findByIdAndExpirationDateAfterAndExpired(emailValidLink, LocalDateTime.now(), false);

        return validLink.orElseThrow(()  -> new NoSuchElementException("valid link not found"));
    }

    // 이메일 인증: 유효 링크 확인 및 변경
    @Transactional
    public String verifyEmail(String emailValidLink){

        // 유효 링크 확인
        EmailValidLink findValidLink = findByIdAndExpirationDateAfterAndExpired(emailValidLink);
        String userId = findValidLink.getUserId();

        // 유저 인증 정보 가져오기
        Optional<User> user = userAuthRepository.findByLoginId(userId);

        // 유효 링크 사용 완료 체크
        findValidLink.makeInvalidLink();

        if (user.isPresent()){
            userAuthRepository.updateauthenticationStatus(userId);
            return "SUCCESS";
        } else {
            return "FAIL";
        }
    }


}
