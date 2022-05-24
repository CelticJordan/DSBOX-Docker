package es.uvigo.esei.dsbox.gui.editor.events;

import es.uvigo.esei.dsbox.gui.editor.LinkView;
import es.uvigo.esei.dsbox.gui.editor.NetworkView;
import es.uvigo.esei.dsbox.gui.editor.NodeView;
import es.uvigo.esei.dsbox.gui.editor.actions.EditActionDestination;
import es.uvigo.esei.dsbox.gui.editor.actions.RemoveLinkAction;
import es.uvigo.esei.dsbox.gui.editor.actions.RemoveNodeAction;


public class RemoveEventProcessor extends BaseNetworkEventProcessor {

    public RemoveEventProcessor(NetworkView networkView, EditActionDestination actionDestination) {
        super(networkView, actionDestination);
    }



    @Override
    public void onNodeSelected(NetworkNodeEvent ne) {
        NodeView node = ne.nodeView;
        
        // OLD this.networkView.removeNode(node);
        // NEW (with actions)        
        RemoveNodeAction action = new RemoveNodeAction(networkView, node);
        actionDestination.processEditAction(action);
    }
    
 @Override
    public void onNodeClicked(NetworkNodeEvent ne) {
         onNodeSelected(ne);  // TODO: check equivalence
    } 
    
    @Override
    public void onLinkSelected(NetworkLinkEvent ne) {
        LinkView link = ne.linkView;
        //this.networkView.removeLink(link);
        
        RemoveLinkAction action = new RemoveLinkAction(networkView, link);
        actionDestination.processEditAction(action);
    }

     @Override
    public void onLinkClicked(NetworkLinkEvent ne) {
         onLinkSelected(ne);  // TODO: check equivalence
    } 
}
