package es.uvigo.esei.dsbox.docker.gui;

import es.uvigo.esei.dsbox.docker.execution.DockerDriverSpec;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DockerDriverSpecEditorPanel extends Parent {

    private TextField dockerPath;
    
    private TextField initialSSHPort;
    
    private Button save;
    private Button cancel;

    private DockerDriverSpec driverSpec;
    private Stage stage;

    public DockerDriverSpecEditorPanel(DockerDriverSpec driverSpec, Stage stage) {
        this.driverSpec = driverSpec;
        this.stage = stage;
        initComponents();
    }

    public DockerDriverSpec getDriverSpec() {
        return driverSpec;
    }

    public void setDriverSpec(DockerDriverSpec driverSpec) {
        this.driverSpec = driverSpec;
        initComponents();
    }

    protected Parent createButtons() {
        save = new Button("Save");
        save.setDefaultButton(true);
        save.setOnAction((ActionEvent ae) -> {
            this.updateModelValues();
            stage.close();
        });

        cancel = new Button("Cancel");
        cancel.setCancelButton(true);
        cancel.setOnAction((ActionEvent ae) -> {
            stage.close();
        });

        double maxWidth = Math.max(cancel.getPrefWidth(), save.getPrefWidth());
        cancel.setPrefWidth(maxWidth);
        save.setPrefWidth(maxWidth);

        HBox buttons = new HBox(8, save, cancel);
        buttons.setAlignment(Pos.CENTER);
        return buttons;
    }

    protected ChangeListener<String> defaultStringPropertyListener() {
        return (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            save.setDisable(false);
        };
    }

    protected void addPropertyControl(GridPane grid, int row, String label, Control control) {
        addPropertyControl(grid, row, label, control, null);
    }

    protected void addPropertyControl(GridPane grid, int row, String label, Control control, String description) {
        if (description != null) {
            control.setTooltip(new Tooltip(description));
        }
        grid.addRow(row, new Label(label), control);
    }

    public void disableCancelButton() {
        cancel.setDisable(true);
    }
    
    protected void initComponents() {
        GridPane grid = new GridPane();
        grid.setHgap(4);
        grid.setVgap(4);
        grid.setPadding(new Insets(8));
        grid.getColumnConstraints().addAll(new ColumnConstraints(180, 200, 250, Priority.ALWAYS, HPos.LEFT, true),
                new ColumnConstraints(200, 350, 400, Priority.SOMETIMES, HPos.LEFT, true));
        
        dockerPath = new TextField(driverSpec.getDockerServerHost());
        dockerPath.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 0, "Docker path :", dockerPath);
        
        grid.add(new Separator(Orientation.HORIZONTAL), 0, 1, 2, 1);
        
        initialSSHPort = new TextField(Integer.toString(driverSpec.getDockerServerPort()));
        initialSSHPort.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 2, "Initial SSH port :", initialSSHPort);
        
        // TODO: crear los componentes que correspondan dentro del GridPane "grid"
        Parent buttons = createButtons();
        save.setDisable(true);

        VBox panel = new VBox(8, grid, buttons);
        this.getChildren().clear();
        this.getChildren().add(panel);
    }
    
    public void updateModelValues() {
        driverSpec.setDockerServerHost(dockerPath.getText());
        driverSpec.setDockerServerPort(Integer.parseInt(initialSSHPort.getText()));
    }

}
