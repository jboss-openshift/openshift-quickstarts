package org.openshift.quickstarts.processserver.library.types;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "returnResponse", propOrder = {"acknowledged"})
@XmlRootElement(name = "returnResponse")
public class ReturnResponse implements Serializable {

    protected boolean acknowledged;

    /**
     * Gets the value of the acknowledged property.
     * 
     */
    public boolean isAcknowledged() {
        return acknowledged;
    }

    /**
     * Sets the value of the acknowledged property.
     * 
     */
    public void setAcknowledged(boolean value) {
        this.acknowledged = value;
    }

    @Override
    public String toString() {
        return "ReturnResponse [acknowledged=" + acknowledged + "]";
    }

}
