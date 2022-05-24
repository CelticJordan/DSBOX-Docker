package es.uvigo.esei.dsbox.core.model;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum(String.class)
public enum NetworkType {
        HUB,
        SWITCH,
        EXTERNAL_BRIDGED,
        EXTERNAL_NATTED
}
