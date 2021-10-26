package servlets;

import utils.*;
import users.UserManager;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "LoginServlet", urlPatterns = "/Login")
public class LoginServlet extends HttpServlet {

    private final int CHAT_VERSION = 0;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String usernameFromParameter = request.getParameter(Constants.USERNAME);
        String userTypeFromParameter = request.getParameter(Constants.CUSTOMER_TYPE);
        usernameFromParameter = usernameFromParameter.trim();

        if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
            response.sendRedirect(Constants.LOGIN_URL);
        } else {
            synchronized (this) {
                if (!userManager.isUserExists(usernameFromParameter)) {
                    userManager.addUser(usernameFromParameter, userTypeFromParameter.equals(Constants.BUYER));
                    request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter);
                    request.getSession(false).setAttribute(Constants.CHAT_VERSION_PARAMETER, CHAT_VERSION);
                    request.getSession(false).setAttribute(Constants.CUSTOMER_TYPE, userTypeFromParameter);
                    response.sendRedirect(Constants.MAIN_URL);
                } else {
                    response.sendRedirect(request.getContextPath());
                }
            }
        }
        return;
    }

    protected void checkUserName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String usernameFromParameter = request.getParameter(Constants.USERNAME);
        usernameFromParameter = usernameFromParameter.trim();
        String massage = "";
        synchronized (this) {
            if(userManager.isUserExists(usernameFromParameter)){
                massage = "Username " + usernameFromParameter + " already exists";
            } else if (!usernameFromParameter.isEmpty()){
                massage ="Username available";
            }
            try (PrintWriter out = response.getWriter()) {
                out.write(massage);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        checkUserName(request, response);
    }

//    @Override
//    public String getServletInfo() {
//        return "Short description";
//    }
}
