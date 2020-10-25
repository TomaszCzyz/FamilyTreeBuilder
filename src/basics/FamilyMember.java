package basics;

import java.time.LocalDate;

public class FamilyMember {

    private String firstName;

    private String secondName;

    private String lastName;

    private LocalDate birthDate;

    private String notes;

    public FamilyMember() {
        firstName = "";
        secondName = "";
        lastName = "";
        birthDate = LocalDate.of(1,1,1);
//        birthDate = LocalDate.ofEpochDay(1);
        notes = "";
    }

    public FamilyMember(String firstName, String secondName, String lastName, LocalDate birthDate, String notes) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.notes = notes;
    }

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
}
