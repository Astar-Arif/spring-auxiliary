package com.astar.spring.library;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity<ID extends Serializable> extends AbstractPersistable<ID> {

    private static final String DEFAULT_CREATOR = "SYSTEM";

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
}
