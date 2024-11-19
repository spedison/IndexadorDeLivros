package br.com.spedison.util;

import lombok.Data;
import lombok.Getter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.sql.rowset.spi.XmlReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;

@Getter
public class ConexaoConfigJDBC {

    private String usuario;
    private String senha;
    private String url;
    private String driver;

    public ConexaoConfigJDBC() {
        this("default");
    }

    public ConexaoConfigJDBC(String persistenceUnit) {
        // Abrir o arquivo de configuração e lê todas as linhas em META-INF/persistence.xml
        // Para cada elemento <property> na tag <persistence-unit> lê as configurações
        try {
            // Carrega o arquivo persistence.xml a partir do classpath
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("META-INF/persistence.xml");

            if (inputStream == null) {
                throw new IllegalArgumentException("Arquivo persistence.xml não encontrado no caminho especificado.");
            }

            // Configura o DocumentBuilder para processar o XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            // Configura o XPath para consultar o XML
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            // Expressão XPath para buscar uma tag específica, por exemplo, "persistence-unit"
            XPathExpression expression =
                    xpath
                            .compile("/persistence/persistence-unit[@name='%s']/properties/property"
                                    .formatted(persistenceUnit));

            NodeList nodeList = (NodeList) expression.evaluate(document, XPathConstants.NODESET);

            // Itera sobre os elementos encontrados e imprime o conteúdo
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element property = (Element) nodeList.item(i);
                String name = property.getAttribute("name");
                String value = property.getAttribute("value");

                if (name.toLowerCase().contains(".driver"))
                    driver = value;
                else if (name.toLowerCase().contains(".url"))
                    url = value;
                else if (name.toLowerCase().contains(".user"))
                    usuario = value;
                else if (name.toLowerCase().contains(".password"))
                    senha = value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        ConexaoConfigJDBC config = new ConexaoConfigJDBC();
//        System.out.println("Configurações JDBC:");
//        System.out.println("Usuário: " + config.getUsuario());
//        System.out.println("Senha: " + config.getSenha());
//        System.out.println("URL: " + config.getUrl());
//        System.out.println("Driver: " + config.getDriver());
//    }

}
