package UI.app.login;

//import Engine.impl.Engine;
import UI.app.AppController;
import UI.app.login.constants.Constants;
import UI.app.login.utils.HttpClientUtil;
import UI.utils.LoadCustomerData;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;

import static UI.app.login.constants.Constants.GSON_INSTANCE;

public class LoginController {

    @FXML private BorderPane loginBorderPane;
    @FXML private Button helloUserCircle;
    @FXML private ImageView backgroundImage;
    private TranslateTransition translateTransition;
    @FXML private StackPane animationStackPane;
    @FXML private TextField userNameTextField;
    @FXML private Button loginBtn;
    @FXML private Label errorLabel;
//    private Engine engine;
    private AppController mainController;


    public void startAnimation(){

        this.translateTransition = new TranslateTransition();
        this.translateTransition.setDuration(Duration.seconds(2));
        this.translateTransition.setNode(helloUserCircle);
        this.translateTransition.setToY(-100);
        this.translateTransition.setAutoReverse(true);
        this.translateTransition.setCycleCount(translateTransition.INDEFINITE);

        this.translateTransition.play();
    }

    @FXML
    void moveCircle(ActionEvent event) {
        backgroundImage.setFitWidth(300);
        backgroundImage.setFitHeight(200);
        RotateTransition rt = new RotateTransition(Duration.millis(3000), backgroundImage);
        rt.setByAngle(360);
        rt.setCycleCount(Animation.INDEFINITE);
        rt.setInterpolator(Interpolator.LINEAR);
        rt.play();

    }

//    public void setMainController(AppController mainController){
//        this.mainController = mainController;
//    }

    public void login(ActionEvent actionEvent) {
        String userName = userNameTextField.getText();
        if (userName.isEmpty()) {
            errorLabel.setText("User name is empty. You can't login with empty user name");
            return;
        }

        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorLabel.setText("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            errorLabel.setText("Something went wrong: " + " " + responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        try {
                            String jsonArrayOfUsersNames = response.body().string();
                            LoadCustomerData customerData = GSON_INSTANCE.fromJson(jsonArrayOfUsersNames, LoadCustomerData.class);
                            mainController.updateUserName(userName);
                            mainController.setCustomerView(userName, customerData.getLoansAsBorrower(),customerData.getLoansAsLender(),
                                    customerData.getAccountStatement(), customerData.getMessages());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
                }
            }
        });
    }


    public void setStartingAnimation(){
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/UI/app/login/login.fxml");
        fxmlLoader.setLocation(url);
        this.startAnimation();
    }

    public void setMainController(AppController appController) {
        this.mainController = appController;
    }
}
