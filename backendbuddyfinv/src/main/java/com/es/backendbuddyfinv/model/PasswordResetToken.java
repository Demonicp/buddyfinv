package com.es.backendbuddyfinv.model;

public class PasswordResetToken {
    private String code;
    private long expiresAt; // epoch millis
    private int attemptsLeft;
    private boolean verified;
    private long cooldownUntil; // epoch millis

    public PasswordResetToken(String code, long expiresAt, int attemptsLeft) {
        this.code = code;
        this.expiresAt = expiresAt;
        this.attemptsLeft = attemptsLeft;
        this.verified = false;
        this.cooldownUntil = 0L;
    }

    // getters & setters
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public long getExpiresAt() {
        return expiresAt;
    }
    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }
    public void setAttemptsLeft(int attemptsLeft) {
        this.attemptsLeft = attemptsLeft;
    }

    public boolean isVerified() {
        return verified;
    }
    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public long getCooldownUntil() {
        return cooldownUntil;
    }
    public void setCooldownUntil(long cooldownUntil) {
        this.cooldownUntil = cooldownUntil;
    }

}
