package school.sptech.dto;

import java.util.Map;

public record EventoNotificacaoDto(String tipoEvento, Map<String, Object> payload) {}

