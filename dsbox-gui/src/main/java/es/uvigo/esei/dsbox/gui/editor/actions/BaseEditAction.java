package es.uvigo.esei.dsbox.gui.editor.actions;

import es.uvigo.esei.dsbox.gui.editor.NetworkView;

public abstract class BaseEditAction implements EditAction {

    protected NetworkView networkView;

    public BaseEditAction(NetworkView networkView) {
        this.networkView = networkView;
    }

}
