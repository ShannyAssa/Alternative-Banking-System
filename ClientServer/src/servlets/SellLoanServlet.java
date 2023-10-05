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

public class SellLoanServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());
        Boolean wasPurposedSale = false;
        if (usernameFromSession == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        String loanIdFromParameter = request.getParameter("loanId");
        if(!engine.isPurposedForSale(usernameFromSession, loanIdFromParameter)){
            engine.sellLoan(usernameFromSession, loanIdFromParameter);
            wasPurposedSale = true;
        }

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(wasPurposedSale);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
            response.setStatus(HttpServletResponse.SC_OK);

        }
    }

}
