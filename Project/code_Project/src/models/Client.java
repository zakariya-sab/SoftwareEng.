package models;

public class Client extends User {

    private String _accountNumber;
    private double _balance;

    //construct
    public Client(int userId, String firstName, String lastName, String email,
                  String phoneNumber, String password, String _accountNumber, double _balance) {
        super(userId, firstName, lastName, email, phoneNumber, password, "CLIENT");
        this._accountNumber = _accountNumber;
        this._balance = _balance;
    }

    // get and set
    public String getAccountNumber() { return _accountNumber; }
    public double getBalance() { return _balance; }
    public void setBalance(double _balance) { this._balance = _balance; }

    // the methods
    public boolean login(String email, String password) {
        return (this.email.equals(email) && this.password.equals(password));
    }
    public boolean deposit(double amount) {
        if (amount > 0) {
            this._balance += amount;
            return true;
        }
        return false;
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && this._balance >= amount) {
            this._balance -= amount;
            return true;
        }
        return false;
    }

    public String toString() {
        return ("models.Client["+ firstName + " " + lastName + ", Account: " + _accountNumber + ", Balance: "+_balance  + "]" );
    }
}