package es.uvigo.esei.dsbox.virtualbox.manager;

import es.uvigo.esei.dsbox.core.model.Link;
import es.uvigo.esei.dsbox.core.model.Network;
import es.uvigo.esei.dsbox.core.model.Host;
import es.uvigo.esei.dsbox.core.model.HostType;
import es.uvigo.esei.dsbox.core.model.VMImage;
import es.uvigo.esei.dsbox.core.model.execution.VMDriverSpec;
import es.uvigo.esei.dsbox.core.model.execution.VirtualMachine;
import es.uvigo.esei.dsbox.core.model.execution.VirtualNetwork;
import es.uvigo.esei.dsbox.virtualbox.execution.VirtualBoxDriverSpec;
import es.uvigo.esei.dsbox.virtualbox.execution.VirtualBoxMachine;
import es.uvigo.esei.dsbox.virtualbox.execution.VirtualBoxNetwork;
import es.uvigo.esei.dsbox.core.manager.VMDriver;
import es.uvigo.esei.dsbox.core.manager.VMDriverException;
import es.uvigo.esei.dsbox.core.model.ArchitectureType;
import es.uvigo.esei.dsbox.core.model.OperativeSystemType;
import es.uvigo.esei.dsbox.virtualbox.execution.VirtualBoxExecutionSpec;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.virtualbox_5_0.AccessMode;
import org.virtualbox_5_0.CleanupMode;
import org.virtualbox_5_0.DeviceType;
import org.virtualbox_5_0.IConsole;
import org.virtualbox_5_0.IMachine;
import org.virtualbox_5_0.IMedium;
import org.virtualbox_5_0.IMediumAttachment;
import org.virtualbox_5_0.INetworkAdapter;
import org.virtualbox_5_0.IProgress;
import org.virtualbox_5_0.ISession;
import org.virtualbox_5_0.IStorageController;
import org.virtualbox_5_0.IVirtualBox;
import org.virtualbox_5_0.LockType;
import org.virtualbox_5_0.MediumVariant;
import org.virtualbox_5_0.NetworkAdapterPromiscModePolicy;
import org.virtualbox_5_0.NetworkAdapterType;
import org.virtualbox_5_0.NetworkAttachmentType;
import org.virtualbox_5_0.StorageBus;
import org.virtualbox_5_0.StorageControllerType;
import org.virtualbox_5_0.VBoxException;
import org.virtualbox_5_0.VirtualBoxManager;

public class VirtualBoxDriver implements VMDriver {

    public static final String MACHINES_SUBDIR = "MACHINES";
    public static final String STORAGE_CONTROLLER_SUFFIX = "_SATA";

    public final static int DEFAULT_TIMEOUT = 2000;  // 2000 milisegundos

    private String simulationsBaseDirectory;
    private String imagesBaseDirectory;
    private VirtualBoxDriverSpec vmDriverSpec;
    private VirtualBoxManager vboxManager;
    private boolean connectedToVBoxWebserver;
    private IVirtualBox vbox;

    private VBoxWebserverManager serverManager;

    
    @Override
    public void setVMDriverSpec(VMDriverSpec vmDriverSpec) {
        // TODO evitar "instance of" ¿mejorar con genericos?
        if (vmDriverSpec instanceof VirtualBoxDriverSpec) {
            this.vmDriverSpec = (VirtualBoxDriverSpec) vmDriverSpec;
            this.imagesBaseDirectory = this.vmDriverSpec.getImagesBasePath();
            this.simulationsBaseDirectory = this.vmDriverSpec.getSimulationsBasePath();
        }
    }

