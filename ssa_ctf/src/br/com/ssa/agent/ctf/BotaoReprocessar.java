package br.com.ssa.agent.ctf;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
//import br.com.sankhya.jape.EntityFacade;
//import br.com.sankhya.jape.dao.JdbcWrapper;
//import br.com.sankhya.modelcore.util.EntityFacadeFactory;
//
//import java.math.BigDecimal;
//import java.sql.CallableStatement;
//import java.sql.Connection;

/**
 * Created by Henrique Eichler on 11/06/2017.
 */
public class BotaoReprocessar implements AcaoRotinaJava {

    @Override
    public void doAction(final ContextoAcao contextoAcao) throws Exception {

        // Contadores
        int impedidos = 0;
        int processados = 0;

        // Identifica o registro selecionado
        for (final Registro registro : contextoAcao.getLinhas()) {

            // Se o registro não está em status de processado
            if (!registro.getCampo("STATUS_PROCESSAMENTO").toString().equals("1")) {
                // Reenvia registro para Reprocessamento
                registro.setCampo("STATUS_PROCESSAMENTO", "0");
                registro.setCampo("DETALHE_PROCESSAMENTO", "");

                // Salva o registro
                registro.save();

                // Incrementa contador
                processados++;
            } else {
                impedidos++;
            }
        }

        // Exibe mensagens apropriadamente
        if (processados == 0 && impedidos == 0) {
            contextoAcao.setMensagemRetorno("Nada foi selecionado. Nenhuma ação foi tomada.");

        } else if (processados == 1 && impedidos == 0) {
            contextoAcao.setMensagemRetorno("Registro enviado para Reprocessamento com sucesso.");

        } else if (processados == 0 && impedidos == 1) {
            contextoAcao.mostraErro("O registro selecionado já foi processado.");

        } else if (processados == 1 && impedidos == 0) {
            contextoAcao.setMensagemRetorno("1 registro enviado para Reprocessamento com sucesso e 1 registro já estava processado.");

        } else if (processados > 1 && impedidos == 0) {
            contextoAcao.setMensagemRetorno(processados + " registros enviados para Reprocessamento com sucesso.");

        } else if (processados == 0 && impedidos > 1) {
            contextoAcao.mostraErro("Os registros selecionados já foram processados.");

        } else {
            contextoAcao.setMensagemRetorno(processados + " registros enviados para Reprocessamento com sucesso e " + impedidos + " já estavam processados.");

        }
    }

//    private void processar(final BigDecimal indice) throws Exception {
//        JdbcWrapper jdbcWrapper = null;
//        try {
//            final EntityFacade entityFacade = EntityFacadeFactory.getDWFFacade();
//            jdbcWrapper = entityFacade.getJdbcWrapper();
//            jdbcWrapper.openSession();
//
//            final Connection connection = jdbcWrapper.getConnection();
//            final CallableStatement callableStatement = connection.prepareCall("{call processa_abastece_indice_sf(?)}");
//            callableStatement.setBigDecimal(1, indice);
//            callableStatement.execute();
//
//        } finally {
//            JdbcWrapper.closeSession(jdbcWrapper);
//        }
//    }
}