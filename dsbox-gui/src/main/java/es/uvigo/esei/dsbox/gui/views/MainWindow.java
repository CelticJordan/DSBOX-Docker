package es.uvigo.esei.dsbox.gui.views;

import es.uvigo.esei.dsbox.core.manager.VMDriverException;
import es.uvigo.esei.dsbox.core.model.SimulationSpec;
import es.uvigo.esei.dsbox.core.model.exceptions.DSBOXException;
import es.uvigo.esei.dsbox.core.model.execution.ExecutionSpec;
import es.uvigo.esei.dsbox.core.model.execution.VMDriverSpec;
import es.uvigo.esei.dsbox.docker.execution.DockerDriverSpec;
import es.uvigo.esei.dsbox.gui.controllers.Factory;
import es.uvigo.esei.dsbox.gui.controllers.MainController;
import es.uvigo.esei.dsbox.gui.editor.NetworkEditor;
import es.uvigo.esei.dsbox.gui.editor.NetworkEditorObserver;
import es.uvigo.esei.dsbox.gui.editor.NodeView;
import es.uvigo.esei.dsbox.gui.editor.actions.EditAction;
import es.uvigo.esei.dsbox.gui.views.panels.RegisteredHostTypesPanel;
import es.uvigo.esei.dsbox.gui.views.panels.SimulationEditorPanel;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainWindow extends Parent implements NetworkEditorObserver {

    private MainController controller;
    private NetworkEditor editor;
    private Label notifications;

    private String simulationSpecFilename;

    private boolean inEditionMode = true;
    private boolean isEdited = false;

    private CommandPair newCommand;
    private CommandPair loadCommand;
    private CommandPair saveCommand;
    private CommandPair saveAsCommand;
    private CommandPair exitCommand;

    private CommandPair undoCommand;
//    private CommandPair copyCommand;
//    private CommandPair pasteCommand;
    private CommandPair editPropertiesCommand;

    private CommandPair startCommand;
    private CommandPair pauseCommand;
    private CommandPair resumeCommand;
    private CommandPair stopCommand;
    private CommandPair exportCommand;
    private CommandPair importCommand;

    private CommandPair hostTypesCommand;
    private CommandPair configureCommand;
    //private CommandPair vboxDriverCommand;

    private CommandPair helpCommand;
    private CommandPair aboutCommand;
    private SimulationSpec simulationSpec;

    public MainWindow(MainController mainController) {
        this.controller = mainController;
    }

    public void startUp() {

        if (!controller.isValidConfiguration()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("DSBOX start up failed.");
            alert.setContentText("DSBOX working directory at " + controller.getWorkingDirectory() + " does not exits or is corrupted.\n"
                    + "Create it or regenerate it ?");
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                try {
                    controller.createWorkingDirectory();
                } catch (DSBOXException ex) {
                    System.err.println("ERROR: Unable to create DSBOX working directory at " + controller.getWorkingDirectory());
                    ex.printStackTrace();
                    Platform.exit();
                }
            } else {
                Platform.exit();
            }
        }
        try {
            this.controller.startUp();
        } catch (DSBOXException | VMDriverException ex) {
            ex.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("DSBOX start up failed.");
            alert.setContentText("DSBOX start up failed with message \n\t" + ex.getMessage() + "\n\nApplication will halt.");
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(ButtonType.OK);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();

            finishApplication();
            System.exit(0);
        }
        this.initComponents();
    }

    public void shutDown() {
        if (inEditionMode) {
            saveSimulation();
        } else if (isSimulationRunning()) {
            stopSimulation();
        }
        finishApplication();
    }

    private void initComponents() {
        createCommandControls();
        Parent content = createContent();
        this.getChildren().add(content);

        this.editor.addObserver(this);
        this.editor.setRegisteredHostTypes(controller.getRegisteredHostTypes());
        this.editor.startUp();

        this.simulationSpec = new SimulationSpec();

        this.activateEditionMode();
        this.startCommand.disable();
        this.editPropertiesCommand.disable();

        editor.setDisable(true);
    }

    private void createCommandControls() {
        newCommand = CommandPair.create("New", "icons/NEW.png", (ActionEvent ae) -> this.newSimulation());
        loadCommand = CommandPair.create("Load", "icons/LOAD.png", (ActionEvent ae) -> this.loadSimulation());
        saveCommand = CommandPair.create("Save", "icons/SAVE.png", (ActionEvent ae) -> this.saveSimulation());
        saveAsCommand = CommandPair.createWithoutButton("Save as", (ActionEvent ae) -> this.saveAsSimulation());
        exitCommand = CommandPair.create("Exit", "icons/EXIT.png", (ActionEvent ae) -> this.exit());

        undoCommand = CommandPair.create("Undo", "icons/UNDO.png", (ActionEvent ae) -> this.undo());
//        copyCommand = CommandPair.createWithoutButton("Copy", (ActionEvent ae) -> this.copy());
//        pasteCommand = CommandPair.createWithoutButton("Paste", (ActionEvent ae) -> this.paste());
        editPropertiesCommand = CommandPair.create("Edit", "icons/EDIT.png", (ActionEvent ae) -> this.editSimulationSpec());

        startCommand = CommandPair.create("Start", "icons/START.png", (ActionEvent ae) -> this.startSimulation());
        pauseCommand = CommandPair.create("Pause", "icons/PAUSE.png", (ActionEvent ae) -> this.pauseSimulation());
        resumeCommand = CommandPair.create("Resume", "icons/RESUME.png", (ActionEvent ae) -> this.resumeSimulation());
        stopCommand = CommandPair.create("Stop", "icons/STOP.png", (ActionEvent ae) -> this.stopSimulation());
        exportCommand = CommandPair.create("Export", "icons/EXPORT.png", (ActionEvent ae) -> this.exportSimulation());
        importCommand = CommandPair.create("Import", "icons/IMPORT.png", (ActionEvent ae) -> this.importSimulation());

        hostTypesCommand = CommandPair.createWithoutButton("Host types", (ActionEvent ae) -> this.manageHostTypes());
        configureCommand = CommandPair.create("Configure", "icons/CONFIG.png", (ActionEvent ae) -> this.configVMDriver());
//        vboxDriverCommand = CommandPair.create("VBox driver", "icons/CONFIG.png", (ActionEvent ae) -> this.configVMDriver());

        helpCommand = CommandPair.createWithoutButton("Help", (ActionEvent ae) -> this.help());
        aboutCommand = CommandPair.createWithoutButton("About", (ActionEvent ae) -> this.about());
    }

    private Parent createContent() {
        editor = new NetworkEditor(this);
        notifications = new Label("DSbox.");

        VBox pane = new VBox();
        pane.setAlignment(Pos.TOP_LEFT);
        pane.setStyle("-fx-focus-color: transparent ; -fx-faint-focus-color: transparent ;");
        pane.getChildren().add(createMenuBar());
        pane.getChildren().add(createToolBar());

        pane.getChildren().add(editor);

        ToolBar spacer = new ToolBar();
        spacer.getItems().add(notifications);
        pane.getChildren().add(spacer);
        return pane;

    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(newCommand.getMenuItem(),
                loadCommand.getMenuItem(),
                saveCommand.getMenuItem(),
                saveAsCommand.getMenuItem(),
                new SeparatorMenuItem(),
                exitCommand.getMenuItem());

        Menu editMenu = new Menu("Edit");
        editMenu.getItems().addAll(undoCommand.getMenuItem(), new SeparatorMenuItem(), editPropertiesCommand.getMenuItem());
//                copyCommand.getMenuItem(),
//                pasteCommand.getMenuItem());

        Menu simulationMenu = new Menu("Simulation");
        simulationMenu.getItems().addAll(startCommand.getMenuItem(),
                pauseCommand.getMenuItem(),
                resumeCommand.getMenuItem(),
                stopCommand.getMenuItem(),
                new SeparatorMenuItem(),
                exportCommand.getMenuItem(),
                importCommand.getMenuItem());

        Menu configMenu = new Menu("Configure");
        configMenu.getItems().addAll(hostTypesCommand.getMenuItem(),
                configureCommand.getMenuItem());
        //vboxDriverCommand.getMenuItem());

        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().addAll(helpCommand.getMenuItem(), aboutCommand.getMenuItem());

        menuBar.getMenus().addAll(fileMenu, editMenu, simulationMenu, configMenu, helpMenu);

        return menuBar;
    }

    private ToolBar createToolBar() {
        ToolBar toolBar = new ToolBar();

        toolBar.getItems().addAll(newCommand.getButton(), loadCommand.getButton(), saveCommand.getButton());
        toolBar.getItems().add(new Separator(Orientation.VERTICAL));
        toolBar.getItems().add(editPropertiesCommand.getButton());
        toolBar.getItems().add(new Separator(Orientation.VERTICAL));
        toolBar.getItems().add(undoCommand.getButton());
        toolBar.getItems().add(new Separator(Orientation.VERTICAL));
        toolBar.getItems().addAll(startCommand.getButton(), pauseCommand.getButton(), resumeCommand.getButton(), stopCommand.getButton());
        toolBar.getItems().add(new Separator(Orientation.VERTICAL));
        toolBar.getItems().addAll(exportCommand.getButton(), importCommand.getButton());
        toolBar.getItems().add(new Separator(Orientation.VERTICAL));
        toolBar.getItems().addAll(configureCommand.getButton());
        toolBar.getItems().add(new Separator(Orientation.VERTICAL));
        toolBar.getItems().addAll(exitCommand.getButton());

        return toolBar;
    }

    private boolean inEditionMode() {
        return inEditionMode;
    }

    private boolean inSimulationMode() {
        return !inEditionMode;
    }

    private void activateEditionMode() {
        this.inEditionMode = true;
        this.isEdited = false;
        editor.activateEditionMode();

        notifications.setText("DSbox [edition mode].");

        startCommand.enable();
        pauseCommand.disable();
        resumeCommand.disable();
        stopCommand.disable();
        exportCommand.disable();
        importCommand.enable();

        newCommand.enable();
        saveCommand.disable();
        saveAsCommand.disable();
        loadCommand.enable();

        undoCommand.disable();
//        copyCommand.disable();
//        pasteCommand.disable();
        editPropertiesCommand.enable();

        hostTypesCommand.enable();
        configureCommand.enable();
//        vboxDriverCommand.enable();

        helpCommand.enable();
        aboutCommand.enable();

        exitCommand.enable();

//        editor.setInEditionMode();
//        editor.setEditionListener(this);  -> habitar saveComand y undoCommand cuando haya cambios
//                                          -> habilitar copyCommand cuando haya selección
//                                          -> habilitar pasteCommand cuando se haya copiado (y deshabilitar copyCommand)
    }

    private void activateSimulationMode(ExecutionSpec executionSpec) throws DSBOXException {
        System.out.println(executionSpec.getVmDriverSpec() instanceof DockerDriverSpec);
        this.inEditionMode = false;
        this.isEdited = false;
        editor.activateSimulationMode(executionSpec);

        notifications.setText("DSbox [simulation mode].");

        startCommand.disable();
        pauseCommand.enable();
        resumeCommand.disable();
        stopCommand.enable();
        exportCommand.enable();
        importCommand.disable();

        newCommand.disable();
        saveCommand.disable();
        saveAsCommand.disable();
        loadCommand.disable();

        undoCommand.disable();
//        copyCommand.disable();
//        pasteCommand.disable();
        editPropertiesCommand.enable();

        hostTypesCommand.disable();
        configureCommand.disable();
//        vboxDriverCommand.disable();

        helpCommand.enable();
        aboutCommand.enable();

        exitCommand.enable();

//        editor.setInViewMode();
//        editor.attachViewModeListener();
    }

    private Pane createNotificationsPanel() {
        return new Pane();
    }

    private void newSimulation() {
        if (inEditionMode && confirmUnsavedChanges()) {
            // New
            simulationSpecFilename = null;

            simulationSpec = new SimulationSpec();
            openSimulationSpecEditor();

            if (editor.isDisable()) {
                editor.setDisable(false);
            }
            editor.clear();
            editor.clearActions();

            activateEditionMode();
            undoCommand.disable();
            saveAsCommand.disable();
        }
    }

    public boolean confirmUnsavedChanges() {
        if (isEdited) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Unsaved changes.");
            alert.setContentText("There are non saved changes.\nContinue with this operation ?");
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
            return (alert.getResult() == ButtonType.YES);
        } else {
            return true;
        }
    }

    private void loadSimulation() {
        if (inEditionMode && confirmUnsavedChanges()) {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Load DSBOX simulation spec");
            chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("DSBOX xml file", "xml"));
            chooser.setInitialDirectory(new File(controller.getWorkingDirectory()));
            File selectedFile = chooser.showOpenDialog(this.getScene().getWindow());
            if (selectedFile != null) {
                simulationSpecFilename = selectedFile.getAbsolutePath();
                try {
                    simulationSpec = controller.loadSimulationSpec(simulationSpecFilename);

                    if (editor.isDisable()) {
                        editor.setDisable(false);
                    }

                    editor.setSimulationSpec(simulationSpec);
                    editor.clearActions();

                    activateEditionMode();
                    undoCommand.disable();
                    saveAsCommand.enable();
                } catch (DSBOXException ex) {
                    Alert alert = new Alert(AlertType.ERROR,
                            "Error loading Simulation from file " + simulationSpecFilename,
                            ButtonType.OK);
                    alert.setTitle("Error");
                    alert.setHeaderText("Load error");
                    alert.showAndWait();
                }
            }
        }
    }

    private void saveAsSimulation() {
        if (!isEdited) {
            Alert alert = new Alert(AlertType.INFORMATION,
                    "There are no changes to be saved.",
                    ButtonType.OK);
            alert.setHeaderText("Information");
            alert.setTitle("Information");
            alert.showAndWait();
        } else {
            simulationSpecFilename = chooseDestinationFile();
            if (simulationSpecFilename != null) {
                saveSimulationSpecToFile(simulationSpecFilename);
                isEdited = false;
                editor.clearActions();
                undoCommand.disable();
                startCommand.enable();;
            }
        }
    }

    private String chooseDestinationFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save DSBOX simulation spec");
        chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("DSBOX xml file", "xml"));
        chooser.setInitialDirectory(new File(controller.getWorkingDirectory()));
        if (simulationSpecFilename != null) {
            chooser.setInitialFileName(simulationSpecFilename);
        }
        File selectedFile = chooser.showSaveDialog(this.getScene().getWindow());
        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        } else {
            return null;
        }
    }

    private void saveSimulationSpecToFile(String filename) {
        try {
            retrieveSimulationSpecFromEditor();
            controller.saveSimulationSpec(simulationSpec, filename);
        } catch (DSBOXException ex) {
            Alert alert = new Alert(AlertType.ERROR,
                    "Error writing Simulation to file " + filename,
                    ButtonType.OK);
            alert.setTitle("Error");
            alert.setHeaderText("Wrting error");
            alert.showAndWait();
            ex.printStackTrace();
        }
    }

    private void saveSimulation() {
        if (!isEdited) {
            Alert alert = new Alert(AlertType.INFORMATION,
                    "There are no changes to be saved.",
                    ButtonType.OK);
            alert.setHeaderText("Information");
            alert.setTitle("Information");
            alert.showAndWait();
        } else {
            if (simulationSpecFilename == null) {
                simulationSpecFilename = chooseDestinationFile();
            }
            if (simulationSpecFilename != null) {
                saveSimulationSpecToFile(simulationSpecFilename);
                isEdited = false;
                editor.clearActions();
                undoCommand.disable();
                startCommand.enable();;
            }
        }
    }

    private void exit() {
        if (inEditionMode) {
            if (confirmUnsavedChanges()) {
                finishApplication();
            }
        } else if (isSimulationRunning()) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Simulation is running.");
            alert.setContentText("Current Simulation is running. \n Confirm exit?");
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(ButtonType.NO, ButtonType.YES);
            alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                stopSimulation();
                finishApplication();
            }
        }
    }

    private boolean simulationIsRunning = false;
    private boolean simulationIsPaused = false;

    private boolean isSimulationRunning() {
        return simulationIsRunning;
    }

    private boolean isSimulationPaused() {
        return simulationIsPaused;
    }

    private boolean isSimulationNotStarted() {
        return (!simulationIsRunning && !simulationIsPaused);
    }

    private void startSimulation() {
        if (inEditionMode && isEdited) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Unsaved changes.");
            alert.setContentText("There are not saved changes.\n Can not continue with this operation.");
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(ButtonType.OK);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        } else {
            retrieveSimulationSpecFromEditor();
            try {
                controller.startSimulation(simulationSpec);
                simulationIsRunning = true;
                simulationIsPaused = false;

                startCommand.disable();
                pauseCommand.enable();
                resumeCommand.disable();
                stopCommand.enable();
                
                activateSimulationMode(controller.getExecutionSpec());

            } catch (VMDriverException | DSBOXException ex) {
                ex.printStackTrace();
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Simulation error");
                alert.setHeaderText("Simulation error (start)");
                alert.setContentText("Unable to start Simulation, with error\n\t" + ex.getMessage());
                alert.getButtonTypes().clear();
                alert.getButtonTypes().add(ButtonType.OK);
                alert.showAndWait();

                activateEditionMode();
                startCommand.enable();
                pauseCommand.disable();
                resumeCommand.disable();
                stopCommand.disable();

                simulationIsRunning = false;
                simulationIsPaused = false;
            }
        }
    }

    private void pauseSimulation() {
        if (inSimulationMode() && isSimulationRunning() && !isSimulationPaused()) {
            try {
                controller.pauseSimulation(simulationSpec);
                simulationIsRunning = true;
                simulationIsPaused = true;

                startCommand.disable();
                pauseCommand.disable();
                resumeCommand.enable();
                stopCommand.enable();
            } catch (VMDriverException ex) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Simulation error");
                alert.setHeaderText("Simulation error (pause)");
                alert.setContentText("Unable to pause Simulation, with error\n\t" + ex.getMessage());
                alert.getButtonTypes().clear();
                alert.getButtonTypes().add(ButtonType.OK);
                alert.showAndWait();

                simulationIsPaused = false;
            }
        }
    }

    private void resumeSimulation() {
        if (inSimulationMode() && isSimulationRunning() && isSimulationPaused()) {
            try {
                controller.resumeSimulation(simulationSpec);
                simulationIsRunning = true;
                simulationIsPaused = false;

                startCommand.disable();
                pauseCommand.enable();
                resumeCommand.disable();
                stopCommand.enable();
            } catch (VMDriverException ex) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Simulation error");
                alert.setHeaderText("Simulation error (resume)");
                alert.setContentText("Unable to resume Simulation, with error\n\t" + ex.getMessage());
                alert.getButtonTypes().clear();
                alert.getButtonTypes().add(ButtonType.OK);
                alert.showAndWait();

                simulationIsPaused = true;
            }
        }
    }

    private void stopSimulation() {
        if (inSimulationMode() && isSimulationRunning()) {
            try {
                controller.stopSimulation(simulationSpec);
                simulationIsRunning = false;
                simulationIsPaused = false;

                activateEditionMode();

            } catch (VMDriverException ex) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Simulation error");
                alert.setHeaderText("Simulation error (stop)");
                alert.setContentText("Unable to stop Simulation, with error\n\t" + ex.getMessage());
                alert.getButtonTypes().clear();
                alert.getButtonTypes().add(ButtonType.OK);
                alert.showAndWait();

                simulationIsRunning = true;
            }
        }

    }

    private void undo() {
        editor.undoLasEditAction();
    }