    @Override
    public void initializeVMDriver() throws VMDriverException {
        if (this.vmDriverSpec == null) {
            throw new VMDriverException("VirtualBox driver spec not set");
        }
        // conectar con servidor ...

        vboxWebserverConnect(vmDriverSpec.getWebServerHost(), vmDriverSpec.getWebServerPort(), vmDriverSpec.getWebServerUser(), vmDriverSpec.getWebServerPassword());
        if (!connectedToVBoxWebserver) {
            // Try to start server
            serverManager = new VBoxWebserverManager(vmDriverSpec.getWebServerHost(), vmDriverSpec.getWebServerPort());
            try {
                serverManager.start();
            } catch (VMDriverException ex) {
                throw new VMDriverException("Unable to start VirtualBox webserver", ex);
            }

            int tries = 0;
            do {
                // Try to connect again
                try {
                    Thread.sleep(250L); // Wait
                    vboxWebserverConnect(vmDriverSpec.getWebServerHost(), vmDriverSpec.getWebServerPort(), vmDriverSpec.getWebServerUser(), vmDriverSpec.getWebServerPassword());
                    if (connectedToVBoxWebserver) {
                        return; // Conencted -> continue
                    } else {
                        tries++;
                    }
                } catch (InterruptedException ex) {
                    throw new VMDriverException("VirtualBox web server start was interrupted ¿?", ex);
                }
            } while (tries < 8);

            if (!connectedToVBoxWebserver) {
                throw new VMDriverException("Unable to connect with VBox webserver at port " + vmDriverSpec.getWebServerPort()
                        + " in host " + vmDriverSpec.getWebServerHost()
                        + " with credentials " + vmDriverSpec.getWebServerUser() + "/" + vmDriverSpec.getWebServerPassword());
            }
        }
    }

    private void vboxWebserverConnect(String webServerHost, int webServerPort, String webServerUser, String webServerPassword) {
        connectedToVBoxWebserver = false;
        VirtualBoxManager mgr = VirtualBoxManager.createInstance(null);

        String webserverURL = "http://" + webServerHost + ":" + webServerPort;
        boolean connected = true;
        try {
            mgr.connect(webserverURL, webServerUser, webServerPassword);
        } catch (VBoxException e) {
            connected = false;
        }

        if (connected) {

            connectedToVBoxWebserver = true;
            vboxManager = mgr;
            vboxManager.waitForEvents(0);
            vbox = mgr.getVBox();
        }
    }

    @Override
    public void finalizeVMDriver() throws VMDriverException {
        if (!this.connectedToVBoxWebserver || (this.vboxManager == null)) {
            throw new VMDriverException("VBoxManager does not exists");
        }
        // TODO
        // esperar a procesos ¿? vbox.getProgressOperations()
        // cerrar sesiones ¿?
        // guardar lo que haya en curso ¿?
        this.vboxManager.disconnect();
        this.vboxManager.cleanup();

        if ((serverManager != null) && (serverManager.isRunning())) {
            serverManager.stop();
        }
    }

    @Override
    public boolean isHostTypeRegistered(HostType hostType) {
        VMImage image = hostType.getVmImage();
        String imageFilename = image.getLocalURI();
        IMedium foundMedium = findImageByPath(imageFilename);
        return (foundMedium != null);
    }

    @Override
    public void registerHostType(HostType hostType) throws VMDriverException {
        // comprobar tipo de imagen
        // descargarla y descomprimirla si es necesario
        // comprobar si esta registrada
        // registrarla
        
        if (!isHostTypeRegistered(hostType)) {
            VMImage image = hostType.getVmImage();
            checkVMImage(image);
            checkVMImageAvailable(image);
            checkImageFile(image.getLocalURI());

            // Registar imagen base (¿basta con esto?)
            String imageFilename = image.getLocalURI();
            IMedium medium = vbox.openMedium(imageFilename, DeviceType.HardDisk, AccessMode.ReadOnly, Boolean.FALSE);
        }
    }

    private void checkVMImage(VMImage image) throws VMDriverException {
        if (image.getImageType() != VMImage.ImageType.VDI) {
            throw new VMDriverException("Wrong image type " + image.getImageType() + " for " + image.getLocalURI());
        }
    }

    private void checkVMImageAvailable(VMImage image) throws VMDriverException {
        if (!image.isDownloaded() || image.isCompressed()) {
            throw new VMDriverException("Image at " + image.getLocalURI() + " is not ready");
        }
    }

