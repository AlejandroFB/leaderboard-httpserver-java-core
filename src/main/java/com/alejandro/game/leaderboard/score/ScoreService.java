package com.alejandro.game.leaderboard.score;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * HttpHandler to process incoming requests to add scores to a level.
 *
 * @author afernandez
 */
public class ScoreService {
    private static final Logger LOGGER = Logger.getLogger(ScoreService.class.getName());

    private static final int LEADERBOARD_SIZE = 15;

    private ConcurrentHashMap<Integer, ConcurrentSkipListSet<Score>> leaderboard;

    public ScoreService() {
        this.leaderboard = new ConcurrentHashMap<>();
    }

    /**
     * Adds a score to the leaderboard. Only the highest score for a user counts, that means only one user appears
     * at most in the higher scores for a level.
     *
     * @param levelId The id of the level
     * @param userId The user id
     * @param scoreNumber The score to add
     */
    public void addScore(int levelId, int userId, int scoreNumber) {
        ConcurrentSkipListSet<Score> scores = leaderboard.get(levelId);

        if (scores == null) {
            scores = new ConcurrentSkipListSet<>(Comparator.comparing(Score::getScore).thenComparing(Score::getUserId).reversed());
            scores.add(new Score(userId, scoreNumber));

            leaderboard.putIfAbsent(levelId, scores);
            return;
        }

        Score score = scores.stream()
            .filter(elem -> userId == elem.getUserId())
            .findFirst()
            .orElse(null);

        if (score != null) {
            LOGGER.info(String.format("Adding existing user %s with score %s to level %s", userId, scoreNumber, levelId));
            addExistingScoreToExistingLevel(scores, score, userId, scoreNumber);
        } else {
            LOGGER.info(String.format("Adding new user %s with score %s to level %s", userId, scoreNumber, levelId));
            addNewScoreToExistingLevel(scores, userId, scoreNumber);
        }
    }

    /**
     * Convert to CSV format the high score list for a specific level.
     *
     * @param levelId The level id
     * @return String representing the high score list in CSV format
     */
    public String convertToCsv(int levelId) {
        ConcurrentSkipListSet<Score> scores = leaderboard.get(levelId);

        if (scores == null) {
            return "";
        }

        return scores.stream()
                .map(Score::toString)
                .collect(Collectors.joining(","));
    }

    private void addExistingScoreToExistingLevel(ConcurrentSkipListSet<Score> scores, Score score, int userId, int scoreNumber) {
        if (scoreNumber > score.getScore()) {
            scores.remove(score);
            scores.add(new Score(userId, scoreNumber));
        }
    }

    private void addNewScoreToExistingLevel(ConcurrentSkipListSet<Score> scores, int userId, int scoreNumber) {
        if (scores.size() < LEADERBOARD_SIZE){
            scores.add(new Score(userId, scoreNumber));
        } else {
            Score lowestElement = scores.last();

            if (scoreNumber > lowestElement.getScore()) {
                scores.pollLast();
                scores.add(new Score(userId, scoreNumber));
            }
        }
    }
}