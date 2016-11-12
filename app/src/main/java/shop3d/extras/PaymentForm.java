package shop3d.extras;

public interface PaymentForm {
    public String getCardHolderName();
    public String getCardNumber();
    public String getCvc();
    public Integer getExpMonth();
    public Integer getExpYear();
    public String getZipcode();
}
