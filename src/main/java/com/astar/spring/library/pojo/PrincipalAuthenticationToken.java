package com.astar.spring.library.pojo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
@Setter
public class PrincipalAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private Integer principalType;

    public PrincipalAuthenticationToken(
            Object principal, Object credentials, Integer principleType) {
        super(principal, credentials);
        this.principalType = principleType;
    }
}
