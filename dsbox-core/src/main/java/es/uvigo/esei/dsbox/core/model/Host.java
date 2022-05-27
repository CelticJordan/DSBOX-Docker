package es.uvigo.esei.dsbox.core.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"id", "name", "label", "hostType", "description", "machineRAM", "machineCPU",
    "machineFullName", "machineShortName", "defaultGW", "dnsServers",
    "knownHosts", "routesToAdd", "servicesToStart", "links", "vmProperties"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Host {

    private static String DNS_SERVER_SEPARATOR = ",";

    @XmlAttribute
    @XmlID
    private String id;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String label;

    private String description;

    @XmlAttribute(name = "host-type-ref")
    @XmlIDREF
    private HostType hostType;

    @XmlElement(name = "machine-ram")
    private int machineRAM;

    @XmlElement(name = "machine-cpu")
    private int machineCPU;

    @XmlElement(name = "machine-fullname")
    private String machineFullName;

    @XmlElement(name = "machine-shortname")
    private String machineShortName;

    @XmlElement(name = "default-gateway")
    private String defaultGW;

    @XmlElement(name = "dns-servers")
    private String dnsServers;

    @XmlElementWrapper(name = "known-hosts")
    @XmlElement(name = "known-host")
    private List<KnownHost> knownHosts;

    @XmlElementWrapper(name = "routes-to-add")
    @XmlElement(name = "route")
    private List<RouteToAdd> routesToAdd;

    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    private List<Link> links;

    @XmlElementWrapper(name = "services-to-start")
    @XmlElement(name = "service")
    private List<String> servicesToStart;

    @XmlElementWrapper(name = "vm-properties")
    @XmlElement(name = "vm-property")
    private List<VMProperty> vmProperties;

    public Host() {
    }

    public Host(String id, String name, String description, HostType hostType) {
        this(id, name, "", description, hostType, hostType.getDefaultRAM(), hostType.getDefaultCPU(),
                "", "", "", "");
        this.label = normalizeNodeName(name);
        if (name.contains(".")) {
            // Try to extract short and long name
            String parts[] = name.split("\\.", 2);
            this.machineFullName = name;
            this.machineShortName = parts[0];
        } else {
            this.machineFullName = label;
            this.machineShortName = label;
        }
    }

    public Host(String id, String name, String label, String description, HostType hostType, int machineRAM, int machineCPU, String machineFullName, String machineShortName, String defaultGW, String dnsServers) {
        this.id = id;
        this.name = name;
        this.label = label;
        this.description = description;
        this.hostType = hostType;
        this.machineRAM = machineRAM;
        this.machineCPU = machineCPU;
        this.machineFullName = machineFullName;
        this.machineShortName = machineShortName;
        this.defaultGW = defaultGW;
        this.dnsServers = dnsServers;

        this.links = new ArrayList<>();

        //this.knownHosts = new ArrayList<KnownHost>();
        //this.vmProperties = new ArrayList<VMProperty>();
        if (hostType.isServicesAutostartSupported()) {
            this.servicesToStart = new ArrayList<String>();
        } else {
            this.servicesToStart = null;
        }
        //this.routesToAdd = new ArrayList<RouteToAdd>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HostType getHostType() {
        return hostType;
    }

    public void setHostType(HostType hostType) {
        this.hostType = hostType;
    }

    public int getMachineRAM() {
        return machineRAM;
    }

    public void setMachineRAM(int machineRAM) {
        this.machineRAM = machineRAM;
    }

    public int getMachineCPU() {
        return machineCPU;
    }

    public void setMachineCPU(int machineCPU) {
        this.machineCPU = machineCPU;
    }

    public String getMachineFullName() {
        return machineFullName;
    }

    public void setMachineFullName(String machineFullName) {
        this.machineFullName = machineFullName;
    }

    public String getMachineShortName() {
        return machineShortName;
    }

    public void setMachineShortName(String machineShortName) {
        this.machineShortName = machineShortName;
    }

    public String getDefaultGW() {
        return defaultGW;
    }

    public void setDefaultGW(String defaultGW) {
        this.defaultGW = defaultGW;
    }

    public String getDnsServers() {
        return dnsServers;
    }

    public void setDnsServers(String dnsServers) {
        this.dnsServers = dnsServers;
    }

    public List<KnownHost> getKnownHosts() {
        return knownHosts;
    }

    public void setKnownHosts(List<KnownHost> knownHost) {
        this.knownHosts = knownHost;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getServicesToStart() {
        return servicesToStart;
    }

    public void setServicesToStart(List<String> servicesToStart) {
        this.servicesToStart = servicesToStart;
    }

    public List<VMProperty> getVmProperties() {
        return vmProperties;
    }

    public List<RouteToAdd> getRoutesToAdd() {
        return routesToAdd;
    }

    public void setRoutesToAdd(List<RouteToAdd> routesToAdd) {
        this.routesToAdd = routesToAdd;
    }

    public void setVmProperties(List<VMProperty> vmProperties) {
        this.vmProperties = vmProperties;
    }

    public void addVMProperty(VMProperty vmProperty) {
        if (this.vmProperties == null) {
            this.vmProperties = new ArrayList<VMProperty>();
        }
        this.vmProperties.add(vmProperty);
    }

    public void removeVMProperty(VMProperty vmProperty) {
        if (this.vmProperties != null) {
            this.vmProperties.remove(vmProperty);
        }
    }

    public void addLink(Link link) {
        if (this.links == null) {
            this.links = new ArrayList<Link>();
        }
        this.links.add(link);
    }

    public void removeLink(Link link) {
        if (this.links != null) {
            this.links.remove(link);
        }
    }

    public void addKnownHost(KnownHost knownHost) {
        if (this.knownHosts == null) {
            this.knownHosts = new ArrayList<KnownHost>();
        }
        this.knownHosts.add(knownHost);
    }

    public void removeKnownHost(KnownHost knownHost) {
        if (this.knownHosts != null) {
            this.knownHosts.remove(knownHost);
        }
    }

    public void addRouteToAdd(RouteToAdd routeToAdd) {
        if (this.routesToAdd == null) {
            this.routesToAdd = new ArrayList<RouteToAdd>();
        }
        this.routesToAdd.add(routeToAdd);
    }

    public void removeRouteToAdd(RouteToAdd routeToAdd) {
        if (this.routesToAdd != null) {
            this.routesToAdd.remove(routeToAdd);
        }
    }

    public void addServiceToStart(String serviceName) {
        if (this.servicesToStart == null) {
            this.servicesToStart = new ArrayList<String>();
        }
        this.servicesToStart.add(serviceName);
    }

    public void removeServiceToStart(String serviceName) {
        if (this.servicesToStart != null) {
            this.servicesToStart.remove(serviceName);
        }
    }

    private String normalizeNodeName(String name) {
        return name.trim().replace(" ", "_");
    }

    public int getNextLinkPosition() {
        if (this.links == null) {
            return 1;
        } else {
            int max = 0;
            for (Link link : this.links) {
                max = Math.max(max, link.getInterfaceOrder());
            }
            return (max + 1);
        }
    }

    @XmlType(propOrder = {"fullName", "shortName", "ipAddress"})
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class KnownHost {

        @XmlAttribute(name = "full-name")
        private String fullName;

        @XmlAttribute(name = "short-name")
        private String shortName;

        @XmlAttribute(name = "ip-address")
        private String ipAddress;

        public KnownHost() {
        }

        public KnownHost(String fullName, String shortName, String ipAddress) {
            this.fullName = fullName;
            this.shortName = shortName;
            this.ipAddress = ipAddress;
        }

        public KnownHost(String name, String ipAddress) {
            if (name.contains(".")) {
                String parts[] = name.split("\\.", 2);
                this.fullName = name;
                this.shortName = parts[0];
            } else {
                this.fullName = name;
                this.shortName = name;
            }
            this.ipAddress = ipAddress;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

    }

    @XmlType(propOrder = {"network", "networkMask", "gateway", "networkInterface"})
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class RouteToAdd {

        @XmlAttribute
        private String network;

        @XmlAttribute(name = "network-mask")
        private String networkMask;

        @XmlAttribute
        private String gateway;

        @XmlAttribute(name = "interface")
        private String networkInterface;

        public RouteToAdd() {
        }

        public RouteToAdd(String network, String networkMask, String gateway, String networkInterface) {
            this.network = network;
            this.networkMask = networkMask;
            this.gateway = gateway;
            this.networkInterface = networkInterface;
        }

        public RouteToAdd(String network, String netMask, String gatewayOrInterface) {
            this.network = network;
            this.networkMask = netMask;
            if (gatewayOrInterface.contains(".")) {
                // It's a gateway
                this.gateway = gatewayOrInterface;
                this.networkInterface = null;
            } else {
                // It's an interface
                this.gateway = null;
                this.networkInterface = gatewayOrInterface;
            }
        }

        public String getNetwork() {
            return network;
        }

        public void setNetwork(String network) {
            this.network = network;
        }

        public String getNetworkMask() {
            return networkMask;
        }

        public void setNetworkMask(String networkMask) {
            this.networkMask = networkMask;
        }

        public String getGateway() {
            return gateway;
        }

        public void setGateway(String gateway) {
            this.gateway = gateway;
            this.networkInterface = null;
        }

        public String getNetworkInterface() {
            return networkInterface;
        }

        public void setNetworkInterface(String networkInterface) {
            this.networkInterface = networkInterface;
            this.gateway = null;
        }

    }

}
