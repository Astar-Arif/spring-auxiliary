package com.astar.spring.library.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RequestMetric {
    String requestName;
    List<Throwable> errorList;

    public RequestMetric(String requestURI, ArrayList<Throwable> es) {
        this.requestName = requestURI;
        this.errorList = es;
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
