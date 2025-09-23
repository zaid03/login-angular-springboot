package com.example.backend.service;

import java.io.ByteArrayInputStream;
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


@Service
public class SicalService {

  @Value("${sical.ws.url}")           // ADD THESE
    private String wsUrl;
    
    @Value("${sical.username}")
    private String username;
    
    @Value("${sical.password}")
    private String password;
    
    @Value("${sical.public.key}")
    private String publicKey;
    
    @Value("${sical.org.code}")
    private String orgCode;

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
                "<org> 0000000000 </org>" +
                "<ent> 0000000001 </ent>" +
                "<eje>2024</eje>" +
                "<usu>" + username + "</usu>" +
                "<pwd>" + CryptoSical.encodeSha1Base64(password) + "</pwd>" +
                "<fecha>" + fecha + "</fecha>" +
                "<nonce>" + nonce + "</nonce>" +
                "<token>" + token + "</token>" +
                "<tokenSha1>" + tokenSha1 + "</tokenSha1>" +
              "</sec>" +
              "<par><l_tercero><tercero>" +
                "<portal>S</portal>" +
                (nif != null ? "<NIFtercero>" + nif + "</NIFtercero>" : "") +
                (nom != null ? "<nomTercero>" + nom + "</nomTercero>" : "") +
                (apell != null ? "<apellTercero>" + apell + "</apellTercero>" : "") +
                "<indice>0</indice><NumRegDev>50</NumRegDev>" +
              "</tercero></l_tercero></par>" +
            "</e>";

        String soapEnvelope =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ci=\"http://ci.sw.aytos\">" +
              "<soapenv:Header/>" +
              "<soapenv:Body>" +
                "<ci:servicio>" +
                   "<ci:in0><![CDATA[" + xml + "]]></ci:in0>" +
                "</ci:servicio>" +
              "</soapenv:Body>" +
            "</soapenv:Envelope>";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/xml; charset=utf-8");
        headers.add(HttpHeaders.ACCEPT, "text/xml");
        headers.add("SOAPAction", "");

        HttpEntity<String> request = new HttpEntity<>(soapEnvelope, headers);

        RestTemplate restTemplate = new RestTemplate();
        String responseXml = restTemplate.postForObject(wsUrl, request, String.class);

        return parseTerceros(responseXml);
    }

    private List<Tercero> parseTerceros(String xml) throws Exception {
        List<Tercero> result = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));

        NodeList tercerosNodes = doc.getElementsByTagName("tercero");
        for (int i = 0; i < tercerosNodes.getLength(); i++) {
            Element e = (Element) tercerosNodes.item(i);
            Tercero t = new Tercero();
            t.setIdenTercero(getTagValue(e, "idenTercero"));
            t.setNIFtercero(getTagValue(e, "NIFtercero"));
            t.setNomTercero(getTagValue(e, "nomTercero"));
            t.setApellTercero(getTagValue(e, "apellTercero"));
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
