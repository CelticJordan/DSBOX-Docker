package es.uvigo.esei.dsbox.core.xml;

import es.uvigo.esei.dsbox.core.model.Host;
import es.uvigo.esei.dsbox.core.model.HostType;
import es.uvigo.esei.dsbox.core.model.Link;
import es.uvigo.esei.dsbox.core.model.Network;
import es.uvigo.esei.dsbox.core.model.NetworkSpec;
import es.uvigo.esei.dsbox.core.model.NetworkType;
import es.uvigo.esei.dsbox.core.model.SimulationSpec;
import es.uvigo.esei.dsbox.core.model.VMImage;
import es.uvigo.esei.dsbox.core.model.exceptions.DSBOXException;
//import es.uvigo.esei.dsbox.virtualbox.execution.VirtualBoxDriverSpec;
import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class DSBOXXMLDAO {

    private Class[] knownJAXBClasses;

    public DSBOXXMLDAO() {
        this.knownJAXBClasses = new Class[]{
            SimulationSpec.class
        };
    }

    public DSBOXXMLDAO(Class[] knownJAXBClasses) {
        this();
        this.addKnownJAXBClasses(knownJAXBClasses);
    }

    public void setKnownJAXBClasses(Class[] knownJAXBClasses) {
        this.knownJAXBClasses = knownJAXBClasses;
    }

    public void addKnownJAXBClasses(Class[] addedJAXBClasses) {
        if (addedJAXBClasses != null) {
            int size = this.knownJAXBClasses.length + addedJAXBClasses.length;

            Class[] initialKnownClasses = this.knownJAXBClasses;
            this.knownJAXBClasses = new Class[size];

            int i = 0;
            for (Class c : initialKnownClasses) {
                this.knownJAXBClasses[i] = c;
                i++;
            }

            for (Class c : addedJAXBClasses) {
                this.knownJAXBClasses[i] = c;
                i++;
            }
        }
    }

    public SimulationSpec loadSimulationSpecFromFile(String filename) throws DSBOXException {
        try {
            JAXBContext context = JAXBContext.newInstance(knownJAXBClasses);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            SimulationSpec simulationSpec = (SimulationSpec) unmarshaller.unmarshal(new File(filename));
            return simulationSpec;
        } catch (JAXBException ex) {
            throw new DSBOXException("Error loading SimulationSpec from " + filename, ex);
        }
    }

    public void saveSimulationSpecToFile(String filename, SimulationSpec simulationSpec) throws DSBOXException {
        try {
            JAXBContext context = JAXBContext.newInstance(knownJAXBClasses);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(simulationSpec, new File(filename));
        } catch (JAXBException ex) {
            throw new DSBOXException("Error saving SimulationSpec to " + filename, ex);
        }
    }

    public HostType loadHostTypeFromFile(String filename) throws DSBOXException {
        try {
            JAXBContext context = JAXBContext.newInstance(HostType.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            HostType hostType = (HostType) unmarshaller.unmarshal(new File(filename));
            return hostType;
        } catch (JAXBException ex) {
            throw new DSBOXException("Error loading HostType from " + filename, ex);
        }
    }

    public void saveHostTypeToFile(String filename, HostType hostType) throws DSBOXException {
        try {
            JAXBContext context = JAXBContext.newInstance(HostType.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(hostType, new File(filename));
        } catch (JAXBException ex) {
            throw new DSBOXException("Error saving SimulationSpec to " + filename, ex);
        }
    }

//    public VirtualBoxDriverSpec loadVirtualBoxDriverSpecFromFile(String filename) throws DSBOXException {
//    try {
//            JAXBContext context = JAXBContext.newInstance(VirtualBoxDriverSpec.class);
//            Unmarshaller unmarshaller = context.createUnmarshaller();
//            VirtualBoxDriverSpec spec = (VirtualBoxDriverSpec) unmarshaller.unmarshal(new File(filename));
//            return spec;
//        } catch (JAXBException ex) {
//            throw new DSBOXException("Error loading VirtualBoxDriverSpec from " + filename, ex);
//        }
//    }
//    
//    public void saveVirtualBoxDriverSpecFromFile(String filename, VirtualBoxDriverSpec spec) throws DSBOXException {
//        try {
//            JAXBContext context = JAXBContext.newInstance(VirtualBoxDriverSpec.class);
//            Marshaller marshaller = context.createMarshaller();
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
//            marshaller.marshal(spec, new File(filename));
//        } catch (JAXBException ex) {
//            throw new DSBOXException("Error saving VirtualBoxDriverSpec from " + filename, ex);
//        }
//    }
    
    public final static void main(String[] args) throws DSBOXException {
        SimulationSpec spec = new SimulationSpec("aaa", "aaaaa", "author");
        
        VMImage vmImage = new VMImage(VMImage.ImageType.VDI, "http://", "local.vdi");
        HostType ht1 = new HostType("uno", "uno", "description", vmImage);
        Host h1 = new Host("1", "host1", "host1", ht1);
        Host h2 = new Host("2", "host2", "host2", ht1);
        
        Network n1 = new Network("aaa", "aaa", "description", NetworkType.HUB);
        Network n2 = new Network("bbb", "bbb", "description", NetworkType.EXTERNAL_BRIDGED);

        Link l11 = new Link(h1, n1, 1);
        h1.addLink(l11);
        Link l12 = new Link(h1, n2, 2);
        h1.addLink(l12);
        Link l21 = new Link(h2, n1, 1);
        h2.addLink(l21);
        
        NetworkSpec nspec = new NetworkSpec();
        nspec.addHostType(ht1);
        nspec.addHost(h1);
        nspec.addHost(h2);
        
        nspec.addNetwork(n1);
        nspec.addNetwork(n2);
        
        spec.setNetworkSpec(nspec);
        
        
        DSBOXXMLDAO dao = new DSBOXXMLDAO();
        dao.saveSimulationSpecToFile("/tmp/red.xml", spec);
        
    }
    
}
