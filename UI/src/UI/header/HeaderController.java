package UI.header;


import UI.app.login.constants.Constants;
import UI.app.login.utils.HttpClientUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static UI.app.login.constants.Constants.GSON_INSTANCE;

public class HeaderController {
    @FXML private TextField currentYazField;
    @FXML private TextField systemModeField;
    @FXML private Label helloUserLabel;

    public void setName(String customerName){
        this.helloUserLabel.setText("Hello " + customerName + '!');
    }

    public void setMode() {
        String finalUrl = HttpUrl
                .parse(Constants.FETCH_MODE)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    System.out.println("fetchMode error in HEADER");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(() -> {
                    try {
                        String jsonYazInfo = response.body().string();
                        String mode = GSON_INSTANCE.fromJson(jsonYazInfo, String.class);
                        if(mode.equals("regular")) {
                            setRegularSystemModeField();
                        }
                        else{
                            setRewindSystemModeField();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }


    public void setCurrentYazField() {
        String finalUrl = HttpUrl
                .parse(Constants.FETCH_YAZ)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    System.out.println("fetchYaz error in headerController");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(() -> {
                    try {
                        String jsonYazInfo = response.body().string();
                        int yaz = GSON_INSTANCE.fromJson(jsonYazInfo, int.class);
                        currentYazField.setText(String.valueOf(yaz));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });

    }
    public void setRegularSystemModeField() {
        this.systemModeField.setText("Regular");
    }

    public void setRewindSystemModeField() {
        this.systemModeField.setText("Rewind");
    }

    public int getCurrentYazField() {
        return Integer.parseInt(this.currentYazField.getText());
    }

    public String getSystemModeField() {
        return systemModeField.getText();
    }

    public void setCurrentYazField(int currentYazField) {
        this.currentYazField.setText(String.valueOf(currentYazField));
    }
}
