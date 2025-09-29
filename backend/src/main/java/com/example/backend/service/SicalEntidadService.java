package com.example.backend.service;

import com.example.backend.dto.Entidad;
import com.example.sical.CryptoSical;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class SicalEntidadService {

    @Value("${sical.org.code}")
    private String orgCode;
    @Value("${sical.entidad}")
    private String entidad;
    @Value("${sical.eje}")
    private String eje;
    @Value("${sical.username}")
    private String username;
    @Value("${sical.password}")
    private String password;
    @Value("${sical.ws.url}")
    private String wsUrl;
    @Value("${sical.public.key}")
private String publicKey;

    public List<Entidad> getEntidades() throws Exception {
    CryptoSical.SecurityFields sec = CryptoSical.calculateSecurityFields(publicKey);

    String xml =
        "<e>" +
          "<ope><apl>SNP</apl><tobj>ListaEntidades</tobj><cmd>LST</cmd><ver>2.0</ver></ope>" +
          "<sec>" +
            "<cli>SAGE-AYTOS</cli>" +
            "<org>" + CryptoSical.encodeBase64(orgCode) + "</org>" +
            "<ent>" + CryptoSical.encodeBase64(entidad) + "</ent>" +
            "<eje>" + CryptoSical.encodeBase64(eje) + "</eje>" +
            "<usu>" + CryptoSical.encodeBase64(username) + "</usu>" +
            "<pwd>" + CryptoSical.encodeSha1Base64(password) + "</pwd>" +
            "<fecha>" + sec.created + "</fecha>" +
            "<nonce>" + sec.nonce + "</nonce>" +
            "<token>" + sec.token + "</token>" +
            "<tokenSha1>" + CryptoSical.encodeSha1Base64(sec.origin) + "</tokenSha1>" +
          "</sec>" +
          "<par><ruta></ruta></par>" +
        "</e>";

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
    headers.add(HttpHeaders.CONTENT_TYPE, "text/xml; charset=utf-8");
    headers.add("SOAPAction", "");

    System.out.println("=== SICAL REQUEST XML ===");
    System.out.println(xml);
    System.out.println("=== SICAL SOAP ENVELOPE ===");
    System.out.println(soapEnvelope);

    HttpEntity<String> request = new HttpEntity<>(soapEnvelope, headers);
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.postForEntity(wsUrl, request, String.class);

    System.out.println("=== SICAL RAW RESPONSE ===");
    System.out.println(response.getBody());
    System.out.println("=== END RESPONSE ===");

    // TODO: Parse response XML and extract Entidad list
    return new ArrayList<>();
}
}