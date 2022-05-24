package es.uvigo.esei.dsbox.gui.editor.actions;

import es.uvigo.esei.dsbox.gui.editor.LinkView;
import es.uvigo.esei.dsbox.gui.editor.NetworkView;
import es.uvigo.esei.dsbox.gui.editor.NodeView;

public class CreateNodeAction extends BaseEditAction {

    private NodeView createdNode;

    public CreateNodeAction(NetworkView networkView, NodeView createdNode) {
        super(networkView);
        this.createdNode = createdNode;
    }

    @Override
    public void undoAction() {
        networkView.removeNodeView(createdNode);
    }

    @Override
    public void doAction() {
        networkView.addNodeView(createdNode);
    }

}
