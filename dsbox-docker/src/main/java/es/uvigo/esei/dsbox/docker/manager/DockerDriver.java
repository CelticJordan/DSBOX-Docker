package es.uvigo.esei.dsbox.docker.manager;

import es.uvigo.esei.dsbox.core.manager.VMDriver;
import es.uvigo.esei.dsbox.core.manager.VMDriverException;
import es.uvigo.esei.dsbox.core.model.Host;
import es.uvigo.esei.dsbox.core.model.HostType;
import es.uvigo.esei.dsbox.core.model.Link;
import es.uvigo.esei.dsbox.core.model.Network;
import es.uvigo.esei.dsbox.core.model.NetworkType;
import es.uvigo.esei.dsbox.core.model.execution.VMDriverSpec;
import es.uvigo.esei.dsbox.core.model.execution.VirtualMachine;
import es.uvigo.esei.dsbox.core.model.execution.VirtualNetwork;
import es.uvigo.esei.dsbox.docker.execution.DockerDriverSpec;
import es.uvigo.esei.dsbox.docker.execution.DockerExecutionSpec;
import es.uvigo.esei.dsbox.docker.execution.DockerMachine;
import es.uvigo.esei.dsbox.docker.execution.DockerMachine.DockerStatus;
import es.uvigo.esei.dsbox.docker.execution.DockerNetwork;
import java.io.BufferedReader;
import org.apache.commons.net.util.SubnetUtils;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DockerDriver implements VMDriver {

    private DockerDriverSpec driverSpec;

    HashMap<String, DockerMachine> containers = new HashMap<>();        //HashMap para poder llevar un registro de los contenedores creados

    HashMap<String, DockerNetwork> networks = new HashMap<>();          //HashMap para poder llevar un registro de las redes creadas

    public String dockerPath = "docker";                       //Ruta donde se encuentra Docker en caso de no encontrarlo en el Path del sistema

    public int initSshPort = 10022;                                     //Primer puerto ssh para asignar a los contenedores (se incrementa mas adelante)

    @Override
    public void setVMDriverSpec(VMDriverSpec vmDriverSpec) {
        if (vmDriverSpec instanceof DockerDriverSpec) {
            this.driverSpec = (DockerDriverSpec) vmDriverSpec;
            this.dockerPath = this.driverSpec.getDockerServerHost();
            this.initSshPort = this.driverSpec.getDockerServerPort();
        }
    }

    @Override
    public void initializeVMDriver() throws VMDriverException {
        if (this.driverSpec == null) {
            throw new VMDriverException("VirtualBox driver spec not set");
        }
        
        
    }

    @Override
    public void finalizeVMDriver() throws VMDriverException {
        
        Iterator<Map.Entry<String, DockerMachine>> contSet = containers.entrySet().iterator();
        
        while (contSet.hasNext())
        {
            Map.Entry<String, DockerMachine> entryCont = contSet.next();
            
            removeVirtualMachine(entryCont.getValue(), "delete");
        }
        containers.clear();
        
        Iterator<Map.Entry<String, DockerNetwork>> netSet = networks.entrySet().iterator();
        
        while (netSet.hasNext())
        {
            Map.Entry<String, DockerNetwork> entryNet = netSet.next();
            
            removeVirtualNetwork(entryNet.getValue());
        }
        networks.clear();
        
    }

    @Override
    public boolean isHostTypeRegistered(HostType hostType) throws VMDriverException {
        // TODO: comprobar si el hosType (= imagen Docker) está disponible
        return true;
    }

    @Override
    public void registerHostType(HostType hostType) throws VMDriverException {

        // TODO: descargar la imagen Docker que corresponda ¿?
    }

    @Override
    public void unregisterHostType(HostType hostType) throws VMDriverException {

        // TODO
    }

    @Override
    public VirtualNetwork createVirtualNetwork(String simulationName, Network network) throws VMDriverException {

        // Esto toma una direccion IP y una mascara para poder mas tarde obtener una direccion IP con CIDR
        SubnetUtils auxIP = new SubnetUtils(network.getNetAddress(), network.getNetMask());

        String aux = String.format("%s network create --driver=bridge --subnet=%s %s", dockerPath, auxIP.getInfo().getCidrSignature(), network.getName());
        
        // En caso de que la red a crear sea una red sin conexion a Internet
        if ((network.getType() == NetworkType.HUB) || (network.getType() == NetworkType.SWITCH)) {
            aux = String.format("%s network create --driver=bridge --internal --subnet=%s %s", dockerPath, auxIP.getInfo().getCidrSignature(), network.getName());
        }

        String DockerNetID = commandExecuter(aux);

        // Si el mensaje de consola es la ID de la red creada (caso exitoso)
        if (DockerNetID.length() == 64) {
            DockerNetwork dNetwork = new DockerNetwork(DockerNetID, network);
            networks.put(dNetwork.getDockerNetworkName(), dNetwork);    // Añadimos la nueva red al registro correspondiente
            return dNetwork;
        } else {
            throw new VMDriverException("There was an error during Docker network creation");
        }

    }

    private DockerNetwork convertToDockerVirtualNetwork(VirtualNetwork virtualNetwork) throws VMDriverException {
        if (!(virtualNetwork instanceof DockerNetwork)) {
            throw new VMDriverException("VirtualNetwork not compatible Docker Driver");
        }
        return (DockerNetwork) virtualNetwork;
    }

    private DockerMachine convertToDockerVirtualMachine(VirtualMachine virtualMachine) throws VMDriverException {
        if (!(virtualMachine instanceof DockerMachine)) {
            throw new VMDriverException("VirtualMachine not compatible Docker Driver");
        }
        return (DockerMachine) virtualMachine;
    }

    @Override
    public VirtualNetwork updateVirtualNetwork(String simulationName, VirtualNetwork virtualNetwork, Network newNtwork) throws VMDriverException {
        DockerNetwork dockerNetwork = convertToDockerVirtualNetwork(virtualNetwork);

        // TODO: modificar la red de Docker
        return dockerNetwork;  // Ajustar
    }

    @Override
    public void removeVirtualNetwork(String simulationName, VirtualNetwork virtualNetwork) throws VMDriverException {
        DockerNetwork dockerNetwork = convertToDockerVirtualNetwork(virtualNetwork);

        String aux = String.format("%s network rm %s", dockerPath, dockerNetwork.getDockerNetworkName());

        String DockerNetID = commandExecuter(aux);

        // En caso de borrado exitoso, la consola devuelve el nombre de la red eliminada
        if (DockerNetID.equals(dockerNetwork.getDockerNetworkName())) {
            networks.remove(dockerNetwork.getDockerNetworkName());  // Eliminamos la red del registro
        } else {
            throw new VMDriverException("There was an error during Docker network deletion");
        }
    }
    
    public void removeVirtualNetwork(VirtualNetwork virtualNetwork) throws VMDriverException {
        DockerNetwork dockerNetwork = convertToDockerVirtualNetwork(virtualNetwork);

        String aux = String.format("%s network rm %s", dockerPath, dockerNetwork.getDockerNetworkName());

        String DockerNetID = commandExecuter(aux);

        // En caso de borrado exitoso, la consola devuelve el nombre de la red eliminada
        if (DockerNetID.equals(dockerNetwork.getDockerNetworkName())) {
            DockerNetID = "success";
        } else {
            throw new VMDriverException("There was an error during Docker network deletion");
        }
    }

    @Override
    public VirtualMachine createVirtualMachine(String simulationName, Host host) throws VMDriverException {

        String aux = String.format("%s container run --name %s --privileged -d -it %s bash", dockerPath, host.getName(), host.getHostType().getVmImage().getName());

        String DockerUUID = commandExecuter(aux);

        // Si el mensaje de consola es la ID del contenedor creado (caso exitoso)
        if (DockerUUID.length() == 64) {
            DockerMachine container = new DockerMachine(host, DockerUUID, initSshPort);
            initSshPort++;  //Incrementamos este valor para asignar puertos ssh distintos a cada contenedor

            aux = String.format("%s network disconnect bridge %s", dockerPath, container.getDockerInternalMachineName());   //Desconectamos el contenedor de la red bridge de Docker (se conecta de forma predeterminada)     
            DockerUUID = commandExecuter(aux);

            List<Link> contLinks = host.getLinks(); // Obtenemos la lista de redes a la que debemos conectar el contenedor

            // Iteramos la lista
            for (Link l : contLinks) {
                
                aux = String.format("%s network connect --ip=%s %s %s", dockerPath, l.getIpAddress(), l.getNetwork().getName(), l.getHost().getName());
                
                DockerUUID = commandExecuter(aux);
            }
            
            containers.put(container.getDockerInternalMachineName(), container);    //Añadimos el contenedor creado al registro

            return container;
        } else {
            throw new VMDriverException("There was an error during Docker container creation");
        }

    }

    @Override
    public VirtualMachine updateVirtualMachine(String simulationName, VirtualMachine virtualMachine, Host newHost) throws VMDriverException {

        DockerMachine dockerMachine = convertToDockerVirtualMachine(virtualMachine);

        // TODO: modificar el contenedor Docker
        return dockerMachine; // Ajustar
    }

    @Override
    public void removeVirtualMachine(String simulationName, VirtualMachine virtualMachine) throws VMDriverException {

        DockerMachine dockerMachine = convertToDockerVirtualMachine(virtualMachine);

        // Antes de su borrado, paramos la actividad del contenedor seleccionado
        if (dockerMachine.getStatus() == DockerStatus.RUNNING) {
            dockerMachine = convertToDockerVirtualMachine(stopVirtualMachine(simulationName, virtualMachine));
        } else if (dockerMachine.getStatus() == DockerStatus.PAUSED) {
            dockerMachine = convertToDockerVirtualMachine(resumeVirtualMachine(simulationName, virtualMachine));
            
        }

        String aux = String.format("%s container rm %s", dockerPath, dockerMachine.getDockerUUID());

        String DockerUUID = commandExecuter(aux);

        // En caso de borrado exitoso, la consola devuelve la ID del contenedor eliminado
        if (DockerUUID.equals(dockerMachine.getDockerUUID())) {
            containers.remove(dockerMachine.getDockerInternalMachineName());    // Eliminamos el contenedor del registro
        } else {
            throw new VMDriverException("There was an error during Docker container deletion");
        }
    }
    
    public void removeVirtualMachine(DockerMachine dockerMachine, String simulationName) throws VMDriverException {

        // Antes de su borrado, paramos la actividad del contenedor seleccionado
        if (dockerMachine.getStatus() == DockerStatus.RUNNING) {
            dockerMachine = convertToDockerVirtualMachine(stopVirtualMachine(simulationName, dockerMachine));
        } else if (dockerMachine.getStatus() == DockerStatus.PAUSED) {
            dockerMachine = convertToDockerVirtualMachine(resumeVirtualMachine(simulationName, dockerMachine));
            
        }

        String aux = String.format("%s container rm %s", dockerPath, dockerMachine.getDockerUUID());

        String DockerUUID = commandExecuter(aux);

        // En caso de borrado exitoso, la consola devuelve la ID del contenedor eliminado
        if (DockerUUID.equals(dockerMachine.getDockerUUID())) {
            DockerUUID = "success";    // Eliminamos el contenedor del registro
        } else {
            throw new VMDriverException("There was an error during Docker container deletion");
        }
    }

    @Override
    public VirtualMachine startVirtualMachine(String simulationName, VirtualMachine virtualMachine) throws VMDriverException {

        DockerMachine dockerMachine = convertToDockerVirtualMachine(virtualMachine);

        if (dockerMachine.getStatus() == DockerStatus.RUNNING) {
            return dockerMachine;
        }

        String aux = String.format("%s container start %s", dockerPath, dockerMachine.getDockerInternalMachineName());

        String DockerContName = commandExecuter(aux);

        if (DockerContName.equals(dockerMachine.getDockerInternalMachineName())) {
            dockerMachine.setStatus(DockerMachine.DockerStatus.RUNNING);
            containers.replace(dockerMachine.getDockerInternalMachineName(), dockerMachine);
            return dockerMachine;
        } else {
            throw new VMDriverException("There was an error during Docker container startup");
        }
    }

    @Override
    public VirtualMachine pauseVirtualMachine(String simulationName, VirtualMachine virtualMachine) throws VMDriverException {

        DockerMachine dockerMachine = convertToDockerVirtualMachine(virtualMachine);

        if (dockerMachine.getStatus() == DockerStatus.PAUSED) {
            return dockerMachine;
        }

        String aux = String.format("%s container pause %s", dockerPath, dockerMachine.getDockerInternalMachineName());

        String DockerContName = commandExecuter(aux);
        
        if (DockerContName.equals(dockerMachine.getDockerInternalMachineName())) {
            dockerMachine.setStatus(DockerMachine.DockerStatus.PAUSED);
            containers.replace(dockerMachine.getDockerInternalMachineName(), dockerMachine);
            return dockerMachine;
        } else {
            throw new VMDriverException("There was an error during Docker container pausing\n" + DockerContName + "\n" + DockerContName.length() + "\n" + dockerMachine.getDockerInternalMachineName() + "\n" + dockerMachine.getDockerInternalMachineName().length());
        }
    }

    @Override
    public VirtualMachine resumeVirtualMachine(String simulationName, VirtualMachine virtualMachine) throws VMDriverException {

        DockerMachine dockerMachine = convertToDockerVirtualMachine(virtualMachine);

        String aux = String.format("%s container unpause %s", dockerPath, dockerMachine.getDockerInternalMachineName());

        String DockerContName = commandExecuter(aux);

        if (DockerContName.equals(dockerMachine.getDockerInternalMachineName())) {
            dockerMachine.setStatus(DockerMachine.DockerStatus.RUNNING);
            containers.replace(dockerMachine.getDockerInternalMachineName(), dockerMachine);
            return dockerMachine;
        } else {
            throw new VMDriverException("There was an error during Docker container resuming");
        }
    }

    @Override
    public VirtualMachine stopVirtualMachine(String simulationName, VirtualMachine virtualMachine) throws VMDriverException {

        DockerMachine dockerMachine = convertToDockerVirtualMachine(virtualMachine);

        if (dockerMachine.getStatus() == DockerStatus.NON_STARTED) {
            return dockerMachine;
        }

        String aux = String.format("%s container stop %s", dockerPath, dockerMachine.getDockerInternalMachineName());

        String DockerContName = commandExecuter(aux);

        if (DockerContName.equals(dockerMachine.getDockerInternalMachineName())) {
            dockerMachine.setStatus(DockerMachine.DockerStatus.NON_STARTED);
            containers.replace(dockerMachine.getDockerInternalMachineName(), dockerMachine);
            return dockerMachine;
        } else {
            throw new VMDriverException("There was an error during Docker container stopping");
        }
    }

    @Override
    public void exportVirtualMachine(String simulationName, VirtualMachine virtualMachine, String destinationPath) throws VMDriverException {
        // Por ahora no hace falta hacer nada aqui
    }

    @Override
    public VirtualMachine importVirtualMachine(String simulationName, String sourcePath, Host node) throws VMDriverException {
        // Por ahora no hace falta hacer nada aqui
        return null;
    }

    // Metodo para ejecutar una cadena de caracteres en la consola de comandos (Linux)
    public String commandExecuter(String command) {

        String toRet = null;

        String[] aux = command.split(" ");

        ProcessBuilder process = new ProcessBuilder(aux);
        Process p;
        try {
            p = process.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            String result = builder.toString();
            toRet = result.replaceAll("[^0-9a-zA-Z\\-_]+", "");

        } catch (IOException e) {
            System.out.print("error");
            e.printStackTrace();
        }

        return toRet;

    }

    @Override
    public Class[] exposeJAXBClasses() {
        List<Class> jaxbClasses = new ArrayList<>();

        jaxbClasses.add(DockerDriverSpec.class);
        jaxbClasses.add(DockerExecutionSpec.class);
        jaxbClasses.add(DockerMachine.class);
        jaxbClasses.add(DockerNetwork.class);

        return jaxbClasses.toArray(new Class[jaxbClasses.size()]);
    }

}

