package utils;

import chat.ChatManager;
import order.OrderManager;
import users.UserManager;
import zone.ZonesManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class ServletUtils {

    private static final Object userManagerLock = new Object();
    private static final Object chatManagerLock = new Object();
    private static final Object orderManagerLock = new Object();
    private static final Object zonesManagerLock = new Object();

    public static UserManager getUserManager(ServletContext servletContext) {
        synchronized (userManagerLock) {
            if (servletContext.getAttribute(Constants.USER_MANAGER_ATT) == null) {
                servletContext.setAttribute(Constants.USER_MANAGER_ATT, new UserManager());
            }
        }
        return (UserManager) servletContext.getAttribute(Constants.USER_MANAGER_ATT);
    }

    public static ChatManager getChatManager(ServletContext servletContext) {
        synchronized (chatManagerLock) {
            if (servletContext.getAttribute(Constants.CHAT_ATT) == null) {
                servletContext.setAttribute(Constants.CHAT_ATT, new ChatManager());
            }
        }
        return (ChatManager) servletContext.getAttribute(Constants.CHAT_ATT);
    }

    public static int getIntParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException numberFormatException) {
            }
        }
        return Constants.INT_PARAMETER_ERROR;
    }

    public static ZonesManager getZonesManager(ServletContext servletContext) {
        synchronized (zonesManagerLock) {
            if (servletContext.getAttribute(Constants.ZONES_ATT) == null) {
                servletContext.setAttribute(Constants.ZONES_ATT, new ZonesManager());
            }
        }
        return (ZonesManager) servletContext.getAttribute(Constants.ZONES_ATT);
    }

    public static OrderManager getOrderManager(ServletContext servletContext) {
        synchronized (orderManagerLock) {
            if (servletContext.getAttribute(Constants.ORDER_ATT) == null) {
                servletContext.setAttribute(Constants.ORDER_ATT, new OrderManager());
            }
        }
        return (OrderManager) servletContext.getAttribute(Constants.ORDER_ATT);
    }
}
