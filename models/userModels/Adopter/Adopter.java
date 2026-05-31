package models.userModels.Adopter;

import models.petModels.Pet.Pet;
import models.userModels.User.User;

public class Adopter extends User {
    private int phoneNumber;
    private String Address;
    private String favPetType;
    private Pet[] previousPets;
    private double accountBalance;

    public Adopter(String firstName, String lastName, int age, String emailAddress, String password, int phoneNumber, String address, String favPetType, Pet[] previousPets) {
        super(firstName, lastName, age, emailAddress, password);
        this.phoneNumber = phoneNumber;
        this.Address = address;
        this.favPetType = favPetType;
        this.previousPets = previousPets;
    }

    public Adopter() {
        super();
        this.phoneNumber = 0;
        this.Address = "";
        this.favPetType = "";
        this.previousPets = new Pet[0];
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public String getFavPetType() {
        return favPetType;
    }

    public void setFavPetType(String favPetType) {
        this.favPetType = favPetType;
    }

    public Pet[] getPreviousPets() {
        return previousPets;
    }

    public void setPreviousPets(Pet[] previousPets) {
        this.previousPets = previousPets;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }
}
