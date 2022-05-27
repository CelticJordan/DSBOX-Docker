package es.uvigo.esei.dsbox.gui.editor;

import es.uvigo.esei.dsbox.core.model.HostType;
import es.uvigo.esei.dsbox.core.model.NetworkType;
import es.uvigo.esei.dsbox.core.model.SimulationSpec;
import es.uvigo.esei.dsbox.core.model.exceptions.DSBOXException;
import es.uvigo.esei.dsbox.core.model.execution.ExecutionSpec;
import es.uvigo.esei.dsbox.gui.controllers.Factory;
import es.uvigo.esei.dsbox.gui.editor.actions.EditAction;
import es.uvigo.esei.dsbox.gui.editor.actions.EditActionDestination;
import es.uvigo.esei.dsbox.gui.editor.events.CreateNodeEventProcessor;
import es.uvigo.esei.dsbox.gui.editor.events.CreateLinkEventProcessor;
import es.uvigo.esei.dsbox.gui.editor.events.RemoveEventProcessor;
import es.uvigo.esei.dsbox.gui.editor.events.SelectEventProcessor;
import es.uvigo.esei.dsbox.gui.editor.events.SimulationModeEventProcessor;
import es.uvigo.esei.dsbox.gui.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class NetworkEditor extends Parent implements EditActionDestination {

//    public static final Background DEFAULT_BACKGROUND = new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY));
    public static final Background DEFAULT_BACKGROUND = new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY));
    public static final int TOOL_ICON_SIZE = 60;
    public static final String DEFAULT_HOST_TOOL_ICON = "icons/HOST_icon.png";

    NetworkView networkView;
    Stack<EditAction> performedActions;
    Pane toolPanel;
    //Label messages;

    List<HostType> registeredHostTypes;

    SimulationSpec simulationSpec;

    List<NetworkEditorObserver> observers;

    private ToggleButton selectButton;
    private ToggleButton linkButton;
    private ToggleButton removeButton;
    private ToggleButton switchButton;
    private ToggleButton hubButton;
    private ToggleButton natButton;
    private ToggleButton bridgedButton;
    private List<ToggleButton> hostTypeButtons;
    private Region hostTypeSubPanel;
    private StackPane hostTypeSubPanelContainer;
    private Parent mainWindow;

    public NetworkEditor(Parent mainWindow) {
        performedActions = new Stack<>();
        this.mainWindow = mainWindow;
    }

    @Override
    public void processEditAction(EditAction action) {
        action.doAction();
        performedActions.push(action);
        notifyEditAction(action);
    }

    public void undoLasEditAction() {
        if (!performedActions.empty()) {
            EditAction lastAction = performedActions.pop();
            lastAction.undoAction();
            if (!performedActions.isEmpty()) {
                notifyEditAction(performedActions.peek());
            }
        }
    }

    public void clearActions() {
        performedActions.clear();
    }

    public void startUp() {
        initComponents();
        clearActions();
    }

    private void initComponents() {
        this.toolPanel = initToolPanel();
        this.networkView = initNetworkView();
        //this.messages = new Label(" blablabla ");

        ScrollPane scroll = new ScrollPane();
        // disble blue focus border
        scroll.setStyle("-fx-focus-color: transparent ; -fx-faint-focus-color: transparent ;");

        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scroll.setPrefSize(1100, 775);
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);
        scroll.setContent(networkView);

        HBox mainPanel = new HBox(toolPanel, scroll);
        mainPanel.setAlignment(Pos.BOTTOM_CENTER);

        this.getChildren().add(mainPanel);

    }

    public void clearObservers() {
        if (this.observers != null) {
            this.observers.clear();
        }
    }

    public void addObserver(NetworkEditorObserver observer) {
        if (this.observers == null) {
            this.observers = new ArrayList<>();
        }
        this.observers.add(observer);
    }

    public void notifySelectedNode(NodeView nodeView) {
        if (this.observers != null) {
            for (NetworkEditorObserver observer : this.observers) {
                observer.notifySelectedNode(nodeView);
            }
        }
    }

    public void notifyEditAction(EditAction editAction) {
        if (this.observers != null) {
            for (NetworkEditorObserver observer : this.observers) {
                observer.nofityEditAction(editAction);
            }
        }
    }

    public void activateEditionMode() {
        enableAllButtons();
        selectButton.setSelected(true);
        networkView.setDelegate(new SelectEventProcessor(networkView, this));
        performedActions.clear();
        // zoom a 1.0
    }

    public void activateSimulationMode(ExecutionSpec executionSpec) throws DSBOXException {
        disableAllButtons();
        selectButton.setDisable(false);
        selectButton.setSelected(true);
        networkView.setDelegate(new SimulationModeEventProcessor(networkView, this, Factory.createConnectionManager(executionSpec), mainWindow));

    }

    private void enableAllButtons() {
        selectButton.setDisable(false);
        linkButton.setDisable(false);
        removeButton.setDisable(false);

        hubButton.setDisable(false);
        switchButton.setDisable(false);
        natButton.setDisable(false);
        bridgedButton.setDisable(false);

        if (hostTypeButtons != null) {
            for (ToggleButton hostTypeButton : hostTypeButtons) {
                hostTypeButton.setDisable(false);
            }
        }
    }

    private void disableAllButtons() {
        selectButton.setDisable(true);
        linkButton.setDisable(true);
        removeButton.setDisable(true);

        hubButton.setDisable(true);
        switchButton.setDisable(true);
        natButton.setDisable(true);
        bridgedButton.setDisable(true);

        if (hostTypeButtons != null) {
            for (ToggleButton hostTypeButton : hostTypeButtons) {
                hostTypeButton.setDisable(true);
            }
        }
    }

    public void clear() {
        this.networkView.clear();
        this.selectButton.setSelected(true);
    }

    public void setSimulationSpec(SimulationSpec simulationSpec) {
        this.simulationSpec = simulationSpec;
        this.networkView.clear();
        this.networkView.loadSimulationSpec(simulationSpec);
        this.selectButton.setSelected(true);
    }

    public SimulationSpec getSimulationSpec() {
        this.simulationSpec = this.networkView.retrieveSimulationSpec();
        return this.simulationSpec;
    }

    public void setRegisteredHostTypes(List<HostType> registeredHostTypes) {
        this.registeredHostTypes = registeredHostTypes;
        if (this.hostTypeButtons != null) {
            refreshHostTypeSubPanel();
        }
    }

    public List<HostType> getRegisteredHostTypes() {
        return this.registeredHostTypes;
    }

    public void addRegisteredHostType(HostType hostType) {
        if (this.registeredHostTypes == null) {
            this.registeredHostTypes = new ArrayList<>();
        }
        this.registeredHostTypes.add(hostType);
    }

    private NetworkView initNetworkView() {
        NetworkView networkView = new NetworkView(this);
        //networkView.setViewDimensions(950, 750);
        networkView.setSnapToGrid(true);

        return networkView;
    }

    private Pane initToolPanel() {
        ToggleGroup toggleGroup = new ToggleGroup();

        GridPane toolBar1 = new GridPane();
        toolBar1.setBackground(DEFAULT_BACKGROUND);
        toolBar1.setHgap(3.0);
        toolBar1.setVgap(3.0);
        toolBar1.setPadding(new Insets(3));

//        toolBar1.add(new Separator(Orientation.HORIZONTAL), 0, 0, 2, 1);
        Label label1 = new Label("Edit tools");
        label1.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize()));
        toolBar1.add(label1, 0, 1, 2, 1);

        selectButton = initSelectButton();
        selectButton.setToggleGroup(toggleGroup);
        toolBar1.add(selectButton, 0, 2);

        linkButton = initLinkButton();
        linkButton.setToggleGroup(toggleGroup);
        toolBar1.add(linkButton, 1, 2);

        removeButton = initRemoveButton();
        removeButton.setToggleGroup(toggleGroup);
        toolBar1.add(removeButton, 0, 3);

        //Slider s = new Slider();
        //s.setPrefWidth(100);
        //HBox kk = new HBox(new Button("-"), s, new Button("+"));
        //kk.setAlignment(Pos.CENTER);
        //toolBar1.add(kk, 0, 4, 2, 1);
        toolBar1.add(new Separator(Orientation.HORIZONTAL), 0, 5, 2, 1);
        Label label2 = new Label("Network devices");
        label2.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize()));
        toolBar1.add(label2, 0, 6, 2, 1);

        switchButton = initSWITCHButton();
        switchButton.setToggleGroup(toggleGroup);
        toolBar1.add(switchButton, 0, 7);

        hubButton = initHUBButton();
        hubButton.setToggleGroup(toggleGroup);
        //toolBar1.add(hubButton, 1, 7);

        natButton = initNATButton();
        natButton.setToggleGroup(toggleGroup);
        //toolBar1.add(natButton, 0, 8);

        bridgedButton = initBRIDGEButton();
        bridgedButton.setToggleGroup(toggleGroup);
        toolBar1.add(bridgedButton, 1, 7);

        hostTypeSubPanel = createHostTypeButtons();
        hostTypeSubPanelContainer = new StackPane(hostTypeSubPanel);
        toolBar1.add(new Separator(Orientation.HORIZONTAL), 0, 9, 2, 1);
        Label label3 = new Label("Host types");
        label3.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize()));
        toolBar1.add(label3, 0, 10, 2, 1);
        toolBar1.add(hostTypeSubPanelContainer, 0, 11, 2, 3);

