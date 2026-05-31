package models.petModels.Pet;

public class Pet {
    private int petId;
    private String name;
    private int age;
    private String breed;
    private String gender;
    private Boolean Vaccinated;
    private String description;
    private int adopted;

    public Pet(int petId, String name, int age, String breed, String gender, Boolean Vaccinated, String description, int adopted) {
        this.petId = petId;
        this.name = name;
        this.age = age;
        this.breed = breed;
        this.gender = gender;
        this.Vaccinated = Vaccinated;
        this.description = description;
        this.adopted = adopted;
    }

    public Pet(String name, int age, String breed, String gender, Boolean Vaccinated, String description, int adopted) {
        this(0, name, age, breed, gender, Vaccinated, description, adopted);
    }

    public Pet() {
        this(0, "", 0, "", "", false, "", 0);
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getVaccinated() {
        return Vaccinated;
    }

    public void setVaccinated(Boolean vaccinated) {
        Vaccinated = vaccinated;
    }

    public int getAdopted() {
        return adopted;
    }

    public void setAdopted(int adopted) {
        this.adopted = adopted;
    }
}
