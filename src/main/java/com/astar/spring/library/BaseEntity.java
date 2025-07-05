package com.astar.spring.library;

import com.astar.java.library.utils.UniqueUtility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseEntity<ID extends Serializable> extends AbstractPersistable<ID> {

    public static final int DEFAULT_DB_STRING_LENGTH_LIMIT = 255;
    private static final String DEFAULT_CREATOR = "SYSTEM";
    @Column
    private UUID uuid;

    @CreatedBy
    @JsonIgnore
    private String createdByID;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @JsonIgnore
    private Instant createdAt;

    @LastModifiedBy
    @JsonIgnore
    private String modifiedBy;

    @LastModifiedDate
    @JsonIgnore
    private Instant modifiedDate;

    @Version
    @JsonIgnore
    //!
    private Long version;

    @PrePersist
    private void prePersistBase() {
        // Set default values if fields are null before the entity is persisted
        if (this.createdByID == null) {
            this.createdByID = DEFAULT_CREATOR;
        }
        if (this.createdAt == null) {
            this.createdAt = Instant.now();  // Use LocalDateTime.now() for current date
        }
        if (this.modifiedBy == null) {
            this.modifiedBy = DEFAULT_CREATOR;
        }
        if (this.modifiedDate == null) {
            this.modifiedDate = Instant.now();  // Use LocalDateTime.now() for current date
        }
        if (this.uuid == null) {
            this.uuid = UniqueUtility.generateUUID((byte) 7);
        }
    }

    @PreUpdate
    private void preUpdateBase() {
        if (this.modifiedBy == null) {
            this.modifiedBy = DEFAULT_CREATOR;
        }
        if (this.modifiedDate == null) {
            this.modifiedDate = Instant.now();  // Set modification date on updates
        }
    }

    public Object get(String fieldStr) throws NoSuchFieldException, IllegalAccessException {
        Field field = getClass().getDeclaredField(fieldStr);
        field.setAccessible(true);
        return field.get(this);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getCreatedByID() {
        return createdByID;
    }

    public void setCreatedByID(String createdByID) {
        this.createdByID = createdByID;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Instant modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
