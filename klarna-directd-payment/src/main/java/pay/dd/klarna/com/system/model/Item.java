package pay.dd.klarna.com.system.model;

public class Item {
    private  String type;
    private  String description;
    private  int quantity;
    private  String currency;
    private  int amount;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Item(String type, String description, int quantity, String currency, int amount) {
        this.type = type;
        this.description = description;
        this.quantity = quantity;
        this.currency = currency;
        this.amount = amount;
    }

    public Item(String type, String description, String currency, int amount) {
        this.type = type;
        this.description = description;
        this.currency = currency;
        this.amount = amount;
    }
    public Item() {

    }

}
