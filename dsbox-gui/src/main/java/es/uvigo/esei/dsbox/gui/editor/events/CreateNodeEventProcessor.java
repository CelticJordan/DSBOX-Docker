package es.uvigo.esei.dsbox.gui.editor.events;

import es.uvigo.esei.dsbox.core.model.Host;
import es.uvigo.esei.dsbox.core.model.HostType;
import es.uvigo.esei.dsbox.core.model.Network;
import es.uvigo.esei.dsbox.core.model.NetworkType;
import es.uvigo.esei.dsbox.gui.editor.HostNodeView;
import es.uvigo.esei.dsbox.gui.editor.NetworkNodeView;
import es.uvigo.esei.dsbox.gui.editor.NetworkView;
import es.uvigo.esei.dsbox.gui.editor.NodeView;
import es.uvigo.esei.dsbox.gui.editor.Position;
import es.uvigo.esei.dsbox.gui.editor.actions.CreateNodeAction;
import es.uvigo.esei.dsbox.gui.editor.actions.EditActionDestination;

public class CreateNodeEventProcessor extends BaseNetworkEventProcessor {

    private static int nextNewNodeID = 1;

    private static void setNextNewNodeID(int newID) {
        nextNewNodeID = newID;
    }

    public static Network nextNewNetwork(NetworkType networkType) {
        //String newNetworkName = "network_"+nextNewNodeID;
        String newNetworkName = "network";
        switch (networkType) {
            case SWITCH:
                newNetworkName = "switch";
                break;
            case HUB:
                newNetworkName = "hub";
                break;
            case EXTERNAL_NATTED:
                newNetworkName = "nat";
                break;
            case EXTERNAL_BRIDGED:
                newNetworkName = "bridge";
                break;
        }
        newNetworkName = newNetworkName + "_" + nextNewNodeID;
        Network network = new Network(Integer.toString(nextNewNodeID), newNetworkName, newNetworkName, networkType);
        nextNewNodeID++;

        return network;
    }

    public static Host nextNewHost(HostType hostType) {
        String newHostName = "host_" + nextNewNodeID;
        Host host = new Host(Integer.toString(nextNewNodeID), newHostName, newHostName, hostType);
        nextNewNodeID++;

        return host;
    }

    private boolean createHostNode = true;
    private boolean createNetworkNode = false;

    private NetworkType networkTypeToCreate;
    private HostType hostTypeToCreate;


    public CreateNodeEventProcessor(NetworkView networkView, EditActionDestination actionDestination, NetworkType networkType) {
        super(networkView, actionDestination);
        enableNetworkNodeCreation(networkType);
    }

    public CreateNodeEventProcessor(NetworkView networkView, EditActionDestination actionDestination, HostType hostType) {
        super(networkView, actionDestination);
        enableHostNodeCreation(hostType);
    }

    public void enableHostNodeCreation(HostType hostType) {
        this.createHostNode = true;
        this.createNetworkNode = false;
        this.hostTypeToCreate = hostType;
    }

    public void enableNetworkNodeCreation(NetworkType networkType) {
        this.createHostNode = false;
        this.createNetworkNode = true;
        this.networkTypeToCreate = networkType;
    }

    public void setNetworkTypeToCreate(NetworkType networkTypeToCreate) {
        this.networkTypeToCreate = networkTypeToCreate;
    }

    public void setHostTypeToCreate(HostType hostTypeToCreate) {
        this.hostTypeToCreate = hostTypeToCreate;
    }

    @Override
    public void onCanvasClicked(NetworkEvent ne) {
        Position nodePosition = new Position(ne.getPosition().getX()-(NodeView.NODEVIEW_IMAGE_SIZE/2), ne.getPosition().getY()-(NodeView.NODEVIEW_IMAGE_SIZE/2));
        NodeView newNodeView;
        if (createHostNode) {
            newNodeView = createNewHostNode(this.hostTypeToCreate, nodePosition);
        } else {
            newNodeView = createNewNetworkNode(this.networkTypeToCreate, nodePosition);
        }
        
        // OLD networkView.addNode(newNodeView);
        // NEW (with Actions)
        CreateNodeAction action = new CreateNodeAction(networkView, newNodeView);
        actionDestination.processEditAction(action);
    }

    private NodeView createNewHostNode(HostType hostTypeToCreate, Position correctedPosition) {
        Host newhost = CreateNodeEventProcessor.nextNewHost(hostTypeToCreate);
        HostNodeView newHostView = new HostNodeView(newhost, correctedPosition, this.networkView);
        newHostView.createGraphicalRepresentation();
        return newHostView;
    }

    private NodeView createNewNetworkNode(NetworkType networkTypeToCreate, Position correctedPosition) {
        Network newNetwork = CreateNodeEventProcessor.nextNewNetwork(networkTypeToCreate);
        NetworkNodeView newNetworkView = new NetworkNodeView(newNetwork, correctedPosition, this.networkView);
        newNetworkView.createGraphicalRepresentation();
        return newNetworkView;
    }

}
