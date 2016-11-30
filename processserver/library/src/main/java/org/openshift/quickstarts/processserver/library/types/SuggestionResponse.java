package org.openshift.quickstarts.processserver.library.types;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "suggestionResponse", propOrder = {"suggestion"})
@XmlRootElement(name = "suggestionResponse")
public class SuggestionResponse implements Serializable {

    @XmlElement
    protected Suggestion suggestion;

    /**
     * Gets the value of the suggestion property.
     * 
     * @return
     *     possible object is
     *     {@link Suggestion }
     *     
     */
    public Suggestion getSuggestion() {
        return suggestion;
    }

    /**
     * Sets the value of the suggestion property.
     * 
     * @param value
     *     allowed object is
     *     {@link Suggestion }
     *     
     */
    public void setSuggestion(Suggestion value) {
        this.suggestion = value;
    }

    @Override
    public String toString() {
        return "SuggestionResponse [suggestion=" + suggestion + "]";
    }

}
