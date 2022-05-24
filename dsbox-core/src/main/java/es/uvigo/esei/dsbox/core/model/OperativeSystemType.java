package es.uvigo.esei.dsbox.core.model;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum(String.class)
public enum OperativeSystemType {
    GNU_LINUX("GNU/Linux"),
    MS_WINDOWS("MS Windows");

    private String name;

    private OperativeSystemType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public static OperativeSystemType fromValue(String v) {
        return valueOf(v);
    }
}
