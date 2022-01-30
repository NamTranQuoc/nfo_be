package com.nfo.core.mail;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void send(Mail mail) {
        try {
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            if (StringUtils.isNotBlank(mail.getMail_to())) {
                helper.setTo(mail.getMail_to());
            } else {
                String[] array = new String[mail.getMail_tos().size()];
                mail.getMail_tos().toArray(array);
                helper.setTo(array);
            }
            helper.setSubject(mail.getMail_subject());
            helper.setText(mail.getMail_content(), true);
//            helper.addAttachment("my_photo.png", new ClassPathResource("android.png"));

            javaMailSender.send(msg);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
