package es.uvigo.esei.dsbox.gui.editor;

import es.uvigo.esei.dsbox.gui.editor.actions.EditAction;

public interface NetworkEditorObserver {
    public void notifySelectedNode(NodeView selectedNode);
    public void nofityEditAction(EditAction lasteEditAction);
}
