// File name: Painter.java
// Written by: Dennis Monich
// Description: This program will allow you to draw shapes on a screen and choose whatever color
//              you want it to be and the size of it. It will act as Microsoft Paint but with
//              less features
// Challenges:  Trying to figure out the "undo" button was a little hard until I started
//              making objects for each shape. Then, learning how to draw the shapes and how
//              to gather the appropriate information in order to draw those shapes took some
//              time. Then, once I had a good idea on how to draw the shapes, having it be
//              formatted on the screen correctly took a lot of time.
// Time Spent:  17 hours
// Date created: 11/3/22
// Revision History:
// Date:         		By:      Action:
// ---------------------------------------------------
// 11/3         DVM         Created a Combo Box that has the dropdown for all the shape options,
//                          then created a Titled Pane for the drawing shape color, that has the sliders, labels,
//                          and everything.The value of the slider and the text field match when either are altered.
// 3 hours
// 11/5         DVM         Created a TitledPane that has lets you choose the background colors.
//                          Canvas wasn't showing before, but now it looks like it is. I will fix the
//                          formatting later. Created a final Map variable/object that will assist
//                          with determining which color to set the background color when a certain
//                          text (button) is selected. This works as intended at the moment.
// 2 hours
// 11/7         DVM         Created a class called DefaultTitledPane because I noticed I was using
//                          the same code to prepare a TiltedPane object, so the class simplifies
//                          that process. Created a TitledPane for the pen sizes and a VBox for
//                          the three buttons. I then created separate methods in order to set
//                          something to the default, depending on the object. Each object has
//                          a final variable associated with it to assist with determining the
//                          default variable. I edited the methods that were to set up to start
//                          with the default values to instead just call the respective method
//                          to do it for it. I also created a lambda event for the "Clear" and "Exit"
//                          buttons. The "Clear" button will use the methods that I just created
//                          to set certain objects back to default. I also created some final
//                          variables for padding. Everything looks like it is working as intended.
// 2 hours
// 11/7/22      DVM         Spent some time trying to work on getting the canvas set up. I created
//                          a Label that keeps track of the mouse coordinates which works successfully
//                          Currently I am working on a method that draws shapes. So far I am only
//                          doing the circle to make sure my method works. Currently, I can draw
//                          the shape (not with the color decided by the user, I will add that later),
//                          but it is currently off center. I'll need to do some research on it.
//                          I am having the program print out the mouse coordinates and the results to
//                          some calculations I have set up, but all the numbers look correct, yet,
//                          the program will still draw a circle in a completely different spot,
//                          even with constants. I think it has to do with the sizing of the canvas.
// 2.5 hours
// 11/9         DVM         I had an eureka moment and fixed the placement of the circles. I set up
//                          the parameters for the .fillOval() method wrong.

// 30 minutes
// 11/9         DVM         You can now draw oval shapes. Placement is still a little off, but I will
//                          resolve that later.
// 30 minutes
// 11/10        DVM         Added all the other shapes. I still need to work on colors.
// 30 minutes
// 11/10        DVM         Created a hierarchy of shape classes. Redid the switch statement to instead
//                          of creating a shape when the switch statement is used, it will instead
//                          create an object from the shape hierarchy I created, and save the shape
//                          profiles to an ArrayList called drawnShapes. That way, when I have to
//                          use the undo button or change the background with clearing everything,
//                          I can use the drawnShapes array in order to rebuild all the shapes.
// 2.5 hours
// 11/11        DVM         I have made it so that the shape is now being drawn when the mouse is
//                          being dragged. I also fixed the placement of the rectangles and squares.
//                          Circles work great, but now the ovals need to be fixed.
// 1.5 hours
// 11/12        DVM         Cleaned up the program and fixed whatever bugs that exist
// 2 hours

package com.example.painter;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Map;

public class Painter extends Application {

    // Shapes that user can use
    private final static String[] SHAPE_CHOICES = {
            "Circle",
            "Oval",
            "Rectangle",
            "Square",
            "Line"
    };

    // Column refers to left side of the window with all the selections
    private final static int NUM_SLIDERS = 4,
            COLUMN_WIDTH = 150,
            DEFAULT_CHILD_PADDING = 5;
    private final static Insets DEFAULT_COLUMN_PADDING = new Insets(5);

