package com.paymate.paymate.module.auth.domain;

/**
 * Represents the KYC (Know Your Customer) verification state of a user.
 *
 * <p>KYC is a financial regulation requirement to verify user identity
 * before allowing certain transactions. In PayMate, KYC status controls
 * transaction limits:</p>
 * <ul>
 *   <li>{@link #PENDING} — can make small transfers only</li>
 *   <li>{@link #VERIFIED} — full transaction limits unlocked</li>
 *   <li>{@link #REJECTED} — identity verification failed, limited access</li>
 * </ul>
 *
 * @author Shubham Kumbhar
 */
public enum KycStatus {

    /** Identity not yet verified. Default for all new users. */
    PENDING,

    /** Identity successfully verified. Full access granted. */
    VERIFIED,

    /** Identity verification failed or documents rejected. */
    REJECTED
}