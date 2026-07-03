package com.example.backend.service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.example.backend.dto.Operaciones;
import com.example.backend.dto.Operaciones.Dto;
import com.example.backend.dto.Operaciones.Iva;
import com.example.backend.dto.Operaciones.Linea;
import com.example.backend.dto.Operaciones.Relacion;
import com.example.backend.exception.SmlProcessingException;
import com.example.backend.exception.XmlParsingException;
import com.example.sical.CryptoSical;

@Service
public class OperacionesService {

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

    @Autowired(required = false)
    private RestTemplate restTemplate;

    public static class SearchCriteria {
        public final String numeroOperDesde;
        public final String numeroOperHasta;
        public final String codigoOperacion;
        public final String signo;
        public final String areaGestora;
        public final String fase;
        public final String fechaOperDesde;
        public final String fechaOperHasta;
        public final String tercero;
        public final String ascendente;
        public final String referencia;
        public final String organica;
        public final String funcional;
        public final String economica;
        public final String importeDesde;
        public final String importeHasta;
        public final String expediente;
        public final String grupoApunte; 
        public final String oficina;
        public final String fechaArqueo;
        public final String ordinal;
        public final String codterr;
        public final String pActMun;
        public final String ejeapli;
        public final String tipContrato;
        public final String proContrato;
        public final String criContrato;
        public final String tipoRelacion;
        public final String annoRelacion;
        public final String ordenRelacion;
        public final String solosaldo;
        public final String nlinea;
        public final String indice;
        public final Integer numRegDev;
        public final String expedienteElectronico;
        public final String desdetalle;

        private SearchCriteria(Builder builder) {
            this.numeroOperDesde = builder.numeroOperDesde;
            this.numeroOperHasta = builder.numeroOperHasta;
            this.codigoOperacion = builder.codigoOperacion;
            this.signo = builder.signo;
            this.areaGestora = builder.areaGestora;
            this.fase = builder.fase;
            this.fechaOperDesde = builder.fechaOperDesde;
            this.fechaOperHasta = builder.fechaOperHasta;
            this.tercero = builder.tercero;
            this.ascendente = builder.ascendente;
            this.referencia = builder.referencia;
            this.organica = builder.organica;
            this.funcional = builder.funcional;
            this.economica = builder.economica;
            this.importeDesde = builder.importeDesde;
            this.importeHasta = builder.importeHasta;
            this.expediente = builder.expediente;
            this.grupoApunte = builder.grupoApunte;
            this.oficina = builder.oficina;
            this.fechaArqueo = builder.fechaArqueo;
            this.ordinal = builder.ordinal;
            this.codterr = builder.codterr;
            this.pActMun = builder.pActMun;
            this.ejeapli = builder.ejeapli;
            this.tipContrato = builder.tipContrato;
            this.proContrato = builder.proContrato;
            this.criContrato = builder.criContrato;
            this.tipoRelacion = builder.tipoRelacion;
            this.annoRelacion = builder.annoRelacion;
            this.ordenRelacion = builder.ordenRelacion;
            this.solosaldo = builder.solosaldo;
            this.nlinea = builder.nlinea;
            this.indice = builder.indice;
            this.numRegDev = builder.numRegDev;
            this.expedienteElectronico = builder.expedienteElectronico;
            this.desdetalle = builder.desdetalle;
        }

        public static class Builder {
            private String numeroOperDesde;
            private String numeroOperHasta;
            private String codigoOperacion;
            private String signo;
            private String areaGestora;
            private String fase;
            private String fechaOperDesde;
            private String fechaOperHasta;
            private String tercero;
            private String ascendente;
            private String referencia;
            private String organica;
            private String funcional;
            private String economica;
            private String importeDesde;
            private String importeHasta;
            private String expediente;
            private String grupoApunte;
            private String oficina;
            private String fechaArqueo;
            private String ordinal;
            private String codterr;
            private String pActMun;
            private String ejeapli;
            private String tipContrato;
            private String proContrato;
            private String criContrato;
            private String tipoRelacion;
            private String annoRelacion;
            private String ordenRelacion;
            private String solosaldo;
            private String nlinea;
            private String indice;
            private Integer numRegDev;
            private String expedienteElectronico;
            private String desdetalle;

