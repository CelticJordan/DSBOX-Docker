package es.uvigo.esei.dsbox.gui.views.panels;

import es.uvigo.esei.dsbox.core.model.Host;
import es.uvigo.esei.dsbox.core.model.HostType;
import es.uvigo.esei.dsbox.core.model.Link;
import es.uvigo.esei.dsbox.core.model.Network;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Separator;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

public class HostEditorPanel extends ElementPanel {

    private TextField id;

    private TextField name;

    private TextField description;

    private TextField label;

    private ComboBox<HostType> hostType;

    private TextField machineRAM;

    private TextField machineCPU;

    private TextField machineFullName;

    private TextField machineShortName;

    private TextField defaultGW;

    private TextField dnsServers;

//    private List<Host.KnownHost> knownHosts;
//    private List<Host.RouteToAdd> routesToAdd;
//    @XmlElementWrapper(name = "links")
//    @XmlElement(name = "link")
//    private List<Link> links;
//
//    @XmlElementWrapper(name = "services-to-start")
//    @XmlElement(name = "service")
//    private List<String> servicesToStart;
//
//    @XmlElementWrapper(name = "vm-properties")
//    @XmlElement(name = "vm-property")
//    private List<VMProperty> vmProperties;
    private Host host;

    private List<HostType> registeredHostTypes = new ArrayList<>();
    private List<Link> editingLinks;
    private LinkEditorPanel selectedLinkPanel;
    private Link selectedLink;
    private TableView linkTable;

    public HostEditorPanel(Host host) {
        super();
        this.host = host;
        this.editingLinks = cloneLinkList(host.getLinks());
        initComponents();

    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
        this.editingLinks = cloneLinkList(host.getLinks());
        initComponents();
    }

    public void setRegisteredHostTypes(List<HostType> registeredHostTypes) {
        this.registeredHostTypes = registeredHostTypes;
        initComponents();
    }

    @Override
    protected void initComponents() {
        GridPane grid = createTopGrid();

        TabPane tab = new TabPane();
        Tab tabDetails = new Tab();
        tabDetails.setClosable(false);
        tabDetails.setText("Host details");
        tabDetails.setContent(createHostDetailsPanel());
        tab.getTabs().add(tabDetails);

        if ((host.getLinks() != null) && (!host.getLinks().isEmpty())) {
            Tab tabLinks = new Tab();
            tabLinks.setClosable(false);
            tabLinks.setText("Host links");
            tabLinks.setContent(createHostLinksPanel());
            tab.getTabs().add(tabLinks);
        }

//        tab.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                tab.setPrefWidth(tab.getPrefWidth());
//                tab.getTabs().get(oldValue.intValue()).getContent().setDisable(true);
//                tab.getTabs().get(newValue.intValue()).getContent().setDisable(false);
//            }
//        });
        Parent buttons = createButtons();
        save.setDisable(true);

        VBox panel = new VBox(8, grid, tab, new Separator(Orientation.HORIZONTAL), buttons);

        this.getChildren().clear();
        this.getChildren().add(panel);
    }

    private GridPane createTopGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(4);
        grid.setVgap(4);
        grid.setPadding(new Insets(8));

        grid.getColumnConstraints().addAll(new ColumnConstraints(75, 85, 200, Priority.SOMETIMES, HPos.LEFT, true),
                new ColumnConstraints(150, 350, 450, Priority.ALWAYS, HPos.LEFT, true));

