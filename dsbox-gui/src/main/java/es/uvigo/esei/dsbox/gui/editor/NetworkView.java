package es.uvigo.esei.dsbox.gui.editor;

import es.uvigo.esei.dsbox.core.model.Host;
import es.uvigo.esei.dsbox.core.model.HostType;
import es.uvigo.esei.dsbox.core.model.Link;
import es.uvigo.esei.dsbox.core.model.Network;
import es.uvigo.esei.dsbox.core.model.NetworkSpec;
import es.uvigo.esei.dsbox.core.model.SimulationSpec;
import es.uvigo.esei.dsbox.core.model.graphical.GraphicalSpec;
import es.uvigo.esei.dsbox.core.model.graphical.HostPosition;
import es.uvigo.esei.dsbox.core.model.graphical.NetworkPosition;
import es.uvigo.esei.dsbox.gui.editor.events.NetworkEvent;
import es.uvigo.esei.dsbox.gui.editor.events.NetworkEventType;
import es.uvigo.esei.dsbox.gui.editor.events.NetworkLinkEvent;
import es.uvigo.esei.dsbox.gui.editor.events.NetworkNodeEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import es.uvigo.esei.dsbox.gui.editor.events.NetworkEventProcessor;

public class NetworkView extends Region implements NetworkEventProcessor {

    public final static int MAX_VIEW_WIDTH = 4000;
    public final static int MAX_VIEW_HEIGHT = 2500;
    private final static Background DEFAULT_BACKGROUND = new Background(new BackgroundFill(Color.gray(0.55), CornerRadii.EMPTY, Insets.EMPTY));

    private boolean snapToGrid = false;
    private int gridSize = 20;
    private int viewWidth = 2000;
    private int viewHeight = 1500;

    private List<NodeView> nodes;
    private List<LinkView> links;

    private NetworkEditor networkEditor;
    private NetworkEventProcessor delegate;

    public NetworkView(NetworkEditor networkEditor) {
        this.networkEditor = networkEditor;
        this.nodes = new ArrayList<>();
        this.links = new ArrayList<>();

//        populateNodes();
        initView();
        setNetworkEvents();
    }

    public void setNetworkEditor(NetworkEditor networkEditor) {
        this.networkEditor = networkEditor;
    }

    private void initView() {
        this.setBackground(DEFAULT_BACKGROUND);
        // disble blue focus border
        //this.setStyle("-fx-focus-color: -fx-control-inner-background ; -fx-faint-focus-color: -fx-control-inner-background ;");
        this.setStyle("-fx-focus-color: transparent ; -fx-faint-focus-color: transparent ;");

        setViewDimensions(viewWidth, viewHeight);

        if (snapToGrid) {
            createGrid();
        }

        if (this.nodes != null) {
            for (NodeView nv : this.nodes) {
                this.getChildren().add(nv);
            }
        }
        if (this.links != null) {
            for (LinkView lv : this.links) {
                this.getChildren().add(lv);
                lv.toBack();
            }
        }
    }

    public void setNetworkEvents() {
        setOnMouseClicked((MouseEvent me) -> {
            if (this.delegate != null) {
                Position position = new Position(me.getX(), me.getY());
                NetworkEvent ne = new NetworkEvent(NetworkEventType.CANVAS_CLICKED, position);
                ne.setScreenPosition(new Position(me.getScreenX(), me.getScreenY()));
                this.delegate.onCanvasClicked(ne);
            }
            me.consume();
        });

        setOnMouseMoved((MouseEvent me) -> {
            if (this.delegate != null) {
                Position position = new Position(me.getX(), me.getY());
                NetworkEvent ne = new NetworkEvent(NetworkEventType.MOUSE_MOVED, position);
                ne.setScreenPosition(new Position(me.getScreenX(), me.getScreenY()));
                this.delegate.onMouseMoved(ne);
            }
            me.consume();
        });
    }

    public void clear() {
        for (NodeView n : this.nodes) {
            this.getChildren().remove(n);
        }
        for (LinkView l : this.links) {
            this.getChildren().remove(l);
        }
        this.nodes.clear();
        this.links.clear();
    }

