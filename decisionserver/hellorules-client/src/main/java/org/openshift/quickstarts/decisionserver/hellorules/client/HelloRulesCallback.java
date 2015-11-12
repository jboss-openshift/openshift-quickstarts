package org.openshift.quickstarts.decisionserver.hellorules.client;

public class HelloRulesCallback {

    private String protocol = null;
    private String host = null;
    private String port = null;
    private String username = null;
    private String password = null;
    private String qusername = null;
    private String qpassword = null;
    private int queryResultsSize = 0;
    private String salutation = null;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQUsername() {
        return qusername;
    }

    public void setQUsername(String qusername) {
        this.qusername = qusername;
    }

    public String getQPassword() {
        return qpassword;
    }

    public void setQPassword(String qpassword) {
        this.qpassword = qpassword;
    }

    public int getQueryResultsSize() {
        return queryResultsSize;
    }

    public void setQueryResultsSize(int queryResultsSize) {
        this.queryResultsSize = queryResultsSize;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

}
