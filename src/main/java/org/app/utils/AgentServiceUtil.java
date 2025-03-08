package org.app.utils;

import org.app.Exceptions.BadRequestException;
import org.app.model.IAPrompt;
import org.app.model.requests.IAPropmptRequest;
import org.app.repository.IAPropmpRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

public class AgentServiceUtil {

    public static void handlePropmpts(IAPropmptRequest prompt, IAPropmpRepository iaPropmpRepository) {
        IAPrompt baseIAPrompt = iaPropmpRepository.findByIp(prompt.getIp())
                .orElseGet(() -> {
                    IAPrompt newIAPrompt = new IAPrompt(
                            prompt.getIp(),
                            new ArrayList<>(Collections.singletonList(prompt.getNewPrompt())),
                            false,
                            prompt.getEmail(),
                            LocalDateTime.now(),
                            LocalDateTime.now()
                    );
                    return iaPropmpRepository.save(newIAPrompt);
                });

        if (baseIAPrompt.getPrompts().size() > 20 && baseIAPrompt.getLastUpdate().plusMinutes(5).isBefore(LocalDateTime.now())) {
            throw new BadRequestException(":money_bag IA busy, try again in a few minutes");
        }

        ArrayList<String> prompts = baseIAPrompt.getPrompts();
        prompts.add(prompt.getNewPrompt());
        baseIAPrompt.setPrompts(prompts);
        baseIAPrompt.updateLastUpdate();
        iaPropmpRepository.save(baseIAPrompt);
    }
}
