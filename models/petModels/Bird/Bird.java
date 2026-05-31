package models.petModels.Bird;

import models.petModels.Pet.Pet;

public class Bird extends Pet {
    private String wingSpan;
    private boolean canFly;

    public Bird(String name, int age, String breed, String gender, Boolean Vaccinated, String description, String wingSpan, boolean canFly, int adopted) {
        super(name, age, breed, gender, Vaccinated, description, adopted);
        this.wingSpan = wingSpan;
        this.canFly = canFly;
    }

    public Bird() {
        super();
        this.wingSpan = "";
        this.canFly = false;
    }

    public String getWingSpan() {
        return wingSpan;
    }

    public void setWingSpan(String wingSpan) {
        this.wingSpan = wingSpan;
    }

    public boolean canFly() {
        return canFly;
    }

    public void setCanFly(boolean canFly) {
        this.canFly = canFly;
    }
}
