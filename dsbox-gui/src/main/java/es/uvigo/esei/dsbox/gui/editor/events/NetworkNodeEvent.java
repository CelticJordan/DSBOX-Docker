/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uvigo.esei.dsbox.gui.editor.events;

import es.uvigo.esei.dsbox.gui.editor.NodeView;
import es.uvigo.esei.dsbox.gui.editor.Position;

/**
 *
 * @author ribadas
 */
public class NetworkNodeEvent extends NetworkEvent {
    public NodeView nodeView;


    public NetworkNodeEvent(NetworkEventType type, NodeView nodeView, Position position) {
        super(type, position);
        this.nodeView = nodeView;
    }

    public NodeView getNodeView() {
        return nodeView;
    }

    public void setNodeView(NodeView nodeView) {
        this.nodeView = nodeView;
    }

    
}
