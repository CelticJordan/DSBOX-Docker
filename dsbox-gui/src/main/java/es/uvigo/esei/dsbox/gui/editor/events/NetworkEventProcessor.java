/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uvigo.esei.dsbox.gui.editor.events;

import es.uvigo.esei.dsbox.gui.editor.actions.EditActionDestination;


public interface NetworkEventProcessor {

    public void onNodeSelected(NetworkNodeEvent ne);

    public void onNodeUnselected(NetworkNodeEvent ne);

    public void onNodeDragged(NetworkNodeEvent ne);

    public void onNodeDoubleClicked(NetworkNodeEvent ne);

    public void onNodeSecondaryClicked(NetworkNodeEvent ne);

    public void onNodeClicked(NetworkNodeEvent ne);

    public void onLinkSelected(NetworkLinkEvent ne);

    public void onLinkClicked(NetworkLinkEvent ne);

    public void onLinkDoubleClicked(NetworkLinkEvent ne);

    public void onLinkSecondaryClicked(NetworkLinkEvent ne);

    public void onCanvasClicked(NetworkEvent ne);

    public void onMouseMoved(NetworkEvent ne);
}