    public void loadSimulationSpec(SimulationSpec simulationSpec) {
        this.clear();

        Map<Host, HostNodeView> loadedHosts = loadHosts(simulationSpec);
        Map<Network, NetworkNodeView> loadedNetworks = loadNetworks(simulationSpec);

        for (HostNodeView hostNodeView : loadedHosts.values()) {
            hostNodeView.createGraphicalRepresentation();
            this.addNodeView(hostNodeView);
        }
        for (NetworkNodeView networkBodeView : loadedNetworks.values()) {
            networkBodeView.createGraphicalRepresentation();
            this.addNodeView(networkBodeView);
        }

        List<LinkView> linksViews = createLinksViews(loadedHosts, loadedNetworks);
        // And links to View  (model Links were already updated)
        for (LinkView newLinkView : linksViews) {
            this.links.add(newLinkView);
            this.getChildren().add(newLinkView);
            this.setLinkViewPosition(newLinkView);
            newLinkView.toBack();
        }
    }

    public SimulationSpec retrieveSimulationSpec() {
        NetworkSpec networkSpec = new NetworkSpec();
        GraphicalSpec graphicalSpec = new GraphicalSpec();

        Set<HostType> usedHostTypes = new HashSet<>();
        for (NodeView nodeView : this.nodes) {
            Position p = nodeView.getPosition();
            int x = (int) Math.round(p.getX());
            int y = (int) Math.round(p.getY());

            if (nodeView instanceof HostNodeView) {
                HostNodeView hostNodeView = (HostNodeView) nodeView;
                Host host = hostNodeView.getHost();
                
                usedHostTypes.add(host.getHostType());
                networkSpec.addHost(host);
                graphicalSpec.addHostPosition(new HostPosition(host, x, y));
            } else if (nodeView instanceof NetworkNodeView) {
                NetworkNodeView networkNodeView = (NetworkNodeView) nodeView;
                Network network = networkNodeView.getNetwork();
                networkSpec.addNetwork(network);
                graphicalSpec.addNetworkPosition(new NetworkPosition(network, x, y));
            }
        }
        networkSpec.setHostTypes(new ArrayList<>(usedHostTypes));

        SimulationSpec result = new SimulationSpec();
        result.setNetworkSpec(networkSpec);
        result.setGraphicalSpec(graphicalSpec);
        return result;
    }

    public boolean isSnapToGrid() {
        return snapToGrid;
    }

    public void setSnapToGrid(boolean newSnapTogrid) {
        if (newSnapTogrid && !this.snapToGrid) {
            createGrid();
        }
        this.snapToGrid = newSnapTogrid;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    public void setViewDimensions(int viewWidth, int viewHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;

        this.setMaxSize(MAX_VIEW_WIDTH, MAX_VIEW_HEIGHT);
        this.setWidth(this.viewWidth);
        this.setHeight(this.viewHeight);
        this.setPrefSize(viewWidth, viewHeight);
        this.setMinSize(this.viewWidth, this.viewHeight);
        //this.setPrefSize(MAX_VIEW_WIDTH, MAX_VIEW_HEIGHT);

    }

    public List<NodeView> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeView> nodes) {
        this.nodes = nodes;
    }

    public List<LinkView> getLinks() {
        return links;
    }

    public void setLinks(List<LinkView> links) {
        this.links = links;
    }

    public void addNodeView(NodeView newNodeView) {
        // Update View
        this.nodes.add(newNodeView);
        this.getChildren().add(newNodeView);

        // Set node position
        moveNodeView(newNodeView, newNodeView.getPosition());
    }

    public void removeNodeView(NodeView nodeView) {
        // Update View
        this.nodes.remove(nodeView);
        this.getChildren().remove(nodeView);
        if (nodeView instanceof HostNodeView) {
            HostNodeView hostNodeView = (HostNodeView) nodeView;
            for (LinkView outputLinkView : hostNodeView.getOutputLinks()) {
                this.links.remove(outputLinkView);
                this.getChildren().remove(outputLinkView);
                outputLinkView.getDestination().getInputLinks().remove(outputLinkView);
            }
        } else { // Remove Network
            NetworkNodeView networkNodeView = (NetworkNodeView) nodeView;
            for (LinkView inputLinkView : networkNodeView.getInputLinks()) {
                this.links.remove(inputLinkView);
                this.getChildren().remove(inputLinkView);
                inputLinkView.getSource().getOutputLinks().remove(inputLinkView);
            }

            // Update Model  (delete links at source Host)
            for (LinkView inputLinkView : networkNodeView.getInputLinks()) {
                Link link = inputLinkView.getLink();
                Host sourceHost = inputLinkView.getSource().getHost();
                sourceHost.removeLink(link);
            }
        }
    }