//        toolBar.add(new Separator(Orientation.HORIZONTAL), 0, 5, 2, 1);
//        for (SNodeTypeInfo nodeTypeInfo : DEFAULT_NODE_TYPES) {
//            ToggleButton newNodeButton = initNewNodeButton(nodeTypeInfo);
//            newNodeButton.setToggleGroup(toggleGroup);
//            toolBar.getItems().add(newNodeButton);
//        }
//        Pane toolPanel = new VBox(toolBar1, new Separator(), toolBar2, new Separator());
//        return toolPanel;
//        toolPanel.setFillHeight(true);
//        VBox toolPanel = new VBox(toolBar1, new Separator(Orientation.HORIZONTAL), new Label("Host types"), hostTypeSubPanel);
//        return toolPanel;
        return toolBar1;

    }

    private void refreshHostTypeSubPanel() {
        hostTypeSubPanel = createHostTypeButtons();
        hostTypeSubPanelContainer.getChildren().clear();
        hostTypeSubPanelContainer.getChildren().add(hostTypeSubPanel);
    }

    private ToggleButton initSelectButton() {
        ToggleButton tb = createToggleButton("Select", "Select tool", "icons/SELECT_icon.png", TOOL_ICON_SIZE);
        tb.setOnAction((ActionEvent ae) -> {
            this.networkView.setDelegate(new SelectEventProcessor(this.networkView, this));
        });
        return tb;
    }

    private ToggleButton initLinkButton() {
        ToggleButton tb = createToggleButton("Link", "Link creation tool", "icons/NEW_LINK_icon.png", TOOL_ICON_SIZE);
        tb.setOnAction((ActionEvent ae) -> {
            this.networkView.setDelegate(new CreateLinkEventProcessor(this.networkView, this));
        });
        return tb;

    }

    private ToggleButton initRemoveButton() {
        ToggleButton tb = createToggleButton("Remove", "Remove tool", "icons/REMOVE_icon.png", TOOL_ICON_SIZE);
        tb.setOnAction((ActionEvent ae) -> {
            this.networkView.setDelegate(new RemoveEventProcessor(this.networkView, this));
        });
        return tb;
    }

    private ToggleButton initNATButton() {
        ToggleButton tb = createToggleButton("NAT", "WAN connection (NAT mode)", "icons/NETWORK_NAT_icon.png", TOOL_ICON_SIZE);
        tb.setOnAction((ActionEvent ae) -> {
            this.networkView.setDelegate(new CreateNodeEventProcessor(this.networkView, this, NetworkType.EXTERNAL_NATTED));
        });
        return tb;
    }

    private ToggleButton initBRIDGEButton() {
        ToggleButton tb = createToggleButton("Bridge", "WAN connection (Bridged mode)", "icons/NETWORK_BRIDGE_icon.png", TOOL_ICON_SIZE);
        tb.setOnAction((ActionEvent ae) -> {
            this.networkView.setDelegate(new CreateNodeEventProcessor(this.networkView, this, NetworkType.EXTERNAL_BRIDGED));
        });
        return tb;
    }

    private ToggleButton initSWITCHButton() {
        ToggleButton tb = createToggleButton("Switch", "LAN connection (Switch mode)", "icons/NETWORK_SWITCH_icon.png", TOOL_ICON_SIZE);
        tb.setOnAction((ActionEvent ae) -> {
            this.networkView.setDelegate(new CreateNodeEventProcessor(this.networkView, this, NetworkType.SWITCH));
        });
        return tb;
    }

    private ToggleButton initHUBButton() {
        ToggleButton tb = createToggleButton("Hub", "LAN connection (Hub mode)", "icons/NETWORK_HUB_icon.png", TOOL_ICON_SIZE);
        tb.setOnAction((ActionEvent ae) -> {
            this.networkView.setDelegate(new CreateNodeEventProcessor(this.networkView, this, NetworkType.HUB));
        });
        return tb;
    }