    // Colors that user can choose from
    // Second element is for CSS
    private final static String[] COLOR_NAMES = {
            "WHITE",
            "LIGHT GRAY",
            "CORAL",
            "LIGHT CYAN",
            "BEIGE"
    };
    private final static Map<String, Color> BACKGROUND_COLORS = Map.of(
            COLOR_NAMES[0], Color.WHITE,
            COLOR_NAMES[1], Color.LIGHTGRAY,
            COLOR_NAMES[2], Color.CORAL,
            COLOR_NAMES[3], Color.LIGHTCYAN,
            COLOR_NAMES[4], Color.BEIGE
    );
    private Color backgroundColor;

    // Below are variables for pen sizes that the user can choose from
    private final static String[] PEN_SIZES = {
            "Small (2px)",
            "Medium (4px)",
            "Large (6px)"
    };
    private final static Map<String, Integer> PEN_SIZE_VALUES = Map.of(
            PEN_SIZES[0], 2,
            PEN_SIZES[1], 4,
            PEN_SIZES[2], 6
    );

    // Default choices for menus
    private final static int DEFAULT_SHAPE = 0,
            DEFAULT_SLIDER = 3, // This represents which slider should be maxed, whiles others are zero
            DEFAULT_BACKGROUND_COLOR = 0,
            DEFAULT_PEN_SIZE = 1;
    private final static boolean DEFAULT_FILL = false;

    // Drawing canvas
    private static Canvas canvas = new Canvas(0,0);
    private static GraphicsContext gc = canvas.getGraphicsContext2D();

    // Below are mouse coordinates to that we will keep track of
    private static double mouseXCoords = 0.0,
            mouseYCoords = 0.0,
            mouseXStart,
            mouseYStart,
            mouseXEnd,
            mouseYEnd;

    // This stores all the shapes that are drawn
    private ArrayList<Shape> drawnShapes = new ArrayList<>();

    // All the different panes that will be put together
    private static VBox shapeMenu = new VBox();
    private static DefaultTitledPane rgbSliders = new DefaultTitledPane("Drawing Shape Color");
    private static DefaultTitledPane backgroundColorChoices = new DefaultTitledPane("Board Background Color");
    private static DefaultTitledPane penSize = new DefaultTitledPane("Pen Size");
    private static VBox buttons = new VBox(DEFAULT_CHILD_PADDING);
    private static Label mouseCoords = new Label();

    // Checkbox that decides to fill in color
    private static CheckBox filledColor = new CheckBox("Filled Color");

    @Override
    public void start(Stage stage) {
        // Main panes that will have everything
        HBox window = new HBox();
        VBox controls = new VBox();

        // Sets up the mouse coords on the bottom left of the screen
        refreshMouseCoords();
        mouseCoords.setPadding(DEFAULT_COLUMN_PADDING);

        // Left side of the window
        controls.getChildren().addAll(
                getShapeMenu(),
                getRGBSliders(),
                getBackgroundColorChoices(),
                getPenSize(),
                getButtons(),
                mouseCoords
        );


        // Displays window
        window.getChildren().addAll(controls, getCanvas());
        Scene scene = new Scene(window,800, 800);
        canvas.setWidth(scene.getWidth());
        canvas.setHeight(window.getHeight());
        // Canvas
        gc.setFill(Color.WHITE);
        gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());

