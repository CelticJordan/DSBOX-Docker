package es.uvigo.esei.dsbox.gui.views.panels;

import es.uvigo.esei.dsbox.core.model.ArchitectureType;
import es.uvigo.esei.dsbox.core.model.HostType;
import es.uvigo.esei.dsbox.core.model.OperativeSystemType;
import es.uvigo.esei.dsbox.core.model.VMImage;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class HostTypeEditorPanel extends ElementPanel {
    
    private HostType hostType;
    private VMImage vmImage;
    
    private TextField id;
    private TextField name;
    private TextField description;
    private ComboBox<ArchitectureType> arch;
    private ComboBox<OperativeSystemType> os;
    private TextField iconURL;
    private Label creationDate;
    private CheckBox networkAutoconfigureSupported;
//    private CheckBox servicesAutostartSupported;
    private TextField maxInterfaces;
    private TextField defaultRAM;
    private TextField defaultCPU;
    private CheckBox useCommonSwap;
//    private CheckBox useCommonSharedFolder;

    private ComboBox<VMImage.ImageType> vmImage_imageType;
    private TextField vmImage_name;
    private TextField vmImage_downloadURI;
    private TextField vmImage_localURI;
    private CheckBox vmImage_downloaded;
    private CheckBox vmImage_compressed;
    private ComboBox<VMImage.CompressorType> vmImage_compressorType;
    private TextField vmImage_login;
    private TextField vmImage_password;
    
    public HostTypeEditorPanel(HostType hostType) {
        super();
        this.hostType = hostType;
        if (hostType.getVmImage() != null) {
            this.vmImage = hostType.getVmImage();
        } else {
            this.vmImage = new VMImage();
            this.vmImage.setImageType(VMImage.ImageType.DOCKER); // TODO: hacer esto independiente
        }
        
        initComponents();
    }
    
    public HostType getHostType() {
        hostType.setVmImage(vmImage);
        return hostType;
    }
    
    public void setHostType(HostType hostType) {
        this.hostType = hostType;
        if (hostType.getVmImage() != null) {
            this.vmImage = hostType.getVmImage();
        } else {
            this.vmImage = new VMImage();
            this.vmImage.setImageType(VMImage.ImageType.VDI);
        }
        
        initComponents();
    }
    
    @Override
    protected void initComponents() {
        GridPane grid = new GridPane();
        grid.setHgap(4);
        grid.setVgap(4);
        grid.setPadding(new Insets(4));
        grid.getColumnConstraints().addAll(new ColumnConstraints(100, 150, 200, Priority.SOMETIMES, HPos.LEFT, true),
                new ColumnConstraints(200, 350, 400, Priority.ALWAYS, HPos.LEFT, true));
        
        id = new TextField(hostType.getId());
        addPropertyControl(grid, 0, "Id :", id);
        name = new TextField(hostType.getName());
        addPropertyControl(grid, 1, "Name :", name);
        description = new TextField(hostType.getDescription());
        addPropertyControl(grid, 2, "Description :", description);
        
        os = new ComboBox<>(FXCollections.observableArrayList(OperativeSystemType.values()));
        os.setValue(hostType.getOs());
        addPropertyControl(grid, 3, "Operating System :", os);
        
        arch = new ComboBox<>(FXCollections.observableArrayList(ArchitectureType.values()));
        arch.setValue(hostType.getArch());
        addPropertyControl(grid, 4, "Architecture :", arch);
        
        grid.add(new Separator(Orientation.HORIZONTAL), 0, 5, 2, 1);
        
        iconURL = new TextField(hostType.getIconURL());
        addPropertyControl(grid, 6, "Icon URL/Path :", iconURL);
        
        grid.add(new Separator(Orientation.HORIZONTAL), 0, 7, 2, 1);
        
        defaultRAM = new TextField(Integer.toString(hostType.getDefaultRAM()));
        addPropertyControl(grid, 8, "Default RAM :", defaultRAM);
        
        defaultCPU = new TextField(Integer.toString(hostType.getDefaultCPU()));
        addPropertyControl(grid, 9, "Default % CPU :", defaultCPU);
        
        maxInterfaces = new TextField(Integer.toString(hostType.getMaxInterfaces()));
        addPropertyControl(grid, 10, "Max. # interfaces :", maxInterfaces);
        
        networkAutoconfigureSupported = new CheckBox();
        networkAutoconfigureSupported.setSelected(hostType.isNetworkAutoconfigureSupported());
        addPropertyControl(grid, 11, "Network autoconfig :", networkAutoconfigureSupported);

        //      servicesAutostartSupported = new CheckBox();
        //      servicesAutostartSupported.setSelected(hostType.isServicesAutostartSupported());
        //      addPropertyControl(grid, 12, "Services autoconfig :", servicesAutostartSupported);
        useCommonSwap = new CheckBox();
        useCommonSwap.setSelected(hostType.isUseCommonSwap());
        addPropertyControl(grid, 13, "Common swap :", useCommonSwap);
        
        grid.add(new Separator(Orientation.HORIZONTAL), 0, 14, 2, 1);
        
        vmImage_imageType = new ComboBox<>(FXCollections.observableArrayList(VMImage.ImageType.values()));
        vmImage_imageType.setValue(vmImage.getImageType());
        vmImage_imageType.setOnAction((e) -> {
            if (vmImage_imageType.getValue() == VMImage.ImageType.DOCKER) {
                disablePropertiesForDOCKER();
            } else {
                disablePropertiesForNonDOCKER();
            }
        });
        
        addPropertyControl(grid, 15, "Image Type :", vmImage_imageType);
        
        vmImage_name = new TextField();
        vmImage_name.setText(vmImage.getName());
        addPropertyControl(grid, 16, "Name :", vmImage_name);
        
        vmImage_downloadURI = new TextField();
        vmImage_downloadURI.setText(vmImage.getDownloadURI());
        addPropertyControl(grid, 17, "Download URL :", vmImage_downloadURI);
        
        vmImage_localURI = new TextField();
        vmImage_localURI.setText(vmImage.getLocalURI());
        addPropertyControl(grid, 18, "Local path :", vmImage_localURI);
        
        vmImage_downloaded = new CheckBox();
        vmImage_downloaded.setSelected(vmImage.isDownloaded());
        addPropertyControl(grid, 19, "Downloaded :", vmImage_downloaded);
        
        vmImage_compressed = new CheckBox();
        vmImage_compressed.setSelected(vmImage.isCompressed());
        addPropertyControl(grid, 20, "Compressed :", vmImage_compressed);
        
        vmImage_compressorType = new ComboBox<>(FXCollections.observableArrayList(VMImage.CompressorType.values()));
        vmImage_compressorType.setValue(vmImage.getCompressorType());
        addPropertyControl(grid, 21, "Compressor :", vmImage_compressorType);
        
        grid.add(new Separator(Orientation.HORIZONTAL), 0, 22, 2, 1);
        
        vmImage_login = new TextField();
        vmImage_login.setText(vmImage.getDefaultLogin());
        addPropertyControl(grid, 23, "Default login :", vmImage_login);
        
        vmImage_password = new TextField();
        vmImage_password.setText(vmImage.getDefaultPassword());
        addPropertyControl(grid, 24, "Default password :", vmImage_password);
        
        grid.add(new Separator(Orientation.HORIZONTAL), 0, 25, 2, 1);
        
        if (vmImage.getImageType() == VMImage.ImageType.DOCKER) {
            disablePropertiesForDOCKER();
        }
        
        Parent buttons = createButtons("Save", "Cancel", true);
        
        HBox panel = new HBox(8, grid, buttons);
        panel.setAlignment(Pos.TOP_LEFT);
        
        this.getChildren().clear();
        this.getChildren().add(panel);
    }
    
    @Override
    public void updateModelValues() {
        hostType.setId(id.getText());
        hostType.setName(name.getText());
        hostType.setDescription(description.getText());
        hostType.setOs(os.getValue());
        hostType.setArch(arch.getValue());
        hostType.setIconURL(iconURL.getText());
        hostType.setNetworkAutoconfigureSupported(networkAutoconfigureSupported.isSelected());
        hostType.setMaxInterfaces(Integer.parseInt(maxInterfaces.getText()));
        hostType.setDefaultCPU(Integer.parseInt(defaultCPU.getText()));
        hostType.setDefaultRAM(Integer.parseInt(defaultRAM.getText()));
        hostType.setUseCommonSwap(useCommonSwap.isSelected());
        
        vmImage.setImageType(vmImage_imageType.getValue());
        vmImage.setName(vmImage_name.getText());
        vmImage.setDownloadURI(vmImage_downloadURI.getText());
        vmImage.setLocalURI(vmImage_localURI.getText());
        vmImage.setCompressed(vmImage_compressed.isSelected());
        vmImage.setCompressorType(vmImage_compressorType.getValue());
        vmImage.setDownloaded(vmImage_downloaded.isSelected());
        
        vmImage.setDefaultLogin(vmImage_login.getText());
        vmImage.setDefaultPassword(vmImage_password.getText());
    }
    
    private void disablePropertiesForDOCKER() {
        // TODO
        vmImage_compressed.setDisable(true);
        vmImage_compressorType.setDisable(true);
        vmImage_downloadURI.setDisable(true);
        vmImage_downloaded.setDisable(true);
        vmImage_localURI.setDisable(true);
        
        defaultRAM.setDisable(true);
        defaultCPU.setDisable(true);
        useCommonSwap.setDisable(true);
        
    }
    
    private void disablePropertiesForNonDOCKER() {
        // TODO
        vmImage_compressed.setDisable(false);
        vmImage_compressorType.setDisable(false);
        vmImage_downloadURI.setDisable(false);
        vmImage_downloaded.setDisable(false);
        vmImage_localURI.setDisable(false);

        defaultRAM.setDisable(false);
        defaultCPU.setDisable(false);
        useCommonSwap.setDisable(false);
    }
    
}
