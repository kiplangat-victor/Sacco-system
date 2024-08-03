package co.ke.emtechhouse.CustomerProducts.Dtos;




public interface LookupDto {
    public String getProductCode();
    String getProductType();
    public String getProductDescription();
    Character getDeletedFlag();
    Character getVerifiedFlag();
}