        stage.setTitle("Painter");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    // Below method sets attributes for when user interacts with it
    private Canvas getCanvas() {

        // As the user moves their mouse across the canvas, the mouse coords will be updated
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            mouseXCoords = event.getX();
            mouseYCoords = event.getY();
            refreshMouseCoords();
        });

        // As the user drags their mouse, the shape that they are trying to
        // draw will be displayed as they resize it.
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            mouseXCoords = event.getX();
            mouseYCoords = event.getY();
            mouseXEnd = event.getX();
            mouseYEnd = event.getY();
            redraw(drawnShapes.size() - 1, backgroundColor);
            drawShape();
            refreshMouseCoords();
        });

        // When the user clicks on the canvas, it will record where the shape will start
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            mouseXStart = event.getX();
            mouseYStart = event.getY();
            mouseXEnd = event.getX();
            mouseYEnd = event.getY();
            drawShape();
        });

        // When the user releases their mouse, then the shape is fully drawn
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            mouseXEnd = event.getX();
            mouseYEnd = event.getY();
            redraw(drawnShapes.size() - 1, backgroundColor);
            drawShape();
        });

        return canvas;
    }

    // Method for drawing a shape
    public void drawShape() {
        ComboBox shapes = (ComboBox) shapeMenu.getChildren().get(0);

        // Below segment sets up the color of the shape of the user's choosing
        double red = 0,
                green = 0,  // Values here don't mean anything
                blue = 0,   // It is just to initialize them
                a = 1;
        // Gets the values of the sliders to determine color
        VBox colorOptions = (VBox) rgbSliders.getContent();
        HBox colorSliders = (HBox) colorOptions.getChildren().get(0);
        // Goes through each slider and assigns its value to its appropriate variable
        for (int i = 0; i < colorSliders.getChildren().size(); i++) {
            VBox details = (VBox) colorSliders.getChildren().get(i);
            Slider slider = (Slider) details.getChildren().get(1);
            double color = slider.getValue();
            try {
                switch (i) {
                    case 0 -> red = color;
                    case 1 -> green = color;
                    case 2 -> blue = color;
                    case 3 -> a = color;
                    default -> throw new Exception();
                }
            } catch (Exception e) {
                System.err.println("Unknown color value: color = " + color);
            }
        }
        // Stores the color that the user chose
        Color color = new Color(red, green, blue, a);

        // Sets up the pen size
        double lineWidth = 0;
        VBox size = (VBox) penSize.getContent();
        for (int i = 0; i < size.getChildren().size(); i++) {
            RadioButton radioButton = (RadioButton) size.getChildren().get(i);
            if (radioButton.isSelected()) {
                lineWidth = PEN_SIZE_VALUES.get(radioButton.getText());
                break;
            }
        }

        // Basic variables that will be used to determine where to put shapes
        // Mostly used for circle and oval shapes
        double centerX = ((mouseXStart <= mouseXEnd) ?
                mouseXStart + ((mouseXEnd - mouseXStart) / 2) :
                mouseXEnd + ((mouseXStart - mouseXEnd) / 2)),
                centerY = (mouseYStart <= mouseYEnd) ?
                        mouseYStart + ((mouseYEnd - mouseYStart) / 2) :
                        mouseYEnd + ((mouseYStart - mouseYEnd) / 2),
                diameter = (Math.sqrt(Math.pow(mouseXStart - mouseXEnd, 2) +
                        Math.pow(mouseYStart - mouseYEnd, 2))),
                topLeftX = centerX - (diameter / 2),
                topLeftY = centerY - (diameter / 2);

        // Goes through each value from the combobox to determine which shape to draw
        try {
            switch ((String) shapes.getValue()) {

                // For each shape drawn, an object is created and stored into
                // an ArrayList, so when we need to undo a shape or change the background
                // color, we can clear the canvas and redraw everything that the user added

                // Draws a circle if user selects "circle"
                case "Circle" -> {

                    DrawOval circle = new DrawOval(
                            topLeftX,
                            topLeftY,
                            diameter,
                            diameter,
                            color,
                            lineWidth,
                            filledColor.isSelected(),
                            mouseXEnd,
                            mouseYEnd
                    );
                    circle.draw(gc);
                    drawnShapes.add(circle);
                }

                // Draws an oval if user selects "oval"
                case "Oval" -> {

                    DrawOval oval = new DrawOval(
                            mouseXStart,
                            mouseYStart,
                            diameter,
                            (diameter / 2),
                            color,
                            lineWidth,
                            filledColor.isSelected(),
                            mouseXEnd,
                            mouseYEnd
                    );
                    oval.draw(gc);
                    drawnShapes.add(oval);

                }

                // Draws a rectangle if user selects "rectangle"
                case "Rectangle" -> {

                    DrawRect rect = new DrawRect(
                            mouseXStart,
                            mouseYStart,
                            Math.abs(mouseXStart - mouseXEnd),
                            Math.abs(mouseYStart - mouseYEnd),
                            color,
                            lineWidth,
                            filledColor.isSelected(),
                            mouseXEnd,
                            mouseYEnd
                    );
                    rect.draw(gc);
                    drawnShapes.add(rect);

                }

                // Draws a square if user selects "square"
                case "Square" -> {

                    DrawRect square = new DrawRect(
                            mouseXStart,
                            mouseYStart,
                            Math.abs(mouseXStart - mouseXEnd),
                            Math.abs(mouseXStart - mouseXEnd),
                            color,
                            lineWidth,
                            filledColor.isSelected(),
                            mouseXEnd,
                            mouseYEnd
                    );
                    square.draw(gc);
                    drawnShapes.add(square);

                }

                // Draws a line if user selects "line"
                case "Line" -> {

                    DrawLine line = new DrawLine(
                            mouseXStart,
                            mouseYStart,
                            mouseXEnd,
                            mouseYEnd,
                            color,
                            lineWidth,
                            filledColor.isSelected()
                    );
                    line.draw(gc);
                    drawnShapes.add(line);
                }
                default -> throw new Exception();

            }
        } catch (Exception e) {
            // In case unlikely error occurs
            System.err.println("Shape does not exist: " + shapes.getValue());
        }
    }

    // Superclass to extend each shape too
    abstract class Shape {

        private double var1,
                var2,
                var3,
                var4,
                size;
        private Color color;
        boolean filled;

        protected Shape(double var1, double var2, double var3, double var4, Color color,
                        double size, boolean filled) {
            this.var1 = var1;
            this.var2 = var2;
            this.var3 = var3;
            this.var4 = var4;
            this.color = color;
            this.size = size;
            this.filled = filled;
        }

        protected double getVar1() {
            return var1;
        }
        protected double getVar2() {
            return var2;
        }
        protected double getVar3() {
            return var3;
        }
        protected double getVar4() {
            return var4;
        }
        protected Color getColor() {
            return color;
        }
        protected double getSize() {
            return size;
        }
        protected boolean getFilled() {
            return filled;
        }

        protected abstract void draw(GraphicsContext gc);

    }

    // Class for creating ovals and circles
    private class DrawOval extends Shape {

        double xEnd,
                yEnd;

        private DrawOval(double topLeftX, double topLeftY, double diameterX, double diameterY,
                         Color color, double size, boolean filled, double xEnd, double yEnd) {
            super(topLeftX, topLeftY, diameterX, diameterY, color, size, filled);
            this.xEnd = xEnd;
            this.yEnd = yEnd;
        }

        // Method for drawing circles and ovals
        @Override
        public void draw(GraphicsContext gc) {
            // Sets up attributes
            gc.setFill(getColor());
            gc.setStroke(getColor());
            gc.setLineWidth(getSize());

            // Below segment changes how the oval is drawn based on ending position
            // of the mouse coordinates. Circles are unaffected
            // 1 = x and 2 = y
            double topLeftX = getVar1(),
                    topLeftY = getVar2();
            if (getVar3() != getVar4()) {
                topLeftX = (getVar1() > xEnd) ?
                        getVar1() - (getVar1() - xEnd) : getVar1();
                topLeftY = (getVar2() > yEnd) ?
                        getVar2() - getVar4() : getVar2();
            }

            if (getFilled()) {
                gc.fillOval(topLeftX, topLeftY, getVar3(), getVar4());
            } gc.strokeOval(topLeftX, topLeftY, getVar3(), getVar4());
        }

    }

    // Class for creating rectangles and squares
    private class DrawRect extends Shape {

        double xEnd,
                yEnd;

        private DrawRect(double topLeftX, double topLeftY, double width, double height,
                         Color color, double size, boolean filled, double xEnd, double yEnd) {
            super(topLeftX, topLeftY, width, height, color, size, filled);
            this.xEnd = xEnd;
            this.yEnd = yEnd;
        }

        // Method for drawing rectangles and squares
        @Override
        public void draw(GraphicsContext gc) {
            gc.setFill(getColor());
            gc.setStroke(getColor());
            gc.setLineWidth(getSize());

            // Below segment changes how the rectangle and square is drawn based on
            // ending position of the mouse coordinates
            // 1 = x and 2 = y
            double topLeftX = (getVar1() > xEnd) ?
                    getVar1() - (getVar1() - xEnd) : getVar1(),
                    topLeftY = (getVar2() > yEnd && getVar3() == getVar4()) ?
                            getVar2() - getVar3() : getVar2();
            if (topLeftY == getVar2()) topLeftY = (getVar2() > yEnd) ?
                    getVar2() - (getVar2() - yEnd) : getVar2();
            if (getFilled()) {
                gc.fillRect(topLeftX, topLeftY, getVar3(), getVar4());
            } gc.strokeRect(topLeftX, topLeftY, getVar3(), getVar4());
        }
    }

    // Class for creating lines
    private class DrawLine extends Shape {
        private DrawLine(double topLeftX, double topLeftY, double bottomRightX,
                         double bottomRightY, Color color, double size, boolean filled) {
            super(topLeftX, topLeftY, bottomRightX, bottomRightY, color, size, filled);
        }

        // Method fo drawing lines
        @Override
        public void draw(GraphicsContext gc) {
            gc.setStroke(getColor());
            gc.setLineWidth(getSize());
            gc.strokeLine(getVar1(), getVar2(), getVar3(), getVar4());
        }
    }

    // Menus with all the shapes that the user can choose from
    private static VBox getShapeMenu() {
        ComboBox menu = new ComboBox(FXCollections.observableArrayList(SHAPE_CHOICES));
        menu.setPrefWidth(COLUMN_WIDTH);

        // In order to add padding appropriately
        shapeMenu.getChildren().add(menu);
        shapeMenu.setPadding(DEFAULT_COLUMN_PADDING);

        // Default value is circle
        setDefaultShapeMenu();

        return shapeMenu;
    }

    // This includes the sliders and the text fields associated with it, along with
    // the checkbox for filled color
    private static TitledPane getRGBSliders() {

        // Each slider is accompanies by a label and a text field that gives the value of the slider
        // Creates 4 sliders
        VBox red = new VBox(new Label("R"), new Slider(), new TextField());
        VBox green = new VBox(new Label("G"), new Slider(), new TextField());
        VBox blue = new VBox(new Label("B"), new Slider(), new TextField());
        VBox a = new VBox(new Label ("A"), new Slider(), new TextField());
        HBox sliders = new HBox(red, green, blue, a);

        // Below loop goes through each slider created above and gives the same attributes to them.
        for (Node node : sliders.getChildren()) {
            VBox vbox = (VBox) node;
            Slider slider = (Slider) vbox.getChildren().get(1);
            TextField value = (TextField) vbox.getChildren().get(2);

            // Customizing sliders
            slider.setMax(1.0);
            slider.setMin(0.0);
            slider.setOrientation(Orientation.VERTICAL);
            slider.setPadding(DEFAULT_COLUMN_PADDING);

            // Customizing text fields
            value.setText(String.valueOf(slider.getValue()));
            value.setMaxSize(30,10);

            // When slider moves, the text field changes
            slider.valueProperty().addListener((val, oldValue, newValue) -> {
                value.setText(String.format("%.1f", (double) newValue));
                slider.setValue(Double.parseDouble(String.format("%.1f", (double) newValue)));
            });

            // When text field changes, the slider moves
            value.setOnAction(event -> {
               try {
                   double newValue = Double.parseDouble(value.getText());
                   slider.setValue(newValue);

                   // Setting the text to be higher than one will reset it to one
                   value.setText((newValue > 1) ? String.format("%.1f", slider.getMax()) :
                           String.format("%.1f", newValue));

               } catch (NumberFormatException e) {

                   // If user doesn't input a number, resets text
                   value.setText(String.format("%.1f", slider.getValue()));
               }
            });
        }

        // Finishes creation of titled pane
        VBox colorOptions = new VBox(sliders, filledColor);
        setDefaultRGBSliders(colorOptions);
        rgbSliders.setContent(colorOptions);
        return rgbSliders;
    }

    private TitledPane getBackgroundColorChoices() {
        // Creates all the buttons for color choices
        VBox radioButtons = new VBox(DEFAULT_CHILD_PADDING);
        ToggleGroup colorButtons = new ToggleGroup();
        for (int i = 0; i < BACKGROUND_COLORS.size(); i++) {
            RadioButton colorButton = new RadioButton(COLOR_NAMES[i]);
            int placeholderI = i;
            colorButton.setOnAction(event -> {
                backgroundColor = BACKGROUND_COLORS.get(COLOR_NAMES[placeholderI]);
                redraw(drawnShapes.size(), backgroundColor);
            });
            colorButton.setToggleGroup(colorButtons);
            radioButtons.getChildren().add(colorButton);
        }

        // Sets default value
        setDefaultBackgroundColor(radioButtons);

        backgroundColorChoices.setContent(radioButtons);
        return backgroundColorChoices;
    }

    private static TitledPane getPenSize() {
        // Below will hold the buttons
        ToggleGroup sizeGroup = new ToggleGroup();
        VBox penSizeList = new VBox(DEFAULT_CHILD_PADDING);

        // Creates each button for each size button
        for (String size : PEN_SIZES) {
            RadioButton sizeButton = new RadioButton(size);
            sizeButton.setToggleGroup(sizeGroup);
            penSizeList.getChildren().add(sizeButton);
        }

        // Default
        setDefaultPenSize(penSizeList);

        penSize.setContent(penSizeList);
        return penSize;
    }

    private VBox getButtons() {
        // Formatting
        buttons.setPadding(DEFAULT_COLUMN_PADDING);

        // Buttons which all will do different things based on its name
        // Undo button
        Button undo = new Button("Undo");
        undo.setOnAction(event -> {
            Color color = (drawnShapes.size() == 0) ?
                    BACKGROUND_COLORS.get(COLOR_NAMES[DEFAULT_BACKGROUND_COLOR]) : backgroundColor;
            redraw(drawnShapes.size() - 1, color);
        });

        // Clear button
        Button clear = new Button("Clear");
        clear.setOnAction(event -> {

            // This just resets the background and clears the drawings
            setDefaultBackgroundColor((VBox) backgroundColorChoices.getContent());
        });

        // Exit button
        Button exit = new Button("Exit");
        exit.setOnAction(event -> {
            System.exit(0);
        });

        // Below will hold the three buttons
        buttons.getChildren().addAll(undo, clear, exit);

        // Goes through each button to give each button the same style
        for (Node button : buttons.getChildren()) {
            Button placeholder = (Button) button;
            placeholder.setPrefWidth(COLUMN_WIDTH);
        }

        return buttons;
    }

    // Class has been created as all the TitledPane objects have some of the same characteristics.
    // This is to repeat less code.
    public static class DefaultTitledPane extends TitledPane {
        public DefaultTitledPane(String text) {
            super();
            super.setAnimated(false);
            super.setCollapsible(false);
            super.setText(text);
            super.setPadding(DEFAULT_COLUMN_PADDING);
            super.prefWidth(COLUMN_WIDTH);
        }
    }

    // Below methods sets up default values

    // Sets up default shape
    private static void setDefaultShapeMenu() {
        ComboBox shape = (ComboBox) shapeMenu.getChildren().get(0);
        shape.setValue(SHAPE_CHOICES[DEFAULT_SHAPE]);
    }

    // Sets up default values of RGB sliders
    private static void setDefaultRGBSliders(VBox rgb) {

        // First child has sliders and second child is checkbox
        HBox sliders = (HBox) rgb.getChildren().get(0);
        CheckBox filledColor = (CheckBox) rgb.getChildren().get(1);

        // Default checkbox value
        filledColor.setSelected(DEFAULT_FILL);

        // Default RGB slider values
        int counter = 0;
        for (Node node : sliders.getChildren()) {
            VBox vbox = (VBox) node;
            Slider slider = (Slider) vbox.getChildren().get(1);
            TextField value = (TextField) vbox.getChildren().get(2);
            if (counter == DEFAULT_SLIDER) {
                slider.setValue(slider.getMax());
                value.setText(String.format("%.1f", slider.getValue()));
            } else {
                slider.setValue(slider.getMin());
                value.setText(String.format("%.1f", slider.getValue()));
            }
            counter++;
        }
    }

    // Sets up default background color
    private void setDefaultBackgroundColor(VBox backgroundColors) {
        int counter = 0;
        for (Node color : backgroundColors.getChildren()) {
            RadioButton colorButton = (RadioButton) color;
            colorButton.setSelected(counter == DEFAULT_BACKGROUND_COLOR);
            counter++;
        }
        backgroundColor = BACKGROUND_COLORS.get(COLOR_NAMES[DEFAULT_BACKGROUND_COLOR]);
        redraw(0, backgroundColor);
    }

    // Sets up default pen size
    private static void setDefaultPenSize(VBox penSizeList) {
        int counter = 0;
        for (Node button : penSizeList.getChildren()) {
            RadioButton sizeButton = (RadioButton) button;
            sizeButton.setSelected(counter == DEFAULT_PEN_SIZE);
            counter++;
        }
    }

    // Updates the coordinates of the mouse every time it is moved
    private static void refreshMouseCoords() {
        mouseCoords.setText(String.format("Mouse move: [%.1f, %.1f]", mouseXCoords, mouseYCoords));
    }

    // When called, this will clear the screen and redraw everything that
    // the user drew up to the amount that needs to be drawn.
    private void redraw(int amount, Color color) {

        gc.setFill(color);
        gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        ArrayList<Shape> placeholder = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            drawnShapes.get(i).draw(gc);
            placeholder.add(drawnShapes.get(i));
        }
        drawnShapes = placeholder;
    }

}