//    private ToggleButton initNewNodeButton(SNodeTypeInfo nodeTypeInfo) {
//        ToggleButton tb = new ToggleButton();
//        tb.setGraphic(new VBox(new ImageView(new Image("file:PC.png")), new Label("Create "+nodeTypeInfo.getLabel())));
//        tb.setTextAlignment(TextAlignment.CENTER);
//        tb.setPrefSize(32, 32);
//        tb.setMinSize(32,32);
//        tb.setMaxSize(32,32);
//                
//        return tb;
//    }
    private ToggleButton createToggleButton(String label, String description, String iconPath, int size) {
        ToggleButton tb = new ToggleButton();
        ImageView imageView = new ImageView(new Image(Utils.loadResource(iconPath), size, size, true, true));

        tb.setGraphic(imageView);
        tb.setTooltip(new Tooltip(description));
        tb.setTextAlignment(TextAlignment.CENTER);

        return tb;

    }

    private Region createHostTypeButtons() {
        ScrollPane panel = new ScrollPane();
        panel.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        panel.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        panel.setBackground(DEFAULT_BACKGROUND);
        hostTypeButtons = new ArrayList<>();

        GridPane inside = new GridPane();
        inside.setBackground(DEFAULT_BACKGROUND);
        inside.setHgap(3.0);
        inside.setVgap(3.0);
        inside.setPadding(new Insets(3));

        int buttonCount = 0;
        int columnIdx, rowIdx;

        for (HostType hostType : registeredHostTypes) {
            ToggleButton tb = new ToggleButton();
            VBox buttonGraphics = new VBox();
            buttonGraphics.setAlignment(Pos.CENTER);
            buttonGraphics.getChildren().add(Utils.createHostTypeImage(hostType, DEFAULT_HOST_TOOL_ICON, TOOL_ICON_SIZE));

            Label hostTypeLabel = new Label(hostType.getName());
            hostTypeLabel.setAlignment(Pos.CENTER);
            hostTypeLabel.setFont(Font.font(0.8 * Font.getDefault().getSize()));
            hostTypeLabel.setPrefWidth(TOOL_ICON_SIZE - 2);

            buttonGraphics.getChildren().add(hostTypeLabel);

            tb.setGraphic(buttonGraphics);
            tb.setTooltip(new Tooltip(hostType.getDescription()));
            tb.setTextAlignment(TextAlignment.CENTER);
            tb.setOnAction((ActionEvent ae) -> {
                this.networkView.setDelegate(new CreateNodeEventProcessor(this.networkView, this, hostType));
            });

            hostTypeButtons.add(tb);
            rowIdx = buttonCount / 2;
            columnIdx = buttonCount % 2;
            inside.add(tb, columnIdx, rowIdx);
            buttonCount++;
        }

        panel.setContent(inside);
        return panel;
    }

}
