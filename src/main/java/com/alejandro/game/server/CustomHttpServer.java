package com.alejandro.game.server;

import com.alejandro.game.handler.MainHandler;
import com.alejandro.game.leaderboard.LeaderboardServiceFactory;
import com.alejandro.game.leaderboard.session.CleanUpSessionJob;
import com.alejandro.game.util.RequestParser;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Self-contained simple http server. It uses a main handler to receive all requests for the path specified.
 * <p>
 * Leaderboard service is instantiated in this class and passed to the main handler.
 *
 * @author afernandez
 */
public class CustomHttpServer {
    private static final Logger LOGGER = Logger.getLogger(CustomHttpServer.class.getName());

    private static final int THREADS_PER_CORE = 10;

    private HttpServer httpServer;
    private ExecutorService executorService;
    private LeaderboardServiceFactory leaderboardFactory;
    private CleanUpSessionJob cleanUpSessionJob;

    public CustomHttpServer(int port) {
        executorService = initExecutor();
        leaderboardFactory = initServiceFactory();

        try {
            httpServer = HttpServer.create(new InetSocketAddress(port), 0);

            HttpContext context = httpServer.createContext("/", new MainHandler(leaderboardFactory.createLeaderboardService(),
                    leaderboardFactory.getRequestParser(), leaderboardFactory.getSessionService()));

            // Get request parser from the Factory
            context.getFilters().add(new CustomHttpFilter(new RequestParser()));
            httpServer.setExecutor(executorService);
            runCleanUpSessionJob();

        } catch (IOException ex) {
            final String errorMessage = "Error during server creation or response generation.";
            LOGGER.severe(errorMessage);

            throw new IllegalStateException(errorMessage, ex);
        }
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
            executorService.shutdown();
            cleanUpSessionJob.shutDown();
        }
    }

    private void runCleanUpSessionJob() {
        cleanUpSessionJob = leaderboardFactory.getCleanUpSessionJob();
        cleanUpSessionJob.run();
    }

    private ExecutorService initExecutor() {
        int threadAmount = Runtime.getRuntime().availableProcessors() * THREADS_PER_CORE;

        LOGGER.info(String.format("Thread pool is going to be created with size: %s", threadAmount));
        return Executors.newFixedThreadPool(threadAmount);
    }

    private LeaderboardServiceFactory initServiceFactory() {
        return new LeaderboardServiceFactory();
    }
}