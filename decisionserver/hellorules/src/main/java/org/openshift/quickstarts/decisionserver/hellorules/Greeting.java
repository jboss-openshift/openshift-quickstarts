package org.openshift.quickstarts.decisionserver.hellorules;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "greeting", propOrder = {"salutation"})
@XmlRootElement(name = "greeting")
public class Greeting implements Serializable {

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
