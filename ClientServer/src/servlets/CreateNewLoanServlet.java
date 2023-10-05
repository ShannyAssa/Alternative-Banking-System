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

public class CreateNewLoanServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());
        Boolean wasCreated = false;
        if (usernameFromSession == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        String loanIdFromParameter = request.getParameter("loanId");
        if(!engine.isLoanExists(loanIdFromParameter)){
            String principalFromParameter = request.getParameter("principal");
            String loanTermFromParameter = request.getParameter("loanTerm");
            String loanIntervalFromParameter = request.getParameter("loanInterval");
            String categoryFromParameter = request.getParameter("category");
            String interestFromParameter = request.getParameter("interest");
            int principal = Integer.parseInt(principalFromParameter);
            int loanTerm = Integer.parseInt(loanTermFromParameter);
            int loanInterval = Integer.parseInt(loanIntervalFromParameter);
            int interest = Integer.parseInt(interestFromParameter);

            engine.createNewLoan(usernameFromSession, principal, loanTerm, loanInterval,
                    loanIdFromParameter, categoryFromParameter, interest);
            wasCreated = true;
        }

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(wasCreated);

        try (PrintWriter out = response.getWriter()) {
        out.print(jsonResponse);
        out.flush();
        response.setStatus(HttpServletResponse.SC_OK);

        }




    }

}
