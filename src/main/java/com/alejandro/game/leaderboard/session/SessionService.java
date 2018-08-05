package com.alejandro.game.leaderboard.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Singleton session service to provide session key management to the application.
 * <p>
 * The class initialization is on-demand (lazy load) and performed the first time is used.
 *
 * @author afernandez
 */
public class SessionService {
    private static final Logger LOGGER = Logger.getLogger(SessionService.class.getName());

    public static final int TIME_VALID = 600000;

    private Map<Integer, Session> sessions;

    public SessionService() {
        sessions = new ConcurrentHashMap<>();
    }

    /**
     * Generates a new session key and replaces for a new one if it was already existing.
     *
     * @param userId The used id to associate the key with
     * @return The new session key
     */
    public String refreshSessionKey(int userId) {
        Session session = sessions.get(userId);

        if (session == null) {
            // 2 consequent requests for same userId that enter the 'if (session == null)' statement:
            // Second one could override the first session ID created for the first request, that makes the first
            // session returned for the first request invalid. Solutions:

            // ComputeIfAbsent: Compute the generation of session key/session object only if user ID is null.
            sessions.computeIfAbsent(userId, key -> generateSession(key));

        } else {
            session.setCreatedOn(System.currentTimeMillis());
            sessions.replace(userId, session);
        }

        return sessions.get(userId).getSessionKey();
    }

    private Session generateSession(int userId) {
        final String sessionKey = UUID.randomUUID().toString().replace("-", "");

        return new Session.Builder()
                .withUserId(userId)
                .withSessionKey(sessionKey)
                .withCreatedOn(System.currentTimeMillis())
                .build();
    }

    /**
     * Checks whether a session is valid. If it's still valid it refreshes the session again, otherwise
     * it deletes the session and the user will have to login again.
     *
     * @param userId The user's id
     * @param sessionKey The session key
     * @return True if valid, false otherwise
     */
    public boolean isSessionValid(int userId, String sessionKey) {
        Session session = sessions.get(userId);
        long now = System.currentTimeMillis();

        if (session == null) {
            return false;
        }

        if (sessionKey.equals(session.getSessionKey()) && now - session.getCreatedOn() < TIME_VALID) {
            refreshSessionKey(userId);
            return true;
        }

        sessions.remove(userId);
        return false;
    }

    /**
     * Get the user id associated to a given session key.
     *
     * NOTE: This linear method will be too slow for millions of users, so I need a better way to handle this.
     * Possibilities:
     * <p>
     *     - Create additional HashMap<String, String>: session key => user id to also be able to do a get in
     *     constant time for the user ID.
     *     - To avoid the memory space of having two big HashMaps, what could be the possibilities for the application
     *     if I only decide to store one Map and is Session => user id? In this case the system could generate many
     *     different keys for the same user Id, but as long as the player uses one that is still valid in the request
     *     and exist in the map associated to a player ID then it will work, without doing a get by user ID to retrieve
     *     an existing key when login.
     *
     * @param sessionKey The session key
     * @return The user id
     */
    public Integer getUserIdBySessionKey(String sessionKey) {
        return sessions.keySet().stream()
                .filter(key -> sessionKey.equals(sessions.get(key).getSessionKey()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Removes the expired sessions from the list.
     * Sessions are valid for 10 minutes.
     */
    public void removeExpiredSessions() {
        LOGGER.info("Cleaning up expired sessions...");

        if (sessions != null) {
            long now = System.currentTimeMillis();
            sessions.keySet().forEach(key -> removeSession(key, now));
        }
    }

    private void removeSession(int userId, long now) {
        if (now - sessions.get(userId).getCreatedOn() >= TIME_VALID) {
            sessions.remove(userId);
        }
    }
}