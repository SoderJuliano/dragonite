package org.app.utils;

import org.app.Exceptions.BadRequestException;
import org.app.Exceptions.IAException;
import org.app.Exceptions.RateLimitExceededException;
import org.app.model.IAPrompt;
import org.app.model.Prompt;
import org.app.model.requests.IAPropmptRequest;
import org.app.repository.IAPropmpRepository;
import org.app.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.app.model.Language.PORTUGUESE;
import static org.app.services.RateLimitChecker.checkAndUpdateRateLimit;


/**
 * Classe utilitária para gerenciar e validar requisições relacionadas ao uso de Inteligência Artificial (IA).
 * <p>
 * Esta classe implementa a lógica <b>fail-fast</b>, que visa identificar e tratar erros ou condições inválidas
 * o mais cedo possível, evitando a execução de operações desnecessárias e melhorando a eficiência do sistema.
 * </p>
 * <p>
 * A lógica fail-fast é aplicada no início das requisições de IA, verificando condições como:
 * <ul>
 *     <li>Limite de prompts gratuitos excedidos.</li>
 *     <li>Necessidade de uma conta premium para continuar o uso.</li>
 *     <li>Validação de dados de entrada.</li>
 * </ul>
 * </p>
 * <p>
 * Caso alguma condição não seja atendida, uma exceção é lançada imediatamente, interrompendo o fluxo da requisição
 * e retornando uma mensagem de erro apropriada ao usuário.
 * </p>
 *
 * <h2>Exemplo de Uso:</h2>
 * <pre>
 * {@code
 * AgentServiceUtil util = new AgentServiceUtil();
 * util.handlePropmpts(request, repository);
 * }
 * </pre>
 *
 * @see IAPropmptRequest
 * @see IAPropmpRepository
 * @see BadRequestException
 * @since 1.0
 */
public class AgentServiceUtil {

    /**
     * Valida e processa os prompts de uma requisição de IA.
     * <p>
     * Este método implementa a lógica fail-fast, verificando se o número de prompts excedeu o limite permitido
     * para contas gratuitas. Caso o limite seja excedido e o usuário não tenha uma conta premium, uma exceção
     * é lançada imediatamente.
     * </p>
     *
     * @param prompt            A requisição de IA contendo os dados do prompt.
     * @param iaPropmpRepository O repositório utilizado para buscar e salvar dados de prompts.
     * @throws BadRequestException Se o limite de prompts for excedido e o usuário não tiver uma conta premium.
     */
    public static IAPrompt handlePropmpts(IAPropmptRequest prompt, IAPropmpRepository iaPropmpRepository,
                                          UserRepository userRepository) {
        IAPrompt baseIAPrompt = iaPropmpRepository.findByIp(prompt.getIp())
                .orElseGet(() -> {
                    IAPrompt newIAPrompt = new IAPrompt(
                            prompt.getIp(),
                            new ArrayList<>(),
                            false,
                            prompt.getEmail(),
                            LocalDateTime.now(),
                            LocalDateTime.now()
                    );
                    return iaPropmpRepository.save(newIAPrompt);
                });

        if(!canIUserAINow(baseIAPrompt, userRepository)) {
            boolean isPt = prompt.getLanguage().equals(PORTUGUESE);
            throw new IAException(getErrorMessage(isPt));
        }

        try {
            checkAndUpdateRateLimit(baseIAPrompt);
        } catch (RateLimitExceededException e) {
            LocalLog.logErr(":money_bag  Too many request in less then 5 minutes for ip "+baseIAPrompt.getIp()
            + "and email "+baseIAPrompt.getUserEmail());
            throw e;
        }

        ArrayList<Prompt> prompts = baseIAPrompt.getPrompts();

        prompts.add(new Prompt(prompt.getNewPrompt(), null));

        baseIAPrompt.setPrompts(prompts);
        baseIAPrompt.updateLastUpdate();
        return iaPropmpRepository.save(baseIAPrompt);
    }

    private static boolean canIUserAINow(IAPrompt baseIAPrompt, UserRepository userRepository) {
        return baseIAPrompt.getPrompts().size() <= 20
                || Boolean.TRUE.equals(userRepository.hasPremiumAccount(baseIAPrompt.getUserEmail()));
    }

    private static String getErrorMessage(boolean isPt) {
        return isPt ? """
            Esta conta alcançou o limite de chamadas gratuitas para o agente de Inteligência Artificial. 
            Uma conta premium será necessária para continuar usando serviços de IA.
            """
                : """
            Free tier account ended. 
            Upgrade your account to premium to make unlimited use of AI.
            """;
    }

}
