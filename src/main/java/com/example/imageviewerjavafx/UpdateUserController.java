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
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class UpdateUserController {

    @FXML
    private Button updateButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button selectImageButton;

    @FXML
    private TextField nameText1;

    @FXML
    private Label idText;

    @FXML
    private TextField ImageText;

    @FXML
    private TextField oldNameText;

    private String userId;

    private String selectedUser;

    private String userImage;

    private File file;

    private static final String UPDATE_USER_URL = "http://localhost:8083/updateUser";

    private static final String UPDATE_USERNAME_URL = "http://localhost:8083/updateUserName";

    private static final String EXISTING_USER_URL = "http://localhost:8083/check-username/";

    private RestTemplate restTemplate = new RestTemplate();

    @FXML
    public void initialize() {

        updateButton.setDisable(true);
        //idText.textProperty().addListener((observable, oldValue, newValue) -> validateInputs());
       nameText1.textProperty().addListener((observable, oldValue, newValue) -> validateInputs());
       ImageText.textProperty().addListener((observable, oldValue, newValue) -> validateInputs());

       ImageText.setEditable(false);

    }

    @FXML
    private void onClickSelectImageButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("All Files", "*.*");
        FileChooser.ExtensionFilter imageFilter1 = new FileChooser.ExtensionFilter("JPG Files", "*.jpg*");
        FileChooser.ExtensionFilter imageFilter2 = new FileChooser.ExtensionFilter("PNG Files", "*.png*");
        FileChooser.ExtensionFilter imageFilter3 = new FileChooser.ExtensionFilter("BMP Files", "*.bmp*");
        FileChooser.ExtensionFilter imageFilter4 = new FileChooser.ExtensionFilter("JPEG Files", "*.jpeg*");

        fileChooser.getExtensionFilters().addAll(imageFilter, imageFilter1, imageFilter2, imageFilter3, imageFilter4);

        Stage stage = (Stage) selectImageButton.getScene().getWindow();
        file = fileChooser.showOpenDialog(stage);

        if (file != null) {

            System.out.println("Image selected: " + file.getName());
            ImageText.setText(file.getName());
        } else {

            System.out.println("Image is not selected");
            ImageText.setText(null);
            file = null;
        }
        validateInputs();
    }

    private void validateInputs() {

        boolean isNameTextFilled = !nameText1.getText().trim().isEmpty();
       // boolean isIdTextFilled = !idText.getText().trim().isEmpty();
        boolean isImageTextFilled = !ImageText.getText().trim().isEmpty();

       updateButton.setDisable(!(isNameTextFilled || isImageTextFilled)); //&& (isIdTextFilled));
//        boolean canUpdate = (isNameTextFilled || isImageTextFilled); //&& isIdTextFilled;
//        updateButton.setDisable(!canUpdate);
    }

    @FXML
    public void passUserId(String userId){

        idText.setText(userId);

        System.out.println("user id is :" + userId);

        if (idText == null) {
            System.out.println("idText is null!");
        } else {
            idText.setText(userId);
            System.out.println("user id is set: " + userId);
        }
        hideId();
    }

    public void hideId() {

        idText.setVisible(false);
    }

    @FXML
    public void passSelectedUser(String selectedUser ) {
        oldNameText.setText(selectedUser);
        System.out.println(" Current user name is :" + selectedUser);
    }

    @FXML
    public void passUserImage(String userImage ) {
        ImageText.setText(userImage);
        System.out.println(" Current user image  is :" + userImage);
    }

    @FXML
    public void OnClickUpdateButton() {

        updateButton.setDisable(true);

        String id = idText.getText();

        System.out.println("user id is1 :" + id);

        String userName = nameText1.getText();

        if (id == null || id.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "ID is required", "Please provide a valid User ID.");
            updateButton.setDisable(false);
            return;
        }

        if ((userName == null || userName.isEmpty()) && file == null) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "User name or Image cannot be empty. Please provide at least one.");
            updateButton.setDisable(false);
            return;
        }

        if (!userName.isEmpty() && checkIfUsernameExists(userName)) {
            showAlert(Alert.AlertType.WARNING, "Username Already Exists", "Please try a different User name.");
            updateButton.setDisable(false);
            return;
        }

        if (!userName.isEmpty() && file == null){
            updateButton.setDisable(false);
            boolean isConfirmed = showConfigurationAlert("User update Confirmation","Are you sure to Update the user?");
            if (isConfirmed) {
                updateUserName(id, userName);
            }else {
                showAlert(Alert.AlertType.INFORMATION,"Cancel to update user","Failed to Update User");
            }
            return;
        }

        boolean isConfirmed = showConfigurationAlert("User update Confirmation","Are you sure to Update the user?");
        if (isConfirmed) {
                updateUserMethod(id, userName, file);
        }else{
            showAlert(Alert.AlertType.INFORMATION,"Cancel to update user","Failed to Update User");

        }

        updateButton.setDisable(false);
    }

    public void updateUserMethod(String id, String userName , File file){

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> data = new LinkedMultiValueMap<>();

        data.add("id",id);
        data.add("userName", userName);
        data.add("image", new FileSystemResource(file));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(data, header);

        oldNameText.clear();
        idText.setText(null);
        nameText1.clear();
        ImageText.clear();

        try {
            ResponseEntity<Map> response = restTemplate.exchange(UPDATE_USER_URL, HttpMethod.PUT, requestEntity, Map.class);

            showAlert(Alert.AlertType.INFORMATION, "User Updated Successfully","Image is added for the User");
        }
        catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Failed to update an user", "An error occurred while trying to Update: " + e.getMessage());
        }
    }

    public void updateUserName(String id,String userName ){
        MultiValueMap<String, Object> data1 = new LinkedMultiValueMap<>();

        data1.add("id",id);

        data1.add("userName", userName);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(data1);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(UPDATE_USERNAME_URL, HttpMethod.PUT, requestEntity, Map.class);

            oldNameText.clear();
            idText.setText(null);
            nameText1.clear();
            ImageText.clear();

            showAlert(Alert.AlertType.INFORMATION, "User Updated Successfully","User name is updated : " + userName);
        }
        catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Failed to update UserName", "An error occurred while trying to Update: " + e.getMessage());
        }

    }

    private boolean showConfigurationAlert(String title, String content){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        ButtonType okButton = new ButtonType("OK",ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel",ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton,cancelButton);

        Optional<ButtonType> result =alert.showAndWait();
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
        nameText1.clear();
        ImageText.clear();
        oldNameText.clear();
        idText.setText(null);

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