package org.app.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.app.Exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DefaultAnswer(Boolean valid, Integer status, String message, List<String> messages, Object content, LocalDateTime date) {

    public DefaultAnswer(Object content) {
        this(null, null, null, null, content, null);
    }

    public DefaultAnswer  {
        if (content instanceof Exception ex) {
            valid = false;
            message = ex.getMessage();
            content = ex.getCause().toString();
        } else {
            valid = true;
        }
        ZoneId brazilZone = ZoneId.of("America/Sao_Paulo");
        date = ZonedDateTime.now(brazilZone).toLocalDateTime();
    }
}
