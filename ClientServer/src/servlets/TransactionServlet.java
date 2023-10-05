package servlets;

import Engine.impl.Engine;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class TransactionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());

        if (usernameFromSession == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        String typeFromParameter = request.getParameter("type");
        String amountFromParameter = request.getParameter("amount");
        int amount = Integer.parseInt(amountFromParameter);


        if (typeFromParameter.equals("DEPOSIT")) {
            engine.depositMoney(usernameFromSession, amount);
        } else if (typeFromParameter.equals("WITHDRAW")) {
            engine.moneyWithdrawal(usernameFromSession, amount);
        } else if (typeFromParameter.equals("PAYBACK")) {
            String loanId = request.getParameter("loanId");
            engine.payRisk(usernameFromSession, loanId, amount);
        }
        List<String> accountStatement = engine.getCustomerAccountStatement(usernameFromSession);

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(accountStatement);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }

        response.setStatus(HttpServletResponse.SC_OK);

    }

}
