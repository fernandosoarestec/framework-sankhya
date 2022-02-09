package br.com.ssa.agent.ctf;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by henrique on 20/06/17.
 */
public class BotaoIgnorar implements AcaoRotinaJava {

    @Override
    public void doAction(final ContextoAcao contextoAcao) throws Exception {

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");

        // Contadores
        int processados = 0;

        // Identifica o registro selecionado
        for (final Registro registro : contextoAcao.getLinhas()) {

            // Se o registro não está em status de processado
            if (!registro.getCampo("STATUS_PROCESSAMENTO").toString().equals("1")) {
                // Reenvia registro para Reprocessamento
                registro.setCampo("STATUS_PROCESSAMENTO", "9");
                registro.setCampo("DETALHE_PROCESSAMENTO", "Marcado como ignorado por " + contextoAcao.getUsuarioLogado() + " em " + simpleDateFormat.format(new Date()));

                // Salva o registro
                registro.save();

                // Incrementa contador
                processados++;
            }
        }

        // Exibe mensagens apropriadamente
        if (processados == 0 ) {
            contextoAcao.setMensagemRetorno("Nada foi selecionado. Nenhuma ação foi tomada.");

        } else {
            contextoAcao.setMensagemRetorno("Alteração realizada com sucesso em " + processados + " registro(s).");
        }
    }
}