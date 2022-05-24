package es.uvigo.esei.dsbox.gui.editor.events;

import es.uvigo.esei.dsbox.gui.editor.NetworkView;
import es.uvigo.esei.dsbox.gui.editor.actions.EditActionDestination;

public abstract class BaseNetworkEventProcessor implements NetworkEventProcessor {
    protected EditActionDestination actionDestination;
    protected NetworkView networkView;

    public BaseNetworkEventProcessor(NetworkView networkView, EditActionDestination actionDestination) {
        this.actionDestination = actionDestination;
        this.networkView = networkView;
    }
    
    @Override
    public void onNodeSelected(NetworkNodeEvent ne) {
    }
    
    @Override
    public void onNodeUnselected(NetworkNodeEvent ne) {
    }
    
    @Override
    public void onNodeDragged(NetworkNodeEvent ne) {
    }

    @Override
    public void onNodeDoubleClicked(NetworkNodeEvent ne) {
    }

    @Override
    public void onNodeSecondaryClicked(NetworkNodeEvent ne) {
    }

    @Override
    public void onCanvasClicked(NetworkEvent ne) {
    }

    @Override
    public void onLinkSelected(NetworkLinkEvent ne) {
    }

    @Override
    public void onLinkDoubleClicked(NetworkLinkEvent ne) {
    }

    @Override
    public void onLinkSecondaryClicked(NetworkLinkEvent ne) {
    }

    @Override
    public void onMouseMoved(NetworkEvent ne) {
    }

    @Override
    public void onNodeClicked(NetworkNodeEvent ne) {
    }

    @Override
    public void onLinkClicked(NetworkLinkEvent ne) {
    }



}
