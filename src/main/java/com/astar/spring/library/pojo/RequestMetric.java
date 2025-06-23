package com.astar.spring.library.pojo;


import java.util.ArrayList;
import java.util.List;

/**
 * 1. IP
 * 2. Time Taken
 *
 *
 *
 */
public class RequestMetric {
    String requestName;
    List<Throwable> errorList;

    public RequestMetric(String requestURI, ArrayList<Throwable> es) {
        this.requestName = requestURI;
        this.errorList = es;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public List<Throwable> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<Throwable> errorList) {
        this.errorList = errorList;
    }

    public void addError(Throwable t) {
        this.errorList.add(t);
    }

    @Override
    public String toString() {
        return "RequestMetric : \n{" +
                "\n\trequestName='" + requestName + '\'' +
                ",\n\terrorList=" + errorList +
                "\n}";
    }

}
