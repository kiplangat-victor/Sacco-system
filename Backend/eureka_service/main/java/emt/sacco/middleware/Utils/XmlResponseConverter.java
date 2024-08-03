package emt.sacco.middleware.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

public class XmlResponseConverter {

    private final ObjectMapper objectMapper;

    public XmlResponseConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String convertToJSON(String xml) throws Exception {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
        Object obj = xmlMapper.readValue(xml, Object.class);
        return objectMapper.writeValueAsString(obj);
    }
}
