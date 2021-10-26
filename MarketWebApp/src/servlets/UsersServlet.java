package servlets;

import com.google.gson.Gson;
import market.Customer;
import users.UserManager;
import utils.*;
import utils.ServletUtils;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

public class UsersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        getUser(request, response);
    }

    private void getUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //get user logged in
        response.setContentType("application/json");
        String username = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        Customer customer =  userManager.getUser(username);

        //check if user exist
        if(username == null){
            response.sendRedirect(request.getContextPath() + "/index.html");
        }

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(customer);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        getUsers(request, response);
    }

    private void getUsers(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            Collection<Customer> users = userManager.getUsers();
            String json = gson.toJson(users);
            out.println(json);
            out.flush();
        }
    }

}