    private void checkImageFile(String imageFilename) throws VMDriverException {
        File imageFile = new File(imageFilename);
        if (!imageFile.exists()) {
            throw new VMDriverException("Image file " + imageFilename + " does not exists");
        }
    }

    @Override
    public void unregisterHostType(HostType hostType) throws VMDriverException {
        // verificar que la imagen esta registrada
        // desregistrarla (no borrarla)

        if (!isHostTypeRegistered(hostType)) {
            throw new VMDriverException("HostType " + hostType.getName() + " is not registered");
        } else {
            VMImage image = hostType.getVmImage();
            String imageFilename = image.getLocalURI();

            IMedium foundMedium = findImageByPath(imageFilename);

            // Check image is not used        
            if (mediumInUse(foundMedium)) {
                throw new VMDriverException("Image " + imageFilename + " is in use (has child images or is attached to VMs)");
            }
            foundMedium.close();
        }
    }

    @Override
    public VirtualBoxNetwork createVirtualNetwork(String simulationName, Network network) {
        // no 100% necesario
        // solo anota el nombre de las "internal net"

        String vboxNetworkName = createVBoxNetworkName(simulationName, network.getName());
        VirtualBoxNetwork vn = new VirtualBoxNetwork(vboxNetworkName);
        vn.setNetwork(network);

        return vn;
    }

    @Override
    public VirtualBoxNetwork updateVirtualNetwork(String simulationName, VirtualNetwork virtualNetwork, Network newNetwork) {
        // no 100% necesario
        // anotar nombre de las "internal net"
        return this.createVirtualNetwork(simulationName, newNetwork);

        //String vboxNetworkName = createVBoxNetworkName(simulationName, newNetwork.getName());
        //VirtualBoxNetwork vn = new VirtualBoxNetwork(vboxNetworkName);
        //vn.setNetwork(newNetwork);
        // 
        //return vn;
    }

    @Override
    public void removeVirtualNetwork(String simulationName, VirtualNetwork virtualNetwork) {
        // no necesario
    }

    @Override
    public VirtualBoxMachine createVirtualMachine(String simulationName, Host host) throws VMDriverException {
        String vboxMachineName = createVBoxMachineName(simulationName, host.getName());
        String vboxMachineBaseFilename = composeVBoxMachineBaseFilename(simulationName, vboxMachineName);

        String machineUUID;
        IMachine foundMachine = findMachineByName(vboxMachineName);
        if (foundMachine == null) {

            // Create machine base directory  (TODO ¿responsabilidad del Gestor de almacenamiento o sólo de que exita el directorio de simulaciones? )
            File vboxMachineBaseDir = new File(vboxMachineBaseFilename);
            vboxMachineBaseDir.mkdirs();

            // Create machine
            IMachine createdMachine = createVBoxMachine(vboxMachineName, vboxMachineBaseFilename, host);
            machineUUID = createdMachine.getId();
        } else {
            machineUUID = foundMachine.getId();
        }
        // Anotate VBoxMachine metadata
        VirtualBoxMachine vm = new VirtualBoxMachine();
        vm.setVboxInternalMachineName(vboxMachineName);
        vm.setVboxUUID(machineUUID);
        vm.setHost(host);
        vm.setStatus(VirtualBoxMachine.VBoxStatus.NON_STARTED);
        vm.setBasePath(vboxMachineBaseFilename);

        return vm;
    }

    @Override
    public VirtualBoxMachine updateVirtualMachine(String simulationName, VirtualMachine virtualMachine, Host newNode) throws VMDriverException {
        // lo mas sencillo: eliminar y crear de nuevo  ¿basta con eso?
        this.removeVirtualMachine(simulationName, virtualMachine);
        return this.createVirtualMachine(simulationName, newNode);
    }

