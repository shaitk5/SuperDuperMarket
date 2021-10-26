package servlets;

import enums.Action;
import market.Customer;
import market.Wallet;
import market.Transactions;
import users.UserManager;
import utils.Constants;
import utils.ServletUtils;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

@WebServlet(name = "WalletServlet", urlPatterns = "/pages/main/wallet")
public class WalletServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        PrintWriter out = response.getWriter();

        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String dateFromParameter = request.getParameter(Constants.DATE);
        String creditFromParameter = request.getParameter(Constants.CREDIT);

        // add money action
        synchronized (this) {
            Customer customer = userManager.getUser(request.getSession(false).getAttribute(Constants.USERNAME).toString());
            Wallet customerWallet = customer.getWallet();
            Transactions action = new Transactions(Action.LOAD, Double.parseDouble(creditFromParameter), customerWallet.getBalance(), dateFromParameter);
            customerWallet.addTransaction(action);

            out.print("Transaction completed successfully!" + System.lineSeparator() +
                    customer.getName() + ", " + Double.parseDouble(creditFromParameter) + " $  was added to your wallet!");
        }

        out.flush();
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processRequest(request, response);
    }

}
