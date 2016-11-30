package org.openshift.quickstarts.processserver.library.client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class LibraryServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LibraryServlet.class);

    public LibraryServlet() {}

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<html><head><title>LibraryServlet</title></head><body>");
        try {
            String command = req.getParameter("command");
            LibraryConfig appcfg = new LibraryConfig();
            appcfg.setProtocol(req.getParameter("protocol"));
            appcfg.setHost(req.getParameter("host"));
            appcfg.setPort(req.getParameter("port"));
            appcfg.setUsername(req.getParameter("username"));
            appcfg.setPassword(req.getParameter("password"));
            appcfg.setQUsername(req.getParameter("qusername"));
            appcfg.setQPassword(req.getParameter("qpassword"));
            appcfg.setHtml(true);
            LibraryClient client = new LibraryClient(appcfg);
            if (client.runCommand(command, out)) {
                String result = "Command: " + command + " run.";
                logger.info("********** " + result + " **********");
                out.println("<p><em>" + result + "</em></p>");
                out.println("<a href=\"/library\">Back</a>");
            } else {
                String hostname = System.getenv("HOSTNAME");
                out.println("<em>Nothing run!</em><p>Must specify ?command=&lt;command&gt;<br/><ul>");
                out.println("<li><a href=\"/library?command=runLocal\">runLocal</a></li>");
                out.println("<li><a href=\"/library?command=runRemoteRest\">runRemoteRest</a></li>");
                out.println("<li><a href=\"/library?command=runRemoteRest&protocol=https&host=" + hostname + "&port=8443\">runRemoteRest (secure)</a></li>");
                out.println("<li><a href=\"/library?command=runRemoteHornetQ\">runRemoteHornetQ</a> (only works with HornetQ)</li>");
                out.println("<li><a href=\"/library?command=runRemoteActiveMQ&host=amqhost\">runRemoteActiveMQ</a> (only works with ActiveMQ; must change host parameter to the amq host)</li>");
                out.println("</ul></p>");
                out.println("Can also specify query parameters: protocol, host, port, username, password, qusername, qpassword.<p/>");
                out.println("For example: /library?command=runRemoteRest&amp;protocol=https&amp;host=" + hostname + "&amp;port=8443 (if https is configured)");
            }
        } catch (Exception e) {
            out.println("<em>Oops!</em><p><font color=\"red\"><pre>");
            e.printStackTrace(out);
            out.println("</pre></font></p>");
            out.println("<a href=\"/library\">Back</a>");
        }
        finally {
            out.println("</body></html>");
            out.flush();
        }
    }

}
