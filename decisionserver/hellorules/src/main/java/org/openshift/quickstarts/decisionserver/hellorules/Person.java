package org.openshift.quickstarts.decisionserver.hellorules;

public class Person {

    private String name;

    public Person() {}

    public Person(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
