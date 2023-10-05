package servlets;

import DTO.LoanDTO;
import Engine.impl.Engine;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.CustomerData;
import utils.DeltaData;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class CustomerDeltaInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());
        response.setContentType("application/json");

        if (usernameFromSession == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        Gson gsonI = new Gson();
        String currentInfoFromParameter = request.getParameter("currentInfo");
        CustomerData currentInfo = gsonI.fromJson(currentInfoFromParameter, CustomerData.class);
        Map<String, LoanDTO> currLoansAsBorrower , currLoansAsLender;

        Map<String, LoanDTO> loansAsBorrower = engine.loansAsBorrower(usernameFromSession);
        Map<String, LoanDTO> loansAsLender = engine.loansAsLender(usernameFromSession);
        List<String> accountStatement = engine.getCustomerAccountStatement(usernameFromSession);
        List<String> messages = engine.getCustomerDTO(usernameFromSession).getMessages();
        boolean updateBorrower=false, updateLender=false;

        if (currentInfo != null) {
            currLoansAsBorrower= currentInfo.getLoansAsBorrower();
            currLoansAsLender= currentInfo.getLoansAsLender();
            updateBorrower=updateLoans(currLoansAsBorrower, loansAsBorrower);
            updateLender=updateLoans(currLoansAsLender, loansAsLender);
        } else {
            updateBorrower = true;
            updateLender = true;
        }

        CustomerData customerData = new CustomerData(loansAsBorrower, loansAsLender, accountStatement, messages);
        DeltaData dataToReturn= new DeltaData(customerData, updateBorrower, updateLender);

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(dataToReturn);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    boolean updateLoans(Map<String, LoanDTO> currentInfo, Map<String, LoanDTO> newInfo) {

        if(currentInfo.keySet().size() != newInfo.keySet().size()){
            System.out.println("1");
            return true;
        }
        else{
            for(String curr: currentInfo.keySet()){
                if(newInfo.get(curr).getNextRepayment() != currentInfo.get(curr).getNextRepayment()){
                    System.out.println("2");
                    return true;
                }
                if(!newInfo.get(curr).getStatus().equals(currentInfo.get(curr).getStatus())){
                    System.out.println("3");
                    return true;
                }
                if(newInfo.get(curr).getHistory().size() != currentInfo.get(curr).getHistory().size()){
                    System.out.println("4");
                    return true;

                }

                List<LoanDTO.LenderDTO> participantsNew = newInfo.get(curr).getParticipants();
                List<LoanDTO.LenderDTO> participantsCurr = currentInfo.get(curr).getParticipants();
                if(participantsNew.size() != participantsCurr.size()){
                    System.out.println("5");
                    return true;
                }
                for(int i=0; i<participantsCurr.size(); i++){
                    LoanDTO.LenderDTO currL = participantsCurr.get(i);
                    LoanDTO.LenderDTO newL = participantsNew.get(i);

                    if(!currL.getLenderName().equals(newL.getLenderName())){
                        System.out.println("6");
                        return true;
                    }
                    if(currL.getContribution()!=newL.getContribution()){
                        System.out.println("7");
                        return true;
                    }
                }
            }
            System.out.println("8");
        return false;
        }

    }
}
