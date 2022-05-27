package es.uvigo.esei.dsbox.core.manager;

import es.uvigo.esei.dsbox.core.model.VMImage;
import java.io.IOException;

public interface ImageStoreManager {
        public void initialize(String imageStorePath);
        public String addNewImage(VMImage image, String  newImageFilename)throws IOException ; 
        public void removeImage(VMImage image)throws IOException ;
        public boolean imageIsStored(VMImage image);
                
}
