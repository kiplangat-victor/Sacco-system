package com.emtechhouse.usersservice.utils.HttpInterceptor;

public class EntityRequestContext {
    private static ThreadLocal<String> currentEntityId = new InheritableThreadLocal<>();

    public static String getCurrentEntityId() {
        return currentEntityId.get();
    }

    public static void setCurrentEntityId(String entityId) {
        currentEntityId.set(entityId);
    }

    public static void clear() {
        currentEntityId.set(null);
    }
}
