package com.alejandro.game.leaderboard;

import com.alejandro.game.leaderboard.score.ScoreService;
import com.alejandro.game.leaderboard.session.SessionService;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for LeaderboardService.
 *
 * @author afernandez
 */
public class LeaderboardServiceTest {

    @Mock
    private ScoreService scoreService;
    @Mock
    private SessionService sessionService;
    @InjectMocks
    private LeaderboardService leaderboardService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void getSessionKey() throws Exception {
        leaderboardService.getSessionKey(1);
        verify(sessionService).refreshSessionKey(1);
    }

    @Test
    public void addScore() throws Exception {
        when(sessionService.getUserIdBySessionKey("UserKey")).thenReturn(15);

        leaderboardService.addScore(1, 500, "UserKey");

        verify(scoreService).addScore(1, 15, 500);
    }

    @Test
    public void getHighScores() throws Exception {
        leaderboardService.getHighScores(1);
        verify(scoreService).convertToCsv(1);
    }
}