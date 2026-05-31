package models.adoptionModels.Adoption;

public class Adoption {
    private int id;
    private int adopterId;
    private int petId;
    private String adoptedAt;

    public Adoption() {
        this.id = 0;
        this.adopterId = 0;
        this.petId = 0;
        this.adoptedAt = "";
    }

    public Adoption(int adopterId, int petId, String adoptedAt) {
        this.adopterId = adopterId;
        this.petId = petId;
        this.adoptedAt = adoptedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAdopterId() {
        return adopterId;
    }

    public void setAdopterId(int adopterId) {
        this.adopterId = adopterId;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public String getAdoptedAt() {
        return adoptedAt;
    }

    public void setAdoptedAt(String adoptedAt) {
        this.adoptedAt = adoptedAt;
    }
}