    @Override
    public void removeVirtualMachine(String simulationName, VirtualMachine virtualMachine) throws VMDriverException {
        VirtualBoxMachine vboxMachine = validateVirtualBoxMachine(virtualMachine);
        String vboxMachineName = vboxMachine.getVboxInternalMachineName();

        if (vboxMachine.getStatus() != VirtualBoxMachine.VBoxStatus.RUNNING) {
            IMachine machine = vbox.findMachine(vboxMachineName);
            if (machine != null) {
                List<IMedium> machineDisks = new ArrayList<IMedium>();
                String storageControllerName = composeStorageControllerName(vboxMachineName);

                // Retrieve machine disks
                for (IMediumAttachment attachment : machine.getMediumAttachmentsOfController(storageControllerName)) {
                    machineDisks.add(attachment.getMedium());
                }
                machine.unregister(CleanupMode.DetachAllReturnNone); // Libera discos, pero no los borra

                IProgress progress = machine.deleteConfig(machineDisks);
                progress.waitForCompletion(DEFAULT_TIMEOUT);
                if (!progress.getCompleted()) {
                    progress.cancel();
                }
            } else {
                throw new VMDriverException("Cannot remove virtual machine " + vboxMachineName + " [machine not registered]");
            }
        } else {
            throw new VMDriverException("Cannot remove virtual machine " + vboxMachineName + " [machine is in use]");
        }
    }

    @Override
    public VirtualBoxMachine startVirtualMachine(String simulationName, VirtualMachine virtualMachine) throws VMDriverException {
        VirtualBoxMachine vboxMachine = validateVirtualBoxMachine(virtualMachine);

        String vboxMachineName = vboxMachine.getVboxInternalMachineName();
        IMachine machine = vbox.findMachine(vboxMachineName);

        ISession session = this.vboxManager.getSessionObject();
        IProgress progress = machine.launchVMProcess(session, "gui", "");
        progress.waitForCompletion(DEFAULT_TIMEOUT); // TODO :  mejorar gestion de procesos/hilos
        if (!progress.getCompleted()) {
            progress.cancel();
            session.unlockMachine();
            throw new VMDriverException("Timeout exceded trying to start VirtualMachine " + vboxMachineName);
        } else {
            vboxMachine.setControlConsole(session.getConsole()); // Anotate IConsole
            session.unlockMachine();  // TODO es necesario
            vboxMachine.setStatus(VirtualBoxMachine.VBoxStatus.RUNNING);
            return vboxMachine;
        }
    }

    @Override
    public VirtualBoxMachine pauseVirtualMachine(String simulationName, VirtualMachine virtualMachine) throws VMDriverException {
        VirtualBoxMachine vboxMachine = validateVirtualBoxMachine(virtualMachine);

        if (vboxMachine.getStatus() == VirtualBoxMachine.VBoxStatus.RUNNING) {
            IConsole controlConsole = vboxMachine.getControlConsole();
            controlConsole.pause();  // TODO:  no probado ¿basta con esto?
            vboxMachine.setStatus(VirtualBoxMachine.VBoxStatus.PAUSED);
            return vboxMachine;
        } else {
            // ¿lanzar excepcion?
            return vboxMachine;
        }
    }

    @Override
    public VirtualBoxMachine resumeVirtualMachine(String simulationName, VirtualMachine virtualMachine) throws VMDriverException {
        VirtualBoxMachine vboxMachine = validateVirtualBoxMachine(virtualMachine);
        if (vboxMachine.getStatus() == VirtualBoxMachine.VBoxStatus.PAUSED) {
            IConsole controlConsole = vboxMachine.getControlConsole();
            controlConsole.resume();   // TODO:  basta con esto?
            vboxMachine.setStatus(VirtualBoxMachine.VBoxStatus.RUNNING);
            return vboxMachine;
        } else {
            // ¿lanzar excepcion?
            return vboxMachine;
        }
    }

