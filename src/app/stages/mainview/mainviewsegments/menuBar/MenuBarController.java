package app.stages.mainview.mainviewsegments.menuBar;

import app.basics.LinkType;
import app.stages.mainview.mainviewsegments.MainViewSegment;
import app.stages.newmenuitem.NewMenuItemController;
import app.basics.ConfirmBox;
import app.basics.FamilyMember;
import com.opencsv.bean.*;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/*
 * MenuBarController responsibilities:
 * -Saving
 * -Opening
 * -Closing
 * */


public class MenuBarController extends MainViewSegment {

    private String saveURL;

    private final BooleanProperty ifCanSave;

    @FXML
    public Button saveButton;


    public MenuBarController() {
        ifCanSave = new SimpleBooleanProperty(true);    //false
//        saveURL = "";
        saveURL = new File("").getAbsolutePath() + "\\" + "outfile.csv";

    }

    @FXML
    public void handleNewMenuItemAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/stages/newmenuitem/NewMenuItemStage.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Create a new Family Tree");
            stage.setScene(new Scene(root, 500, 300));
            stage.showAndWait();

            NewMenuItemController controller = loader.getController();
            saveURL = controller.getFullURL();
            ifCanSave.setValue(controller.isFileCreated());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleOpenMenuItemAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"));
        String url = fileChooser.showOpenDialog(saveButton.getScene().getWindow()).toString();
        if (url != null) {
            if (!mainViewController.getFamilyMembersHashMap().isEmpty()) {
                boolean answer = ConfirmBox.display("Warning", "Open family tree will be close.\n Are you sure you want to continue?");
                if (!answer) {
                    return;
                }
            }
            openExistingFamilyTree(url);
        }
    }

    @FXML
    public void handleCloseMenuItemAction() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleSaveButtonAction() throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        if (!ifCanSave.getValue()) {
            handleNewMenuItemAction();
        }
        if (ifCanSave.getValue()) {
            //updating coordinates of familyMembers
            canvasController.pannableCanvas.getChildren().forEach(node -> {
                if (node instanceof Rectangle) {
                    FamilyMember familyMember = mainViewController.getFamilyMembersHashMap().get(node.getId());
                    familyMember.setPosX((float) node.getTranslateX());
                    familyMember.setPosY((float) node.getTranslateY());
                }
            });

            List<FamilyMember> familyMemberList = new ArrayList<>(mainViewController.getFamilyMembersHashMap().values());

            Writer writer = new FileWriter("outfile1.csv");
//            writer.append(Arrays.toString(new String[]{"id", "posX", "posY", "firstName", "secondName", "lastName", "birthDate", "notes", "fatherId", "motherId", "partners"}).append('\n'));

            StatefulBeanToCsvBuilder<FamilyMember> builder = new StatefulBeanToCsvBuilder<>(writer);
            StatefulBeanToCsv<FamilyMember> beanWriter = builder/*.withSeparator(',').withQuotechar('"')*/.build();

            beanWriter.write(familyMemberList);
            writer.close();
        }
    }

    public void openExistingFamilyTree(String url) {
        saveURL = url;
        ifCanSave.setValue(true);

        if (!mainViewController.getFamilyMembersHashMap().isEmpty()) {
            mainViewController.getFamilyMembersHashMap().clear();
        }
        canvasController.pannableCanvas.getChildren().clear();

        try {
            FileReader fileReader = new FileReader("outfile.csv");
            CsvToBeanBuilder<FamilyMember> builder = new CsvToBeanBuilder<>(fileReader);
            List<FamilyMember> familyMemberList = builder
                    .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_QUOTES)
                    .withType(FamilyMember.class).build().parse();

            familyMemberList.forEach(System.out::println);

            //adding just rectangle with data
            for (var familyMember : familyMemberList) {
                mainViewController.getFamilyMembersHashMap().put(familyMember.getId(), familyMember);
                mainViewController.getCanvasController().addMemberToBoard(familyMember);
            }
            //linking added rectangles
            for (var familyMember : familyMemberList) {
                if (!familyMember.getMotherId().equals("")) {
                    canvasController.createLineFromTo(lookFor(familyMember.getId()), lookFor(familyMember.getMotherId()), LinkType.MOTHER);
                }
                if (!familyMember.getPartners().isEmpty()) {
                    System.out.println(familyMember.getPartners());
                    System.out.println(familyMember.getPartners().size());
                    for (String partnerId : familyMember.getPartners()) {
                        canvasController.createLineFromTo(lookFor(familyMember.getId()), lookFor(partnerId), LinkType.SPOUSE);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public final Rectangle lookFor(String id) {
        Node node = canvasController.pannableCanvas.lookup("#" + id);
        if (node instanceof Rectangle){
            return (Rectangle) node;
        }
        return (Rectangle) canvasController.pannableCanvas.getChildren().get(0); //theoretically it should never have happen...
    }


    public String getSaveURL() {
        return saveURL;
    }

    public boolean isIfCanSave() {
        return ifCanSave.get();
    }

    public BooleanProperty ifCanSaveProperty() {
        return ifCanSave;
    }
}
