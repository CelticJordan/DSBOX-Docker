package es.uvigo.esei.dsbox.core.manager;

public interface DownloadObserver {

    public void notifyStart();

    public void notifyFinish();

    public void notifyCancel();

    public void notifyAdvance(long downloaded, long totalSize);
}
