package today.todaysentence.util.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSenderService {
    private final JavaMailSender mailSender;

    public void sendEmailCertify(String to,String code) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String subject = "오늘의 명언에서 이메일 인증코드를 보내드립니다. ";

        String content = """
        <div style="max-width: 500px; margin: auto; padding: 20px; 
                    border: 1px solid #ddd; border-radius: 10px; 
                    font-family: Arial, sans-serif; background-color: #f9f9f9; text-align: center;">
            <h2 style="color: #333;">📧 오늘의 명언 이메일 인증</h2>
            <p style="font-size: 12px; padding: 6px; color: #999;">안녕하세요. 오늘의 명언을 이용해주셔서 감사합니다.😄</p>
            <p style="font-size: 16px; color: #555;">아래 인증 코드를 입력하여 이메일 인증을 완료하세요.</p>
            <div style="margin: 20px 0; padding: 15px; font-size: 24px; 
                        font-weight: bold; color: #ffffff; background-color: #007bff; 
                        display: inline-block; border-radius: 5px;">
                %s
            </div>
            <p style="font-size: 14px; color: #777;">이 코드는 15분 동안 유효합니다.</p>
            <hr style="margin: 20px 0;">
            <p style="font-size: 12px; color: #999;">오늘의 명언 팀</p>
        </div>
        """.formatted(code);

        helper.setFrom("mical0108@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    public void sendEmailTemporaryPassword(String to, String password) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String subject = "오늘의 명언에서 새로운 비밀번호를 보내드립니다. ";

        String content = """
        <div style="max-width: 500px; margin: auto; padding: 20px; 
                    border: 1px solid #ddd; border-radius: 10px; 
                    font-family: Arial, sans-serif; background-color: #f9f9f9; text-align: center;">
            <h2 style="color: #333;">📧 오늘의 명언 임시 비밀번호 발급해 드립니다.</h2>
            <p style="font-size: 12px; padding: 6px; color: #999;">안녕하세요. 오늘의 명언을 이용해주셔서 감사합니다.😄</p>
            <div style="margin: 20px 0; padding: 15px; font-size: 24px; 
                        font-weight: bold; color: #ffffff; background-color: #007bff; 
                        display: inline-block; border-radius: 5px;">
                %s
            </div>
            <hr style="margin: 20px 0;">
            <p style="font-size: 12px; color: #999;">오늘의 명언 팀</p>
        </div>
        """.formatted(password);

        helper.setFrom("mical0108@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }
}