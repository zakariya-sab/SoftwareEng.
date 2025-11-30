package models;

public abstract class User {

    protected int userId;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String phoneNumber;
    protected String password;
    protected String userType;

    // Constor
    public User(int userId, String firstName, String lastName, String email,
                String phoneNumber, String password, String userType) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.userType = userType;
    }

    // set and get :
    public int getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPassword() { return password; }  // Changed getter name
    public void setPassword(String password) { this.password = password; }  // Changed setter name
    public String getUserType() { return userType; }

    // method to implement
    public abstract boolean login(String email, String password);
}