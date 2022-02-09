package br.com.sankhya.rizzo.business;

import java.sql.ResultSet;
import java.sql.Timestamp;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.PlatformService;
import br.com.sankhya.modelcore.PlatformServiceFactory;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class PedidosFaturar {
	
	public void processar() throws Exception {
		
		JdbcWrapper jdbcWrapper = null;
		PlatformService faturamentoService = PlatformServiceFactory.getInstance().lookupService("@core:faturamento.service");
		try {

			final EntityFacade entityFacade = EntityFacadeFactory.getDWFFacade();
			jdbcWrapper = entityFacade.getJdbcWrapper();
			jdbcWrapper.openSession();
			
			final NativeSql nativeSql = new NativeSql(jdbcWrapper);
			nativeSql.appendSql("select nunota, codtipoper, codtipoperdestino, serie from vw_fat_aut_ped_rizzo ");

			final ResultSet resultSet = nativeSql.executeQuery();

			if (resultSet.next()) {
				faturamentoService.set("NUNOTA",  resultSet.getBigDecimal("NUNOTA")); //nro único do pedido	
				faturamentoService.set("CODTIPOPER", resultSet.getBigDecimal("CODTIPOPERDESTINO") ); //essa variável tem que vir de uma configuração ou parametro
				faturamentoService.set("DTFATURAMENTO", new Timestamp(System.currentTimeMillis() ) );
				faturamentoService.set("SERIE", resultSet.getString("SERIE") ); //isso tem que vir de algum lugar ou fixo, depende do caso
				
				faturamentoService.execute(); //aqui o faturamento acontece. Se o faturamento estiver configurado para confirmar a nota, então a NF-e/NFS-e será montada e enviada
		   }

		} catch (Exception e) {
			System.out.println("Erro ao tentar faturar pedido ");
			e.printStackTrace();
			// ignoramos o erro para não para o faturamento dos outros pedidos.
			// No caso real deve-se gerar algum aviso (tabela de avisos) ou
			// mesmo o envio de e-mail para um responsável
		}

		finally {
			JdbcWrapper.closeSession(jdbcWrapper);
		}	
	}
}
