package com.astar.spring.library.pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthRequest {
    private Object principal;
    private String password;
    private Object principalType;
}
