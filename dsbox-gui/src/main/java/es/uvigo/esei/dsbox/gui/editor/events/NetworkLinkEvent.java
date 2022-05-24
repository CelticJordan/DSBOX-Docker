/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uvigo.esei.dsbox.gui.editor.events;

import es.uvigo.esei.dsbox.gui.editor.LinkView;
import es.uvigo.esei.dsbox.gui.editor.Position;

/**
 *
 * @author ribadas
 */
public class NetworkLinkEvent extends NetworkEvent {
    public LinkView linkView;

    public NetworkLinkEvent() {
    }

    public NetworkLinkEvent(NetworkEventType type, LinkView linkView, Position position) {
        super(type, position);
        this.linkView = linkView;
    }

    public LinkView getLinkView() {
        return linkView;
    }

    public void setLinkView(LinkView linkView) {
        this.linkView = linkView;
    }

}
