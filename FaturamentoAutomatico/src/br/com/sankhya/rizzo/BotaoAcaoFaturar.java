package br.com.sankhya.rizzo;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.rizzo.business.*;

public class BotaoAcaoFaturar implements AcaoRotinaJava {

	@Override
	public void doAction(final ContextoAcao contextoAcao) throws Exception {

		PedidosFaturar pedidos = new PedidosFaturar();
		pedidos.processar();
		contextoAcao.setMensagemRetorno("Procedimento executado");

	}

}
