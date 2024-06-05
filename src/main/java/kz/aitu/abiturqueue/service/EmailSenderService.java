package kz.aitu.abiturqueue.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailSenderService {
    @Autowired
    private JavaMailSender mailSender;

    private final Random random = new Random();

    public Integer generateFourDigitNumber(){
        return 1000 + random.nextInt(9000);
    }

    public Integer sendEmail(String email){
        SimpleMailMessage message = new SimpleMailMessage();
        Integer code = generateFourDigitNumber();

        message.setFrom("sagyndyk.aibyn@gmail.com");
        message.setTo(email);
        message.setText(code.toString());
        message.setSubject("Код для регистрации в очереди AITU");

        mailSender.send(message);

        System.out.println("Mail sent successfully...");
        return code;
    }
}
