package com.sismics.docs.core.model.jpa;

import com.google.common.base.MoreObjects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;

/**
 * UserCreateRequest entity.
 * 
 * Represents a user's account creation request.
 */
@Entity
@Table(name = "T_USER_CREATE_REQUEST")
public class UserCreateRequest implements Loggable {
    /**
     * Request ID.
     */
    /**
     * Request ID.
     */
    @Id
    @Column(name = "USE_ID_C", length = 36)
    private String id;
    
    /**
     * Role ID for the requested account.
     */
    @Column(name = "USE_IDROLE_C", nullable = false, length = 36)
    private String roleId;
    
    /**
     * Requested username.
     */
    @Column(name = "USE_USERNAME_C", nullable = false, length = 50)
    private String username;
    
    /**
     * Requested password.
     */
    @Column(name = "USE_PASSWORD_C", nullable = false, length = 100)
    private String password;

    /**
     * Email address in the request.
     */
    @Column(name = "USE_EMAIL_C", nullable = false, length = 100)
    private String email;

    /**
     * Storage quota requested.
     */
    @Column(name = "USE_STORAGEQUOTA_N", nullable = false)
    private Long storageQuota;
    
    /**
     * Request status: PENDING, APPROVED, or REJECTED.
     */
    @Column(name = "REQ_STATUS_C", nullable = false, length = 20)
    private String status;

    /**
     * ID of the user who created the request (nullable for anonymous).
     */
    @Column(name = "REQ_CREATORID_C", length = 36)
    private String creatorId;

    /**
     * ID of the admin who reviewed the request.
     */
    @Column(name = "REQ_REVIEWERID_C", length = 36)
    private String reviewerId;

    /**
     * Date when the request was created.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "USE_CREATEDATE_D", nullable = false)
    private Date createDate;

    /**
     * Date when the request was reviewed.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REQ_REVIEWDATE_D")
    private Date reviewDate;

    /**
     * Reason provided when rejecting the request.
     */
    @Column(name = "REQ_REASON_C", length = 500)
    private String reason;

    /**
     * Required by Loggable. Not applicable for requests.
     */
    @Override
    public Date getDeleteDate() {
        return null;
    }

    // ... existing getters/setters ...
    public String getId() {
        return id;
    }

    public UserCreateRequest setId(String id) {
        this.id = id;
        return this;
    }

    public String getRoleId() {
        return roleId;
    }

    public UserCreateRequest setRoleId(String roleId) {
        this.roleId = roleId;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserCreateRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserCreateRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserCreateRequest setEmail(String email) {
        this.email = email;
        return this;
    }

    public Long getStorageQuota() {
        return storageQuota;
    }

    public UserCreateRequest setStorageQuota(Long storageQuota) {
        this.storageQuota = storageQuota;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public UserCreateRequest setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public UserCreateRequest setCreatorId(String creatorId) {
        this.creatorId = creatorId;
        return this;
    }

    public String getReviewerId() {
        return reviewerId;
    }

    public UserCreateRequest setReviewerId(String reviewerId) {
        this.reviewerId = reviewerId;
        return this;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public UserCreateRequest setCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public UserCreateRequest setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
        return this;
    }

    public String getReason() {
        return reason;
    }

    public UserCreateRequest setReason(String reason) {
        this.reason = reason;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("username", username)
                .add("email", email)
                .add("status", status)
                .toString();
    }

    @Override
    public String toMessage() {
        return username;
    }
}
