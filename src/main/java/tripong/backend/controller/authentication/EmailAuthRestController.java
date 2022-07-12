package tripong.backend.controller.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tripong.backend.dto.authentication.EmailAuthRequestDto;
import tripong.backend.service.authentication.EmailAuthService;
import javax.mail.MessagingException;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class EmailAuthRestController {

    private final EmailAuthService emailAuthService;

    // 이메일 인증
    @GetMapping("/users/auth/email/send")
    public void sendEmailAuth(@RequestBody EmailAuthRequestDto dto) throws MessagingException {

        emailAuthService.createEmailValidLink(dto);

    }

    // 이메일 재인증
    @GetMapping("/users/auth/email/resend")
    public ResponseEntity<Object> resendEmailAuth(@RequestBody EmailAuthRequestDto dto) throws MessagingException {

        String result = emailAuthService.verifyResendEmailValidLink(dto);

        if(result == "SUCCESS"){
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    // 이메일 인증: URL 매핑
    @GetMapping("/users/auth/email/confirm")
    public ResponseEntity<Object> emailConfirm(@Validated @RequestParam String emailValidLink){
        String result = emailAuthService.verifyEmailLink(emailValidLink);

        if(Objects.equals(result, "SUCCESS")){
            return new ResponseEntity<>(HttpStatus.FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }
}
