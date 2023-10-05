package servlets;

import Engine.impl.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;

public class BuyLoanServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());

        String loanIdFromParameter = request.getParameter("loanId");
        String sellerNameFromParameter = request.getParameter("sellerName");
        String priceForSaleFromParameter = request.getParameter("priceForSale");




        int priceForSale = (int)Double.parseDouble(priceForSaleFromParameter);

        engine.updateParticipantForLoan(loanIdFromParameter, sellerNameFromParameter,
                usernameFromSession, priceForSale);

        response.setStatus(HttpServletResponse.SC_OK);

    }
}
