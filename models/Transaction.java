package models;

import java.time.LocalDateTime;
public class Transaction {

    private int transactionId;
    private String fromAccountNumber;
    private String toAccountNumber;
    private double amount;
    private String transactionType; //
    // this the status for the transactionType :  "DEPOSIT", "WITHDRAWAL", "TRANSFER"
    private LocalDateTime timestamp;
    //for time ask deepseek to get more informmetion about it !!!
    private String description;
//constructor
    public Transaction(int transactionId, String fromAccountNumber, String toAccountNumber,
                       double amount, String transactionType, LocalDateTime timestamp, String description) {
        this.transactionId = transactionId;
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
        this.transactionType = transactionType;
        this.timestamp = timestamp;
        this.description = description;
    }

    // get and set
    public int getTransactionId() { return transactionId; }
    public String getFromAccountNumber() { return fromAccountNumber; }
    public String getToAccountNumber() { return toAccountNumber; }
    public double getAmount() { return amount; }
    public String getTransactionType() { return transactionType; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return "models.Transaction[ " +transactionType +": "+ amount +" from "+ fromAccountNumber +" to "+ toAccountNumber +" at " + timestamp +"]";
    }
}