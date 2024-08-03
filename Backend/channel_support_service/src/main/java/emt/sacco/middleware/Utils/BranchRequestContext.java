package emt.sacco.middleware.Utils;

public class BranchRequestContext {
    private static ThreadLocal<String> branchId = new InheritableThreadLocal<>();


    public static String getCurrentBranchId() {
        return branchId.get();
    }


    public static void setCurrentEntityId(String tenant) {
        branchId.set(tenant);
    }

    public static void clear() {
        branchId.set(null);
    }
}