            public Builder numeroOperDesde(String numeroOperDesde) {
                this.numeroOperDesde = numeroOperDesde;
                return this;
            }

            public Builder numeroOperHasta(String numeroOperHasta) {
                this.numeroOperHasta = numeroOperHasta;
                return this;
            }

            public Builder codigoOperacion(String codigoOperacion) {
                this.codigoOperacion = codigoOperacion;
                return this;
            }

            public Builder signo(String signo) {
                this.signo = signo;
                return this;
            }

            public Builder areaGestora(String areaGestora) {
                this.areaGestora = areaGestora;
                return this;
            }

            public Builder fase(String fase) {
                this.fase = fase;
                return this;
            }

            public Builder fechaOperDesde(String fechaOperDesde) {
                this.fechaOperDesde = fechaOperDesde;
                return this;
            }

            public Builder fechaOperHasta(String fechaOperHasta) {
                this.fechaOperHasta = fechaOperHasta;
                return this;
            }

            public Builder tercero(String tercero) {
                this.tercero = tercero;
                return this;
            }

            public Builder ascendente(String ascendente) {
                this.ascendente = ascendente;
                return this;
            }

            public Builder referencia(String referencia) {
                this.referencia = referencia;
                return this;
            }

            public Builder organica(String organica) {
                this.organica = organica;
                return this;
            }

            public Builder funcional(String funcional) {
                this.funcional = funcional;
                return this;
            }

            public Builder economica(String economica) {
                this.economica = economica;
                return this;
            }

            public Builder importeDesde(String importeDesde) {
                this.importeDesde = importeDesde;
                return this;
            }

            public Builder importeHasta(String importeHasta) {
                this.importeHasta = importeHasta;
                return this;
            }

            public Builder expediente(String expediente) {
                this.expediente = expediente;
                return this;
            }

            public Builder grupoApunte(String grupoApunte) {
                this.grupoApunte = grupoApunte;
                return this;
            }

            public Builder oficina(String oficina) {
                this.oficina = oficina;
                return this;
            }

            public Builder fechaArqueo(String fechaArqueo) {
                this.fechaArqueo = fechaArqueo;
                return this;
            }

            public Builder ordinal(String ordinal) {
                this.ordinal = ordinal;
                return this;
            }

            public Builder codterr(String codterr) {
                this.codterr = codterr;
                return this;
            }

            public Builder pActMun(String pActMun) {
                this.pActMun = pActMun;
                return this;
            }

            public Builder ejeapli(String ejeapli) {
                this.ejeapli = ejeapli;
                return this;
            }

            public Builder tipContrato(String tipContrato) {
                this.tipContrato = tipContrato;
                return this;
            }

            public Builder proContrato(String proContrato) {
                this.proContrato = proContrato;
                return this;
            }

            public Builder criContrato(String criContrato) {
                this.criContrato = criContrato;
                return this;
            }

            public Builder tipoRelacion(String tipoRelacion) {
                this.tipoRelacion = tipoRelacion;
                return this;
            }

            public Builder annoRelacion(String annoRelacion) {
                this.annoRelacion = annoRelacion;
                return this;
            }

            public Builder ordenRelacion(String ordenRelacion) {
                this.ordenRelacion = ordenRelacion;
                return this;
            }

            public Builder solosaldo(String solosaldo) {
                this.solosaldo = solosaldo;
                return this;
            }

            public Builder nlinea(String nlinea) {
                this.nlinea = nlinea;
                return this;
            }

            public Builder indice(String indice) {
                this.indice = indice;
                return this;
            }

            public Builder numRegDev(Integer numRegDev) {
                this.numRegDev = numRegDev;
                return this;
            }

            public Builder expedienteElectronico(String expedienteElectronico) {
                this.expedienteElectronico = expedienteElectronico;
                return this;
            }

            public Builder desdetalle(String desdetalle) {
                this.desdetalle = desdetalle;
                return this;
            }

            public SearchCriteria build() {
                return new SearchCriteria(this);
            }
        }
    }

