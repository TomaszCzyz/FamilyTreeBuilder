package app.basics;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;

public class FamilyMemberBox extends Group {

    private final Rectangle rectangle;

    private final Text text;

    public FamilyMemberBox(FamilyMember familyMember) {
        super();
        rectangle = new Rectangle(100, 50);
        text = new Text();
        text.setMouseTransparent(true);

        rectangle.setStroke(Color.BLUE);
        rectangle.setFill(Color.BLUE.deriveColor(1, 1, 1, 0.5));

        text.setTranslateX(10d);
        text.setTranslateY(15d);
        text.setWrappingWidth(100);
        text.setText(familyMember.getFirstName() + " " + familyMember.getLastName() + "\n" +
                "secondname: " + familyMember.getSecondName() + "\n");

        rectangle.widthProperty().bind(text.wrappingWidthProperty());

        this.setId(familyMember.getId());
        this.getChildren().addAll(rectangle, text);
    }


    public void setSelect(boolean bool) {
        if (!bool) {
            rectangle.setStroke(Color.BLUE);
            rectangle.setStrokeType(StrokeType.CENTERED);
            rectangle.setStrokeWidth(1);
        }
    }

    public void mark(Color c) {
        rectangle.setFill(c);
    }

    public void mark(LinkType linkType) {
        rectangle.setStrokeType(StrokeType.OUTSIDE);
        rectangle.setStrokeWidth(3);
        switch (linkType) {
            case MOTHER:
                rectangle.setStroke(Color.RED);
                break;
            case SPOUSE:
                rectangle.setStroke(Color.GREEN);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + linkType);
        }
    }

    public double getWidth() {
        return rectangle.getWidth();
    }

    public void setWidth(double width) {
        this.rectangle.setWidth(width);
    }

    public double getHeight() {
        return rectangle.getHeight();
    }

    public void setHeight(double height) {
        this.rectangle.setHeight(height);
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public Text getText() {
        return text;
    }
}
