package com.astar.spring.library.pojo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicContext {

    private static final AtomicReference<AtomicContext> INSTANCE = new AtomicReference<>();

    private final Map<String, Object> context = new HashMap<>();

    // Private constructor for singleton pattern
    private AtomicContext() {
    }

    public static AtomicContext getInstance() {
        AtomicContext currentInstance = INSTANCE.get();
        if (currentInstance == null) {
            AtomicContext newInstance = new AtomicContext();
            if (INSTANCE.compareAndSet(null, newInstance)) {
                currentInstance = newInstance;
            } else {
                currentInstance = INSTANCE.get();
            }
        }
        return currentInstance;
    }

    public void set(String key, Object value) {
        context.put(key, value);
    }

    public Object get(String key) {
        return context.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object value = context.get(key);
        if (type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    public void remove(String key) {
        context.remove(key);
    }

    public void clear() {
        context.clear();
    }
}
