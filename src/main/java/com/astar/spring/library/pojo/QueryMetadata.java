package com.astar.spring.library.pojo;

public class QueryMetadata<T> {
    T data;
    int currPage;
    int totalPage;
    long currRowCount;
    long totalRowCount;

    public QueryMetadata() {
    }

    public QueryMetadata(
            T data, int currPage, int totalPage, long currRowCount, long totalRowCount) {
        this.data = data;
        this.currPage = currPage;
        this.totalPage = totalPage;
        this.currRowCount = currRowCount;
        this.totalRowCount = totalRowCount;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public long getCurrRowCount() {
        return currRowCount;
    }

    public void setCurrRowCount(long currRowCount) {
        this.currRowCount = currRowCount;
    }

    public long getTotalRowCount() {
        return totalRowCount;
    }

    public void setTotalRowCount(long totalRowCount) {
        this.totalRowCount = totalRowCount;
    }
}
