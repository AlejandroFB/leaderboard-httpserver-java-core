package com.alejandro.game.leaderboard.session;

/**
 * Session object.
 *
 * @author afernandez
 */
public class Session {
    private int userId;
    private String sessionKey;
    private long createdOn;

    public Session(Builder builder) {
         this.userId = builder.userId;
         this.sessionKey = builder.sessionKey;
         this.createdOn = builder.createdOn;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    public static class Builder {
        private int userId;
        private String sessionKey;
        private long createdOn;

        public Builder withUserId(int userId) {
            this.userId = userId;
            return this;
        }

        public Builder withSessionKey(String sessionKey) {
            this.sessionKey = sessionKey;
            return this;
        }

        public Builder withCreatedOn(long date) {
            this.createdOn = date;
            return this;
        }

        public Session build() { return new Session(this); }
    }
}