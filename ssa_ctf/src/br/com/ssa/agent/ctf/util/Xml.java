package br.com.ssa.agent.ctf.util;

import java.io.*;
import java.util.*;

/**
 * @author Henrique Eichler
 */
public class Xml {

    private static final int NOME_TAG = 1,
            VALOR_TAG = 2,
            NOME_ATRIBUTO = 3,
            VALOR_ATRIBUTO_DELIMITADO_ASPAS_SIMPLES = 4,
            VALOR_ATRIBUTO_DELIMITADO_ASPAS_DUPLAS = 5,
            VALOR_ATRIBUTO_NAO_DELIMITADO = 6,
            FECHAMENTO = 7;

    private Xml parent;
    private String tag;
    private String value = null;
    private Map<String, String> attributes;
    private List<Xml> elements;

    public Xml(final Xml parent, final String tag) {
        this.parent = parent;
        this.tag = tag;
    }

    public Xml(final Xml parent, final String tag, final String value) {
        this.parent = parent;
        this.tag = tag;
        this.value = value;
    }

    public Xml getParent() {
        return parent;
    }

    public String getTag() {
        return tag;
    }

    public boolean getBoolean(final boolean default_) {
        return value != null ? value.toLowerCase().equals("true") : default_;
    }

    public boolean getBoolean(final String element, final boolean default_) {
        final Xml xml = getElement(element);
        return xml != null ? xml.getBoolean(default_) : default_;
    }

    public Integer getInteger(final Integer default_) {
        return value != null ? Integer.valueOf(value) : default_;
    }

    public Integer getInteger(final String element, final Integer default_) {
        final Xml xml = getElement(element);
        return xml != null ? xml.getInteger(default_) : default_;
    }

    public Long getLong(final Long default_) {
        return value != null ? Long.valueOf(value) : default_;
    }

    public Long getLong(final String element, final Long default_) {
        final Xml xml = getElement(element);
        return xml != null ? xml.getLong(default_) : default_;
    }

    public String getString(final String default_) {
        return value != null ? value : default_;
    }

    public String getString(final String element, final String default_) {
        final Xml xml = getElement(element);
        return xml != null ? xml.getString(default_) : default_;
    }

    public void setString(final String value) {
        this.value = value;
    }

    public void setString(final String element, final String value) {
        final Xml xml = getElement(element);
        if (xml != null) {
            xml.setString(element);
        } else {
            if (elements == null) {
                elements = new ArrayList<>();
            }
            elements.add(new Xml(this, element, value));
        }
    }

    public Xml getElement(final String tag) {
        Xml xml = null;

        if (elements != null) {
            for (final Iterator<Xml> iterator = elements.iterator(); iterator.hasNext() && xml == null; ) {
                xml = iterator.next();
                xml = xml.getTag().equals(tag) ? xml : null;
            }
        }

        return xml;
    }

    public List<Xml> getElements() {
        return elements;
    }

    public List<Xml> getElements(final String tag) {
        final List<Xml> list = new ArrayList<>();

        if (elements != null) {
            for (final Iterator<Xml> iterator = elements.iterator(); iterator.hasNext(); ) {
                final Xml xml = iterator.next();
                if (xml.getTag().equals(tag)) {
                    list.add(xml);
                }
            }
        }

        return list;
    }

    public Xml add(final Xml xml) {
        if (elements == null) {
            elements = new ArrayList<>();
        }
        elements.add(xml);

        return xml;
    }

    public void remover(final Xml xml) {
        if (elements != null) {
            elements.remove(xml);
        }
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public Xml getPath(final String path) {
        Xml xml = null;

        final String[] elements = path.split("\\.");
        for (final String element : elements) {
            if (xml == null) {
                xml = getElement(element);
            } else {
                xml = xml.getElement(element);
            }
        }

        return xml;
    }


    public void destroy() {
        if (parent != null) {
            parent.remover(this);
        }

        parent = null;
        tag = null;
        value = null;

        for (final Iterator<Xml> iterator = elements.iterator(); iterator.hasNext(); ) {
            iterator.next().destroy();
        }

        elements = null;
        attributes = null;
    }

    public static Xml parser(final String xml) throws Exception {
        try (final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xml.replace("&lt;", "<").replace("&gt;", ">").getBytes())) {
            return parser(byteArrayInputStream);
        }
    }

