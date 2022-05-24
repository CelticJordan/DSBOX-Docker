package es.uvigo.esei.dsbox.core.model;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum(String.class)
public enum VMType {
//    VIRTUALBOX, VMWARE, QEMU, XEM, UML
        VIRTUALBOX,
        DOCKER
}
