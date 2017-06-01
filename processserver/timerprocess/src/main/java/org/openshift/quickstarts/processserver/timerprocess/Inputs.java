package org.openshift.quickstarts.processserver.timerprocess;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Created by fspolti on 1/18/17.
 */
@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "inputs", propOrder = {"number1", "number2", "operator"})
@XmlRootElement(name = "inputs")
public class Inputs implements Serializable {

    private Integer number1;
    private Integer number2;
    private String operator;

    public Inputs(){}

    public Inputs(Integer number1, Integer number2, String operator) {
        this.number1 = number1;
        this.number2 = number2;
        this.operator = operator;
    }

    public Integer getNumber1() {
        return number1;
    }

    public void setNumber1(Integer number1) {
        this.number1 = number1;
    }

    public Integer getNumber2() {
        return number2;
    }

    public void setNumber2(Integer number2) {
        this.number2 = number2;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public boolean isValid() {
        if (this.number1 == null || this.number2 == null || operator == null) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Inputs{" +
                "number1=" + number1 +
                ", number2=" + number2 +
                ", operator='" + operator + '\'' +
                '}';
    }
}