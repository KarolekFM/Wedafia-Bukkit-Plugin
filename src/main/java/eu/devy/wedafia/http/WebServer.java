package eu.devy.wedafia.http;

import eu.devy.wedafia.Wedafia;

import org.bukkit.plugin.java.JavaPlugin;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.ServerConfiguration;

import java.util.logging.Logger;

public class WebServer {
    private static Logger log;
    private static HttpServer server;
    private static Wedafia wedafia;

    public static void run(Wedafia wedafia) {
        WebServer.wedafia = wedafia;
        log = wedafia.getLogger();
        server = HttpServer.createSimpleServer(null, 8022);

        addHandlers();

        try {
            server.start();
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }

    private static void addHandlers() {
        HandlersManager.setWedafiaInstance(wedafia);
        ServerConfiguration config = server.getServerConfiguration();

        config.addHttpHandler(HandlersManager.getPlayerInvHandler(), "/");
    }

}
