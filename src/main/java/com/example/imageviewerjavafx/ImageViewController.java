package com.example.imageviewerjavafx;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ImageViewController {

    @FXML
    private ImageView imageView;

    @FXML
    private Button clearButton;

    @FXML
    private Button loadButton;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private Button registerUser;

    @FXML
    private Button updateUserButton;

    @FXML
    private Button deleteButton;

    private String imagedata1;

    //private static final String USERS_URL = "http://13.51.138.12:8080/getAll";
    private static final String USERS_URL = "http://localhost:8083/getAll";

    private static final String USER_By_ID_URL = "http://localhost:8083/getUserById/";

    private static final String DELETE_By_ID_URL = "http://localhost:8083/deleteUserById/";

    private RestTemplate restTemplate = new RestTemplate();

    private List<User> usersList;

    private Map<String, String> userIdValue = new HashMap<>();

    private Map<String, String> userImageValue = new HashMap<>();

    @FXML
    public void initialize() {

        loadButton.setDisable(true);

        deleteButton.setDisable(true);

        updateUserButton.setDisable(true);

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                loadButton.setDisable(false);
                deleteButton.setDisable(false);
                updateUserButton.setDisable(false);
            } else {
                loadButton.setDisable(true);
                deleteButton.setDisable(true);
                updateUserButton.setDisable(true);
            }
        });

        getUserList();
    }

    @FXML
    private void getUserList() {
        try {
            User[] usersArray = restTemplate.getForObject(USERS_URL, User[].class);
            if (usersArray != null) {
                for (User user : usersArray) {
                    System.out.println("User ID: " + user.getId());
                    System.out.println("User Name: " + user.getUserName());
                }
            } else {
                System.out.println("No users found.");
            }
            if (usersArray != null) {
                usersList = Arrays.asList(usersArray);
                for (User user : usersList) {
                    comboBox.getItems().add(user.getUserName());
                    userIdValue.put(user.getUserName(), user.getId());
                    userImageValue.put(user.getUserName(), user.getImage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void OnClickLoadImageButton() {

        String selectedUser = comboBox.getValue();
        if (selectedUser != null) {
            String userId = userIdValue.get(selectedUser);

            System.out.println("User: " + userId);

            if (userId != null) {
                loadButton.setDisable(true);
                loadImageById(userId);

            }
        }
    }

    @FXML
    private void loadImageById(String userId) {
        try {
            User user = restTemplate.getForObject(USER_By_ID_URL + userId, User.class);

            if (user != null) {
                String userName = user.getUserName();
                String imageData = user.getImage();

                //Debug for checking data is null or not---

                System.out.println("User: " + userName + ", Image Data: " + imageData);

                if (imageData != null && !imageData.isEmpty()) {

                    loadImage(imageData);

                } else {
                    System.out.println("File is null");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  To load the image  --

    private void loadImage(String imageData) {
        try {

            File file = new File(imageData);
            System.out.println("Image data found: " + imageData);
            //System.out.println("File is: " + file.getPath());

//            Image image = new Image(file.toURI().toURL().toString());
//            imageView.setImage(image);

            if (file.exists()) {
                //Image image = new Image(file.toURL().toString());
                Image image = new Image(file.toURI().toURL().toString());
                //Image image = new Image("https://motownindia.com/images/Auto-Industry/LUXURY-CARS-IN-INDIA-A-Painful-Growth-Motown-India-Bureau-1-989.jpg");
                imageView.setImage(image);
                System.out.println("Image loaded from file: " + imageData);

            } else  {

                if(!file.exists()) {
                    //String imagedata1 = file.getName();
                    String imagedata1 = imageData;

                    Image image1 = new Image(imagedata1);

                    imageView.setImage(image1);
                }
                    else{

                    System.out.println("File not found: " + file.getPath());

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load image from file: " + imageData);
        }
    }

    @FXML
    private void OnClickClearButton() {
        comboBox.setValue(comboBox.getPromptText());
        imageView.setImage(null);
        //label.setText("");
        loadButton.setDisable(true);
        deleteButton.setDisable(true);
        updateUserButton.setDisable(true);
    }

    @FXML
    private void OnClickComboBox() {

    }

// To Register new User---

    @FXML
    private void OnClickRegisterButton() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Register-New.fxml"));
            Parent root = fxmlLoader.load();

            Stage currentStage = (Stage) registerUser.getScene().getWindow();
            currentStage.close();

            Stage stage = new Stage();
            stage.setTitle("User Registration");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to Load the page", "An error occured , please try again later " + e.getMessage());
        }
    }

    // To Delete User---

    @FXML
    private void OnClickDeleteButton() {

        String selectedUser = comboBox.getValue();
        if (selectedUser != null) {
            String userId = userIdValue.get(selectedUser);
            System.out.println("User: " + userId);

            if (userId != null) {
                boolean isConfirmed = showConfirmationAlert("Delete Confirmation",
                        "Are you sure you want to delete this user?");
                if (isConfirmed) {
                    deleteUserById(userId);
                    refreshComboBox();

                    imageView.setImage(null);
                    loadButton.setDisable(true);
                    deleteButton.setDisable(true);
                    updateUserButton.setDisable(true);
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Action Canceled", "The deletion was canceled.");
                }
            }
        }
    }

    @FXML
    private void deleteUserById(String userId) {

        User user = restTemplate.getForObject(USER_By_ID_URL + userId, User.class);

        if (user != null) {

            try {
                restTemplate.delete(DELETE_By_ID_URL + userId);
                showAlert(Alert.AlertType.INFORMATION,
                        "User Deleted Successfully",
                        "continue? ");

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Failed to Delete the user", "An error occured , please try again later " + e.getMessage());

            }
        }
    }

    private boolean showConfirmationAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == okButton;
    }

    private void refreshComboBox() {
        try {
            List<User> userList = Arrays.asList(restTemplate.getForObject(USERS_URL, User[].class));

            List<String> userNames = userList.stream()
                    .map(User::getUserName)
                    .collect(Collectors.toList());
            comboBox.setItems(FXCollections.observableArrayList(userNames));
            comboBox.getSelectionModel().clearSelection();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Failed to Refresh User List", "An error occurred while refreshing the user list. " + e.getMessage());
        }
    }

    //To Update User---

    @FXML
    private void OnClickUpdateUserButton(ActionEvent event) throws IOException {

        String selectedUser = comboBox.getValue();
        if (selectedUser != null) {
            System.out.println("Selected user: "+ selectedUser);
            String userId = userIdValue.get(selectedUser);
            String userImage =userImageValue.get(selectedUser);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Update-User.fxml"));
            Parent root = loader.load();

            UpdateUserController controller = loader.getController();
            controller.passUserId(userId);
            controller.passUserImage(userImage);
            controller.passSelectedUser(selectedUser);
            Stage currentStage = (Stage) updateUserButton.getScene().getWindow();
            currentStage.close();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } else {
            System.out.println("No user selected.");
        }
    }

    //Alert message---

    private void showAlert (Alert.AlertType alertType, String title, String message){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }


        //ToConsumeAPI---

        public static void main (String args[]){

            RestTemplate restTemplate = new RestTemplate();

            //GetAll---
            ResponseEntity<String> getAllUser = restTemplate.getForEntity("http://localhost:8083/getAll", String.class);
            System.out.println(getAllUser.getBody());

            //GetUserById--
            ResponseEntity<String> getUserById = restTemplate.getForEntity("http://localhost:8083/getUserById/70e386de-772c-11ef-b6b1-a08cfda3dc09", String.class);
            System.out.println(getUserById.getBody());
        }

    }