package org.openshift.quickstarts.processserver.library.client;

import org.kie.api.runtime.KieSession;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.RuleServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibraryConfig {

    private static final Logger logger = LoggerFactory.getLogger(LibraryConfig.class);

    private KieSession kieSession;
    private RuleServicesClient ruleServicesClient;
    private ProcessServicesClient processServicesClient;
    private MarshallingFormat marshallingFormat;
    private String protocol;
    private String host;
    private String port;
    private String username;
    private String password;
    private String qusername;
    private String qpassword;
    private boolean html;

    public LibraryConfig() {}

    public KieSession getKieSession() {
        return kieSession;
    }

    public void setKieSession(KieSession kieSession) {
        this.kieSession = kieSession;
    }

    public RuleServicesClient getRuleServicesClient() {
        return ruleServicesClient;
    }

    public void setRuleServicesClient(RuleServicesClient ruleServicesClient) {
        this.ruleServicesClient = ruleServicesClient;
    }

    public ProcessServicesClient getProcessServicesClient() {
        return processServicesClient;
    }

    public void setProcessServicesClient(ProcessServicesClient processServicesClient) {
        this.processServicesClient = processServicesClient;
    }

    public MarshallingFormat getMarshallingFormat() {
        if (marshallingFormat == null) {
            // can use xstream, xml (jaxb), or json
            String type = System.getProperty("MarshallingFormat", "xstream");
            if (type.trim().equalsIgnoreCase("jaxb")) {
                type = "xml";
            }
            marshallingFormat = MarshallingFormat.fromType(type);
        }
        return marshallingFormat;
    }

    public void setMarshallingFormat(MarshallingFormat marshallingFormat) {
        this.marshallingFormat = marshallingFormat;
    }

    public String getProtocol(String defaultProtocol) {
        String protocol = trimToNull(this.protocol);
        if (protocol == null) {
            protocol = trimToNull(System.getProperty("protocol", defaultProtocol));
        }
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost(String defaultHost) {
        String host = trimToNull(this.host);
        if (host == null) {
            host = trimToNull(System.getProperty("host", System.getProperty("jboss.bind.address", defaultHost)));
        }
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort(String defaultPort, String protocol) {
        String port = trimToNull(this.port);
        if (port == null) {
            if ("https".equalsIgnoreCase(protocol)) {
                defaultPort = null;
            }
            port = trimToNull(System.getProperty("port", defaultPort));
        }
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        String username = trimToNull(this.username);
        if (username == null) {
            username = trimToNull(System.getProperty("username", "kieserver"));
        }
        logger.debug("---------> username: " + username);
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        String password = this.password;
        if (password == null) {
            password = System.getProperty("password", "kieserver1!");
        }
        logger.debug("---------> password: " + password);
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQUsername() {
        String qusername = trimToNull(this.qusername);
        if (qusername == null) {
            qusername = trimToNull(System.getProperty("qusername", getUsername()));
        }
        logger.debug("---------> qusername: " + qusername);
        return qusername;
    }

    public void setQUsername(String qusername) {
        this.qusername = qusername;
    }

    public String getQPassword() {
        String qpassword = this.qpassword;
        if (qpassword == null) {
            qpassword = System.getProperty("qpassword", getPassword());
        }
        logger.debug("---------> qpassword: " + qpassword);
        return qpassword;
    }

    public void setQPassword(String qpassword) {
        this.qpassword = qpassword;
    }

    public boolean isHtml() {
        return html;
    }

    public void setHtml(boolean html) {
        this.html = html;
    }

    public String getBaseUrl(String defaultProtocol, String defaultHost, String defaultPort) {
        String protocol = getProtocol(defaultProtocol);
        String host = getHost(defaultHost);
        String port = getPort(defaultPort, protocol);
        String baseurl = protocol + "://" + host + (port != null ? ":" + port : "");
        logger.info("---------> baseurl: " + baseurl);
        return baseurl;
    }

    public String trimToNull(String str) {
        if (str != null) {
            str = str.trim();
            if (str.length() == 0) {
                str = null;
            }
        }
        return str;
    }

}
