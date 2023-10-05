package servlets;

import Engine.impl.Engine;
import Exceptions.*;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.InvalidPathException;

public class LoadDataServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());

        if (usernameFromSession == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        String pathFromParameter = request.getParameter("path");
        String exceptionMessage = "";
        Boolean wasSuccessful = false;


        try{
            wasSuccessful = engine.loadData(pathFromParameter, usernameFromSession);
        }

        catch (CustomerAlreadyExistsException e){
            exceptionMessage = e.getMessage();
            wasSuccessful = false;
        }
        catch (IntervalsNotMatchException e){
                exceptionMessage = e.getMessage();
                wasSuccessful = false;
                }
        catch (NoCatagoryException e){
                exceptionMessage = e.getMessage();
                wasSuccessful = false;
                }
        catch (NoSuchOwnerException e){
                exceptionMessage = e.getMessage();
                wasSuccessful = false;
                }
        catch (OverdraftException e){
                exceptionMessage = e.getMessage();
                wasSuccessful = false;
                }
        catch (FileDoesNotExistException e){
                exceptionMessage = e.getMessage();
                wasSuccessful = false;
                }
        catch (InvalidPathException e){
                exceptionMessage = e.getMessage();
                wasSuccessful = false;
                }
        catch (LoanAlreadyExists e){
            exceptionMessage = e.getMessage();
            wasSuccessful = false;
        }


        Gson gson = new Gson();
        SuccessfulAndMessage successAndMessage = new SuccessfulAndMessage(wasSuccessful, exceptionMessage);
        String jsonResponse = gson.toJson(successAndMessage);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }

        response.setStatus(HttpServletResponse.SC_OK);

    }

    //a class for the result of loading data (success/failure) and the exception message (if failed).
    private static class SuccessfulAndMessage {
        final private Boolean wasSuccessful;
        final private String exceptionMessage;

        public SuccessfulAndMessage(Boolean wasSuccessful,String exceptionMessage) {
            this.wasSuccessful = wasSuccessful;
            this.exceptionMessage = exceptionMessage;
        }
    }
}
