package org.openshift.quickstarts.tomcat.dao;

import org.openshift.quickstarts.tomcat.model.TomcatEntry;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *  TODO: proper exception handling
 *  TODO: initialize schema whenever necessary (what if db is not persistent and is restarted while app is running)
 */
public class JdbcTomcatDAO implements TomcatDAO {

    private final DataSource dataSource;

    public JdbcTomcatDAO() {
        dataSource = lookupDataSource();
        initializeSchemaIfNeeded();
    }

    private DataSource lookupDataSource() {
        try {
            Context initialContext = new InitialContext();
            try {
                return (DataSource) initialContext.lookup(System.getenv("DB_JNDI"));
            } catch (NameNotFoundException e) {
                Context envContext = (Context) initialContext.lookup("java:comp/env");  // Tomcat places datasources inside java:comp/env
                return (DataSource) envContext.lookup(System.getenv("DB_JNDI"));
            }
        } catch (NamingException e) {
            throw new RuntimeException("Could not look up datasource", e);
        }
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
    public void save(TomcatEntry entry) {
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
        return new Random().nextLong();
    }

    @Override
    public List<TomcatEntry> list() {
        try {
            Connection connection = getConnection();
            try {
                Statement statement = connection.createStatement();
                List<TomcatEntry> list;
                try {
                    ResultSet rset = statement.executeQuery("SELECT id, summary, description FROM todo_entries");
                    try {
                        list = new ArrayList<TomcatEntry>();
                        while (rset.next()) {
                            Long id = rset.getLong(1);
                            String summary = rset.getString(2);
                            String description = rset.getString(2);
                            list.add(new TomcatEntry(id, summary, description));
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

    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    private DataSource getDataSource() {
        return dataSource;
    }
}