    @Override
    public VirtualBoxMachine stopVirtualMachine(String simulationName, VirtualMachine virtualMachine) throws VMDriverException {
        VirtualBoxMachine vboxMachine = validateVirtualBoxMachine(virtualMachine);
        String vboxMachineName = vboxMachine.getVboxInternalMachineName();

        if ((vboxMachine.getStatus() == VirtualBoxMachine.VBoxStatus.RUNNING)
                || (vboxMachine.getStatus() == VirtualBoxMachine.VBoxStatus.PAUSED)) {
            IConsole controlConsole = vboxMachine.getControlConsole();

            // OPCION 1 : Parada completa (desconexion "fisica") 
            //            [inmediato, peude quedar disco sin sincronizar]
            // IProgress progress = controlConsole.powerDown();
            // // TODO ¿sera mas sencillo no esperar y dejar que acabe sola?
            // progress.waitForCompletion(5 * DEFAULT_TIMEOUT); // TODO :  mejorar gestion de procesos/hilos
            // if (!progress.getCompleted()) {
            //     progress.cancel();
            //     throw new VMDriverException("Timeout exceded trying to stop VirtualMachine " + vboxMachineName);
            // } else {
            //     vboxMachine.setStatus(VirtualBoxMachine.VBoxStatus.STOPPED);
            //     return vboxMachine;
            // }
            // OPCION 2 : Parada ACPI (el SO se apaga "en orden", solo si el SO instalado lo soporta) 
            //         [requiere que el SO haya arrancado, no predecible finalización]
            //         ¿decidir en funcion del NodeType?
            controlConsole.powerButton();
            vboxMachine.setStatus(VirtualBoxMachine.VBoxStatus.NON_STARTED);
            return vboxMachine;
        } else {
            // ¿lanzar excepcion?
            return vboxMachine;
        }
    }

    @Override
    public void exportVirtualMachine(String simulationName, VirtualMachine virtualMachine, String destinationPath) throws VMDriverException {
        VirtualBoxMachine vboxMachine = validateVirtualBoxMachine(virtualMachine);
        if (vboxMachine.getStatus() == VirtualBoxMachine.VBoxStatus.PAUSED) {

        }
        // TODO pendiente
    }

    @Override
    public VirtualBoxMachine importVirtualMachine(String simulationName, String sourcePath, Host node) {
        // TODO pendiente
        return null;
    }

    /**
     * Comprueba que se recive un objeto VirtualBoxMachine y hace el cast
     *
     * @param virtualMachine
     * @return
     * @throws VMDriverException
     */
    private VirtualBoxMachine validateVirtualBoxMachine(VirtualMachine virtualMachine) throws VMDriverException {
        if (virtualMachine instanceof VirtualBoxMachine) { // TODO evitar el instance_of y el cast ¿?
            return (VirtualBoxMachine) virtualMachine;
        } else {
            throw new VMDriverException("Non-valid virtual machine especification");
        }
    }

    private String createVBoxNetworkName(String simulationName, String networkName) {
        return createVBoxElementName(simulationName, networkName);
    }

    private String createVBoxMachineName(String simulationName, String machineName) {
        return createVBoxElementName(simulationName, machineName);
    }

    private String createVBoxElementName(String simulationName, String elementName) {
        return String.format("%s_%s", simulationName, elementName);
    }

    private String composeVBoxSimulationBaseFilename(String simulationName) {
        // TODO ¿asignar carpetas/ficheros seria responsabilidad del "gestor de almacenamiento" ?
        return this.simulationsBaseDirectory + File.separator + simulationName;
    }

    private String composeVBoxMachineBaseFilename(String simulationName, String machineName) {
        // TODO ¿asignar carpetas/ficheros seria responsabilidad del "gestor de almacenamiento" ?
        return this.composeVBoxSimulationBaseFilename(simulationName)
                + File.separator + MACHINES_SUBDIR
                + File.separator + machineName;
    }

    private String composeStorageControllerName(String machineName) {
        return machineName + STORAGE_CONTROLLER_SUFFIX;
    }

    private IMedium findImageByPath(String imagePath) {
        for (IMedium medium : vbox.getHardDisks()) {
            if (medium.getLocation().equals(imagePath)) {
                return medium;
            }
        }
        return null; // not found
    }

    private IMachine findMachineByName(String vboxMachineName) {
        try {
            return this.vbox.findMachine(vboxMachineName);
        } catch (VBoxException e) {
            //  si no existe lanza la excepcion, no devuelve null
            return null;
        }
    }

