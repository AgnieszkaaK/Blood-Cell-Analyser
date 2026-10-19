package com.example.demo2;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;

public class BloodCellAnalyserController {
    @FXML private ImageView originalImageView;
    @FXML private ImageView tricolourImageView;
    @FXML private Canvas overlayCanvas;
    @FXML private CheckBox numberingCheckBox;
    @FXML private Label cellCountLabel;
    @FXML private Label redCountLabel;
    @FXML private Label whiteCountLabel;

    //processor for analysing image pixels
    private final ImageProcessor imageProcessor = new ImageProcessor();

   //store current image from file
    private Image originalImage;

    @FXML
    private void loadImage() {
        FileChooser fileChooser = new FileChooser(); //let user pick a file
        fileChooser.setTitle("Select Image");

        // specific image types (jpg, png, jpeg)
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        //file selection
        File selectedFile = fileChooser.showOpenDialog(null);

        // user selects successfully
        if (selectedFile != null) {
            // load image
            originalImage = new Image(selectedFile.toURI().toString());
            // shows image
            originalImageView.setImage(originalImage);
            imageProcessor.setOriginalImage(originalImage, overlayCanvas);

        } else {
            // user cancels selection
            showAlert("No file selected", "Please select a valid image.");
        }
    }

    @FXML
    private void convertToTricolours() {
        // if no image
        if (originalImage == null) {
            showAlert("Error", "Please load an image first!"); //give error
            return;
        }

        // convert
        imageProcessor.convertToTricolour();

        // show convert image
        tricolourImageView.setImage(imageProcessor.getTriColorImage());
    }

    @FXML
    private void analyseImage() {
        // make sure an image has been loaded
        if (originalImage == null) {
            showAlert("Error", "Please load an image first!");
            return;
        }

        // analysis (counts)
        int count = imageProcessor.analyseCells(originalImageView, numberingCheckBox.isSelected());

        // update all labels
        cellCountLabel.setText("Cell Count: " + count);
        redCountLabel.setText("Red Blood Cells: " + imageProcessor.getRedCellCount());
        whiteCountLabel.setText("White Blood Cells: " + imageProcessor.getWhiteCellCount());
    }

    // pop up error messages
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR); // error popup
        alert.setTitle(title); // title
        alert.setHeaderText(null); //
        alert.setContentText(message); // message
        alert.showAndWait(); // block until user clicks OK
    }
}
