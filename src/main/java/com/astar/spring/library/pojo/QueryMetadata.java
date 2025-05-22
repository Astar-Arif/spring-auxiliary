package com.astar.spring.library.pojo;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@NotBlank
@Getter
@Setter
public class QueryMetadata<T> {
    T data;
    int currPage;
    int totalPage;
    long currRowCount;
    long totalRowCount;
}
