package app.basics;

import com.opencsv.bean.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class FamilyMember implements Serializable {

    @CsvBindByName(required = true)
    @CsvBindByPosition(position = 0)
    private String id;

    @CsvBindByName
    @CsvBindByPosition(position = 1)
    private float posX;

    @CsvBindByName(required = true)
    @CsvBindByPosition(position = 2)
    private float posY;


    @CsvBindByName
    @CsvBindByPosition(position = 3)
    private String firstName = "";

    @CsvBindByName
    @CsvBindByPosition(position = 4)
    private String secondName = "";

    @CsvBindByName
    @CsvBindByPosition(position = 5)
    private String lastName = "";

    @CsvDate(value = "yyyy-MM-dd")
    @CsvBindByName
    @CsvBindByPosition(position = 6)
    private LocalDate birthDate = LocalDate.of(1,1,1);

    @CsvBindByName
    @CsvBindByPosition(position = 7)
    private String notes = "";


    @CsvBindByName
    @CsvBindByPosition(position = 8)
    private String fatherId = "";

    @CsvBindByName
    @CsvBindByPosition(position = 9)
    private String motherId = "";

    @CsvBindByName
    @CsvBindAndSplitByPosition(position = 10, elementType = String.class, splitOn = "#", writeDelimiter = "#", collectionType = ArrayList.class)
    private List<String> partners = new ArrayList<>();


    public FamilyMember() {
        this.id = UUID.randomUUID().toString();
    }

    public FamilyMember(String id, String firstName, String secondName, String lastName, LocalDate birthDate, String notes) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.notes = notes;
    }

    public FamilyMember(String firstName, String secondName, String lastName, LocalDate birthDate, String notes) {
        this(UUID.randomUUID().toString(), firstName, secondName, lastName, birthDate, notes);
    }

    @Override
    public String toString() {
        return  "FamilyMember [id=" + id +
                ", posX=" + posX +
                ", posY=" + posY +
                ", firstName=" + firstName +
                ", secondName=" + secondName +
                ", lastName=" + lastName +
                ", birthDate=" + birthDate.toString() +
                ", notes=" + notes +
                ", fatherId=" + fatherId +
                ", motherId=" + motherId +
                ", partners= " + partners.toString() + "]";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    public String getMotherId() {
        return motherId;
    }

    public void setMotherId(String motherId) {
        this.motherId = motherId;
    }

    public List<String> getPartners() {
        return partners;
    }

    public void setPartners(List<String> partners) {
        this.partners = partners;
    }
}
