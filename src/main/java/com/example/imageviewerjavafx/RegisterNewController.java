package com.example.imageviewerjavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class RegisterNewController {

    @FXML
    private Button registerButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField nameText;

    @FXML
    private TextField ImageText;

    @FXML
    private Label MessageLabel;

    private File file;

    @FXML
    private Button imageSelctorButton;

    private static final String CREATE_USER_URL = "http://localhost:8083/createUser";

    private static final String EXISTING_USER_URL = "http://localhost:8083/check-username/";

    private RestTemplate restTemplate = new RestTemplate();

    @FXML
    public void initialize() {

        registerButton.setDisable(true);
        nameText.textProperty().addListener((observable, oldValue, newValue) -> validateInputs());
        ImageText.textProperty().addListener((observable, oldValue, newValue) -> validateInputs());

        ImageText.setEditable(false);

    }

    @FXML
    private void onClickSelectImageButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        // Add file filters for image files---
        FileChooser.ExtensionFilter imageFilter =  new FileChooser.ExtensionFilter("All Files", "*.*");
        FileChooser.ExtensionFilter imageFilter1 = new FileChooser.ExtensionFilter("JPG Files", "*.jpg*");
        FileChooser.ExtensionFilter imageFilter2 = new FileChooser.ExtensionFilter("PNG Files", "*.png*");
        FileChooser.ExtensionFilter imageFilter3 = new FileChooser.ExtensionFilter("BMP Files", "*.bmp*");
        FileChooser.ExtensionFilter imageFilter4 = new FileChooser.ExtensionFilter("JPEG Files", "*.jpeg*");

        fileChooser.getExtensionFilters().addAll(imageFilter, imageFilter1, imageFilter2, imageFilter3, imageFilter4);

        Stage stage = (Stage) imageSelctorButton.getScene().getWindow();
        file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            System.out.println("Image selected: " + file.getName());
            ImageText.setText(file.getName());
        } else {
            System.out.println("Image is not selected");
            ImageText.setText("");
        }
            validateInputs();
    }

    private void validateInputs() {

        boolean isNameTextFilled = !nameText.getText().trim().isEmpty();
        boolean isImageTextFilled = !ImageText.getText().trim().isEmpty();

        registerButton.setDisable(!(isNameTextFilled && isImageTextFilled));
    }

    @FXML
    private void OnClickRegisterButton() {

        registerButton.setDisable(true);

        String userName = nameText.getText();

        boolean isUsernamePresent = checkIfUsernameExists(userName);

        if (userName.isEmpty() || file == null) {

            showAlert(Alert.AlertType.WARNING, "User name or Image should not be null", "Select User name and Image");

        } else if (isUsernamePresent) {

            showAlert(Alert.AlertType.WARNING, "User name is present already", "Try another User name");


        } else {
            boolean isconfirmed = showConfigurationAlert("Register Confirmation","Are you sure you want to Register this user?");
           if(isconfirmed) {
               registerUserMethod(userName, file);
           }
           else{
               showAlert(Alert.AlertType.INFORMATION, "Registration Canceled", "The Registration was canceled.");

           }
        }

    }

    public void registerUserMethod(String userName , File file) {

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
        data.add("userName", userName);
        data.add("image", new FileSystemResource(file));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(data, header);

        nameText.clear();
        ImageText.clear();

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(CREATE_USER_URL, requestEntity, Map.class);
            showAlert(Alert.AlertType.INFORMATION, "User Registered Successfully", "Image is added for the User name: " + userName);


        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,"User Registration Failed", "An error occurred while trying to Register: " + e.getMessage());
        }
    }

    private boolean showConfigurationAlert(String title ,String content){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton,cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get()==okButton;
    }

    public boolean checkIfUsernameExists(String userName) {
        String url = EXISTING_USER_URL  + userName;

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        String responseBody = response.getBody();
        return responseBody != null && responseBody.contains("true");
    }

    @FXML
    private void OnClickCancelButton() {
        nameText.clear();
        ImageText.clear();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = fxmlLoader.load();

            Stage currentStage = (Stage) cancelButton.getScene().getWindow();
            currentStage.close();

            Stage stage = new Stage();
            stage.setTitle("Image Viewer");
            stage.setResizable(false);
            stage.setScene(new Scene(root));

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
