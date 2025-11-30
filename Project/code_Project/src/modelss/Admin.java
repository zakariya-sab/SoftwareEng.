package models;

public class Admin extends User {
    //construct0r
    public Admin(int userId, String firstName, String lastName, String email,
                 String phoneNumber, String password) {
        super(userId, firstName, lastName, email, phoneNumber, password, "ADMIN");
    }
    // methode
    public boolean login(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }

    public String toString() {
        return ("models.Admin["+ firstName + " " + lastName + ", Email: " + email + "]");
    }
}