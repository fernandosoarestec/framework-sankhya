package baianao;


import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;

public class ClaseBaiano implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao contexto) throws Exception {
		
		
		boolean confirma = contexto.confirmarSimNao("Botao do Baiano", "Pode executar Baiano ? ", 1); 
		
		if (confirma) {
			
			contexto.setMensagemRetorno("clicou no sim baianao");
			
		} else {
			
			contexto.setMensagemRetorno("clicou no nao ");
			
			
		}
		
		
		
		
	}

}
