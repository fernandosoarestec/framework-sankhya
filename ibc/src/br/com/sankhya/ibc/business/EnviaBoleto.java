package br.com.sankhya.ibc.business;

import br.com.sankhya.ibc.utils.ByteArrayDataSource;
import br.com.sankhya.ibc.utils.Util;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.ws.ServiceContext;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class EnviaBoleto {

    public void processar() throws Exception {
        final EntityFacade entityFacade = EntityFacadeFactory.getDWFFacade();
        final JdbcWrapper jdbcWrapper = entityFacade.getJdbcWrapper();
        final Date dataInicioGeral = new Date();

        String notas = null;

        try {
            jdbcWrapper.openSession();

            final Connection connection = jdbcWrapper.getConnection();

            final String modelo      = carregarParametro(jdbcWrapper, "MODEMAILBOLIBC");
            final String copiaoculta = carregarParametro(jdbcWrapper, "LCCEMAILBOLIBC");

            final AuthenticationInfo authenticationInfo = new AuthenticationInfo("SUP", BigDecimal.ZERO, BigDecimal.ZERO, 0);
            authenticationInfo.makeCurrent();

            final ServiceContext serviceContext = new ServiceContext(null);
            serviceContext.setAutentication(authenticationInfo);
            serviceContext.makeCurrent();

            final List<Map<String, Object>> pedidos = carregarPedidos(jdbcWrapper);
            for (final Map<String, Object> pedido : pedidos) {
                final BigDecimal nunota = (BigDecimal) pedido.get("nunota");
                final String numeroNota = (String) pedido.get("numeronota");
                final String numeroPedido = (String) pedido.get("numeroibc");
                final Date dataInicio = new Date();

                try {
                    // Monta impressão do boleto
                    final byte[] arquivo = imprimir(nunota, connection);

                    // Monta o conteudo do e-mail
                    String conteudo = modelo;
                    for (final Map.Entry entry : pedido.entrySet()) {
                        if (entry.getValue() != null) {
                            conteudo = conteudo.replace("%%" + entry.getKey() + "%%", entry.getValue().toString());
                        } else {
                            throw new Exception("A informação de '" + entry.getKey() + "' não foi identificado no banco de dados.");
                        }
                    }

                    pedido.put("assunto", "Ordem de Pagamento #" + pedido.get("numeroibc") + " " + pedido.get("dataemissao"));
                    pedido.put("conteudo", conteudo);
                    pedido.put("copiaoculta", copiaoculta);

                    // Envia o email
                    enviar(pedido, arquivo);

                    // Atualiza a data e hora de envio do boleto
                    atualizaDataHoraEnvio(jdbcWrapper, nunota);

                    // Registra notas com envio
                    notas = (notas == null ? "" : (notas + ", ")) + nunota.toString();

                } catch(final Exception exception) {
                    final String log = "Erro ao enviar boleto:\n" +
                            " - nunota: " + nunota.toString() + ";\n" +
                            " - numero: " + numeroNota.toString() + ";\n" +
                            " - pedido ibc: " + numeroPedido + ";\n" +
                            " - erro:\n " + Util.getStackTrace(exception);
                    salvarLog(jdbcWrapper, dataInicio, new Date(), log);
                }
            }

            // Envia log de sucesso
            notas = "Realizado com sucesso.\n" + notas;
            salvarLog(jdbcWrapper, dataInicioGeral, new Date(), notas);
        } finally {
            JdbcWrapper.closeSession(jdbcWrapper);
        }
    }

    private List<Map<String, Object>> carregarPedidos(final JdbcWrapper jdbcWrapper) throws Exception {
        final List<Map<String, Object>> pedidos = new ArrayList<>();

        final NativeSql nativeSql = new NativeSql(jdbcWrapper);
        nativeSql.appendSql("select c.nunota, c.numnota numeronota, p.nomeparc nomeparceiro, to_char(c.dtneg,'dd/mm/yyyy') dataemissao, v.apelido nomeconsultor, p.email destinatario, v.email remetente, to_char(c.dtneg, 'yyyymmdd') || to_char(c.numnota) numeroibc\n" +
                "  from tgfcab c\n" +
                "    inner join tgfpar p on c.codparc = p.codparc\n" +
                "    inner join tgfven v on c.codvend = v.codvend\n" +
                "  where c.nunota in (select ci.nunota\n" +
                "                       from tgfcab ci\n" +
                "                         inner join tgffin fi on ci.nunota = fi.nunota\n" +
                "                       where ci.statusnota = 'L'\n" +
                "                         and ci.tipmov = 'P'\n" +
                "                         and ci.ad_dhenvboleto is null\n" +
                "                         and fi.linhadigitavel is not null\n" +
                "                         and fi.codigobarra is not null\n" +
                "                         and fi.codtiptit = 4\n" +
                "                         and fi.recdesp = 1\n" +
                "                         and fi.codctabcoint in (6, 7)\n" +
                "                         and fi.ad_dhremessa < trunc(sysdate)\n" +
                "                         and ((ci.codemp = 1 and exists(select 1 from tgfpar where codparc = ci.codparc and tippessoa = 'J') and exists(select 1 from tgfite where usoprod = 'S' and nunota = ci.nunota) and ci.ad_envioimpretido = 1)\n" +
                "                           or not (ci.codemp = 1 and exists(select 1 from tgfpar where codparc = ci.codparc and tippessoa = 'J') and exists(select 1 from tgfite where usoprod = 'S' and nunota = ci.nunota)))" +
                "                       group by ci.nunota)");

        try (final ResultSet resultSet = nativeSql.executeQuery()) {
            while (resultSet.next()) {
                final Map<String, Object> pedido = new HashMap<>();

                pedido.put("nunota", resultSet.getBigDecimal("nunota"));
                pedido.put("numeronota", resultSet.getString("numeronota"));
                pedido.put("nomeparceiro", resultSet.getString("nomeparceiro"));
                pedido.put("dataemissao", resultSet.getString("dataemissao"));
                pedido.put("nomeconsultor", resultSet.getString("nomeconsultor"));
                pedido.put("destinatario", resultSet.getString("destinatario"));
                pedido.put("remetente", resultSet.getString("remetente"));
                pedido.put("numeroibc", resultSet.getString("numeroibc"));

                pedidos.add(pedido);
            }
        }

        return pedidos;
    }

    private byte[] imprimir (final BigDecimal nunota, final Connection connection) throws IOException, JRException {
        try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            final Map<String, Object> parametros = new HashMap<>();
            parametros.put("nunota", nunota.toString());

            final JasperReport jasperReport = (JasperReport) JRLoader.loadObject("/home/mgeweb/modelos/ibc/boleto/boleto_template.jasper");
            final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);

            final JRExporter jrExporter = new JRPdfExporter();
            jrExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            jrExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, byteArrayOutputStream);
            jrExporter.exportReport();

            byteArrayOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    private void enviar(final Map<String, Object> dados, final byte[] anexo) throws MessagingException {
        final Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.host","smtp.sendgrid.net");

        final Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("IBCCOACHING", "ibc@sendgrid123");
            }
        };

        final Session session = Session.getInstance(properties, authenticator);

        final Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("ibc@sendgrid123"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(dados.get("destinatario").toString()));
        message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(dados.get("remetente").toString()));
        message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(dados.get("copiaoculta").toString()));
        message.setSubject(dados.get("assunto").toString());

        final BodyPart htmlBodyPart = new MimeBodyPart();
        htmlBodyPart.setContent(dados.get("conteudo"), "text/html; charset=ISO-8859-1");

        final MimeBodyPart attachement = new MimeBodyPart();
        attachement.setDataHandler(new DataHandler(new ByteArrayDataSource("boleto.pdf", anexo, "application/pdf")));
        attachement.setFileName("boleto.pdf");

        final Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(htmlBodyPart);
        multipart.addBodyPart(attachement);

        message.setContent(multipart);

        Transport.send(message);
    }

    private void salvarLog(final JdbcWrapper jdbcWrapper, final Date dataInicio, final Date dataFim, final String log) throws Exception {

        final String sql = "{ call stp_registra_log_ibc( 0, 'envio boleto java', ?, ?, ?) }";

        final Connection connection = jdbcWrapper.getConnection();
        try (final CallableStatement callableStatement = connection.prepareCall(sql)) {

            callableStatement.setDate(1, new java.sql.Date(dataInicio.getTime()));
            callableStatement.setDate(2, new java.sql.Date(dataFim.getTime()));
            callableStatement.setString(3, log);

            callableStatement.execute();
        }
    }

    private void atualizaDataHoraEnvio(final JdbcWrapper jdbcWrapper, final BigDecimal nunota) throws Exception {

        final String sql = "update tgfcab" +
                "  set ad_dhenvboleto = sysdate" +
                "  where nunota = " + nunota.toString();

        final Connection connection = jdbcWrapper.getConnection();
        try (final Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    private String carregarParametro(final JdbcWrapper jdbcWrapper, final String chave) throws Exception {

        final NativeSql nativeSql = new NativeSql(jdbcWrapper);
        nativeSql.appendSql("select texto ");
        nativeSql.appendSql("  from tsipar ");
        nativeSql.appendSql("  where chave = '" + chave + "'");

        try (final ResultSet resultSet = nativeSql.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getString("texto");
            } else {
                return null;
            }
        }
    }
}