    private boolean mediumInUse(IMedium medium) {
        return ((medium.getChildren() != null) || (medium.getMachineIds() != null));
    }

    private IMachine createVBoxMachine(String vboxMachineName, String vboxMachineBaseFilename, Host host) throws VMDriverException {
        IMachine machine = createBasicMachine(vboxMachineName, vboxMachineBaseFilename, host);
        configureMachineNetwork(machine, host);
        configureMachineStorageController(machine);
        // TODO configurar servicios, etc  configureMachineExtras(machine, node);

        // almacenar cambios
        machine.saveSettings();
        this.vbox.registerMachine(machine);

        // Enlazar imagenes diferenciales  (debe hacerse despues de registrar las MVs ¿?)
        machine = vbox.findMachine(vboxMachineName); // reload IMachine ¿?
        configureMachineDiskImages(machine, vboxMachineBaseFilename, host);

        return machine;
    }

    private IMachine createBasicMachine(String vboxMachineName, String vboxMachineBaseFilename, Host host) {
        String osTypeLabel = composeOsTypeLabel(host.getHostType());
        IMachine machine = vbox.createMachine(vboxMachineBaseFilename + File.separator + vboxMachineName + ".vbox",
                vboxMachineName, null, osTypeLabel, "forceOverwrite=1");  // TODO: evitar que sea siempre Linux26 (¿incluir esa info en NodeType?)

        machine.setMemorySize((long) host.getMachineRAM());
        machine.setCPUExecutionCap((long) host.getMachineCPU());

        return machine;
    }

    private String composeOsTypeLabel(HostType hostType) {
        String label;
        if (hostType.getOs() == OperativeSystemType.MS_WINDOWS) {
            label = "WindowsXP";
        } else if (hostType.getOs() == OperativeSystemType.GNU_LINUX) {
            label = "Linux26";
        } else {
            label = "Other";
        }
        if (hostType.getArch() == ArchitectureType.X86_64) {
            label = label + "_64";
        }
        return label;
    }

    private void configureMachineNetwork(IMachine machine, Host node) {
        machine.setGuestPropertyValue("/DSBOX/host_name", node.getMachineFullName());
        machine.setGuestPropertyValue("/DSBOX/default_gateway", node.getDefaultGW());
        machine.setGuestPropertyValue("/DSBOX/default_nameserver", node.getDnsServers());

        List<Link> links = node.getLinks();
        if ((links != null) && (!links.isEmpty())) {
            machine.setGuestPropertyValue("/DSBOX/num_interfaces", Integer.toString(links.size()));
            for (Link link : links) {
                attachNetworkLink(machine, link);
            }
        }
    }

    private void configureMachineStorageController(IMachine machine) {
        String machineName = machine.getName();
        String storageControllerName = composeStorageControllerName(machineName);

        IStorageController storageController = machine.addStorageController(storageControllerName, StorageBus.SATA);
        storageController.setControllerType(StorageControllerType.IntelAhci);
        storageController.setPortCount(2L);
        storageController.setInstance(1L);
        storageController.setUseHostIOCache(Boolean.FALSE);
    }

    private void configureMachineDiskImages(IMachine machine, String vboxMachineBaseFilename, Host node) throws VMDriverException {
        String machineName = machine.getName();
        String storageControllerName = composeStorageControllerName(machineName);

        IMedium child = createChildImage(node.getHostType(), vboxMachineBaseFilename, "main.vdi");

        // crear y bloquear sesion sobre la MV
        ISession session = this.vboxManager.getSessionObject();
        machine.lockMachine(session, LockType.Write);
        IMachine mutable = session.getMachine();
        mutable.attachDevice(storageControllerName, 0, 0, DeviceType.HardDisk, child);
        mutable.saveSettings();
        session.unlockMachine();

        // TODO (si corresponde) añadir SWAP comun como segundo disco (disk2, a partir del swap por defecto)
        // TODO (si corresponde) añadir imagenes CD/DVD
    }

