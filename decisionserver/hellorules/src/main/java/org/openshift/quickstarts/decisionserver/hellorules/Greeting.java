package org.openshift.quickstarts.decisionserver.hellorules;

public class Greeting {

    private String salutation;

    public Greeting() {}

    public Greeting(String salutation) {
        setSalutation(salutation);
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

}
