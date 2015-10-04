helloworld-mdb: Helloworld Using an MDB (Message-Driven Bean)
============================================================
Author: Serge Pagop, Andy Taylor, Jeff Mesnil  
Level: Intermediate  
Technologies: JMS, EJB, MDB  
Summary: The `helloworld-mdb` quickstart uses *JMS 1.1* and *EJB 3.1 Message-Driven Bean* (MDB) to create and deploy JMS topic and queue resources in JBoss EAP.  
Target Product: JBoss EAP  
Source: <https://github.com/jboss-developer/jboss-eap-quickstarts/>  

What is it?
-----------

This is a modified version of the JBoss Developer quickstart that demonstrates use of *JMS 1.1* and *EJB 3.1 Message-Driven Bean* using EAP and A-MQ on OpenShift.

This project requires two JMS resources, these should be created through the relevant templates:

* A queue named `HELLOWORLDMDBQueue` bound in JNDI as `java:/queue/HELLOWORLDMDBQueue`
* A topic named `HELLOWORLDMDBTopic` bound in JNDI as `java:/topic/HELLOWORLDMDBTopic`

### Test the Application

When the application is running it can be tested by opening the following URL via a browser or other tools.  Be sure to replace the `YOUR_HOST_NAME` in the URL with the hostname through which the EAP http service is exposed.

* <http://YOUR_HOST_NAME/HelloWorldMDBServletClient> to send messages to the queue
* <http://YOUR_HOST_NAME/HelloWorldMDBServletClient?topic> to send messages to the topic

If the application has run succesfully you should see some output in the browser and in the EAP server logs.

08:45:05,855 INFO  [class org.jboss.as.quickstarts.mdb.HelloWorldQueueMDB] (default-threads - 6) Received Message from queue: This is message 2
08:45:05,855 INFO  [class org.jboss.as.quickstarts.mdb.HelloWorldQueueMDB] (default-threads - 5) Received Message from queue: This is message 1
08:45:05,856 INFO  [class org.jboss.as.quickstarts.mdb.HelloWorldQueueMDB] (default-threads - 7) Received Message from queue: This is message 3
08:45:05,858 INFO  [class org.jboss.as.quickstarts.mdb.HelloWorldQueueMDB] (default-threads - 8) Received Message from queue: This is message 4
08:45:05,867 INFO  [class org.jboss.as.quickstarts.mdb.HelloWorldQueueMDB] (default-threads - 9) Received Message from queue: This is message 5

or

08:45:16,283 INFO  [class org.jboss.as.quickstarts.mdb.HelloWorldTopicMDB] (default-threads - 10) Received Message from topic: This is message 1
08:45:16,283 INFO  [class org.jboss.as.quickstarts.mdb.HelloWorldTopicMDB] (default-threads - 13) Received Message from topic: This is message 4
08:45:16,283 INFO  [class org.jboss.as.quickstarts.mdb.HelloWorldTopicMDB] (default-threads - 12) Received Message from topic: This is message 3
08:45:16,286 INFO  [class org.jboss.as.quickstarts.mdb.HelloWorldTopicMDB] (default-threads - 11) Received Message from topic: This is message 2
08:45:16,290 INFO  [class org.jboss.as.quickstarts.mdb.HelloWorldTopicMDB] (default-threads - 14) Received Message from topic: This is message 5