//    private CommandPair copy() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    private CommandPair paste() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    private void exportSimulation() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("Unavaible command");
        alert.setContentText("Simulation export option is not available :-( ");

        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.OK);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    private void importSimulation() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("Unavaible command");
        alert.setContentText("Simulation import option is not available :-( ");

        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.OK);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();

    }

    private void manageHostTypes() {

        // Temporal
        Stage stage = new Stage();
        stage.setTitle("Registered HostTypes");

        RegisteredHostTypesPanel panel = new RegisteredHostTypesPanel(controller, stage);
        stage.setScene(new Scene(panel));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(this.getScene().getWindow());
        stage.setResizable(false);
        stage.showAndWait();

        editor.setRegisteredHostTypes(controller.getRegisteredHostTypes());

    }

    private void configVMDriver() {
        // Temporal

        VMDriverSpec spec = controller.getVMDriverSpec();

        Stage stage = new Stage();
        stage.setTitle("Docker driver configuration");

        /**
         * ***** *
         * VBoxDriverSpecEditorPanel editPanel = new
         * VBoxDriverSpecEditorPanel(spec);
         * editPanel.setCancelActionHandler((ActionEvent ae) -> { stage.close();
         * }); editPanel.setSaveActionHandler((ActionEvent ae) -> {
         * editPanel.updateModelValues(); stage.close(); });
         *
         ****
         */
        try {
            Parent editPanel = Factory.createVMDriverSpecPanel(spec, stage);
            stage.setScene(new Scene(editPanel));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(this.getScene().getWindow());
            stage.setResizable(false);
            stage.showAndWait();

        } catch (DSBOXException ex) {
            Logger.getLogger(MainWindow.class
                    .getName()).log(Level.SEVERE, "Unable to create Edit Panel for current VMDriver", ex);
        }
    }

