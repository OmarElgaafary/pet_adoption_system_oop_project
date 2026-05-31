package models.petModels.Dog;

import models.petModels.Pet.Pet;

public class Dog extends Pet {
    private String size; // Small, Medium, Large
    private boolean isTrained;
    private String breed;

    public Dog(String name, int age, String breed, String gender, Boolean Vaccinated, String description, String size, boolean isTrained, int adopted) {
        super(name, age, breed, gender, Vaccinated, description, adopted);
        this.breed = breed;
        this.size = size;
        this.isTrained = isTrained;
    }

    public Dog() {
        super();
        this.size = "";
        this.isTrained = false;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        super.setBreed(breed);
        this.breed = breed;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean isTrained() {
        return isTrained;
    }

    public void setTrained(boolean trained) {
        isTrained = trained;
    }
}
