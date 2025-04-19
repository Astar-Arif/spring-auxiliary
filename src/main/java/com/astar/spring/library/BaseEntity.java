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
@Getter @Setter
public abstract class BaseEntity<ID extends Serializable> extends AbstractPersistable<ID> {

    @CreatedBy
    @JsonIgnore
    private ID createdByID;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    @JsonIgnore
    private LocalDateTime createdAt;

    @LastModifiedBy
    @JsonIgnore
    private LocalDateTime modifiedBy;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private LocalDateTime modifiedDate;

    @Version
    @JsonIgnore
    private Long version;
}
