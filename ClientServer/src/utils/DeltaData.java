package utils;

public class DeltaData {
    private CustomerData newData;
    boolean updateBorrower;
    boolean updateLender;

    public DeltaData(CustomerData data, boolean borrower, boolean lender){
        this.updateBorrower= borrower;
        this.updateLender= lender;
        this.newData= data;
    }
}
