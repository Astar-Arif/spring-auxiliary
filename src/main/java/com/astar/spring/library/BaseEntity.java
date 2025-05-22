package com.astar.spring.library;

import com.astar.common.library.utils.UniqueUtility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseEntity<ID extends Serializable> extends AbstractPersistable<ID> {

    private static final String DEFAULT_CREATOR = "SYSTEM";

    @Column
    private UUID uuid;

    @CreatedBy
    @JsonIgnore
    private String createdByID;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    @JsonIgnore
    private LocalDateTime createdAt;

    @LastModifiedBy
    @JsonIgnore
    private String modifiedBy;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private LocalDateTime modifiedDate;

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
            this.createdAt = LocalDateTime.now();  // Use LocalDateTime.now() for current date
        }
        if (this.modifiedBy == null) {
            this.modifiedBy = DEFAULT_CREATOR;
        }
        if (this.modifiedDate == null) {
            this.modifiedDate = LocalDateTime.now();  // Use LocalDateTime.now() for current date
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
            this.modifiedDate = LocalDateTime.now();  // Set modification date on updates
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
