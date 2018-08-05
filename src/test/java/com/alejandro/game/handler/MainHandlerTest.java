package com.alejandro.game.handler;

import com.alejandro.game.leaderboard.LeaderboardService;
import com.alejandro.game.util.RequestParser;
import com.alejandro.game.leaderboard.session.SessionService;
import com.sun.net.httpserver.HttpExchange;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.OutputStream;
import java.net.URI;

import static org.mockito.Mockito.*;

/**
 * Unit tests for MainHandler.
 *
 * @author afernandez
 */
public class MainHandlerTest {
    private static final String URI_LOGIN       = "http://domain:8081/4/login";
    private static final String URI_SCORE       = "http://domain:8081/4/score?sessionkey=f93623d5c7c44a89ad90e78333ae9763";
    private static final String URI_LEADERBOARD = "http://domain:8081/4/highscorelist";

    private static final String SESSION_KEY     = "TestSessionKey";

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private HttpExchange httpExchange;
    @Mock
    private LeaderboardService leaderboardService;
    @Mock
    private RequestParser requestParser;
    @Mock
    private SessionService sessionService;
    @InjectMocks
    private MainHandler mainHandler;

    @Before
    public void init() throws Exception {
        when(httpExchange.getResponseBody()).thenReturn(mock(OutputStream.class));
    }

    @Test
    public void handleLoginRequest() throws Exception {
        when(httpExchange.getRequestURI()).thenReturn(new URI(URI_LOGIN));
        when(httpExchange.getAttribute(MainHandler.REQUEST)).thenReturn(MainHandler.LOGIN_REQUEST);
        when(requestParser.getUserOrLevelId("/4/login")).thenReturn(4);
        when(leaderboardService.getSessionKey(4)).thenReturn(SESSION_KEY);

        mainHandler.handle(httpExchange);

        verify(leaderboardService).getSessionKey(4);
    }

    @Test
    public void handleScoreRequest() throws Exception {
        when(httpExchange.getRequestURI()).thenReturn(new URI(URI_SCORE));
        when(httpExchange.getAttribute(MainHandler.REQUEST)).thenReturn(MainHandler.SCORE_REQUEST);
        when(requestParser.getUserOrLevelId("/4/score")).thenReturn(4);
        when(requestParser.getQueryValue("sessionkey=f93623d5c7c44a89ad90e78333ae9763")).thenReturn(SESSION_KEY);
        when(sessionService.isSessionValid(4, SESSION_KEY)).thenReturn(true);
        when(sessionService.getUserIdBySessionKey(SESSION_KEY)).thenReturn(4);
        when(httpExchange.getAttribute(MainHandler.SCORE_ATTR)).thenReturn(1500);

        mainHandler.handle(httpExchange);

        verify(sessionService).isSessionValid(4, SESSION_KEY);
        verify(leaderboardService).addScore(4, 1500, SESSION_KEY);
    }

    @Test
    public void handleLeaderboardRequest() throws Exception {
        when(httpExchange.getRequestURI()).thenReturn(new URI(URI_LEADERBOARD));
        when(httpExchange.getAttribute(MainHandler.REQUEST)).thenReturn(MainHandler.LEADERBOARD_REQUEST);
        when(requestParser.getUserOrLevelId("/4/highscorelist")).thenReturn(4);
        when(leaderboardService.getHighScores(4)).thenReturn("25=1500");

        mainHandler.handle(httpExchange);

        verify(leaderboardService).getHighScores(4);
    }
}