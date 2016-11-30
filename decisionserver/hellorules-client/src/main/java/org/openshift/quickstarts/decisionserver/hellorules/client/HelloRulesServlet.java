package org.openshift.quickstarts.decisionserver.hellorules.client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class HelloRulesServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(HelloRulesServlet.class);

    private final HelloRulesClient client;

    public HelloRulesServlet() {
        client = new HelloRulesClient();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<html><head><title>HelloRulesServlet</title></head><body>");
        try {
            String command = req.getParameter("command");
            HelloRulesCallback callback = new HelloRulesCallback();
            callback.setProtocol(req.getParameter("protocol"));
            callback.setHost(req.getParameter("host"));
            callback.setPort(req.getParameter("port"));
            callback.setUsername(req.getParameter("username"));
            callback.setPassword(req.getParameter("password"));
            callback.setQUsername(req.getParameter("qusername"));
            callback.setQPassword(req.getParameter("qpassword"));
            if (client.runCommand(command, callback)) {
                logger.info("********** " + callback.getSalutation() + " **********");
                out.println("Result of " + command + ":<p><em>");
                out.println(callback.getSalutation());
                out.println("</em></p>");
                out.println("<a href=\"/hellorules\">Back</a>");
            } else {
                String hostname = System.getenv("HOSTNAME");
                out.println("<em>Nothing run!</em><p>Must specify ?command=&lt;command&gt;<br/><ul>");
                out.println("<li><a href=\"/hellorules?command=runLocal\">runLocal</a></li>");
                out.println("<li><a href=\"/hellorules?command=runRemoteRest\">runRemoteRest</a></li>");
                out.println("<li><a href=\"/hellorules?command=runRemoteRest&protocol=https&host=" + hostname + "&port=8443\">runRemoteRest (secure)</a></li>");
                out.println("<li><a href=\"/hellorules?command=runRemoteHornetQ\">runRemoteHornetQ</a> (only works with HornetQ)</li>");
                out.println("<li><a href=\"/hellorules?command=runRemoteActiveMQ&host=amqhost\">runRemoteActiveMQ</a> (only works with ActiveMQ; must change host parameter to the amq host)</li>");
                out.println("</ul></p>");
                out.println("Can also specify query parameters: protocol, host, port, username, password, qusername, qpassword.<p/>");
                out.println("For example: /hellorules?command=runRemoteRest&amp;protocol=https&amp;host=" + hostname + "&amp;port=8443 (if https is configured)");
            }
        } catch (Exception e) {
            out.println("<em>Oops!</em><p><font color=\"red\"><pre>");
            e.printStackTrace(out);
            out.println("</pre></font></p>");
            out.println("<a href=\"/hellorules\">Back</a>");
        }
        finally {
            out.println("</body></html>");
            out.flush();
        }
    }

}
