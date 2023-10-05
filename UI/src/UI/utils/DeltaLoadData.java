package UI.utils;

public class DeltaLoadData {
    private LoadCustomerData newData;
    boolean updateBorrower;
    boolean updateLender;


    public LoadCustomerData getNewData() {
        return newData;
    }

    public void setNewData(LoadCustomerData newData) {
        this.newData = newData;
    }

    public boolean isUpdateBorrower() {
        return updateBorrower;
    }

    public void setUpdateBorrower(boolean updateBorrower) {
        this.updateBorrower = updateBorrower;
    }

    public boolean isUpdateLender() {
        return updateLender;
    }

    public void setUpdateLender(boolean updateLender) {
        this.updateLender = updateLender;
    }

}
