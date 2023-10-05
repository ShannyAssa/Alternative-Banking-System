package servlets;

import Engine.impl.Engine;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;

public class PayBackLoan extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());

        if (usernameFromSession == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        String paymentTypeFromParameter = request.getParameter("paymentType");
        String loanIdFromParameter = request.getParameter("loanId");
        boolean wasPaymentSuccessful;
        if(paymentTypeFromParameter.equals("single")){
            wasPaymentSuccessful = engine.paySingleLoan(usernameFromSession, loanIdFromParameter);
        }
        else{
            wasPaymentSuccessful = engine.payFullLoan(usernameFromSession, loanIdFromParameter);
        }

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(wasPaymentSuccessful);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
            response.setStatus(HttpServletResponse.SC_OK);

        }
    }

}