        id = new TextField(host.getId());
        id.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 0, "Id :", id);

        name = new TextField(host.getName());
        name.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 1, "Name :", name);

        description = new TextField(host.getDescription());
        description.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 2, "Description :", description);

        label = new TextField(host.getLabel());
        label.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 3, "Label :", label);

        //grid.add(new Separator(Orientation.HORIZONTAL), 0, XX, 2, 1);
        return grid;
    }

    private Node createHostDetailsPanel() {
        GridPane grid = new GridPane();
        grid.setHgap(4);
        grid.setVgap(4);
        grid.setPadding(new Insets(8));
        grid.getColumnConstraints().addAll(new ColumnConstraints(120, 120, 150, Priority.SOMETIMES, HPos.LEFT, true),
                new ColumnConstraints(120, 200, 450, Priority.ALWAYS, HPos.LEFT, true));

        hostType = new ComboBox<HostType>(FXCollections.observableArrayList(registeredHostTypes));
        hostType.setValue(host.getHostType());
        hostType.setConverter(new StringConverter<HostType>() {
            @Override
            public String toString(HostType ht) {
                return ht.getName() + " [" + ht.getOs() + ", " + ht.getArch() + "]";
            }

            @Override
            public HostType fromString(String string) {
                return null;
            }
        });
        hostType.selectionModelProperty().addListener((ObservableValue<? extends SingleSelectionModel<HostType>> observable, SingleSelectionModel<HostType> oldValue, SingleSelectionModel<HostType> newValue) -> {
            machineRAM = new TextField(Integer.toString(newValue.getSelectedItem().getDefaultRAM()));
            machineCPU = new TextField(Integer.toString(newValue.getSelectedItem().getDefaultCPU()));
            save.setDisable(false);
        });
        addPropertyControl(grid, 0, "Host type :", hostType);

        machineRAM = new TextField(Integer.toString(host.getMachineRAM()));
        machineRAM.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 1, "Machine RAM :", machineRAM);

        machineCPU = new TextField(Integer.toString(host.getMachineCPU()));
        machineCPU.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 2, "Machine CPU % :", machineCPU);

        grid.add(new Separator(Orientation.HORIZONTAL), 0, 3, 2, 1);

        machineFullName = new TextField(host.getMachineFullName());
        machineFullName.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 4, "DNS name :", machineFullName);

        machineShortName = new TextField(host.getMachineShortName());
        machineShortName.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 5, "Host name :", machineShortName);

        defaultGW = new TextField(host.getDefaultGW());
        defaultGW.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 6, "Default gateway :", defaultGW);

        dnsServers = new TextField(host.getDnsServers());
        dnsServers.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 7, "DNS servers :", dnsServers);

        StackPane sp = new StackPane();
        sp.getChildren().addAll(new Pane(), grid);
        return sp;
    }

    private Node createHostLinksPanel() {
        VBox panel = new VBox(8);
        panel.setPadding(new Insets(8));

        selectedLink = editingLinks.get(0);
        selectedLinkPanel = new LinkEditorPanel(selectedLink);
        selectedLinkPanel.setSaveActionHandler((ActionEvent event) -> {
            selectedLinkPanel.updateModelValues();
            selectedLinkPanel.save.setDisable(true);
            save.setDisable(false);

            // Refresh LinkTable
            //linkTable.getItems().remove(selectedLink);
            //linkTable.getItems().add(selectedLink);
            linkTable.getItems().clear();
            linkTable.getItems().addAll(FXCollections.observableArrayList(editingLinks));
        });
        selectedLinkPanel.setCancelActionHandler((ActionEvent event) -> {
            selectedLink = editingLinks.get(0);
            selectedLinkPanel.setLink(selectedLink);
            selectedLinkPanel.save.setDisable(true);
        });

        linkTable = new TableView(FXCollections.observableArrayList(editingLinks));
        linkTable.setEditable(false);
        linkTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        linkTable.setMaxHeight(90);
        linkTable.setPrefWidth(360);

        TableColumn<Link, Integer> positionColumn = new TableColumn<>("Interface");
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("interfaceOrder"));
        positionColumn.setResizable(false);

        TableColumn<Link, String> destinationColumn = new TableColumn<>("Destination");
        destinationColumn.setCellValueFactory((TableColumn.CellDataFeatures<Link, String> cellData) -> {
            Network network = cellData.getValue().getNetwork();
            return new SimpleStringProperty(network.getName());
        });

        TableColumn<Link, String> addressColumn = new TableColumn<>("IPv4 address");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("ipAddress"));

        TableColumn<Link, String> netmaskColumn = new TableColumn<>("Mask");
        //netmaskColumn.setCellValueFactory(new PropertyValueFactory<>("netMask"));
        netmaskColumn.setCellValueFactory((TableColumn.CellDataFeatures<Link, String> cellData) -> {
            Network network = cellData.getValue().getNetwork();
            return new SimpleStringProperty(network.getNetMask());
        });

        TableColumn<Link, String> broadcastColumn = new TableColumn<>("BCast address");
        //broadcastColumn.setCellValueFactory(new PropertyValueFactory<>("broadcast"));
        broadcastColumn.setCellValueFactory((TableColumn.CellDataFeatures<Link, String> cellData) -> {
            Network network = cellData.getValue().getNetwork();
            return new SimpleStringProperty(network.getBroadcast());
        });

        linkTable.getColumns().addAll(positionColumn, destinationColumn, addressColumn, netmaskColumn, broadcastColumn);

        SelectionModel<Link> linkSelection = linkTable.getSelectionModel();
        linkSelection.selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedLink = newSelection;
            selectedLinkPanel.setLink(selectedLink);
        });

        panel.getChildren().addAll(linkTable, new Separator(Orientation.HORIZONTAL), selectedLinkPanel);
        StackPane sp = new StackPane();
        Pane fondo = new Pane();
        fondo.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
        sp.getChildren().addAll(fondo, panel);
        return sp;
    }

    @Override
    public void updateModelValues() {
        host.setId(id.getText());
        host.setName(name.getText());
        host.setLabel(label.getText());
        host.setDescription(description.getText());
        host.setMachineShortName(machineShortName.getText());
        host.setMachineFullName(machineFullName.getText());
        host.setDefaultGW(defaultGW.getText());
        host.setHostType(hostType.getValue());
        host.setMachineCPU(Integer.parseInt(machineCPU.getText()));
        host.setMachineRAM(Integer.parseInt(machineRAM.getText()));
        host.setDnsServers(dnsServers.getText());
        host.setLinks(editingLinks);
    }

    private List<Link> cloneLinkList(List<Link> links) {
        if (links != null) {
            List<Link> result = new ArrayList<>(links.size());
            for (Link link : links) {
                Link cloned = new Link(link.getHost(), link.getNetwork(), link.getInterfaceOrder(), link.getIpAddress());
                cloned.setDescription(link.getDescription());
                cloned.setEnabled(link.isEnabled());
                result.add(cloned);
            }
            return result;
        } else {
            return null;
        }
    }
}
