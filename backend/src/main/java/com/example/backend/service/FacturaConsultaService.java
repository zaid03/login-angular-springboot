package com.example.backend.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.example.backend.dto.FacturaConsultaRequestDto;

@Service
public class FacturaConsultaService {
    public String buildSmlInput(FacturaConsultaRequestDto req) {
        StringBuilder sb = new StringBuilder();
        sb.append("<e>");
        sb.append("<ope>");
        sb.append("<apl>SNP</apl>");
        sb.append("<tobj>Justificantes</tobj>");
        sb.append("<cmd>LST</cmd>");
        sb.append("<ver>2.0</ver>");
        sb.append("</ope>");
        sb.append("<sec>");
        sb.append("<cli>SAGE-AYTOS</cli>");
        sb.append("<org>").append(req.getOrg()).append("</org>");
        sb.append("<ent>").append(req.getEnt()).append("</ent>");
        sb.append("<eje>").append(req.getEje()).append("</eje>");
        sb.append("<usu>").append(req.getUsu()).append("</usu>");
        sb.append("<pwd>").append(req.getPwdSha1Base64()).append("</pwd>");
        sb.append("<fecha>").append(req.getFechaUtc()).append("</fecha>");
        sb.append("<nonce>").append(req.getNonce()).append("</nonce>");
        sb.append("<token>").append(req.getTokenSha512()).append("</token>");
        sb.append("<tokenSha1>").append(req.getTokenSha1()).append("</tokenSha1>");
        sb.append("</sec>");
        sb.append("<par>");
        if (req.getTipoDocumento() != null) sb.append("<tipoDocumento>").append(req.getTipoDocumento()).append("</tipoDocumento>");
        if (req.getCge() != null) sb.append("<cge>").append(req.getCge()).append("</cge>");
        if (req.getSituacionIgual() != null) sb.append("<situacionIgual>").append(req.getSituacionIgual()).append("</situacionIgual>");
        if (req.getEstado() != null) sb.append("<estado>").append(req.getEstado()).append("</estado>");
        if (req.getTercero() != null) sb.append("<tercero>").append(req.getTercero()).append("</tercero>");
        if (req.getDocProveedor() != null) sb.append("<docProveedor>").append(req.getDocProveedor()).append("</docProveedor>");
        if (req.getFecRegDesde() != null) sb.append("<fecRegDesde>").append(req.getFecRegDesde()).append("</fecRegDesde>");
        if (req.getFecRegHasta() != null) sb.append("<fecRegHasta>").append(req.getFecRegHasta()).append("</fecRegHasta>");
        if (req.getFecDocDesde() != null) sb.append("<fecDocDesde>").append(req.getFecDocDesde()).append("</fecDocDesde>");
        if (req.getFecDocHasta() != null) sb.append("<fecDocHasta>").append(req.getFecDocHasta()).append("</fecDocHasta>");
        sb.append("</par>");
        sb.append("</e>");
        return sb.toString();
    }

    public String sendSmlRequest(String smlInput, String url) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML); // or MediaType.APPLICATION_XML if required
        HttpEntity<String> entity = new HttpEntity<>(smlInput, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        return response.getBody();
    }
}