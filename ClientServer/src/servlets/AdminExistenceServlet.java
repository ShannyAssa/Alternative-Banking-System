package servlets;

import Engine.impl.Engine;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;

import java.io.IOException;

public class AdminExistenceServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        Engine engine = ServletUtils.getEngine(getServletContext());
        String errorMessage = "An admin is already logged in.";


        synchronized (this) {
            if (engine.isAdminExist()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getOutputStream().print(errorMessage);
            } else {
                engine.setAdminExistence(true);
                response.setStatus(HttpServletResponse.SC_OK);

            }
        }

    }
}