//    private CommandPair manageProperties() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    private void help() {
        Alert alert = new Alert(AlertType.INFORMATION, "No available help at this moment :-(.", ButtonType.OK);
        alert.setHeaderText("Help");
        alert.showAndWait();
    }

    private void about() {
        Alert alert = new Alert(AlertType.INFORMATION, "DSBOX. 2015-2022", ButtonType.OK);
        alert.setHeaderText("About DSBOX");
        alert.showAndWait();
    }

    @Override
    public void notifySelectedNode(NodeView selectedNode) {
        if (inEditionMode()) {
            // anotate selectedNode
//            this.copyCommand.enable();
        }
    }

    @Override
    public void nofityEditAction(EditAction lasteEditAction) {
        if (inEditionMode()) {
            this.isEdited = true;
            this.undoCommand.enable();
            this.saveCommand.enable();
            this.saveAsCommand.enable();
        }
    }

    public void finishApplication() {
        if (controller != null) {
            try {
                controller.shutDown();
            } catch (VMDriverException ex) {
            }
        }
        Platform.exit();
    }

    private void editSimulationSpec() {
        openSimulationSpecEditor();
    }

    private void openSimulationSpecEditor() {
        if (simulationSpec != null) {
            Stage stage = new Stage();
            stage.setTitle("Properties for current Simulation");

            SimulationEditorPanel editPanel = new SimulationEditorPanel(simulationSpec);
            editPanel.setCancelActionHandler((ActionEvent ae) -> {
                stage.close();
            });
            editPanel.setSaveActionHandler((ActionEvent ae) -> {
                editPanel.updateModelValues();
                stage.close();
            });

            stage.setScene(new Scene(editPanel));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(this.getScene().getWindow());
            stage.setResizable(false);
            stage.showAndWait();
        }
    }

    private void retrieveSimulationSpecFromEditor() {
        SimulationSpec editedSimulationSpec = editor.getSimulationSpec();
        simulationSpec.setNetworkSpec(editedSimulationSpec.getNetworkSpec());
        simulationSpec.setGraphicalSpec(editedSimulationSpec.getGraphicalSpec());
    }
}