    public void moveNodeView(NodeView nodeView, Position newPosition) {
        // Move node
        nodeView.setTranslateX(newPosition.getX());
        nodeView.setTranslateY(newPosition.getY());
        nodeView.setPosition(newPosition);

        // Move links
        if (nodeView instanceof HostNodeView) {
            // It is a Host
            HostNodeView hostNodeView = (HostNodeView) nodeView;
            for (LinkView outputLink : hostNodeView.getOutputLinks()) {
                outputLink.setStartX(newPosition.getX() + (nodeView.getWidth() / 2));
                outputLink.setStartY(newPosition.getY() + (nodeView.getHeight() / 2));
            }
        } else {
            // It is a Network
            NetworkNodeView networkNodeView = (NetworkNodeView) nodeView;
            for (LinkView inputLink : networkNodeView.getInputLinks()) {
                inputLink.setEndX(newPosition.getX() + (nodeView.getWidth() / 2));
                inputLink.setEndY(newPosition.getY() + (nodeView.getHeight() / 2));
            }
        }
    }

    public void addLinkView(LinkView newLinkView) {
        // Update View
        this.links.add(newLinkView);
        this.getChildren().add(newLinkView);
        setLinkViewPosition(newLinkView);
        newLinkView.toBack();

        newLinkView.getSource().getOutputLinks().add(newLinkView);
        newLinkView.getDestination().getInputLinks().add(newLinkView);

        // Update Model  
        Host sourceHost = newLinkView.getSource().getHost();
        sourceHost.addLink(newLinkView.getLink());
    }

    public void setLinkViewPosition(LinkView linkView) {
        setLinkViewStartPosition(linkView);
        setLinkViewEndPosition(linkView);
    }

    private void setLinkViewStartPosition(LinkView linkView) {
        HostNodeView source = linkView.getSource();
        double sourceWidth = Math.max(source.getWidth(), NodeView.NODEVIEW_IMAGE_SIZE);
        double sourceHeight = Math.max(source.getHeight(), NodeView.NODEVIEW_IMAGE_SIZE);
        linkView.setStartX(source.getPosition().getX() + (sourceWidth / 2));
        linkView.setStartY(source.getPosition().getY() + (sourceHeight / 2));
    }

    private void setLinkViewEndPosition(LinkView linkView) {
        NetworkNodeView destination = linkView.getDestination();

        if (destination != null) {
            double destinationWidth = Math.max(destination.getWidth(), NodeView.NODEVIEW_IMAGE_SIZE);
            double destinationHeight = Math.max(destination.getHeight(), NodeView.NODEVIEW_IMAGE_SIZE);
            linkView.setEndX(destination.getPosition().getX() + (destinationWidth / 2));
            linkView.setEndY(destination.getPosition().getY() + (destinationHeight / 2));

        } else {
            linkView.setEndX(linkView.getStartX());
            linkView.setEndY(linkView.getStartY());
        }

    }

    public void removeLinkView(LinkView linkView) {
        // Update View
        this.links.remove(linkView);
        this.getChildren().remove(linkView);

        linkView.getSource().getOutputLinks().remove(linkView);
        linkView.getDestination().getInputLinks().remove(linkView);

        // Update Model
        Host sourceHost = linkView.getSource().getHost();
        sourceHost.removeLink(linkView.getLink());
    }

    public void addTemporalLine(Line temporalLine) {
        this.getChildren().add(temporalLine);
        temporalLine.toFront();
    }

    public void clearTemporalLine(Line temporalLine) {
        this.getChildren().remove(temporalLine);
    }

    private void createGrid() {
        for (int x = 0; x < MAX_VIEW_WIDTH; x = x + gridSize) {
            for (int y = 0; y < MAX_VIEW_HEIGHT; y = y + gridSize) {
                Circle p = new Circle(x, y, 1, Color.LIGHTGRAY);
                this.getChildren().add(p);
                p.toBack();
            }
        }
    }

