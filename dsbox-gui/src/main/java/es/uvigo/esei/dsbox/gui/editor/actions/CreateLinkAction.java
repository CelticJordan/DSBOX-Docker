package es.uvigo.esei.dsbox.gui.editor.actions;

import es.uvigo.esei.dsbox.gui.editor.LinkView;
import es.uvigo.esei.dsbox.gui.editor.NetworkView;

public class CreateLinkAction extends BaseEditAction {

    private LinkView createdLink;

    public CreateLinkAction(NetworkView networkView, LinkView createdLink) {
        super(networkView);
        this.createdLink = createdLink;
    }

    @Override
    public void undoAction() {
        networkView.removeLinkView(createdLink);
    }

    @Override
    public void doAction() {
        networkView.addLinkView(createdLink);
        networkView.setLinkViewPosition(createdLink);
    }

}
