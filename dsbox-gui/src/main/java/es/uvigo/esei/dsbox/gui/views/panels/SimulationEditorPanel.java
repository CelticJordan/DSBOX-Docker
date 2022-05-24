package es.uvigo.esei.dsbox.gui.views.panels;

import es.uvigo.esei.dsbox.core.model.SimulationSpec;
import java.text.SimpleDateFormat;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SimulationEditorPanel extends ElementPanel {

    private SimulationSpec simulationSpec;

    private TextField name;
    private TextField description;
    private TextField author;

    public SimulationEditorPanel(SimulationSpec simulationSpec) {
        super();
        this.simulationSpec = simulationSpec;
        initComponents();
    }

    public SimulationSpec getSimulationSpec() {
        return simulationSpec;
    }

    public void setSimulationSpec(SimulationSpec simulationSpec) {
        this.simulationSpec = simulationSpec;
        initComponents();
    }

    @Override
    protected void initComponents() {
        GridPane grid = new GridPane();
        grid.setHgap(4);
        grid.setVgap(4);
        grid.setPadding(new Insets(8));
        grid.getColumnConstraints().addAll(new ColumnConstraints(100, 120, 200, Priority.ALWAYS, HPos.LEFT, true),
                new ColumnConstraints(200, 250, 400, Priority.SOMETIMES, HPos.LEFT, true));

        name = new TextField(simulationSpec.getName());
        name.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 0, "Name :", name);

        description = new TextField(simulationSpec.getDescription());
        description.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 1, "Description :", description);

        author = new TextField(simulationSpec.getAuthor());
        author.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 2, "Author :", author);

        grid.add(new Separator(Orientation.HORIZONTAL), 0, 3, 2, 1);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy, hh:mm:ss");
        Label creationDate = new Label(formatter.format(simulationSpec.getCreationDate()));
        addPropertyControl(grid, 4, "Creation date :", creationDate);
        
        String simulationPath = "< not set >";
        if (simulationSpec.getSimulationDir() != null) {
            simulationPath = simulationSpec.getSimulationDir();
        }
        addPropertyControl(grid, 5, "Simulation path :", new Label(simulationPath));

        if (simulationSpec.getNetworkSpec() == null) {
            grid.add(new Separator(Orientation.HORIZONTAL), 0, 6, 2, 1);
        } else {
            int numberOfHosts = 0;
            int numberOfNetworks = 0;

            if (simulationSpec.getNetworkSpec().getHosts() != null) {
                numberOfHosts = simulationSpec.getNetworkSpec().getHosts().size();
            }
            if (simulationSpec.getNetworkSpec().getNetworks() != null) {
                numberOfNetworks = simulationSpec.getNetworkSpec().getNetworks().size();
            }
            addPropertyControl(grid, 7, "# of hosts :", new Label(Integer.toString(numberOfHosts)));
            addPropertyControl(grid, 8, "# of hosts :", new Label(Integer.toString(numberOfNetworks)));
            grid.add(new Separator(Orientation.HORIZONTAL), 0, 9, 2, 1);
        }

        Parent buttons = createButtons("Update", "Cancel", false);

        save.setDisable(true);

        VBox panel = new VBox(8, grid, buttons);

        this.getChildren().clear();
        this.getChildren().add(panel);
    }

    @Override
    public void updateModelValues() {
        simulationSpec.setName(name.getText());
        simulationSpec.setDescription(description.getText());
        simulationSpec.setAuthor(author.getText());
    }
}
