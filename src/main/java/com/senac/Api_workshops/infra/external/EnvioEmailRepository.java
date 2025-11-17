package com.senac.Api_workshops.infra.external;

import com.senac.Api_workshops.domain.interfaces.IEnvioEmail;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;

@Component
public class EnvioEmailRepository implements IEnvioEmail {
    @Autowired
    private JavaMailSender JavaMailSender;
    @Autowired
    private JavaMailSender javaMailSender;

    @Async
    public void enviarEmailSimples(String para, String assunto, String texto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("nao-responda@gmail.com");
        message.setTo(para);
        message.setSubject(assunto);
        message.setText(texto);
        JavaMailSender.send(message);
    }

    @Async
    public void enviarEmailComTemplate(String para, String assunto, String texto) {
        try {
            MimeMessage messagem = JavaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(messagem, true);
            String htmlTemplate = carregarTemplateEmail();

            String htmlFinal = htmlTemplate
                    .replace("${mensagem}", texto)
                    .replace("${dataEnvio}", String.valueOf(LocalDateTime.now()));

            helper.setFrom("nao-responda@suaempresa.com");
            helper.setTo(para);
            helper.setSubject(assunto);
            helper.setText(htmlFinal, true);

            javaMailSender.send(messagem);

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private String carregarTemplateEmail() throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/email-template.html");
        byte[] bytes = Files.readAllBytes(resource.getFile().toPath());
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
