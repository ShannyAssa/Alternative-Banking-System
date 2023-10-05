package servlets;

import DTO.LoanDTO;
import Engine.impl.Engine;
import com.google.gson.Gson;
import constants.Constants;
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

import static constants.Constants.USERNAME;

public class LoginServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String usernameFromSession = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());

        if (usernameFromSession == null) {

            String usernameFromParameter = request.getParameter(USERNAME);
            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {

                response.setStatus(HttpServletResponse.SC_CONFLICT);
            } else {
                usernameFromParameter = usernameFromParameter.trim();

                synchronized (this) {
                    if (engine.isUserExists(usernameFromParameter)) {
                        String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getOutputStream().print(errorMessage);
                    }
                    else {
                        engine.addUser(usernameFromParameter);
                        request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter);

                        Map<String, LoanDTO> loansAsBorrower = engine.loansAsBorrower(usernameFromParameter);
                        Map<String, LoanDTO> loansAsLender = engine.loansAsLender(usernameFromParameter);
                        List<String> accountStatement = engine.getCustomerAccountStatement(usernameFromParameter);
                        List<String> messages = engine.getCustomerDTO(usernameFromParameter).getMessages();

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
            }
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

}