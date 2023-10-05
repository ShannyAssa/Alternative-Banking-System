package servlets;

import DTO.LoanDTO;
import Engine.impl.Engine;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.CustomerData;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class CustomerViewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());
        response.setContentType("application/json");

        if (usernameFromSession == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        Map<String, LoanDTO> loansAsBorrower = engine.loansAsBorrower(usernameFromSession);
        Map<String, LoanDTO> loansAsLender = engine.loansAsLender(usernameFromSession);
        List<String> accountStatement = engine.getCustomerAccountStatement(usernameFromSession);
        List<String> messages = engine.getCustomerDTO(usernameFromSession).getMessages();

        CustomerData customerData = new CustomerData(loansAsBorrower, loansAsLender, accountStatement, messages);

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(customerData);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
