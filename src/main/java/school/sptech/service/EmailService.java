package school.sptech.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;


@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }


    public void enviarEmailComTemplate(String destinatario, String assunto, String nomeTemplate, Map<String, Object> variaveis) {
        try {
            Context context = new Context();
            context.setVariables(variaveis);

            String corpoHtml = templateEngine.process(nomeTemplate, context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(corpoHtml, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            System.err.printf("Falha ao enviar e-mail com template '%s' para '%s': %s%n",
                    nomeTemplate, destinatario, e.getMessage());
            throw new RuntimeException("Erro ao processar e-mail com template", e);
        }
    }
}

