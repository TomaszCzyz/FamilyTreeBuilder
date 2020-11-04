package basics;

import java.time.LocalDate;
import java.util.*;

public class FamilyMember {

    private final String id;

    private String firstName;
    private String secondName;
    private String lastName;
    private LocalDate birthDate;
    private String notes;

    private String fatherId;
    private String motherId;


    public FamilyMember() {
        this.id = UUID.randomUUID().toString();
        this.firstName = "";
        this.secondName = "";
        this.lastName = "";
        this.birthDate = LocalDate.of(1,1,1);
        this.notes = "";
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

//    public FamilyMember(FamilyMember f) {
//        this(f.id, f.firstName, f.secondName, f.lastName, f.birthDate, f.notes);
//    }


    @Override
    public String toString() {
        return firstName + "," + secondName + "," + lastName + "," + birthDate.toString() + "," + notes;
    }

    public String getFirstName() { return firstName; }

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

    public String getId() {
        return id;
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
}
