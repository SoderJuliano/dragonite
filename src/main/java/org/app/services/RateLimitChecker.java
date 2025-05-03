package org.app.services;

import org.app.Exceptions.RateLimitExceededException;
import org.app.model.IAPrompt;

import java.time.LocalDateTime;

public class RateLimitChecker {
    private static final int MAX_REQUESTS = 20;
    private static final int BLOCK_TIME_MINUTES = 5;

    public static void checkAndUpdateRateLimit(IAPrompt iaPrompt) {
        LocalDateTime now = LocalDateTime.now();

        // Verifica se está bloqueado
        if (iaPrompt.isBlocked() && iaPrompt.getBlockedUntil() != null) {
            if (now.isBefore(iaPrompt.getBlockedUntil())) {
                throw new RateLimitExceededException(":money_bag IA busy, try again in a few minutes");
            }
            // Desbloqueia e reseta contador
            iaPrompt.setBlocked(false);
            iaPrompt.setBlockedUntil(null);
            iaPrompt.setRequestCount(1);
            iaPrompt.setLastRequestDate(now);
            return;
        }

        // Verifica janela de tempo
        if (iaPrompt.getLastRequestDate() != null &&
                iaPrompt.getLastRequestDate().plusMinutes(BLOCK_TIME_MINUTES).isAfter(now)) {

            if (iaPrompt.getRequestCount() >= MAX_REQUESTS) {
                // Bloqueia
                iaPrompt.setBlocked(true);
                iaPrompt.setBlockedUntil(now.plusMinutes(BLOCK_TIME_MINUTES));
                throw new RateLimitExceededException(":money_bag IA busy, try again in a few minutes");
            }
            // Incrementa contador
            iaPrompt.setRequestCount(iaPrompt.getRequestCount() + 1);
        } else {
            // Nova janela de tempo
            iaPrompt.setRequestCount(1);
        }
        iaPrompt.setLastRequestDate(now);
    }
}