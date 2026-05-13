package com.paymate.paymate.module.auth.domain;

/**
 * Defines the access roles available in PayMate.
 *
 * <p>Roles control which endpoints a user can access:</p>
 * <ul>
 *   <li>{@link #USER} — standard customer, can make payments</li>
 *   <li>{@link #MERCHANT} — business account, can accept payments</li>
 *   <li>{@link #ADMIN} — platform operator, can access all data</li>
 * </ul>
 *
 * <p>Stored as strings in the database (not integers) so that
 * reordering this enum never corrupts existing data.</p>
 *
 * @author Shubham Kumbhar
 */
public enum Role {

    /** Standard customer — can load, send, and receive money. */
    USER,

    /** Business account — can create payment links and accept payments. */
    MERCHANT,

    /** Platform operator — full system access for administration. */
    ADMIN
}