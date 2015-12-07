package org.openshift.quickstarts.todolist.servlet;

import java.io.IOException;
import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openshift.quickstarts.todolist.service.TodoListService;

/**
 *
 */
public class AMQServlet extends HttpServlet {
	
	private static final long serialVersionUID = 6639064923108880002L;

	private TodoListService todoListService = new TodoListService();

    private static final Boolean NON_TRANSACTED = false;
    private static final long MESSAGE_TIME_TO_LIVE_MILLISECONDS = 0;
    private static final int MESSAGE_DELAY_MILLISECONDS = 100;
    private static final int NUM_MESSAGES_TO_BE_SENT = 100;
    //private static final String CONNECTION_FACTORY_NAME = "myJmsFactory";
    
    private static final String CONNECTION_FACTORY = "amqp://admin:admin" + "@" + System.getenv("AMQ_BROKER_AMQ_AMQP_SERVICE_HOST")
    	+ ":" + System.getenv("AMQ_BROKER_AMQ_AMQP_SERVICE_PORT") + "?ssl=false";    
    
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Connection connection = null;

		try {
			// JNDI lookup of JMS Connection Factory and JMS Destination
			Hashtable<String, String> env = new Hashtable<String, String>();
			System.out.println("ConnectionFactory: " + CONNECTION_FACTORY);			
			env.put("connectionfactory.myJmsFactory", CONNECTION_FACTORY);
			env.put("queue", "todo");
			Context context = new InitialContext(env);
			ConnectionFactory factory = (ConnectionFactory) context.lookup("connectionfactory.myJmsFactory");
			Destination destination = (Destination) context.lookup("queue");

			connection = factory.createConnection();
			connection.start();

			Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(destination);

			producer.setTimeToLive(MESSAGE_TIME_TO_LIVE_MILLISECONDS);

			for (int i = 1; i <= NUM_MESSAGES_TO_BE_SENT; i++) {
				TextMessage message = session.createTextMessage(i + ". message sent");
				System.out.println("Sending to destination: " + destination.toString() + " this text: '" + message.getText());
				producer.send(message);
				Thread.sleep(MESSAGE_DELAY_MILLISECONDS);
			}

			// Cleanup
			producer.close();
			session.close();
		} catch (Throwable t) {
			System.err.println("Error sending message" + t.toString());
		} finally {
			// Cleanup code
			// In general, you should always close producers, consumers,
			// sessions, and connections in reverse order of creation.
			// For this simple example, a JMS connection.close will
			// clean up all other resources.
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					System.err.println("Error cloding connection" + e.toString());
				}
			}
		}
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //
    }
}
