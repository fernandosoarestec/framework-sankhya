package br.com.ssa.agent.ctf.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Henrique Eichler on 02/06/2017.
 */
public class Template {

    public static final Template ABASTECIMENTOS;
    public static final Template CANCELADOS;

    private static final List<Template> templates;

    static {
        ABASTECIMENTOS = new Template(21, "soap:Envelope.soap:Body.RecuperarCopiaResponse.RecuperarCopiaResult.ABASTECIMENTOS", "ABASTECIMENTOSRow");
        ABASTECIMENTOS.getColunas().add(new Inteiro("Indice",                                   "INDICE",             "INDICE"));
        ABASTECIMENTOS.getColunas().add(new Inteiro("Número do abastecimento",                  "NUMABAST",           "NUMABAST"));
        ABASTECIMENTOS.getColunas().add(new Inteiro("Código do Veículo",                        "VEICODIGO",          "VEICODIGO"));
        ABASTECIMENTOS.getColunas().add(new Inteiro("Tipo de Registro",                         "TPREG",              "TPREG"));
        ABASTECIMENTOS.getColunas().add(new Inteiro("Número da Bomba",                          "BOMBA",              "BOMBA"));
        ABASTECIMENTOS.getColunas().add(new Inteiro("Código da Rede",                           "REDE",               "REDE"));
        ABASTECIMENTOS.getColunas().add(new Inteiro("Código do Posto",                          "POSTO",              "POSTO"));
        ABASTECIMENTOS.getColunas().add(new Inteiro("Código da Frota",                          "FROTA",              "FROTA"));
        ABASTECIMENTOS.getColunas().add(new Texto("Tipo de Combustível",                        "TPCOMB",             "C"));
        ABASTECIMENTOS.getColunas().add(new Inteiro("Número da UVE",                            "UVE",                "UVE"));
        ABASTECIMENTOS.getColunas().add(new Texto("Placa",                                      "PLACA",              "PLACA"));
        ABASTECIMENTOS.getColunas().add(new Texto("Motorista",                                  "MOTORISTA",          "MOTORISTA"));
        ABASTECIMENTOS.getColunas().add(new Inteiro("Quilometragem",                            "KM",                 "KM"));
        ABASTECIMENTOS.getColunas().add(new Decimal("Quantidade de Litros",                     "QTD",                "QTD"));
        ABASTECIMENTOS.getColunas().add(new Decimal("Preço Unitário",                           "PU",                 "PU"));
        ABASTECIMENTOS.getColunas().add(new Decimal("Preço do Desconto",                        "PUBRAD",             "PUBRAD"));
        ABASTECIMENTOS.getColunas().add(new Decimal("Preço Total",                              "TOTAL",              "TOTAL"));
        ABASTECIMENTOS.getColunas().add(new Data("Data/Hora do Abastecimento",                  "DATA_ABASTECIMENTO", "DATA_ABASTECIMENTO", "dd/MM/yyyy HH:mm:ss"));
        ABASTECIMENTOS.getColunas().add(new Data("Data do Débito",                              "DATA_DEB",           "DATA_DEB",           "dd/MM/yyyy HH:mm:ss"));
        ABASTECIMENTOS.getColunas().add(new Data("Data do Crédito",                             "DATA_CRED",          "DATA_CRED",          "dd/MM/yyyy HH:mm:ss"));
        ABASTECIMENTOS.getColunas().add(new Inteiro("Somatória distância entre abastecimentos", "DIST_PERC",          "DIST_PERC"));
        ABASTECIMENTOS.getColunas().add(new Decimal("Somatória de quantidade",                  "COMB_TOTAL",         "COMB_TOTAL"));
        ABASTECIMENTOS.getColunas().add(new Texto("Status",                                     "STATUS",             "S"));
        ABASTECIMENTOS.getColunas().add(new Texto("Nome do Arquivo",                            "NOME_ARQUIVO",       "ABTARQUIVO"));
        ABASTECIMENTOS.getColunas().add(new Texto("Fantasia do Posto",                          "POSTO_FANTASIA",     "POSTO_FANTASIA"));
        ABASTECIMENTOS.getColunas().add(new Texto("Cidade do Posto",                            "POSTO_CIDADE",       "POSTO_CIDADE"));
        ABASTECIMENTOS.getColunas().add(new Texto("CGC do Posto",                               "CGC",                "CGC"));
        ABASTECIMENTOS.getColunas().add(new Inteiro("Abastecimento Anterior",                   "INDICE_ANTERIOR",    "CD_ABAT_ANTR_VEIC"));

        CANCELADOS = new Template(523, "soap:Envelope.soap:Body.RecuperarCopiaResponse.RecuperarCopiaResult.ABASTECIMENTOSCANCELADOS", "ABASTECIMENTOSCANCELADOSRow");
        CANCELADOS.getColunas().add(new Inteiro("Indice",                                   "INDICE",             "INDICE"));
        CANCELADOS.getColunas().add(new Inteiro("Número do abastecimento",                  "NUMABAST",           "NUMABAST"));
        CANCELADOS.getColunas().add(new Inteiro("Código do Veículo",                        "VEICODIGO",          "VEICODIGO"));
        CANCELADOS.getColunas().add(new Inteiro("Tipo de Registro",                         "TPREG",              "TPREG"));
        CANCELADOS.getColunas().add(new Inteiro("Número da Bomba",                          "BOMBA",              "BOMBA"));
        CANCELADOS.getColunas().add(new Inteiro("Código da Rede",                           "REDE",               "REDE"));
        CANCELADOS.getColunas().add(new Inteiro("Código do Posto",                          "POSTO",              "POSTO"));
        CANCELADOS.getColunas().add(new Inteiro("Código da Frota",                          "FROTA",              "FROTA"));
        CANCELADOS.getColunas().add(new Texto("Tipo de Combustível",                        "TPCOMB",             "C"));
        CANCELADOS.getColunas().add(new Inteiro("Número da UVE",                            "UVE",                "UVE"));
        CANCELADOS.getColunas().add(new Texto("Placa",                                      "PLACA",              "PLACA"));
        CANCELADOS.getColunas().add(new Texto("Motorista",                                  "MOTORISTA",          "MOTORISTA"));
        CANCELADOS.getColunas().add(new Inteiro("Quilometragem",                            "KM",                 "KM"));
        CANCELADOS.getColunas().add(new Decimal("Quantidade de Litros",                     "QTD",                "QTD"));
        CANCELADOS.getColunas().add(new Decimal("Preço Unitário",                           "PU",                 "PU"));
        CANCELADOS.getColunas().add(new Decimal("Preço do Desconto",                        "PUBRAD",             "PUBRAD"));
        CANCELADOS.getColunas().add(new Decimal("Preço Total",                              "TOTAL",              "TOTAL"));
        CANCELADOS.getColunas().add(new Data("Data/Hora do Abastecimento",                  "DATA_ABASTECIMENTO", "DATA_ABASTECIMENTO", "dd/MM/yyyy HH:mm:ss"));
        CANCELADOS.getColunas().add(new Data("Data do Débito",                              "DATA_DEB",           "DATA_DEB",           "dd/MM/yyyy HH:mm:ss"));
        CANCELADOS.getColunas().add(new Data("Data do Crédito",                             "DATA_CRED",          "DATA_CRED",          "dd/MM/yyyy HH:mm:ss"));
        CANCELADOS.getColunas().add(new Inteiro("Somatória distância entre abastecimentos", "DIST_PERC",          "DIST_PERC"));
        CANCELADOS.getColunas().add(new Decimal("Somatória de quantidade",                  "COMB_TOTAL",         "COMB_TOTAL"));
        CANCELADOS.getColunas().add(new Texto("Status",                                     "STATUS",             "S"));
        CANCELADOS.getColunas().add(new Texto("Nome do Arquivo",                            "NOME_ARQUIVO",       "ABTARQUIVO"));
        CANCELADOS.getColunas().add(new Texto("Fantasia do Posto",                          "POSTO_FANTASIA",     "POSTO_FANTASIA"));
        CANCELADOS.getColunas().add(new Texto("Cidade do Posto",                            "POSTO_CIDADE",       "POSTO_CIDADE"));
        CANCELADOS.getColunas().add(new Texto("CGC do Posto",                               "CGC",                "CGC"));
        CANCELADOS.getColunas().add(new Inteiro("Abastecimento Anterior",                   "INDICE_ANTERIOR",    "CD_ABAT_ANTR_VEIC"));

        templates = new ArrayList<>();
        templates.add(ABASTECIMENTOS);
        templates.add(CANCELADOS);
    }

