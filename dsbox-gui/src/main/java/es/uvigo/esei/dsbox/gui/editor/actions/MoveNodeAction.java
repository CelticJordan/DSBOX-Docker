package es.uvigo.esei.dsbox.gui.editor.actions;

import es.uvigo.esei.dsbox.gui.editor.LinkView;
import es.uvigo.esei.dsbox.gui.editor.NetworkView;
import es.uvigo.esei.dsbox.gui.editor.NodeView;
import es.uvigo.esei.dsbox.gui.editor.Position;

public class MoveNodeAction extends BaseEditAction {

    private NodeView movedNode;
    private Position oldPosition;
    private Position newPosition;

    public MoveNodeAction(NodeView movedNode, Position oldPosition, Position newPosition, NetworkView networkView) {
        super(networkView);
        this.movedNode = movedNode;
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
    }


    @Override
    public void doAction() {
        // Already done
    }

    @Override
    public void undoAction() {
        networkView.moveNodeView(movedNode, oldPosition);
    }

}
