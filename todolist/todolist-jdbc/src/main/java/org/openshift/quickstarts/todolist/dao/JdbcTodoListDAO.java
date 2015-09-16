package org.openshift.quickstarts.todolist.dao;

import org.openshift.quickstarts.todolist.model.TodoEntry;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 */
public class JdbcTodoListDAO implements TodoListDAO {

    private long seq = 0;   // TODO: replace with auto-increment column or db sequence

    public JdbcTodoListDAO() {
        initializeSchemaIfNeeded();
    }

    private void initializeSchemaIfNeeded() {
        try {
            Connection connection = getConnection();
            try {
                if (!isSchemaInitialized(connection)) {
                    connection.setAutoCommit(true);
                    Statement statement = connection.createStatement();
                    try {
                        statement.executeUpdate("CREATE TABLE todo_entries (id bigint, summary VARCHAR(255), description TEXT)");
                    } finally {
                        statement.close();
                    }
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isSchemaInitialized(Connection connection) throws SQLException {
        ResultSet rset = connection.getMetaData().getTables(null, null, "todo_entries", null);
        try {
            return rset.next();
        } finally {
            rset.close();
        }
    }

    @Override
    public void save(TodoEntry entry) {
        try {
            Connection connection = getConnection();
            try {
                connection.setAutoCommit(true);
                PreparedStatement statement = connection.prepareStatement("INSERT INTO todo_entries (id, summary, description) VALUES (?, ?, ?)");
                try {
                    statement.setLong(1, getNextId());
                    statement.setString(2, entry.getSummary());
                    statement.setString(3, entry.getDescription());
                    statement.executeUpdate();
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private long getNextId() {
        return seq++;
    }

    @Override
    public List<TodoEntry> list() {
        try {
            Connection connection = getConnection();
            try {
                Statement statement = connection.createStatement();
                List<TodoEntry> list;
                try {
                    ResultSet rset = statement.executeQuery("SELECT id, summary, description FROM todo_entries");
                    try {
                        list = new ArrayList<TodoEntry>();
                        while (rset.next()) {
                            Long id = rset.getLong(1);
                            String summary = rset.getString(2);
                            String description = rset.getString(2);
                            list.add(new TodoEntry(id, summary, description));
                        }
                    } finally {
                        rset.close();
                    }
                } finally {
                    statement.close();
                }
                return list;
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        try {
            DataSource dataSource = getDataSource();
            return dataSource.getConnection();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private DataSource getDataSource() throws NamingException {
        Context initialContext = new InitialContext();
//        Context envContext = (Context) initialContext.lookup("java:comp/env");
        return (DataSource) initialContext.lookup(System.getenv("DB_JNDI"));

//        return (DataSource) initialContext.lookup("java:jboss/datasources/SampleDS");
    }
}
