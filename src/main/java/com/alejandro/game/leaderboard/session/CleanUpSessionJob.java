package com.alejandro.game.leaderboard.session;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Schedule job run every fixed time to clean up expired sessions.
 *
 * @author afernandez
 */
public class CleanUpSessionJob {
    private static final Logger LOGGER = Logger.getLogger(CleanUpSessionJob.class.getName());

    private SessionService sessionService;
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public CleanUpSessionJob(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    /**
     * Initialize the schedule job to run at a fixed rate.
     */
    public void run() {
        LOGGER.info("Schedule task executed - Clean up expired sessions");
        executorService.scheduleAtFixedRate(() -> sessionService.removeExpiredSessions(),
                SessionService.TIME_VALID / 2, SessionService.TIME_VALID, TimeUnit.MILLISECONDS);
    }

    public void shutDown() {
        executorService.shutdown();
    }
}