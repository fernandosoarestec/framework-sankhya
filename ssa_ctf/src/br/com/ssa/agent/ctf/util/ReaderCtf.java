package br.com.ssa.agent.ctf.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Henrique Eichler on 31/05/2017.
 */
public class ReaderCtf {

    private String url;
    private String user;
    private String pass;

    public ReaderCtf(final String url, final String user, final String pass) {
        this.url = url;
        this.user = user;
        this.pass = pass;
    }

    public Xml listar() throws Exception {
        final String request = "" +
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                "  <soap12:Header>\n" +
                "    <SoapLogin xmlns=\"http://tempuri.org/\">\n" +
                "      <login>" + user + "</login>\n" +
                "      <senha>" + pass + "</senha>\n" +
                "    </SoapLogin>\n" +
                "  </soap12:Header>\n" +
                "  <soap12:Body>\n" +
                "    <ListarTemplatesDisponiveis xmlns=\"http://tempuri.org/\" />\n" +
                "  </soap12:Body>\n" +
                "</soap12:Envelope>";
        final byte[] post = request.getBytes();

        final Map<String, String> properties = new HashMap<>();
        properties.put("Content-Type", "application/soap+xml; charset=utf-8");

        final Http http = new Http(url);
        final byte[] response = http.post(properties, post);

        return Xml.parser(new String(response));
    }

    public Xml recuperar(final long ponteiro, final long template, final int quantidade) throws Exception {
        final String request = "" +
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                "  <soap12:Header>\n" +
                "    <SoapLogin xmlns=\"http://tempuri.org/\">\n" +
                "      <login>" + user + "</login>\n" +
                "      <senha>" + pass + "</senha>\n" +
                "    </SoapLogin>\n" +
                "  </soap12:Header>\n" +
                "  <soap12:Body>\n" +
                "    <RecuperarCopia xmlns=\"http://tempuri.org/\">\n" +
                "      <parametroCopia>\n" +
                "        <Ponteiro>" + ponteiro + "</Ponteiro>\n" +
                "        <CodTemplate>" + template + "</CodTemplate>\n" +
                "        <QtdRegistro>" + quantidade + "</QtdRegistro>\n" +
                "      </parametroCopia>\n" +
                "    </RecuperarCopia>\n" +
                "  </soap12:Body>\n" +
                "</soap12:Envelope>";
        final byte[] post = request.getBytes();

        final Map<String, String> properties = new HashMap<>();
        properties.put("Content-Type", "application/soap+xml; charset=utf-8");

        final Http http = new Http(url);

        final byte[] response = http.post(properties, post);
        return Xml.parser(new String(response));
    }
}