package com.astar.spring.library.pojo;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


public class PrincipalAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private Integer principalType;

    public PrincipalAuthenticationToken(
            Object principal, Object credentials, Integer principleType) {
        super(principal, credentials);
        this.principalType = principleType;
    }

    public Integer getPrincipalType() {
        return principalType;
    }

    public void setPrincipalType(Integer principalType) {
        this.principalType = principalType;
    }
}
