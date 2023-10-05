package servlets;

import DTO.CustomerDTO;
import DTO.LoanDTO;
import Engine.impl.Engine;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.AdminData;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class AdminViewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //String usernameFromSession = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());

        Map<String, LoanDTO> loans = engine.loansInfo();
        Map<String, CustomerDTO> customers = engine.customersInfo();

        AdminData adminData = new AdminData(loans,customers);

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(adminData);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
        response.setStatus(HttpServletResponse.SC_OK);

    }
}