        public List<Operaciones> getOperaciones(SearchCriteria criteria) throws SmlProcessingException {
            try {
                CryptoSical.SecurityFields sec = CryptoSical.calculateSecurityFields(publicKey);
                String fecha = sec.created;
                String nonce = sec.nonce;
                String token = sec.token;
                String tokenSha1 = CryptoSical.encodeSha1Base64(sec.origin);

                StringBuilder filtro = new StringBuilder("<filtro>");
                appendRawTag(filtro, "numeroOperDesde", criteria.numeroOperDesde);
                appendRawTag(filtro, "numeroOperHasta", criteria.numeroOperHasta);
                appendB64Tag(filtro, "codigoOperacion", criteria.codigoOperacion);
                appendRawTag(filtro, "signo", criteria.signo);
                appendB64Tag(filtro, "areaGestora", criteria.areaGestora);
                appendB64Tag(filtro, "fase", criteria.fase);
                appendRawTag(filtro, "fechaOperDesde", criteria.fechaOperDesde);
                appendRawTag(filtro, "fechaOperHasta", criteria.fechaOperHasta);
                appendB64Tag(filtro, "tercero", criteria.tercero);
                appendRawTag(filtro, "ascendente", criteria.ascendente);
                appendRawTag(filtro, "referencia", criteria.referencia);
                appendB64Tag(filtro, "organica", criteria.organica);
                appendB64Tag(filtro, "funcional", criteria.funcional);
                appendB64Tag(filtro, "economica", criteria.economica);
                appendRawTag(filtro, "importeDesde", criteria.importeDesde);
                appendRawTag(filtro, "importeHasta", criteria.importeHasta);
                appendB64Tag(filtro, "expediente", criteria.expediente);
                appendB64Tag(filtro, "grupoApunte", criteria.grupoApunte);
                appendB64Tag(filtro, "oficina", criteria.oficina);
                appendRawTag(filtro, "fechaArqueo", criteria.fechaArqueo);
                appendRawTag(filtro, "ordinal", criteria.ordinal);
                appendB64Tag(filtro, "codterr", criteria.codterr);
                appendB64Tag(filtro, "PActMun", criteria.pActMun);
                appendRawTag(filtro, "ejeapli", criteria.ejeapli);
                appendB64Tag(filtro, "tipContrato", criteria.tipContrato);
                appendB64Tag(filtro, "proContrato", criteria.proContrato);
                appendB64Tag(filtro, "criContrato", criteria.criContrato);
                appendB64Tag(filtro, "TipoRelacion", criteria.tipoRelacion);
                appendRawTag(filtro, "AnnoRelacion", criteria.annoRelacion);
                appendRawTag(filtro, "OrdenRelacion", criteria.ordenRelacion);
                appendB64Tag(filtro, "solosaldo", criteria.solosaldo);
                appendRawTag(filtro, "nlinea", criteria.nlinea);
                appendRawTag(filtro, "indice", criteria.indice);
                appendRawTag(filtro, "NumRegDev", criteria.numRegDev != null ? String.valueOf(criteria.numRegDev) : "50");
                appendB64Tag(filtro, "ExpedienteElectronico", criteria.expedienteElectronico);
                filtro.append("</filtro>");
                String filtroXml = filtro.toString();

                String xml =
                    "<e>" +
                    "<ope><apl>SNP</apl><tobj>ConOpeGastos</tobj><cmd>LST</cmd><ver>2.0</ver></ope>" +
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
                    "<desdetalle>" + (normalize(criteria.desdetalle) != null ? normalize(criteria.desdetalle) : "S") + "</desdetalle>" +
                    filtroXml +
                    "</par>" +
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

                // Log the generated XML and SOAP envelope to the terminal
                System.out.println("DEBUG - filtro xmn: " + xml);
                System.out.println("DEBUG - SML XML Payload: " + xml);

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_TYPE, "text/xml");
                headers.add(HttpHeaders.ACCEPT, "text/xml");
                headers.add("SOAPAction", "");

                RestTemplate template = (this.restTemplate != null) ? this.restTemplate : new RestTemplate();
                String endpoint = (wsUrl != null && wsUrl.contains("?")) ? wsUrl.substring(0, wsUrl.indexOf("?")) : wsUrl;
                String responseXml = template.postForObject(endpoint, new HttpEntity<>(soapEnvelope, headers), String.class);

                return parseOperaciones(responseXml);
            } catch (SmlProcessingException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new SmlProcessingException("Error retrieving operaciones: " + ex.getMessage(), ex);
            }
        }

    private List<Operaciones> parseOperaciones(String xml) throws SmlProcessingException {
        List<Operaciones> result = new ArrayList<>();
        
        String innerXml = extractInnerXmlContent(xml);
        String sml = unescapeXmlEntities(innerXml);
        
        if (sml == null) {
            sml = "";
        }
        if (sml.isEmpty()) {
            return result;
        }
        
        try {
            Document doc = parseXmlDocument(sml);
            if (validateAndThrowIfError(doc)) {
                return result;
            }
            
            NodeList operNodes = doc.getElementsByTagName("operacion");
            for (int i = 0; i < operNodes.getLength(); i++) {
                Element opEl = (Element) operNodes.item(i);
                Operaciones op = createOperacionFromElement(opEl);
                result.add(op);
            }
        } catch (XmlParsingException ex) {
            throw new SmlProcessingException("XML parsing error: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new SmlProcessingException("Error processing response: " + ex.getMessage(), ex);
        }
        
        return result;
    }
    
    private String extractInnerXmlContent(String xml) {
        if (xml == null) {
            return "";
        }
        
        int start = xml.indexOf("<servicioReturn");
        if (start < 0) {
            return xml;
        }
        
        int gt = xml.indexOf(">", start);
        int end = xml.indexOf("</servicioReturn>", gt);
        
        if (gt >= 0 && end >= 0) {
            return xml.substring(gt + 1, end);
        }
        
        return xml;
    }
    
    private String unescapeXmlEntities(String xml) {
        return xml.replace("&lt;", "<")
                  .replace("&gt;", ">")
                  .replace("&amp;", "&")
                  .replace("&quot;", "\"")
                  .replace("&apos;", "'");
    }
    
    private Document parseXmlDocument(String sml) throws com.example.backend.exception.XmlParsingException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(sml.getBytes(StandardCharsets.UTF_8)));
        } catch (javax.xml.parsers.ParserConfigurationException | org.xml.sax.SAXException | java.io.IOException ex) {
            throw new com.example.backend.exception.XmlParsingException("Failed to parse XML document", ex);
        }
    }
    
    private boolean validateAndThrowIfError(Document doc) throws SmlProcessingException {
        NodeList exitoNodes = doc.getElementsByTagName("exito");
        if (exitoNodes.getLength() == 0) {
            return false;
        }
        
        String exito = exitoNodes.item(0).getTextContent();
        if ("-1".equals(exito) || "1".equals(exito)) {
            return false;
        }
        
        String desc = "";
        NodeList descNodes = doc.getElementsByTagName("desc");
        if (descNodes.getLength() > 0) {
            desc = descNodes.item(0).getTextContent();
        }
        if (desc != null && desc.toLowerCase().contains("no hay datos")) {
            return true;
        }
        throw new SmlProcessingException("SICAL error: " + desc);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void appendRawTag(StringBuilder sb, String tagName, String value) {
        String normalized = normalize(value);
        if (normalized != null) {
            sb.append("<").append(tagName).append(">")
              .append(normalized)
              .append("</").append(tagName).append(">");
        }
    }

    private void appendB64Tag(StringBuilder sb, String tagName, String value) {
        String normalized = normalize(value);
        if (normalized != null) {
            sb.append("<").append(tagName).append(">")
              .append(CryptoSical.encodeBase64(normalized))
              .append("</").append(tagName).append(">");
        }
    }
    
    private Operaciones createOperacionFromElement(Element opEl) {
        Operaciones op = new Operaciones();
        op.setNumope(toLong(getTagValue(opEl, "numope")));
        op.setCodope(decodeOrNull(getTagValue(opEl, "codope")));
        op.setSigno(decodeOrNull(getTagValue(opEl, "signo")));
        op.setFase(decodeOrNull(getTagValue(opEl, "fase")));
        op.setArea(decodeOrNull(getTagValue(opEl, "area")));
        op.setAgrupacion(decodeOrNull(getTagValue(opEl, "agrupacion")));
        op.setNifter(decodeOrNull(getTagValue(opEl, "nifter")));
        op.setNifend(decodeOrNull(getTagValue(opEl, "nifend")));
        op.setCuenta(decodeOrNull(getTagValue(opEl, "cuenta")));
        op.setFechaentrada(getTagValue(opEl, "fechaentrada"));
        op.setFecope(getTagValue(opEl, "fecope"));
        op.setGapuntes(decodeOrNull(getTagValue(opEl, "gapuntes")));
        op.setDocumento(decodeOrNull(getTagValue(opEl, "documento")));
        op.setFechadocu(getTagValue(opEl, "fechadocu"));
        op.setOrdinal(decodeOrNull(getTagValue(opEl, "ordinal")));
        op.setFechapago(getTagValue(opEl, "fechapago"));
        op.setTipopago(decodeOrNull(getTagValue(opEl, "tipopago")));
        op.setTipoexp(decodeOrNull(getTagValue(opEl, "tipoexp")));
        op.setNexp(decodeOrNull(getTagValue(opEl, "nexp")));
        op.setFechaexp(getTagValue(opEl, "fechaexp"));
        op.setAreages(decodeOrNull(getTagValue(opEl, "areages")));
        op.setOficina(decodeOrNull(getTagValue(opEl, "oficina")));
        op.setImporte(toDouble(getTagValue(opEl, "importe")));
        op.setImpiva(toDouble(getTagValue(opEl, "impiva")));
        op.setImpdto(toDouble(getTagValue(opEl, "impdto")));
        op.setTexto(decodeOrNull(getTagValue(opEl, "texto")));
        op.setNumcaja(toLong(getTagValue(opEl, "numcaja")));
        op.setAnoprestamo(toInteger(getTagValue(opEl, "anoprestamo")));
        op.setTipoprestamo(decodeOrNull(getTagValue(opEl, "tipoprestamo")));
        op.setNumprestamo(decodeOrNull(getTagValue(opEl, "numprestamo")));
        op.setTerite(toLong(getTagValue(opEl, "terite")));
        op.setEndite(toLong(getTagValue(opEl, "endite")));
        op.setNumOpePrev(toLong(getTagValue(opEl, "numopedef")));
        op.setTipContrato(decodeOrNull(getTagValue(opEl, "tipContrato")));
        op.setProContrato(decodeOrNull(getTagValue(opEl, "proContrato")));
        op.setCriContrato(decodeOrNull(getTagValue(opEl, "criContrato")));
        String nExpElec = getTagValue(opEl, "nExpElec");
        if (nExpElec == null) {
            nExpElec = getTagValue(opEl, "NExpElec");
        }
        op.setNExpElec(decodeOrNull(nExpElec));
        
        op.setDtoList(parseDtoList(opEl));
        op.setIvaList(parseIvaList(opEl));
        op.setRelacionList(parseRelacionList(opEl));
        op.setLineaList(parseLineaList(opEl));
        
        return op;
    }
    private List<Dto> parseDtoList(Element opEl) {
        List<Dto> list = new ArrayList<>();
        NodeList dtoNodes = opEl.getElementsByTagName("dto");
        for (int i = 0; i < dtoNodes.getLength(); i++) {
            Element dtoEl = (Element) dtoNodes.item(i);
            Dto dto = new Dto();
            dto.setNumdto(toLong(getTagValue(dtoEl, "numdto")));
            dto.setDtocuenta(decodeOrNull(getTagValue(dtoEl, "dtocuenta")));
            dto.setDtoeje(toInteger(getTagValue(dtoEl, "dtoeje")));
            dto.setDtoorg(decodeOrNull(getTagValue(dtoEl, "dtoorg")));
            dto.setDtofun(decodeOrNull(getTagValue(dtoEl, "dtofun")));
            dto.setDtoeco(decodeOrNull(getTagValue(dtoEl, "dtoeco")));
            dto.setDtoimp(toDouble(getTagValue(dtoEl, "dtoimp")));
            dto.setDtosaldo(toDouble(getTagValue(dtoEl, "dtosaldo")));
            dto.setDtobase(toDouble(getTagValue(dtoEl, "dtobase")));
            dto.setDtosaldobase(toDouble(getTagValue(dtoEl, "dtosaldobase")));
            dto.setDtopretencion(toDouble(getTagValue(dtoEl, "dtopretencion")));
            dto.setDtonumopecan(toDouble(getTagValue(dtoEl, "dtonumopecan")));
            dto.setDtolinopecan(toLong(getTagValue(dtoEl, "dtolinopecan")));
            dto.setDtotipoopecan(decodeOrNull(getTagValue(dtoEl, "dtotipoopecan")));
            dto.setDtotexto(decodeOrNull(getTagValue(dtoEl, "dtotexto")));
            dto.setDtonif(decodeOrNull(getTagValue(dtoEl, "dtonif")));
            dto.setDtoanodevengo(toInteger(getTagValue(dtoEl, "dtoanodevengo")));
            dto.setDtoclave(decodeOrNull(getTagValue(dtoEl, "dtoclave")));
            dto.setDtolobtencion(decodeOrNull(getTagValue(dtoEl, "dtolobtencion")));
            dto.setDtocargo(decodeOrNull(getTagValue(dtoEl, "dtocargo")));
            dto.setDtobase1(toDouble(getTagValue(dtoEl, "dtobase1")));
            dto.setDtobase2(toDouble(getTagValue(dtoEl, "dtobase2")));
            dto.setDtobase3(toDouble(getTagValue(dtoEl, "dtobase3")));
            dto.setDtoiva1(toDouble(getTagValue(dtoEl, "dtoiva1")));
            dto.setDtoiva2(toDouble(getTagValue(dtoEl, "dtoiva2")));
            dto.setDtoiva3(toDouble(getTagValue(dtoEl, "dtoiva3")));
            dto.setDtotiva1(toDouble(getTagValue(dtoEl, "dtotiva1")));
            dto.setDtotiva2(toDouble(getTagValue(dtoEl, "dtotiva2")));
            dto.setDtotiva3(toDouble(getTagValue(dtoEl, "dtotiva3")));
            dto.setDtoporcent1(toDouble(getTagValue(dtoEl, "dtoporcent1")));
            dto.setDtoporcent2(toDouble(getTagValue(dtoEl, "dtoporcent2")));
            dto.setDtoporcent3(toDouble(getTagValue(dtoEl, "dtoporcent3")));
            list.add(dto);
        }
        return list;
    }

    private List<Iva> parseIvaList(Element opEl) {
        List<Iva> list = new ArrayList<>();
        NodeList ivaNodes = opEl.getElementsByTagName("iva");
        for (int i = 0; i < ivaNodes.getLength(); i++) {
            Element ivaEl = (Element) ivaNodes.item(i);
            Iva iva = new Iva();
            iva.setIvabase1(toDouble(getTagValue(ivaEl, "ivabase1")));
            iva.setIvabase2(toDouble(getTagValue(ivaEl, "ivabase2")));
            iva.setIvabase3(toDouble(getTagValue(ivaEl, "ivabase3")));
            iva.setIvasbase1(toDouble(getTagValue(ivaEl, "ivasbase1")));
            iva.setIvasbase2(toDouble(getTagValue(ivaEl, "ivasbase2")));
            iva.setIvasbase3(toDouble(getTagValue(ivaEl, "ivasbase3")));
            iva.setIvativa1(toDouble(getTagValue(ivaEl, "ivativa1")));
            iva.setIvativa2(toDouble(getTagValue(ivaEl, "ivativa2")));
            iva.setIvativa3(toDouble(getTagValue(ivaEl, "ivativa3")));
            iva.setIvaporcent1(toDouble(getTagValue(ivaEl, "ivaporcent1")));
            iva.setIvaporcent2(toDouble(getTagValue(ivaEl, "ivaporcent2")));
            iva.setIvaporcent3(toDouble(getTagValue(ivaEl, "ivaporcent3")));
            iva.setIvaimp1(toDouble(getTagValue(ivaEl, "ivaimp1")));
            iva.setIvaimp2(toDouble(getTagValue(ivaEl, "ivaimp2")));
            iva.setIvaimp3(toDouble(getTagValue(ivaEl, "ivaimp3")));
            iva.setIvabexenta(toDouble(getTagValue(ivaEl, "ivabexenta")));
            iva.setIvaciv1(decodeOrNull(getTagValue(ivaEl, "ivaciv1")));
            iva.setIvaciv2(decodeOrNull(getTagValue(ivaEl, "ivaciv2")));
            list.add(iva);
        }
        return list;
    }

    private List<Relacion> parseRelacionList(Element opEl) {
        List<Relacion> list = new ArrayList<>();
        NodeList relNodes = opEl.getElementsByTagName("Relacion");
        for (int i = 0; i < relNodes.getLength(); i++) {
            Element relEl = (Element) relNodes.item(i);
            Relacion rel = new Relacion();
            rel.setTipoRelacion(decodeOrNull(getTagValue(relEl, "TipoRelacion")));
            rel.setAnnoRelacion(toInteger(getTagValue(relEl, "AnnoRelacion")));
            rel.setOrdenRelacion(toInteger(getTagValue(relEl, "OrdenRelacion")));
            list.add(rel);
        }
        return list;
    }

    private List<Linea> parseLineaList(Element opEl) {
        List<Linea> list = new ArrayList<>();
        NodeList lineaNodes = opEl.getElementsByTagName("linea");
        for (int i = 0; i < lineaNodes.getLength(); i++) {
            Element linEl = (Element) lineaNodes.item(i);
            Linea linea = new Linea();
            linea.setNlinea(toInteger(getTagValue(linEl, "nlinea")));
            linea.setOpeasc(toLong(getTagValue(linEl, "opeasc")));
            linea.setLineasc(toLong(getTagValue(linEl, "lineasc")));
            linea.setLincta(decodeOrNull(getTagValue(linEl, "lincta")));
            linea.setPrya(toInteger(getTagValue(linEl, "prya")));
            linea.setPryt(decodeOrNull(getTagValue(linEl, "pryt")));
            linea.setPryo(toInteger(getTagValue(linEl, "pryo")));
            linea.setPryn(decodeOrNull(getTagValue(linEl, "pryn")));
            linea.setPryx(toInteger(getTagValue(linEl, "pryx")));
            linea.setLineje(toInteger(getTagValue(linEl, "lineje")));
            linea.setLinorg(decodeOrNull(getTagValue(linEl, "linorg")));
            linea.setLinfun(decodeOrNull(getTagValue(linEl, "linfun")));
            linea.setLineco(decodeOrNull(getTagValue(linEl, "lineco")));
            linea.setReferencia(toLong(getTagValue(linEl, "referencia")));
            linea.setLimporte(toDouble(getTagValue(linEl, "limporte")));
            linea.setSaldo(toDouble(getTagValue(linEl, "saldo")));
            linea.setSaldop(toDouble(getTagValue(linEl, "saldop")));
            linea.setLincte(decodeOrNull(getTagValue(linEl, "lincte")));
            linea.setLinpam(decodeOrNull(getTagValue(linEl, "linpam")));
            list.add(linea);
        }
        return list;
    }

    private String getTagValue(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes == null || nodes.getLength() == 0) {
            return null;
        }
        Node node = nodes.item(0);
        return node != null ? node.getTextContent() : null;
    }

    private Double toDouble(String value) {
        if (value == null || value.trim().isEmpty()) return 0.0;
        String normalized = value.replace(',', '.');
        try {
            return Double.parseDouble(normalized);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private Long toLong(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Integer toInteger(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String decodeOrNull(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return CryptoSical.decodeBase64(value);
        } catch (IllegalArgumentException ex) {
            return value;
        }
    }
}