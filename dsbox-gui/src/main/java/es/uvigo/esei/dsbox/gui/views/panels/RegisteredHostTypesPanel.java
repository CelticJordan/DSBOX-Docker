package es.uvigo.esei.dsbox.gui.views.panels;

import es.uvigo.esei.dsbox.core.manager.VMDriverException;
import es.uvigo.esei.dsbox.core.model.HostType;
import es.uvigo.esei.dsbox.core.model.VMImage;
import es.uvigo.esei.dsbox.core.model.exceptions.DSBOXException;
import es.uvigo.esei.dsbox.gui.controllers.MainController;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisteredHostTypesPanel extends Parent {

    private List<HostType> registeredHostTypes;
    private HostType selectedHostType;
    private MainController controller;

    private TableView<HostType> hostTypeTable;
    private HostTypeEditorPanel hostTypeEditorPanel;
    private Button newHostType;
    private Button close;
    private boolean isNewHostType = false;
    private final Stage parentScene;

    public RegisteredHostTypesPanel(MainController controller, Stage parentStage) {
        this.registeredHostTypes = controller.getRegisteredHostTypes();
        this.controller = controller;
        this.parentScene = parentStage;

        initComponents();
    }

    private void initComponents() {
        VBox panel = new VBox(8);
        panel.setAlignment(Pos.TOP_LEFT);
        panel.setPadding(new Insets(8));

        Parent hostTypeTablePanel = createHostTypeTablePanel();

        if ((registeredHostTypes != null) && !registeredHostTypes.isEmpty()) {
            selectedHostType = registeredHostTypes.get(0);
        } else {
            selectedHostType = new HostType();
            selectedHostType.setVmImage(new VMImage());
        }
        hostTypeEditorPanel = new HostTypeEditorPanel(selectedHostType);
        hostTypeEditorPanel.setCancelActionHandler((ActionEvent ae) -> {
            selectedHostType = registeredHostTypes.get(0);
            hostTypeEditorPanel.setHostType(selectedHostType);
            hostTypeEditorPanel.save.setDisable(true);
            newHostType.setDisable(false);
            close.setDisable(false);
        });
        hostTypeEditorPanel.setSaveActionHandler((ActionEvent ae) -> {
            hostTypeEditorPanel.updateModelValues();
            if (isNewHostType) {
                // add new hostType
                hostTypeTable.getItems().add(selectedHostType);
                registeredHostTypes.add(selectedHostType);
                try {
                    controller.registerNewHostType(selectedHostType);
                } catch (DSBOXException | VMDriverException ex) {
                    // TODO
                }
                isNewHostType = false;
                newHostType.setDisable(false);
            } else {
                // Refresh Table
                //hostTypeTable.getItems().remove(selectedHostType);
                //hostTypeTable.getItems().add(selectedHostType);
                hostTypeTable.getItems().clear();
                hostTypeTable.getItems().addAll(registeredHostTypes);
            }
            hostTypeEditorPanel.save.setDisable(true);
            close.setDisable(false);

//            // Refresh Table
//            hostTypeTable.getItems().clear();
//            hostTypeTable.getItems().addAll(FXCollections.observableArrayList(registeredHostTypes));
        });

        close = new Button("Close");
        close.setDefaultButton(true);
        close.setOnAction((ActionEvent ae) -> {
            parentScene.close();
        });
        HBox center = new HBox(close);
        center.setAlignment(Pos.CENTER);

        panel.getChildren().add(hostTypeTablePanel);
        panel.getChildren().add(new Separator(Orientation.HORIZONTAL));
        panel.getChildren().add(hostTypeEditorPanel);
        panel.getChildren().add(new Separator(Orientation.HORIZONTAL));

        panel.getChildren().add(center);

        this.getChildren().clear();
        this.getChildren().add(panel);
    }

    private Parent createHostTypeTablePanel() {

        hostTypeTable = new TableView<>(FXCollections.observableArrayList(registeredHostTypes));
        hostTypeTable.setEditable(false);
        hostTypeTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        hostTypeTable.setMaxHeight(150);
        hostTypeTable.setPrefWidth(500);

        TableColumn<HostType, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setResizable(false);

        TableColumn<HostType, String> os_archColumn = new TableColumn<>("OS");
        os_archColumn.setCellValueFactory((TableColumn.CellDataFeatures<HostType, String> cellData) -> {
            String os_arch = cellData.getValue().getOs().getName() + " (" + cellData.getValue().getArch().getName() + ")";
            return new SimpleStringProperty(os_arch);
        });
        os_archColumn.setResizable(false);

        TableColumn<HostType, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setResizable(false);

        TableColumn<HostType, String> imageColumn = new TableColumn<>("VM image");
        imageColumn.setCellValueFactory((TableColumn.CellDataFeatures<HostType, String> cellData) -> {
            VMImage vmImage = cellData.getValue().getVmImage();
            String imageLocalURI = vmImage.getLocalURI();
            return new SimpleStringProperty(imageLocalURI);
        });

        hostTypeTable.getColumns().addAll(nameColumn, os_archColumn, descriptionColumn, imageColumn);

        SelectionModel<HostType> hostTypeSelection = hostTypeTable.getSelectionModel();
        hostTypeSelection.selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedHostType = newSelection;
            hostTypeEditorPanel.setHostType(selectedHostType);
        });

        newHostType = new Button("New");
        newHostType.setOnAction((ActionEvent ae) -> {
            isNewHostType = true;
            selectedHostType = new HostType();
            selectedHostType.setVmImage(new VMImage());
            hostTypeEditorPanel.setHostType(selectedHostType);
            newHostType.setDisable(true);
            close.setDisable(true);
        });

        HBox layout = new HBox(8);
        //layout.setAlignment(Pos.TOP_LEFT);
        layout.setPadding(new Insets(8));

        layout.getChildren().addAll(hostTypeTable, newHostType);

        return layout;
    }

}
