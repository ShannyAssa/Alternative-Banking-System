package servlets;

import DTO.LoanDTO;
import Engine.impl.Engine;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class LoansInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());

        Map<String, LoanDTO> loans = engine.loansInfo();
        Map<String, Integer> openLoansNumber= new HashMap<>();
        for(String curr: engine.getCustomersNames()){
            openLoansNumber.put(curr, engine.getOpenLoansNumber(curr));
        }

        Gson gson = new Gson();


        FilterLoansInfo filterLoansInfo= new FilterLoansInfo(loans, openLoansNumber);
        String jsonResponse = gson.toJson(filterLoansInfo);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }

        response.setStatus(HttpServletResponse.SC_OK);

    }

    private static class FilterLoansInfo {
         private Map<String, LoanDTO> loans;
         private Map<String, Integer> openLoansNumber;

        public FilterLoansInfo(Map<String, LoanDTO> loans, Map<String, Integer> openLoans) {
            this.loans = loans;
            this.openLoansNumber = openLoans;
        }
    }
}
