package app.stages.addfamilymember;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import app.basics.AlertBox;


public class AddMemberController implements Initializable {

    //i think it would be better if i change it to casual Strings and make a method to take their vaules;
    private final StringProperty firstName = new SimpleStringProperty();
    private final StringProperty secondName = new SimpleStringProperty();
    private final StringProperty lastName = new SimpleStringProperty();
    private final StringProperty birthDate = new SimpleStringProperty("");
    private final StringProperty notes = new SimpleStringProperty();

    @FXML
    public Button okButton;

    @FXML
    public Button cancelButton;

    @FXML
    public TextField firstNameTextField;

    @FXML
    public TextField secondNameTextField;

    @FXML
    public TextField lastNameTextField;

    @FXML
    public DatePicker birthDateField;

    @FXML
    public TextArea notesTextArea;


    @FXML
    public void handleCancelButtonAction() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleOkButtonAction() {
        String message = validateData();

        if(!message.isEmpty()) {
            AlertBox.display("Invalid data", message);
        } else {
            firstName.setValue(firstNameTextField.getText());
            secondName.setValue(secondNameTextField.getText());
            lastName.setValue(lastNameTextField.getText());
            if(birthDateField.getValue() != null) {
                birthDate.setValue(birthDateField.getValue().toString());
            }
            notes.setValue(notesTextArea.getText());

            Stage stage = (Stage) okButton.getScene().getWindow();
            stage.close();
        }
    }

    private String validateData() {
        StringBuilder message = new StringBuilder();

        //check if all required textfields are given
        List<TextField> requiredTextFields = List.of(firstNameTextField);
        for(var textField : requiredTextFields) {
            if(textField.getText().isEmpty()) {
                message.append("Enter ").append(textField.getId()).append('\n');
            }
        }
        if(message.length() > 0)
            return message.toString();

        //check if textfield contain olny letters
        List<TextField> textFields = List.of(firstNameTextField, secondNameTextField, lastNameTextField);
        for(var textField : textFields) {
            if (textField.getText().isEmpty())
                continue;
            if (!textField.getText().matches("[a-zA-Z]+")) {
                message.append("Invalid ").append(textField.getId().toString()).append('\n');
            }
        }
        if(message.length() > 0)
            return message.toString();

        return message.toString();
    }


    public String getFirstName() {
        return firstName.get();
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getSecondName() {
        return secondName.get();
    }

    public StringProperty secondNameProperty() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName.set(secondName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getBirthDate() {
        return birthDate.get();
    }

    public StringProperty birthDateProperty() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate.set(birthDate);
    }

    public String getNotes() {
        return notes.get();
    }

    public StringProperty notesProperty() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes.set(notes);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}

