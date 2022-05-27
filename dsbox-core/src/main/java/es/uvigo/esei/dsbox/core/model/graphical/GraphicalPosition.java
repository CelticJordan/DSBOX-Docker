
package es.uvigo.esei.dsbox.core.model.graphical;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"x", "y"})
@XmlAccessorType(XmlAccessType.FIELD)
public class GraphicalPosition {
    @XmlAttribute
    private int x;
    
    @XmlAttribute
    private int y;

    public GraphicalPosition() {
        this.x = 0;
        this.y = 0;
    }

    public GraphicalPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    
    
    
}
