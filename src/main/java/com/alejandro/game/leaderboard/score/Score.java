package com.alejandro.game.leaderboard.score;

import java.util.Objects;

/**
 * Single object representing a score. A score is defined with two fields, userId and score.
 *
 * @author afernandez
 */
public class Score {
    private int userId;
    private int score;

    public Score(int userId, int score) {
        this.userId = userId;
        this.score = score;
    }

    public int getUserId() {
        return userId;
    }

    public int getScore() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Score)) {
            return false;
        }
        Score score = (Score) o;
        return this.userId == score.userId && this.score == score.score;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.userId, this.score);
    }

    @Override
    public String toString() {
        return userId + "=" + score;
    }
}