package es.uvigo.esei.dsbox.core.model;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum(String.class)
public enum ArchitectureType {
    X86("x86 (32 bits)"),
    X86_64("x86_64 (64 bits)");

    private String name;

    private ArchitectureType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public static ArchitectureType fromValue(String v) {
        return valueOf(v);
    }
}
