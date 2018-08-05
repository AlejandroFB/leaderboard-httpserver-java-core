package com.alejandro.game.leaderboard.session;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

/**
 * Unit tests for SessionService.
 *
 * @author afernandez
 */
public class SessionServiceTest {
    private SessionService sessionService;
    private Map<Integer, Session> sessions;

    @Before
    public void init() throws Exception {
        sessionService = new SessionService();

        Field sessionsField = SessionService.class.getDeclaredField("sessions");
        sessionsField.setAccessible(true);
        sessions = (ConcurrentHashMap<Integer, Session>) sessionsField.get(sessionService);
    }

    @Test
    public void refreshSessionKey() throws Exception {
        String userOneKey = sessionService.refreshSessionKey(1);
        String userTwoKey = sessionService.refreshSessionKey(2);

        long createdOnUserOne = sessions.get(1).getCreatedOn();
        long createdOnUserTwo = sessions.get(2).getCreatedOn();

        // Wait for 1 millisecond
        Thread.sleep(1);

        String userOneKeySecondLogin = sessionService.refreshSessionKey(1);
        String userTwoKeySecondLogin = sessionService.refreshSessionKey(2);

        assertNotNull(userOneKey);
        assertNotNull(userTwoKey);

        assertEquals(userOneKey, userOneKeySecondLogin);
        assertEquals(userTwoKey, userTwoKeySecondLogin);

        // Validates whether created on date was refreshed once the user already existed
        assertTrue(sessions.get(1).getCreatedOn() - createdOnUserOne > 0);
        assertTrue(sessions.get(2).getCreatedOn() - createdOnUserTwo > 0);
    }

    @Test
    public void isSessionValid() throws Exception {
        String userOneKey = sessionService.refreshSessionKey(1);

        assertTrue(sessionService.isSessionValid(1, userOneKey));
        assertFalse(sessionService.isSessionValid(1, userOneKey + "Diff"));

        // Session cleaned up after an invalid key
        assertNull(sessions.get(1));

        String userTwoKey = sessionService.refreshSessionKey(2);
        assertTrue(sessionService.isSessionValid(2, userTwoKey));

        LocalDateTime dateInThePast = LocalDateTime.of(2017, 1, 15, 18, 41, 16);
        sessions.get(2).setCreatedOn(dateInThePast.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli());

        // Expired session key
        assertFalse(sessionService.isSessionValid(2, userTwoKey));
    }

    @Test
    public void getUserIdBySessionKey() throws Exception {
        String userOneKey = sessionService.refreshSessionKey(1);
        assertEquals(Integer.valueOf(1), sessionService.getUserIdBySessionKey(userOneKey));
        assertNull(sessionService.getUserIdBySessionKey("NonExistentKey"));
    }

    @Test
    public void removeExpiredSessions() throws Exception {
        sessionService.refreshSessionKey(1);
        sessionService.refreshSessionKey(2);
        sessionService.refreshSessionKey(3);
        sessionService.refreshSessionKey(4);
        sessionService.refreshSessionKey(5);

        long dateInThePast = LocalDateTime.of(2017, 1, 15, 18, 41, 16).toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli();

        sessions.get(3).setCreatedOn(dateInThePast);
        sessions.get(5).setCreatedOn(dateInThePast);

        sessionService.removeExpiredSessions();

        // Validates that the two expired sessions were correctly removed
        assertEquals(3, sessions.size());
        assertNull(sessions.get(3));
        assertNull(sessions.get(5));
    }
}