package com.example.backend.service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.example.backend.dto.Tercero;
import com.example.sical.CryptoSical;

import org.apache.commons.text.StringEscapeUtils;

@Service
public class SicalService {

  @Value("${sical.ws.url}")           
    private String wsUrl;
    
    @Value("${sical.username}")
    private String username;
    
    @Value("${sical.password}")
    private String password;
    
    @Value("${sical.public.key}")
    private String publicKey;
    
    @Value("${sical.org.code}")
    private String orgCode;

    @Value("${sical.entidad}")
    private String entidad;

    @Value("${sical.eje}")
    private String eje;

    public List<Tercero> getTerceros(String nif, String nom, String apell) throws Exception {
      CryptoSical.SecurityFields sec = CryptoSical.calculateSecurityFields(publicKey);

      String fecha = sec.created;
      String nonce = sec.nonce;
      String token = sec.token;
      String tokenSha1 = CryptoSical.encodeSha1Base64(sec.origin);

      String xml =
          "<e>" +
            "<ope><apl>SNP</apl><tobj>TercerosyCuentas</tobj><cmd>LST</cmd><ver>2.0</ver></ope>" +
            "<sec>" +
              "<cli>SAGE-AYTOS</cli>" +
              "<org>" + orgCode + "</org>" +
              "<ent>" + entidad + "</ent>" +
              "<eje>" + eje + "</eje>" +
              "<usu>" + username + "</usu>" +
              "<pwd>" + CryptoSical.encodeSha1Base64(password) + "</pwd>" +
              "<fecha>" + fecha + "</fecha>" +
              "<nonce>" + nonce + "</nonce>" +
              "<token>" + token + "</token>" +
              "<tokenSha1>" + tokenSha1 + "</tokenSha1>" +
            "</sec>" +
            "<par>" +
              "<ruta></ruta>" +                                      
              "<l_tercero>" +
                "<tercero>" +
                  "<portal>S</portal>" + 
                  "<idenTercero></idenTercero>" +
                  (nif   != null ? "<NIFtercero>"   + CryptoSical.encodeBase64(nif)   + "</NIFtercero>"   : "") +
                  (nom   != null ? "<nomTercero>"   + CryptoSical.encodeBase64(nom)   + "</nomTercero>"   : "") +
                  (apell != null ? "<apellTercero>" + CryptoSical.encodeBase64(apell) + "</apellTercero>" : "") +
                  "<indice>0</indice>" +
                  "<NumRegDev>50</NumRegDev>" +
                "</tercero>" +
              "</l_tercero>" +
            "</par>"+
          "</e>";

      System.out.println("=== USERNAME SENT: " + username);
      System.out.println("=== PASSWORD (plain): " + password);
      System.out.println("=== PASSWORD (SHA1): " + CryptoSical.encodeSha1Base64(password));
      System.out.println("=== XML BEING SENT ===");
      System.out.println(xml);
      System.out.println("=== END XML ===");

      String soapEnvelope =
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
          "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:impl=\"http://desa-sical-ws:8080/services/Ci\">" +
            "<soapenv:Header/>" +
            "<soapenv:Body>" +
              "<impl:servicio>" +
                "<impl:in0><![CDATA[" + xml + "]]></impl:in0>" +
              "</impl:servicio>" +
            "</soapenv:Body>" +
          "</soapenv:Envelope>";

      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_TYPE, "text/xml");
      headers.add(HttpHeaders.ACCEPT, "text/xml");
      headers.add("SOAPAction", "");

      HttpEntity<String> request = new HttpEntity<>(soapEnvelope, headers);

      RestTemplate restTemplate = new RestTemplate();
      String responseXml = restTemplate.postForObject(wsUrl, request, String.class);

      System.out.println("=== SICAL RAW RESPONSE ===");
      System.out.println(responseXml);
      System.out.println("=== END RESPONSE ===");

      return parseTerceros(responseXml);
  }

    private List<Tercero> parseTerceros(String xml) throws Exception {
        List<Tercero> result = new ArrayList<>();

        String inner = null;
        int start = xml != null ? xml.indexOf("<servicioReturn") : -1;
        if (start >= 0) {
            int gt = xml.indexOf(">", start);
            int end = xml.indexOf("</servicioReturn>", gt);
            if (gt >= 0 && end >= 0) {
                inner = xml.substring(gt + 1, end);
            }
        }
        if (inner == null) {
            inner = xml == null ? "" : xml;
        }

        String sml = inner
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&apos;", "'");

        System.out.println("=== UNESCAPED SML ===");
        System.out.println(sml);
        System.out.println("=== END SML ===");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(sml.getBytes(StandardCharsets.UTF_8)));

        NodeList tercerosNodes = doc.getElementsByTagName("tercero");

        System.out.println("Found " + tercerosNodes.getLength() + " tercero nodes");
        for (int i = 0; i < tercerosNodes.getLength(); i++) {
            Element e = (Element) tercerosNodes.item(i);
            Tercero t = new Tercero();
t
            NodeList detterNodes = e.getElementsByTagName("detter");
            if (detterNodes.getLength() > 0) {
                String detter = detterNodes.item(0).getTextContent();
                String normalized = detter.replace("-@-", "@");
                String[] parts = normalized.split("@", -1);

                if (parts.length > 0) t.setIdenTercero(parts[0].trim());
                if (parts.length > 1) t.setNIFtercero(parts[1].trim());
                if (parts.length > 4) t.setNomTercero(parts[4].trim());
                if (parts.length > 22 && (t.getApellTercero() == null || t.getApellTercero().isEmpty())) {
                    t.setApellTercero(parts[22].trim());
                }
            } else {
                t.setIdenTercero(getTagValue(e, "idenTercero"));
                t.setNIFtercero(getTagValue(e, "NIFtercero"));
                t.setNomTercero(getTagValue(e, "nomTercero"));
                t.setApellTercero(getTagValue(e, "apellTercero"));
            }

            result.add(t);
        }

        return result;
    }

   private String getTagValue(Element parent, String tag) {
        NodeList nodes = parent.getElementsByTagName(tag);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return null;
    }

}
