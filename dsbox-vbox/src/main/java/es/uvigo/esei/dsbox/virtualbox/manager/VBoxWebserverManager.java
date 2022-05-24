package es.uvigo.esei.dsbox.virtualbox.manager;

import es.uvigo.esei.dsbox.core.manager.VMDriverException;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VBoxWebserverManager {

    public final static String DEFAULT_VBOX_WEBSERVER_HOST = "localhost";
    public final static int DEFAULT_VBOX_WEBSERVER_PORT = 18083;
    public final static int DEFAULT_FREE_RANDOM_PORT = 55555;
    public final static String EMPTY_USER_PASSWORD = "";

    private String hostname = DEFAULT_VBOX_WEBSERVER_HOST;
    private int port = DEFAULT_VBOX_WEBSERVER_PORT;
    private String username = EMPTY_USER_PASSWORD;
    private String password = EMPTY_USER_PASSWORD;
    private Process runningProcess;
    private boolean wasStarted = false;

    public VBoxWebserverManager() {
    }

    public VBoxWebserverManager(String hostname, int port) {
        this.hostname = hostname;
        if (port > 0) {
            this.port = port;
        } else {
            this.port = findRandomOpenPort();
        }
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void start() throws VMDriverException {
        List<String> command = new ArrayList<>();
        command.add(getExecPath());
        //command.add("--background");
        command.add("--host");
        command.add(this.hostname);
        command.add("--port");
        command.add(Integer.toString(this.port));
        command.add("--timeout");
        command.add("0");  // No sesion timeout
        command.add("--authentication");
        command.add("null"); // Null authentication
 
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        
        try {
            runningProcess = processBuilder.start();
        } catch (IOException ex) {
            throw new VMDriverException("Error trying to start VirtualBox web server (host: " + this.hostname + " port: " + this.port + ")", ex);
        }
        int i = 0;
        while ((i < 8) && (runningProcess != null) && !runningProcess.isAlive()) {
            try {
                Thread.sleep(250L);
                i++;
            } catch (InterruptedException ex) {
                throw new VMDriverException("VirtualBox web server start (host: " + this.hostname + " port: " + this.port + ") was interrupted ¿?", ex);
            }
        }
        if ((runningProcess == null) || (!runningProcess.isAlive())) {
            throw new VMDriverException("Unable to start VirtualBox web server (host: " + this.hostname + " port: " + this.port + ")");
        }

        wasStarted = true;
    }

    public void stop() {
        if (runningProcess != null) {
            runningProcess.destroy();
        }
        wasStarted = false;
    }

    public boolean isRunning() {
        return ((runningProcess != null) && (runningProcess.isAlive()) && wasStarted);
    }

    private String getExecPath() {
        String systemOS = System.getProperty("os.name").toLowerCase();
        if (systemOS.contains("windows")) {
            return getWindowsExecPath();
        } else {
            return getLinuxExecPath();
        }
    }

    private String getWindowsExecPath() {
        String basePath = "C:" + File.separator + "Program Files" + File.separator + "Oracle" + File.separator + "VirtualBox";
        if (System.getenv("VBOX_MSI_INSTALL_PATH") != null) {
            return System.getenv("VBOX_MSI_INSTALL_PATH");
        } else if (System.getenv("VBOX_INSTALL_PATH") != null) {
            basePath = System.getenv("VBOX_INSTALL_PATH");
        }
        return basePath + File.separator + "vboxwebsrv.exe";
    }

    private String getLinuxExecPath() {
        File pathFile = new File("/usr/bin/vboxwebsrv");
        if (pathFile.exists() && pathFile.canExecute()) {
            return pathFile.getAbsolutePath();
        }
        pathFile = new File("/usr/local/bin/vboxwebsrv");
        if (pathFile.exists() && pathFile.canExecute()) {
            return pathFile.getAbsolutePath();
        }
        pathFile = new File("/usr/lib/virtualbox/vboxwebsrv");
        if (pathFile.exists() && pathFile.canExecute()) {
            return pathFile.getAbsolutePath();
        }
        pathFile = new File("/opt/VirtualBox/vboxwebsrv");
        if (pathFile.exists() && pathFile.canExecute()) {
            return pathFile.getAbsolutePath();
        }
        // Assumen it is on PATH
        return "vboxwebsrv";
    }

    private int findRandomOpenPort() {
        int foundPort = DEFAULT_FREE_RANDOM_PORT;
        try (ServerSocket socket = new ServerSocket(0)) {
            foundPort = socket.getLocalPort();
        } catch (IOException ex) {
            Logger.getLogger(VBoxWebserverManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return foundPort;
    }

}
