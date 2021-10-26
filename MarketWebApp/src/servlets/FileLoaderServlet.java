package servlets;

import exception.DuplicateException;
import exception.InvalidActionException;
import exception.ValueOutOfRangeException;
import market.Zone;
import utils.ServletUtils;
import xml.XmlReader;
import zone.ZonesManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;

@MultipartConfig(
        fileSizeThreshold = 1048576,
        maxFileSize = 5242880L,
        maxRequestSize = 26214400L
)
@WebServlet(name = "FileLoaderServlet", urlPatterns = "/pages/main/file")
public class FileLoaderServlet extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text");
        PrintWriter out = response.getWriter();
        try {
            Collection<Part> parts = request.getParts();
            StringBuilder fileContent = new StringBuilder();
            ZonesManager zonesManager = ServletUtils.getZonesManager(getServletContext());
            Iterator var6 = parts.iterator();

            // get file
            Part part = (Part) var6.next();
            fileContent.append(this.readFromInputStream(part.getInputStream()));
            part = (Part) var6.next();
            String owner = this.readFromInputStream(part.getInputStream());

            // get owner name
            Zone newZone = XmlReader.readFile(fileContent);
            newZone.setOwner(owner);
            zonesManager.addZone(newZone);

            out.print("File loaded successfully");
        } catch (DuplicateException | InvalidActionException | ValueOutOfRangeException | JAXBException e) {
            out.print(e.toString());
        }
        out.flush();
    }

    private String readFromInputStream(InputStream inputStream) {
        return (new Scanner(inputStream)).useDelimiter("\\Z").next();
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

}
