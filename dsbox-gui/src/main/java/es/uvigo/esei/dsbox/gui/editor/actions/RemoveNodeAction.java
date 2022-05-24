package es.uvigo.esei.dsbox.gui.editor.actions;

import es.uvigo.esei.dsbox.core.model.Host;
import es.uvigo.esei.dsbox.core.model.Link;
import es.uvigo.esei.dsbox.gui.editor.HostNodeView;
import es.uvigo.esei.dsbox.gui.editor.LinkView;
import es.uvigo.esei.dsbox.gui.editor.NetworkNodeView;
import es.uvigo.esei.dsbox.gui.editor.NetworkView;
import es.uvigo.esei.dsbox.gui.editor.NodeView;
import java.util.ArrayList;
import java.util.List;

public class RemoveNodeAction extends BaseEditAction {

    private NodeView removedNode;

    public RemoveNodeAction(NetworkView networkView, NodeView removedNode) {
        super(networkView);
        this.removedNode = removedNode;
    }

    @Override
    public void doAction() {
        networkView.removeNodeView(removedNode);
    }

    @Override
    public void undoAction() {
        // Restore node
        networkView.addNodeView(removedNode);

        // Restore links
        if (removedNode instanceof HostNodeView) {
            // Restore Host links
            HostNodeView hostNodeView = (HostNodeView) removedNode;
            List<LinkView> initialOutputLinks = hostNodeView.getOutputLinks();
            // CLear LinkView and Link list (will be recreated in addLink())
            hostNodeView.setOutputLinks(new ArrayList<>());
            hostNodeView.getHost().getLinks().clear();
            for (LinkView outputLinkView : initialOutputLinks) {
                networkView.addLinkView(outputLinkView);
            }
        } else { // Remove Network links
            NetworkNodeView networkNodeView = (NetworkNodeView) removedNode;
            List<LinkView> initialInputLinks = networkNodeView.getInputLinks();
            // CLear LinkView list (will be recreated in addLink())
            networkNodeView.setInputLinks(new ArrayList<>());
            for (LinkView inputLinkView : initialInputLinks) {
                networkView.addLinkView(inputLinkView);
            }
        }
    }

}
