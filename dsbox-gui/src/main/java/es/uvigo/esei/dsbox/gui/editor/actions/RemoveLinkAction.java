package es.uvigo.esei.dsbox.gui.editor.actions;

import es.uvigo.esei.dsbox.gui.editor.LinkView;
import es.uvigo.esei.dsbox.gui.editor.NetworkView;

public class RemoveLinkAction extends BaseEditAction {

    private LinkView removedLink;

    public RemoveLinkAction(NetworkView networkView, LinkView removedLink) {
        super(networkView);
        this.removedLink = removedLink;
    }

    @Override
    public void doAction() {
        networkView.removeLinkView(removedLink);
    }

    @Override
    public void undoAction() {
        networkView.addLinkView(removedLink);
    }

}
