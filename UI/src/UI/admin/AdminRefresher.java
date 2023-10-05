package UI.admin;

import java.util.TimerTask;

public class AdminRefresher extends TimerTask {

    AdminController mainController;
    private String mode = "regular";

    @Override
    public void run() {
        if(this.mode.equals("regular")){
            mainController.setAdminTables();
            mainController.updateHeaderInfo();
        }

    }

    public void setMainController(AdminController mainController) {
        this.mainController= mainController;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
