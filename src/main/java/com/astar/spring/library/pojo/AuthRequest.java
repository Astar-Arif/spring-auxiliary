package com.astar.spring.library.pojo;


public class AuthRequest {
    private Object principal;
    private String password;
    private Object principalType;

    public Object getPrincipal() {
        return principal;
    }

    public void setPrincipal(Object principal) {
        this.principal = principal;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Object getPrincipalType() {
        return principalType;
    }

    public void setPrincipalType(Object principalType) {
        this.principalType = principalType;
    }
}
