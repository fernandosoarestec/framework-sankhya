package br.com.sankhya.ibc.acoes;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.ibc.business.EnviaBoleto;

public class BotaoEnviarEmails implements AcaoRotinaJava {

    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        try {
            final EnviaBoleto enviaBoleto = new EnviaBoleto();

            // Processa o envio dos boletos
            enviaBoleto.processar();

            // Retorna mensagem de sucesso.
            contextoAcao.setMensagemRetorno("Ação realizada com sucesso.");

        } catch (final Exception exception) {
            exception.printStackTrace();
            contextoAcao.setMensagemRetorno("Ocorreu um erro na sua solicitação: " + exception.getLocalizedMessage());
        }
    }
}
