package com.alejandro.game.leaderboard;

import com.alejandro.game.util.RequestParser;
import com.alejandro.game.leaderboard.score.ScoreService;
import com.alejandro.game.leaderboard.session.CleanUpSessionJob;
import com.alejandro.game.leaderboard.session.SessionService;

/**
 * Factory class meant to instantiate the needed services that are going to interact with the leaderboard application.
 * <p>
 * Services are instantiated in this class and only in this class in order to maintain one instance of them per
 * application.
 *
 * @author afernandez
 */
public class LeaderboardServiceFactory {
    private SessionService sessionService;
    private CleanUpSessionJob cleanUpSessionJob;
    private ScoreService scoreService;
    private RequestParser requestParser;

    public LeaderboardServiceFactory() {
        this.sessionService = new SessionService();
        this.cleanUpSessionJob = new CleanUpSessionJob(sessionService);
        this.scoreService = new ScoreService();
        this.requestParser = new RequestParser();
    }

    /**
     * Instantiates the global session service and the main application services.
     * 
     * @return The main leaderboard service that the application uses for its main functionality
     */
    public LeaderboardService createLeaderboardService() {
        return new LeaderboardService(sessionService, scoreService);
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public CleanUpSessionJob getCleanUpSessionJob() {
        return cleanUpSessionJob;
    }

    public ScoreService getScoreService() {
        return scoreService;
    }

    public RequestParser getRequestParser() {
        return requestParser;
    }
}