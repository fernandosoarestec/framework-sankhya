package br.com.ssa.agent.ctf;

import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.ssa.agent.ctf.util.ReaderCtf;
import br.com.ssa.agent.ctf.util.Template;
import br.com.ssa.agent.ctf.util.Xml;

import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import javax.ejb.ObjectNotFoundException;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;

/**
 * Created by Henrique Eichler on 03/06/2017.
 */
public class ImportacaoWebServiceCtf implements ScheduledAction {

    private static final String INSTANCIA = "ABASTECECTF";
    private static final String TABELA = "AD_ABASTECECTF";

    private final static int QUANTIDADE = 1000;

    /**
     * Função executada quando Schedule dispara no tempo agendado.
     * Executa a chamada ao WebService do CTF, importando os dados para cada um dos Templates disponíveis.
     *
     * @param context ScheduledActionContext Entidade disponibilizada pelo framework da Sankhya.
     */
    @Override
    public void onTime(final ScheduledActionContext context) {
        try {

            // Tenta executar a integração com webService do CTF
            processar();

        } catch (final Exception exception) {
            // Se ocorreu erro, expoem erro através da saída padrão.
            exception.printStackTrace();
        }
    }

    public void processar() throws Exception {
        // Carrega parametros do Sistemas (Tela de Preferências)
        final String webservice = carregarParametro("CTFWSURL");

        // Se os parâmetros para integração com CTF foram definidos
        if (webservice != null) {

            // Instancia objetos para acesso a dados e ao WebService da CTF.
            final EntityFacade entityFacade = EntityFacadeFactory.getDWFFacade();

            // Carrega lista de usuário de acesso ao ctf
            final FinderWrapper finderWrapper = new FinderWrapper("USUARIOCTF", "", new Object[] {});
            final Collection<PersistentLocalEntity> usuarios = entityFacade.findByDynamicFinder(finderWrapper);

            // Processa cada um dos acesso dos usuários
            for (final PersistentLocalEntity persistentLocalEntity : usuarios) {

                // Recupera a entidade de usuário vinculada
                final DynamicVO usuario = (DynamicVO) persistentLocalEntity.getValueObject();

                // Cria reader para carregar registros no WebService.
                final String user = usuario.asString("USUARIO");
                final String pass = usuario.asString("SENHA");
                final ReaderCtf readerCtf = new ReaderCtf(webservice, user, pass);

                // Identifica o Template que será utilizado
                final long templateId = usuario.asLong("TEMPLATE");
                final Template template = Template.getTemplate(templateId);

                // Identifica o ponteiro atual predefinido no cadastro do usuário
                long ponteiro = usuario.asLong("ULTIMO_APONTADOR");

                // Recupera xml do webservice com relação de registros para o template informado.
                final Xml xml = readerCtf.recuperar(ponteiro, templateId, QUANTIDADE);

                // Percorre a hierarquia do Xml para obter lista de registros.
                final Xml abastecimentos = xml.getPath(template.getPath());
                final List<Xml> registros = abastecimentos.getElements(template.getCollection());

                // Processa cada um dos registros do xml
                for (final Xml registro : registros) {

                    // Identifica o ponteiro atual do xml
                    final BigDecimal indice = new BigDecimal(registro.getLong("INDICE", 0L));

                    // Verifica se o ponteiro já existe na base de dados
                    try {
                        final PersistentLocalEntity persistentLocalEntityAbastece = entityFacade.findEntityByPrimaryKey(INSTANCIA, indice);

                        // Se existe, não faz nada


                    } catch(final ObjectNotFoundException objectNotFoundException) {
                        // Se o registro não existe:
                        // Cria a nova entidade para o indice informado
                        final DynamicVO dynamicVO = (DynamicVO) entityFacade.getDefaultValueObjectInstance(INSTANCIA);

                        // Informa o template utilizado para carregar o registro
                        dynamicVO.setProperty("TEMPLATE", template.getId());

                        // Para cada coluna disponível no template, identifica o valor do mesmo e seta na entidade.
                        for (final Template.Coluna coluna : template.getColunas()) {
                            if (coluna.getTag() != null) {
                                final String valor = registro.getString(coluna.getTag(), "");

                                if (valor != null && valor.length() > 0) {
                                    dynamicVO.setProperty(coluna.getColumn(), coluna.valueOf(valor));
                                }
                            }
                        }

                        // Marca registro para processamento.
                        dynamicVO.setProperty("STATUS_PROCESSAMENTO", "0");

                        // Persiste a entidade
                        entityFacade.createEntity(INSTANCIA, (EntityVO) dynamicVO);
                    }

                    // Incrementa o apontador
                    if (indice.longValue() > ponteiro) {
                        ponteiro = indice.longValue();
                    }
                }

                // Atualiza o ponteiro para o usuário
                usuario.setProperty("ULTIMO_APONTADOR", new BigDecimal(ponteiro));
                persistentLocalEntity.setValueObject((EntityVO) usuario);
            }
        }
    }


    /**
     * Função responsável por ler o valor de uma determinada chave dentro das preferências do sistema.
     *
     * @param chave String Identificador da preferência (parâmetro)
     * @return String valor previamente cadastrado para o parãmetro ou null.
     * @throws Exception Emitido quando da falha no processo de leitura.
     */
    private String carregarParametro(final String chave) throws Exception {
        String parametro = null;

        JdbcWrapper jdbcWrapper = null;
        try {
            final EntityFacade entityFacade = EntityFacadeFactory.getDWFFacade();
            jdbcWrapper = entityFacade.getJdbcWrapper();
            jdbcWrapper.openSession();

            final NativeSql nativeSql = new NativeSql(jdbcWrapper);
            nativeSql.appendSql("select texto ");
            nativeSql.appendSql("  from tsipar ");
            nativeSql.appendSql("  where chave = '" + chave + "'");

            final ResultSet resultSet = nativeSql.executeQuery();

            if (resultSet.next()) {
                parametro = resultSet.getString("texto");
            }

        } finally {
            JdbcWrapper.closeSession(jdbcWrapper);
        }

        return parametro;
    }
}