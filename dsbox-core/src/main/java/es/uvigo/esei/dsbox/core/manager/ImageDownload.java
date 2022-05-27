package es.uvigo.esei.dsbox.core.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ImageDownload implements Runnable {

    private static final int BUFFER_SIZE = 4096;

    private String url;
    private String destinationDir;
    private DownloadObserver observer;
    private boolean cancelDownload;
    private HttpURLConnection httpConn;
    private int contentLength;
    private String fileName;
    private InputStream inputStream;

    public ImageDownload(String url, String destinationDir) {
        this.url = url;
        this.destinationDir = destinationDir;
    }

    public ImageDownload(String url, String destinationDir, DownloadObserver observer) {
        this.url = url;
        this.destinationDir = destinationDir;
        this.observer = observer;
        this.cancelDownload = false;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDestinationDir(String destinationDir) {
        this.destinationDir = destinationDir;
    }

    public void setObserver(DownloadObserver observer) {
        this.observer = observer;
    }

    public void cancel() {
        this.cancelDownload = true;
    }

    @Override
    public void run() {
        if (observer != null) {
            observer.notifyStart();
        }
        doDownload();
        if (observer != null) {
            observer.notifyFinish();
        }
    }

    private void doDownload() {
        try {
            openHttpConnection();

            String saveFilePath = this.destinationDir + File.separator + this.fileName.replace(".zip", "");
            //String saveFilePath = saveDirectory + File.separator + util.getFileName();
            ZipInputStream inputStream = new ZipInputStream(this.inputStream);
            ZipEntry entry = inputStream.getNextEntry();

            String s = String.format("Entry: %s len %d added %TD",
                    entry.getName(), entry.getSize(),
                    new Date(entry.getTime()));
            System.out.println(s);

            //InputStream inputStream = util.getInputStream();
            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            long totalBytesRead = 0;
            long fileSize = this.contentLength;
            fileSize = entry.getSize();

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                if (this.cancelDownload) {
                    if (observer != null) {
                        this.observer.notifyCancel();
                    }
                    break;
                }
                if (observer != null) {
                    this.observer.notifyAdvance(totalBytesRead, fileSize);
                }
            }

            outputStream.close();
            disconnectHttpConnection();
        } catch (IOException ex) {
            this.cancel();
            if (observer != null) {
                this.observer.notifyCancel();
            }
        }
    }

    private void openHttpConnection() throws MalformedURLException, IOException {
        URL url = new URL(this.url);
        httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                fileName = this.url.substring(this.url.lastIndexOf("/") + 1, this.url.length());
            }

            // opens input stream from the HTTP connection
            inputStream = httpConn.getInputStream();

        } else {
            throw new IOException(
                    "No file to download. Server replied HTTP code: "
                    + responseCode);
        }
    }

    private void disconnectHttpConnection() throws IOException {
        inputStream.close();
        httpConn.disconnect();
    }

}
