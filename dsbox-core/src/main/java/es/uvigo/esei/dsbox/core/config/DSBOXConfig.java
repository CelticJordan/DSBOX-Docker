package es.uvigo.esei.dsbox.core.config;

import es.uvigo.esei.dsbox.core.model.ArchitectureType;
import es.uvigo.esei.dsbox.core.model.HostType;
import es.uvigo.esei.dsbox.core.model.OperativeSystemType;
import es.uvigo.esei.dsbox.core.model.VMImage;
import es.uvigo.esei.dsbox.core.model.VMType;
import es.uvigo.esei.dsbox.core.model.exceptions.DSBOXException;
import es.uvigo.esei.dsbox.core.xml.DSBOXXMLDAO;
//import es.uvigo.esei.dsbox.virtualbox.execution.VirtualBoxDriverSpec;
//import es.uvigo.esei.dsbox.virtualbox.manager.VirtualBoxDriver;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DSBOXConfig {

    public final static String DEFAULT_DSBOX_HOME_DIR = System.getProperty("user.home") + File.separator + "DSBOX";
    public final static String IMAGES_DIR = "IMAGES";
    public final static String SIMULATIONS_DIR = "SIMULATIONS";
    public final static String CONFIG_DIR = "config";
    public final static String HOST_TYPES_DIR = "hostTypes";
    public final static String DSBOX_PROPERTIES = "dsbox.properties";


    private VMType vmType;
    private String dsboxHomeDir;
    private DSBOXXMLDAO dao;

    public DSBOXConfig(VMType vmType) {
        this.vmType = vmType;
        this.dsboxHomeDir = DEFAULT_DSBOX_HOME_DIR;
        this.dao = new DSBOXXMLDAO();
    }

    public DSBOXConfig(VMType vmType, String dsboxHomeDir, DSBOXXMLDAO dao) {
        this.vmType = vmType;
        this.dsboxHomeDir = dsboxHomeDir;
        this.dao = dao;
    }

    public VMType getVmType() {
        return vmType;
    }

    public void setVmType(VMType vmType) {
        this.vmType = vmType;
    }

    
    public String getDsboxHomeDir() {
        return dsboxHomeDir;
    }

    public void setDsboxHomeDir(String dsboxHomeDir) {
        this.dsboxHomeDir = dsboxHomeDir;
    }

    public boolean checkDSBOXConfiguration() {
        return checkExistsDSBOXHomeDir()
                && checkStructureDSBOXHomeDir()
                && checkDSBoxProperties();
    }

    private boolean checkExistsDSBOXHomeDir() {
        return checkExistsDir(new File(this.dsboxHomeDir));
    }

    private boolean checkStructureDSBOXHomeDir() {
        File dsboxHomeDirFile = new File(this.dsboxHomeDir);
        
        return checkExistsDir(getImagesDir())
                && checkExistsDir(new File(dsboxHomeDirFile, SIMULATIONS_DIR))
                && checkExistsDir(new File(dsboxHomeDirFile, CONFIG_DIR))
                && checkExistsDir(new File(dsboxHomeDirFile, CONFIG_DIR + File.separator + HOST_TYPES_DIR));
    }

    private boolean checkDSBoxProperties() {
        File dsboxHomeDirFile = new File(this.dsboxHomeDir);
        File propertiesFile = new File(dsboxHomeDirFile, CONFIG_DIR + File.separator + DSBOX_PROPERTIES);

        return propertiesFile.exists() && propertiesFile.isFile();
    }

    private boolean checkExistsDir(File dirFile) {
        return dirFile.exists() && dirFile.isDirectory();

    }

    public void initializeDSBOXConfig() throws DSBOXException {
        File dsboxHomeDirFile = new File(this.dsboxHomeDir);
        if (!checkExistsDir(dsboxHomeDirFile)) {
            dsboxHomeDirFile.mkdirs();
        }
        File imagesDir = new File(dsboxHomeDirFile, IMAGES_DIR);
        if (!checkExistsDir(imagesDir)) {
            imagesDir.mkdir();
            //copyDefaultImage(imagesDir);
        }
        File simulationsDir = new File(dsboxHomeDirFile, SIMULATIONS_DIR);
        if (!checkExistsDir(simulationsDir)) {
            simulationsDir.mkdir();
        }
        File configDir = new File(dsboxHomeDirFile, CONFIG_DIR);

        if (!checkExistsDir(configDir)) {
            configDir.mkdir();
            File hostTypesDir = new File(configDir, HOST_TYPES_DIR);
            hostTypesDir.mkdir();
            createDefaultHostType();
            createDefaultDSBOXproperties();
//            createDefaultVirtualBoxDriverSpec();
        }

    }

    private void copyDefaultImage(File imagesDir) throws DSBOXException {
        File imageFile = new File(imagesDir, "default.vdi");
        String imageFilename = imageFile.getAbsolutePath();
        try {
            imageFile.createNewFile();
        } catch (IOException ex) {
            throw new DSBOXException("Error creating default VirtualBox image at " + imageFilename, ex);
        }
    }

    public File getImagesDir() {
        File dsboxHomeDirFile = new File(this.dsboxHomeDir);
        return new File(dsboxHomeDirFile, IMAGES_DIR);
    }

    public File getSimulationsDir() {
        File dsboxHomeDirFile = new File(this.dsboxHomeDir);
        return new File(dsboxHomeDirFile, SIMULATIONS_DIR);
    }

    public File getConfigDir() {
        File dsboxHomeDirFile = new File(this.dsboxHomeDir);
        return new File(dsboxHomeDirFile, CONFIG_DIR);
    }

    private void createDefaultHostType() throws DSBOXException {
        // TODO: hacer esto independiente del tipo de Driver
        if (this.vmType == VMType.VIRTUALBOX) {
            createDefaultHostTypeForVBOX();
        }
        if (this.vmType == VMType.DOCKER) {
            createDefaultHostTypeForDOCKER();
        }
    }
    
    private void createDefaultHostTypeForVBOX() throws DSBOXException {    
        // TODO: hacer esto independiente del tipo de Driver
        File defaultImageFile = new File(getImagesDir(), "default.vdi");

        VMImage defaultVMImage = new VMImage(VMImage.ImageType.VDI, "default", defaultImageFile.getAbsolutePath(), defaultImageFile.getAbsolutePath());
        defaultVMImage.setCompressed(false);
        defaultVMImage.setCompressorType(VMImage.CompressorType.NONE);

        HostType defaultHostType = new HostType("default", "default", "Default HostType", defaultVMImage);
        defaultHostType.setArch(ArchitectureType.X86);
        defaultHostType.setOs(OperativeSystemType.GNU_LINUX);
        defaultHostType.setNetworkAutoconfigureSupported(true);

        this.addHostType(defaultHostType);
    }

    private void createDefaultHostTypeForDOCKER() throws DSBOXException {    
        // TODO: hacer esto independiente del tipo de Driver

        VMImage defaultVMImage = new VMImage(VMImage.ImageType.DOCKER, "debian:latest");  // TODO: indicar la imagen a usar por defecto, para pruebas "debian:latest"

        HostType defaultHostType = new HostType("default", "default", "Default HostType", defaultVMImage);
        defaultHostType.setArch(ArchitectureType.X86);
        defaultHostType.setOs(OperativeSystemType.GNU_LINUX);
        defaultHostType.setNetworkAutoconfigureSupported(true);

        this.addHostType(defaultHostType);
    }

    public void addHostType(HostType hostType) throws DSBOXException {
        File hostTypeFile = composeHostTypeFile(hostType);
        if (hostTypeFile.exists()) {
            throw new DSBOXException("HostType " + hostType.getName() + " already exists at " + hostTypeFile.getAbsolutePath());
        } else {
            try {
                dao.saveHostTypeToFile(hostTypeFile.getCanonicalPath(), hostType);
            } catch (DSBOXException | IOException ex) {
                throw new DSBOXException("Error saving HostType " + hostType.getName() + " at " + hostTypeFile.getAbsolutePath(), ex);
            }
        }
    }

    public void removeHostType(HostType hostType) throws DSBOXException {
        File hostTypeFile = composeHostTypeFile(hostType);
        if (!hostTypeFile.exists()) {
            throw new DSBOXException("HostType " + hostType.getName() + " does not exists");
        } else if (!hostTypeFile.delete()) {
            throw new DSBOXException("Unable to remove HostType " + hostType.getName());
        }
    }

    public List<HostType> getRegisteredHostTypes() {
        List<HostType> result = new ArrayList<>();

        File hostTypesDir = getHostTypesDir();

        FilenameFilter filter = (File dir, String name) -> name.endsWith(".xml");
        for (File hostTypeFile : hostTypesDir.listFiles(filter)) {
            try {
                HostType ht = dao.loadHostTypeFromFile(hostTypeFile.getAbsolutePath());
                result.add(ht);
            } catch (DSBOXException ex) {
            }
        }

        return result;
    }

    private File composeHostTypeFile(HostType hostType) {
        File hostTypesDir = getHostTypesDir();
        String filename = hostType.getName().replace(" ", "_") + ".xml";
        return new File(hostTypesDir, filename);
    }

    private File getHostTypesDir() {
        return new File(getDsboxHomeDir() + File.separator + CONFIG_DIR + File.separator + HOST_TYPES_DIR);
    }

    private void createDefaultDSBOXproperties() throws DSBOXException {
        File dsboxProperties = new File(getConfigDir(), DSBOX_PROPERTIES);
        try {
            dsboxProperties.createNewFile();
        } catch (IOException ex) {
            throw new DSBOXException("Error creating DSBOX properties file at " + dsboxProperties.getPath(), ex);
        }
    }

//    public VirtualBoxDriverSpec getVirtualBoxDriverSpec() throws DSBOXException {
//        File driverSpecFile = new File(getConfigDir(), VBOXDRIVER_CONFIG);
//        return dao.loadVirtualBoxDriverSpecFromFile(driverSpecFile.getAbsolutePath());
//    }
//
//    public void storeVirtualBoxDriverSpec(VirtualBoxDriverSpec vboxSpec) throws DSBOXException {
//        File driverSpecFile = new File(getConfigDir(), VBOXDRIVER_CONFIG);
//        dao.saveVirtualBoxDriverSpecFromFile(driverSpecFile.getAbsolutePath(), vboxSpec);
//    }
//
//    private void createDefaultVirtualBoxDriverSpec() throws DSBOXException {
//        VirtualBoxDriverSpec defaultSpec = new VirtualBoxDriverSpec();  
//        defaultSpec.setImagesBasePath(getImagesDir().getAbsolutePath());
//        defaultSpec.setSimulationsBasePath(getSimulationsDir().getAbsolutePath());
//        storeVirtualBoxDriverSpec(defaultSpec);
//    }

}
