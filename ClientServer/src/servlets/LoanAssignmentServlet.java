package servlets;

import DTO.CustomerNameListDTO;
import DTO.LoanDTO;
import Engine.impl.Engine;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class LoanAssignmentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());

        if (usernameFromSession == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        else{
            String lenderFromParameter = request.getParameter("lender");
            String loansToAssignFromParameter = request.getParameter("loansToAssign");
            String maxAmountToLoanFromParameter = request.getParameter("maxAmountToLoan");
            String ownershipMaxPercentageFromParameter = request.getParameter("ownershipMaxPercentage");

            CustomerNameListDTO lender = new CustomerNameListDTO(engine.getCustomerDTO(lenderFromParameter));
            Gson gson = new Gson();
            List<String> loansId = gson.fromJson(loansToAssignFromParameter, LinkedList.class);
            List<LoanDTO> loansToAssign = new LinkedList<>();
            for (String currId : loansId){
                loansToAssign.add(engine.getLoanDTO(currId));
            }
            int maxAmountToLoan = (int)(Double.parseDouble(maxAmountToLoanFromParameter));
            int ownershipMaxPercentage = (int)Double.parseDouble(ownershipMaxPercentageFromParameter);
            engine.loansAssignment(lender, loansToAssign, maxAmountToLoan, ownershipMaxPercentage);

            response.setStatus(HttpServletResponse.SC_OK);

        }


    }

}
