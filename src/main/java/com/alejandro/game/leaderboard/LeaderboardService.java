package com.alejandro.game.leaderboard;

import com.alejandro.game.leaderboard.score.ScoreService;
import com.alejandro.game.leaderboard.session.SessionService;

import java.util.logging.Logger;

/**
 * Main leaderboard service. It creates and holds instances of the application services and provides the specified
 * functionality:
 * <p>
 *     Generates session keys for login endpoint
 *     Adds a new score to a specific level
 *     Returns a CSV representation of the leaderboard
 *
 * @author afernandez
 */
public class LeaderboardService {
    private static final Logger LOGGER = Logger.getLogger(LeaderboardService.class.getName());

    private ScoreService scoreService;
    private SessionService sessionService;

    public LeaderboardService(SessionService sessionService, ScoreService scoreService) {
        this.sessionService = sessionService;
        this.scoreService = scoreService;
    }

    /**
     * Gets a new session key for a user id or refreshes the existing one.
     *
     * @param userId The user id
     * @return The session key
     */
    public String getSessionKey(int userId) {
        LOGGER.info(String.format("Get session key run. User ID: %s", userId));
        return sessionService.refreshSessionKey(userId);
    }

    /**
     * Adds a score to a specific level in the leaderboard.
     *
     * @param levelId The level id
     * @param score The score number
     * @param sessionKey The session key identifying the user
     */
    public void addScore(int levelId, int score, String sessionKey) {
        LOGGER.info(String.format("Add score run. Level ID: %s, Score: %s, Session key: %s", levelId, score, sessionKey));

        int userId = sessionService.getUserIdBySessionKey(sessionKey);
        scoreService.addScore(levelId, userId, score);
    }

    /**
     * Returns a CSV representation of the highest score in a specific level.
     *
     * @param levelId The level id
     * @return The string CSV representing the scores
     */
    public String getHighScores(int levelId) {
        LOGGER.info(String.format("Get high scores run. Level ID: %s", levelId));
        return scoreService.convertToCsv(levelId);
    }
}