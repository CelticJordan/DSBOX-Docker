package es.uvigo.esei.dsbox.gui;

import es.uvigo.esei.dsbox.core.config.DSBOXConfig;
import es.uvigo.esei.dsbox.gui.controllers.MainController;
import es.uvigo.esei.dsbox.gui.views.MainWindow;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
    
    public final static String DEFAULT_WORKING_DIRECTORY = DSBOXConfig.DEFAULT_DSBOX_HOME_DIR;
    
    private String workingDirectory = DEFAULT_WORKING_DIRECTORY;
    
    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }
    
    @Override
    public void init() throws Exception {
        super.init();
        List<String> parameters = this.getParameters().getRaw();
        if ((parameters != null) && !parameters.isEmpty()) {
            if ((parameters.size() == 2) && parameters.get(0).equals("--dsbox-home")) {
                this.workingDirectory = parameters.get(1);
            } else {
                System.err.println("ERROR: Unknown command line arguments.");
                System.err.println("\t " + parameters.toString());
                Platform.exit();
            }
        }
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        MainController mainController = new MainController(this.workingDirectory);
        MainWindow mainWindow = new MainWindow(mainController);
        mainController.setMainWindow(mainWindow);
        mainWindow.startUp();
        
        Scene mainScene = new Scene(mainWindow);
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("dsBOX");
        //primaryStage.setAlwaysOnTop(true);
        primaryStage.sizeToScene();
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest((WindowEvent we) -> {
            //mainWindow.finishApplication();
            mainWindow.shutDown();
        });
        primaryStage.show();
    }

    /**
     * Java main for when running without JavaFX launcher
     */
    public final static void main(String[] args) {
        
        launch(args);
    }
    
}
