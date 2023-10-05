package Customer;

public class Message {
    public enum MessageType {PAYMENT_ALERT, RISK_ALERT}

    private MessageType type;
    private final String loanId;
    private final int yazDate;
    private final double totalPayment;
    private final double riskTotal;

    public Message(MessageType type, String loadId, int yazDate, double totalPayment, double riskTotal){
        this.type = type;
        this.loanId = loadId;
        this.yazDate = yazDate;
        this.totalPayment = totalPayment;
        this.riskTotal = riskTotal;
    }

    public String getLoanId() {
        return loanId;
    }

    public int getYazDate() {
        return yazDate;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public double getRiskPayment() {return riskTotal;}
    @Override
    public String toString() {
        String curr = "Loan ID: " + this.loanId + " Yaz date: " + this.yazDate + " total payment: " + this.totalPayment;
        if(this.type == MessageType.PAYMENT_ALERT){
            String paymentType = "Payment alert! ";
           if(this.riskTotal != 0){
               curr += "\n Additional risk payment amount: " + this.riskTotal;
           }
            paymentType += curr;
            return paymentType;
        }
        else{
            String riskType = "Risk alert! ";
            riskType += curr;
            return riskType;
        }
    }
}
