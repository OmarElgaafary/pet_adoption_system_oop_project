package models.userModels.User;

public class User {
    private int userId;
    private String firstName;
    private String lastName;
    private int age;
    private String emailAddress;
    private String password;

    public User(int userId, String firstName, String lastName, int age, String emailAddress, String password) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public User(String firstName, String lastName, int age, String emailAddress, String password) {
        this(0, firstName, lastName, age, emailAddress, password);
    }

    public User() {
        this(0, "", "", 0, "", "");
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
