package com.astar.spring.library.pojo;

public class RequestMetricContext {
    private static final ThreadLocal<RequestMetric> context = new ThreadLocal<>();

    public static void set(RequestMetric metric) {
        context.set(metric);
    }

    public static RequestMetric get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }

    public static void recordError(Throwable ex) {
        RequestMetric metric = RequestMetricContext.get();
        if (metric != null) {
            metric.getErrorList().add(ex);
        }
    }
}