    private Map<Host, HostNodeView> loadHosts(SimulationSpec simulationSpec) {
        
        Map<Host, HostNodeView> result = new HashMap<>();
        if (simulationSpec.getGraphicalSpec() != null) {
            List<HostPosition> hostPositions = simulationSpec.getGraphicalSpec().getHostPositions();
            if (hostPositions != null) {
                for (HostPosition hostPosition : hostPositions) {
                    Host host = hostPosition.getHost();
                    HostNodeView hostView = new HostNodeView(host, new Position(hostPosition.getX(), hostPosition.getY()), this);
                    result.put(host, hostView);
                }
            }
        } else {
            double x = 10.0;
            double y = 10.0;

            List<Host> hosts = simulationSpec.getNetworkSpec().getHosts();
            if (hosts != null) {
                for (Host host : hosts) {
                    HostNodeView hostView = new HostNodeView(host, new Position(x, y), this);
                    result.put(host, hostView);
                    y += (128 + 10);
                }
            }
        }
        return result;
    }

    private Map<Network, NetworkNodeView> loadNetworks(SimulationSpec simulationSpec) {
        Map<Network, NetworkNodeView> result = new HashMap<>();
        if (simulationSpec.getGraphicalSpec() != null) {
            List<NetworkPosition> networkPositions = simulationSpec.getGraphicalSpec().getNetworkPositions();
            if (networkPositions != null) {
                for (NetworkPosition networkPosition : networkPositions) {
                    Network network = networkPosition.getNetwork();
                    NetworkNodeView networkView = new NetworkNodeView(network, new Position(networkPosition.getX(), networkPosition.getY()), this);
                    result.put(network, networkView);
                }
            }
        } else {
            double x = 128 + 10.0;
            double y = 10.0;

            List<Network> networks = simulationSpec.getNetworkSpec().getNetworks();
            if (networks != null) {
                for (Network network : networks) {
                    NetworkNodeView networkView = new NetworkNodeView(network, new Position(x, y), this);
                    result.put(network, networkView);
                    y += (128 + 10);
                }
            }
        }
        return result;
    }

    private List<LinkView> createLinksViews(Map<Host, HostNodeView> loadedHosts, Map<Network, NetworkNodeView> loadedNetworks) {
        List<LinkView> result = new ArrayList<>();

        for (Host host : loadedHosts.keySet()) {
            if (host.getLinks() != null) {
                for (Link link : host.getLinks()) {
                    HostNodeView hostView = loadedHosts.get(host);
                    NetworkNodeView networkView = loadedNetworks.get(link.getNetwork());
                    LinkView linkView = new LinkView(hostView, networkView, link, this);

                    // Attach to NodeViews
                    hostView.getOutputLinks().add(linkView);
                    networkView.getInputLinks().add(linkView);

                    result.add(linkView);
                }
            }
        }

        return result;
    }

    // -----------------------------------
    // Delegate events
    // -----------------------------------
    public void setDelegate(NetworkEventProcessor delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onNodeSelected(NetworkNodeEvent ne) {
        this.delegate.onNodeSelected(ne);
    }

    @Override
    public void onNodeDragged(NetworkNodeEvent ne) {
        this.delegate.onNodeDragged(ne);
    }

    @Override
    public void onNodeDoubleClicked(NetworkNodeEvent ne) {
        this.delegate.onNodeDoubleClicked(ne);
    }

    @Override
    public void onNodeSecondaryClicked(NetworkNodeEvent ne) {
        this.delegate.onNodeSecondaryClicked(ne);
    }

    @Override
    public void onCanvasClicked(NetworkEvent ne) {
        this.delegate.onCanvasClicked(ne);
    }

    @Override
    public void onLinkSelected(NetworkLinkEvent ne) {
        this.delegate.onLinkSelected(ne);
    }

    @Override
    public void onLinkDoubleClicked(NetworkLinkEvent ne) {
        this.delegate.onLinkDoubleClicked(ne);
    }

    @Override
    public void onLinkSecondaryClicked(NetworkLinkEvent ne) {
        this.delegate.onLinkSecondaryClicked(ne);
    }

    @Override
    public void onMouseMoved(NetworkEvent ne) {
        this.delegate.onMouseMoved(ne);
    }

    @Override
    public void onNodeClicked(NetworkNodeEvent ne) {
        this.delegate.onNodeClicked(ne);
    }

    @Override
    public void onLinkClicked(NetworkLinkEvent ne) {
        this.delegate.onLinkClicked(ne);
    }

    @Override
    public void onNodeUnselected(NetworkNodeEvent ne) {
        this.delegate.onNodeUnselected(ne);
    }

    public List<HostType> getRegisteredHostTypes() {
        return networkEditor.getRegisteredHostTypes();
    }

}
