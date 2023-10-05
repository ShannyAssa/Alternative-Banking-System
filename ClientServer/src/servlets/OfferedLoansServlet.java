package servlets;

import DTO.LoanForSaleDTO;
import DTO.LoanForSaleDTOList;
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
import java.util.List;

public class OfferedLoansServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());
        if (usernameFromSession == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        List<LoanForSaleDTO> loansForSale = engine.getLoansForSaleDTOList(usernameFromSession);
        LoanForSaleDTOList loanForSaleDTOList = new LoanForSaleDTOList(loansForSale);

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(loanForSaleDTOList);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
            response.setStatus(HttpServletResponse.SC_OK);

        }
    }

}

