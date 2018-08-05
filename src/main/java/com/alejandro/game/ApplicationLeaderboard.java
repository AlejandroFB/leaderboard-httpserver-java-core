package com.alejandro.game;

import com.alejandro.game.server.CustomHttpServer;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Main class application. Http Server is started here after application is executed.
 *
 * @author afernandez
 */
public class ApplicationLeaderboard {
    private static final Logger LOGGER = Logger.getLogger(ApplicationLeaderboard.class.getName());
    private static final int PORT = 8081;

    public static void main(String args[]) {
        int port = handleArguments(args);
        if (port != -1) {
            CustomHttpServer httpServer = new CustomHttpServer(port);

            // Start server
            httpServer.start();
            LOGGER.info(String.format("Http server started successfully in port: %s.", port));

            waitForStopCommand();

            // Stop the server
            httpServer.stop();
            LOGGER.info("Http server closed successfully.");
        } else {
            System.out.println("Parameters are not set correctly, please use '-p [port-number]'");
        }
    }

    private static int handleArguments(String args[]) {
        if (args.length == 0) {
            return PORT;
        }

        try {
            if (args.length == 2 && ("-p".equals(args[0]) || "-P".equals(args[0]))) {
                return Integer.valueOf(args[1]);
            }
        } catch (NumberFormatException ex) {
            return -1;
        }

        return -1;
    }

    private static void waitForStopCommand() {
        try {
            System.out.println();
            System.out.println("Press Enter to stop the server: ");
            System.out.println();
            System.in.read();
        } catch (IOException ex) {
            LOGGER.severe("Something went wrong waiting for the input close command.");
        }
    }
}