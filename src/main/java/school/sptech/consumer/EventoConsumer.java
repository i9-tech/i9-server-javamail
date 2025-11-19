package school.sptech.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import school.sptech.dto.EventoNotificacaoDto;
import school.sptech.service.EmailService;

import java.util.Map;

@Component
public class EventoConsumer {

    @Autowired
    private EmailService emailService;


    @Value("${app.frontend.url}")
    private String frontendUrl;

    @RabbitListener(queues = "notificacoes.master.queue")
    public void processarEvento(@Payload EventoNotificacaoDto evento) {
        System.out.println("Evento recebido: " + evento.tipoEvento());
        System.out.println("Payload do evento: " + evento.payload());

        switch (evento.tipoEvento()) {
            case "FUNCIONARIO_CADASTRADO":
                handleFuncionarioCadastrado(evento.payload());
                break;

            case "RECUPERACAO_SENHA":
                handleRecuperacaoSenha(evento.payload());
                break;

            case "CHAMADA_ACAO_SITE":
                handleChamadaAcao(evento.payload());
                break;

            default:
                System.err.println("Tipo de evento desconhecido: " + evento.tipoEvento());
        }
    }

    private void handleFuncionarioCadastrado(Map<String, Object> payload) {
    String nome = (String) payload.get("nome");
    String cpf = (String) payload.get("cpf");
    String cargos = (String) payload.get("cargos");
    String email = (String) payload.get("email");

    if (email == null || email.isBlank()) {
        System.err.println("Evento ignorado: e-mail ausente ou inválido para funcionário " + nome);
        return;
    }

    String assunto = "Alerta de Cadastro de Funcionário!";
    String nomeTemplate = "email-boas-vindas";
    Map<String, Object> variaveis = Map.of(
            "nome", nome,
            "cpf", cpf,
            "cargos", cargos,
            "email", email
    );

    emailService.enviarEmailComTemplate(email, assunto, nomeTemplate, variaveis);
}


    private void handleRecuperacaoSenha(Map<String, Object> payload) {
        String email = (String) payload.get("email");
        String nome = (String) payload.get("nome");
        String token = (String) payload.get("token");
        String empresa = (String) payload.get("empresa");
        String cpf = (String) payload.get("cpf");

        String assunto = "Recuperação de Senha i9 Tech";
        String nomeTemplate = "recuperacao-senha";
        if (email == null || email.isBlank()) {
        System.err.println("Evento ignorado: e-mail ausente para evento RECUPERACAO_SENHA");
        return;
        }
        Map<String, Object> variaveis = Map.of(
                "nomeFuncionario", nome,
                "nomeEmpresa", empresa,
                "cpfFuncionario", cpf,
                "linkRecuperacao", frontendUrl + "/redefinir-senha/" + token
        );

        emailService.enviarEmailComTemplate(email, assunto, nomeTemplate, variaveis);
    }

    private void handleChamadaAcao(Map<String, Object> payload) {
        String email = (String) payload.get("email");

        if (email == null || email.isBlank()) {
        System.err.println("Evento ignorado: e-mail ausente para evento CHAMADA_ACAO_SITE");
        return;
        }

        String assunto = "Uma oferta especial da i9 Tech para você!";
        String nomeTemplate = "contato-interesse";
        Map<String, Object> variaveis = Map.of();

        emailService.enviarEmailComTemplate(email, assunto, nomeTemplate, variaveis);
    }
}
