package com.astar.spring.library.pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthRequest {
    private Object principle;
    private String password;
}