    public static Xml parser(final InputStream inputstream) throws Exception {
        int read;
        int dado = 0;
        char atual = 0;
        char anterior = 0;
        String value = "";

        Xml xml = new Xml(null, "");
        Xml attribute = null;

        while ((read = inputstream.read()) != -1) {
            anterior = atual;
            atual = (char) read;

            if (atual == '<') {  // Salva value da tag; Inicio de uma tag;
                if (dado == VALOR_TAG) {
                    xml.setString(value);
                }

                dado = NOME_TAG;
                value = "";

            } else if (atual == '?' && anterior == '<') {  // Inicio de uma tag de documentação do xml

            } else if (atual == ' ' && dado == NOME_TAG) { // Identificação do tag da tag; Inicio da lista de attributes da tag
                xml = xml.add(new Xml(xml, value.trim()));

                dado = NOME_ATRIBUTO;
                value = "";

            } else if (atual == '=' && dado == NOME_ATRIBUTO) { // Identificação do tag do atributo; Inicio do value do atributo sem delimitador
                attribute = new Xml(xml, value.trim());

                dado = VALOR_ATRIBUTO_NAO_DELIMITADO;
                value = "";

            } else if (atual == '\"' && anterior == '=' && dado == VALOR_ATRIBUTO_NAO_DELIMITADO) { // Delimitador do value do atributo por aspas duplas; // Inicio do value do atributo
                dado = VALOR_ATRIBUTO_DELIMITADO_ASPAS_DUPLAS;
                value = "";

            } else if (atual == '\'' && anterior == '=' && dado == VALOR_ATRIBUTO_NAO_DELIMITADO) { // Delimitador o value do atributo por aspas simples; // Inicio do value do atributo
                dado = VALOR_ATRIBUTO_DELIMITADO_ASPAS_SIMPLES;
                value = "";

            } else if ((atual == ' ' && dado == VALOR_ATRIBUTO_NAO_DELIMITADO) ||
                    (atual == '\"' && dado == VALOR_ATRIBUTO_DELIMITADO_ASPAS_DUPLAS) ||
                    (atual == '\'' && dado == VALOR_ATRIBUTO_DELIMITADO_ASPAS_SIMPLES)) {   // Identificação do value do atributo; Inicio do tag de novo atributo
                if (xml.attributes == null) {
                    xml.attributes = new HashMap<>();
                }
                xml.attributes.put(attribute.getTag(), value);

                dado = NOME_ATRIBUTO;
                value = "";

            } else if ((anterior == '?' && atual == '>') || (anterior == '/' && atual == '>')) { // Fechamento de uma tag; volta para identificar value da tag do pai
                xml = xml.getParent();

                dado = VALOR_TAG;
                value = "";

            } else if (anterior != '/' && atual == '>') { // Finalização de uma tag;
                if (dado == NOME_TAG) { // Identificão do tag da tag; inicio do value da tag
                    xml = xml.add(new Xml(xml, value));

                    dado = VALOR_TAG;
                    value = "";

                } else if (dado == NOME_ATRIBUTO) { // Identificação do tag de atributo vazio; inicio do value da tag
                    attribute = new Xml(xml, value.trim());

                    dado = VALOR_TAG;
                    value = "";

                } else if (dado == VALOR_ATRIBUTO_NAO_DELIMITADO) { // Identificação do value do atributo; inicio do value da tag
                    if (xml.attributes == null) {
                        xml.attributes = new HashMap<>();
                    }
                    xml.attributes.put(attribute.getTag(), value);

                    dado = VALOR_TAG;
                    value = "";

                } else if (dado == FECHAMENTO) { // Fechamento da tag atual
                    if (value.trim().equals(xml.getTag())) { // Verifica se o tag corresponde e volta para a tag pai
                        xml = xml.getParent();

                        dado = VALOR_TAG;
                        value = "";
                    } else
                        throw new Exception("Falha no fechamento da tag " + xml.getTag());
                }

            } else if (anterior == '<' && atual == '/') { // Value da tag identificado ; Inicia fechamento da tag
                dado = FECHAMENTO;
                value = "";

            } else {
                value += atual;
            }
        }

        return xml;
    }
}