    private IMedium createChildImage(HostType nodeType, String baseFilename, String childDiskFilename) throws VMDriverException {
        IMedium baseImage = findImageByPath(nodeType.getVmImage().getLocalURI()); // base image already registered

//        IMedium child = vbox.createHardDisk("VDI", baseFilename + File.separator + childDiskFilename);  //VBox 4.3
        IMedium child = vbox.createMedium("VDI", baseFilename + File.separator + childDiskFilename, AccessMode.ReadWrite, DeviceType.HardDisk);  //VBox 5.0
        IProgress progresChild = baseImage.createDiffStorage(child, Arrays.asList(MediumVariant.Diff));
        progresChild.waitForCompletion(DEFAULT_TIMEOUT); // TODO  mejorar lanzamiento de procesos/hilos (no esperar siempre 2 segundos, etc)
        if (!progresChild.getCompleted()) {
            throw new VMDriverException("Unable to create child image for " + baseImage + " in " + child.getLocation() + " [timeout exceded]");
        } else {
            return child;
        }
    }

    private void attachNetworkLink(IMachine machine, Link link) {
        //INetworkAdapter adapter = machine.getNetworkAdapter((long) linkPosition);

        // Create adapter
        INetworkAdapter adapter = machine.getNetworkAdapter((long) (link.getInterfaceOrder() -1));
        adapter.setAdapterType(NetworkAdapterType.I82540EM); // TODO comprobar que sea el "menos raro"
        if (link.isEnabled()) {
            adapter.setCableConnected(Boolean.TRUE);
            adapter.setEnabled(Boolean.TRUE);
        }

        // Set network type params
        Network network = link.getNetwork();
        switch (network.getType()) { // TODO: ¿evitar switch?
            case EXTERNAL_NATTED:
                adapter.setAttachmentType(NetworkAttachmentType.NAT);
                break;
            case EXTERNAL_BRIDGED:
                adapter.setAttachmentType(NetworkAttachmentType.Bridged);
                adapter.setBridgedInterface(this.vmDriverSpec.getBridgedInterface());
                break;
            case SWITCH:
                adapter.setAttachmentType(NetworkAttachmentType.Internal);
                adapter.setInternalNetwork(link.getNetwork().getName());  // TODO: realmente seria el nombre en VirtualBoxNetwork
                break;
            case HUB: // caso particular de modo interno con modo promiscuo activado por defecto
                adapter.setAttachmentType(NetworkAttachmentType.Internal);
                adapter.setInternalNetwork(link.getNetwork().getName());  // TODO: realmente seria el nombre en VirtualBoxNetwork
                adapter.setPromiscModePolicy(NetworkAdapterPromiscModePolicy.AllowAll); // Activar modo promiscuo
                break;
        }

        String interfaceName = "eth" + (link.getInterfaceOrder() - 1); // TODO verificar que siempre sera eth_
        if (link.getIpAddress() != null) {
            machine.setGuestPropertyValue("/DSBOX/" + interfaceName + "/type", "static");
            machine.setGuestPropertyValue("/DSBOX/" + interfaceName + "/address", link.getIpAddress());
            //if (link.getNetMask() != null) {
                machine.setGuestPropertyValue("/DSBOX/" + interfaceName + "/netmask", link.getNetwork().getNetMask());
            //} else {
                machine.setGuestPropertyValue("/DSBOX/" + interfaceName + "/netmask", "24");
            //}
            //if (link.getBroadcast() != null) {
                machine.setGuestPropertyValue("/DSBOX/" + interfaceName + "/broadcast", link.getNetwork().getBroadcast());
            //}
        }
    }

    @Override
    public Class[] exposeJAXBClasses() {
        List<Class> jaxbClasses = new ArrayList<>();

        jaxbClasses.add(VirtualBoxDriverSpec.class);
        jaxbClasses.add(VirtualBoxExecutionSpec.class);
        jaxbClasses.add(VirtualBoxMachine.class);
        jaxbClasses.add(VirtualBoxNetwork.class);

        return jaxbClasses.toArray(new Class[jaxbClasses.size()]);
    }

}
