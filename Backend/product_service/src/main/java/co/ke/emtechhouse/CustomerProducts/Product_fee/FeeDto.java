package co.ke.emtechhouse.CustomerProducts.Product_fee;

public interface FeeDto {
    public String getEventIdCode();
    public String getEventTypeDesc();
    public String getChargeCollectionAccount();
    public Double getInitialAmt();
    public Double getMonthlyAmount();
}
