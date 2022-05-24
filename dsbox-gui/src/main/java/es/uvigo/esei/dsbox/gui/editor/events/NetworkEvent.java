/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uvigo.esei.dsbox.gui.editor.events;

import es.uvigo.esei.dsbox.gui.editor.Position;

/**
 *
 * @author ribadas
 */
public class NetworkEvent {
    public NetworkEventType type;
    public Position position;
    public Position screenPosition;

    public NetworkEvent() {
    }

    public NetworkEvent(NetworkEventType type, Position position) {
        this.type = type;
        this.position = position;
    }

    public NetworkEventType getType() {
        return type;
    }

    public void setType(NetworkEventType type) {
        this.type = type;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getScreenPosition() {
        return screenPosition;
    }

    public void setScreenPosition(Position screenPosition) {
        this.screenPosition = screenPosition;
    }
    
    
    
}
