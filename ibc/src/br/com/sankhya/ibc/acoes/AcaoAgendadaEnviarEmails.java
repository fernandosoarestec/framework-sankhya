package br.com.sankhya.ibc.acoes;

import br.com.sankhya.ibc.business.EnviaBoleto;
import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

public class AcaoAgendadaEnviarEmails implements ScheduledAction {

    @Override
    public void onTime(final ScheduledActionContext scheduledActionContext) {
        try {

            final EnviaBoleto enviaBoleto = new EnviaBoleto();

            // Processa o envio dos boletos
            enviaBoleto.processar();

            // Retorna mensagem de sucesso
            scheduledActionContext.info("Realizado com sucesso.");

        } catch (final Exception exception) {
            exception.printStackTrace();
            scheduledActionContext.log("Ocorreu um erro na execução da rotina: " + exception.getLocalizedMessage());
        }
    }
}