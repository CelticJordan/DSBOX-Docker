package es.uvigo.esei.dsbox.core.manager;

import es.uvigo.esei.dsbox.core.model.VMImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class SimpleImageStoreManager implements ImageStoreManager {

    private String imageStorePath;
    private File imageStoreDir;

    @Override
    public void initialize(String imageStorePath) {
        this.imageStorePath = imageStorePath;
        this.imageStoreDir = new File(imageStorePath);
        if (!imageStoreDir.exists()) {
            imageStoreDir.mkdirs();
        }
    }

    @Override
    public String addNewImage(VMImage image, String newImageFilename) throws IOException {
        File localFile = new File(imageStoreDir, newImageFilename);
        String localPath = localFile.getAbsolutePath();
        if (!image.isDownloaded()) {
            downloadImage(image.getDownloadURI(), localPath);
        }
        return localPath;
    }

    @Override
    public void removeImage(VMImage image) {
        if (imageIsStored(image)) {
            File imageFile = new File(image.getLocalURI());
            boolean wasDeleted = imageFile.delete();
            if (!wasDeleted) {
                // TODO
            }
        }
    }

    @Override
    public boolean imageIsStored(VMImage image) {
        if (image.isDownloaded()) {
            File imageFile = new File(image.getLocalURI());
            return imageFile.exists();
        }
        return false;
    }

    private void downloadImage(String sourceURI, String destinationPath) throws IOException {
        if (sourceURI.startsWith("http://")) {
            downloadFromWeb(sourceURI, destinationPath);
        } else {
            localCopy(sourceURI, destinationPath);
        }
    }

    private void downloadFromWeb(String sourceURI, String destinationPath) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(sourceURI);

        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity fileEntity = httpResponse.getEntity();
        if (fileEntity != null) {
            FileUtils.copyInputStreamToFile(fileEntity.getContent(), new File(destinationPath));
        }
        httpGet.releaseConnection();
    }

    private void localCopy(String source, String destination) throws IOException {
        File sourceFile = new File(source);
        Path sourcePath = sourceFile.toPath();
        File destinationFile = new File(destination);
        Path destinationPath = destinationFile.toPath();

        if (!sourcePath.equals(destinationPath)) {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

}
