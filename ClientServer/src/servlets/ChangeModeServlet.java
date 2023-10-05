package servlets;

import DTO.EngineDTO;
import Engine.impl.Engine;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;

public class ChangeModeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Engine engine = ServletUtils.getEngine(getServletContext());
        String yazToViewFromParameter = request.getParameter("yazToView");
        int yazToView = (int) Double.parseDouble(yazToViewFromParameter);
        String mode =request.getParameter("mode");
        if(mode.equals("rewind"))
        {
            engine.setSystemMode("rewind");
            EngineDTO engineToView;

            engine.setEngineToViewYaz(yazToView);

            if(yazToView == engine.getYaz()){
                engineToView = engine.getCurrEngineToView();
            }


            else {
                engineToView = engine.getPreviousEngines().get(yazToView - 1);
            }

            Gson gson = new Gson();
            String jsonResponse = gson.toJson(engineToView);
            try (PrintWriter out = response.getWriter()) {
                out.print(jsonResponse);
                out.flush();
            }
        }
        else{
            engine.setSystemMode("regular");
        }


        response.setStatus(HttpServletResponse.SC_OK);
    }
}
