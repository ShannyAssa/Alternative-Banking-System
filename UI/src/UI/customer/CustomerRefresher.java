package UI.customer;

import UI.app.AppController;
import UI.app.login.constants.Constants;
import UI.app.login.utils.HttpClientUtil;
import UI.utils.DeltaLoadData;
import UI.utils.LoadCustomerData;
import com.google.gson.Gson;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.TimerTask;

import static UI.app.login.constants.Constants.GSON_INSTANCE;

public class CustomerRefresher extends TimerTask {

    private AppController mainController;

    public void setMode(String mode) {
        this.mode = mode;
        if(mode.equals("regular"))
            mainController.enableButtons();
        else{
            mainController.disableButtons();
        }
    }

    private String mode = "regular";

    public void setMainController(AppController mainController) {
        this.mainController = mainController;

    }

    @Override
    public void run() {
        if(this.mode.equals("regular")){
            //mainController.updateCustomerTables();
            DeltaFetchingCustomerTables();
        }
        mainController.updateHeaderInfo();
    }



    public void DeltaFetchingCustomerTables() {
        Gson gson = new Gson();
        LoadCustomerData currentInfo = mainController.getCurrentInfo();
        String jsonRequest = gson.toJson(currentInfo);
        String finalUrl = HttpUrl
                .parse(Constants.DELTA_INFO_PAGE)
                .newBuilder()
                .addQueryParameter("currentInfo", jsonRequest)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        System.out.println("DeltaFetching in CustomerRefresher")
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code() != 200){
                    System.out.println("Error in deltefetching customerrefersher");
                }
                Platform.runLater(() -> {
                    try {
                        String jsonArrayOfUsersNames = response.body().string();
                        DeltaLoadData data = GSON_INSTANCE.fromJson(jsonArrayOfUsersNames, DeltaLoadData.class);
                        mainController.setCurrentInfo(data);
                        //setCustomersTableItems(customerData.getLoansAsBorrower(), customerData.getLoansAsLender(), customerData.getAccountStatement(), customerData.getMessages());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
}