    public static List<Template> getTemplates() {
        return templates;
    }

    public static Template getTemplate(final long id) {
        Template template = null;

        for(final Template t : templates) {
            if (t.getId().longValue() == id) {
                template = t;
                break;
            }
        }

        return template;
    }

    private BigDecimal id;
    private String path;
    private String collection;
    private List<Coluna> colunas;

    public Template(final int id, final String path, final String collection) {
        this.id = new BigDecimal(id);
        this.path = path;
        this.collection = collection;
        this.colunas = new ArrayList<>();
    }

    public BigDecimal getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getCollection() {
        return collection;
    }

    public List<Coluna> getColunas() {
        return colunas;
    }

    public static class Coluna {
        private String description;
        private String column;
        private String tag;

        private Coluna(final String description, final String column, final String tag) {
            this.description = description;
            this.column = column;
            this.tag = tag;
        }

        private Coluna(final String description, final String column) {
            this(description, column, null);
        }

        public String getDescription() {
            return description;
        }

        public String getColumn() {
            return column;
        }

        public String getTag() {
            return tag;
        }

        public Object valueOf(final String value) throws Exception {
            return null;
        }
    }

    public static class Texto extends Coluna {

        private Texto(final String description, final String column, final String tag) {
            super(description, column, tag);
        }

        @Override
        public Object valueOf(final String value) {
            return value;
        }
    }

    public static class Inteiro extends Coluna {

        private Inteiro(final String description, final String column, final String tag) {
            super(description, column, tag);
        }

        @Override
        public Object valueOf(final String value) {
            return new BigDecimal(value);
        }
    }

    public static class Decimal extends Coluna {

        private Decimal(final String description, final String column, final String tag) {
            super(description, column, tag);
        }

        @Override
        public Object valueOf(final String value) {
            return new BigDecimal(Double.valueOf(value.replace(".", "").replace(",", "."))).setScale(3, BigDecimal.ROUND_HALF_UP);
        }
    }

    public static class Data extends Coluna {

        private SimpleDateFormat simpleDateFormat;

        private Data(final String description, final String column, final String tag, final String formato) {
            super(description, column, tag);
            this.simpleDateFormat = new SimpleDateFormat(formato);
        }

        @Override
        public Object valueOf(final String value) throws ParseException {
            return new Timestamp(simpleDateFormat.parse(value).getTime());
        }
    }
}