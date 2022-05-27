package es.uvigo.esei.dsbox.core.model.execution;

public class SessionSpec {
    private String host;
    private Integer port;

    public SessionSpec() {
        this.host = "localhost";
    }

    
    public SessionSpec(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
    
}
