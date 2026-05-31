package models.petModels.Cat;

import models.petModels.Pet.Pet;

public class Cat extends Pet {
    private String furColor;
    private boolean isLitterTrained;
    private String breed;

    public Cat(String name, int age, String breed, String gender, Boolean Vaccinated, String description, String furColor, boolean isLitterTrained, int adopted) {
        super(name, age, breed, gender, Vaccinated, description, adopted);
        this.furColor = furColor;
        this.isLitterTrained = isLitterTrained;
        this.breed = breed;
    }

    public Cat() {
        super();
        this.furColor = "";
        this.isLitterTrained = false;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        super.setBreed(breed);
        this.breed = breed;
    }

    public String getFurColor() {
        return furColor;
    }

    public void setFurColor(String furColor) {
        this.furColor = furColor;
    }

    public boolean isLitterTrained() {
        return isLitterTrained;
    }

    public void setLitterTrained(boolean litterTrained) {
        isLitterTrained = litterTrained;
    }
}
