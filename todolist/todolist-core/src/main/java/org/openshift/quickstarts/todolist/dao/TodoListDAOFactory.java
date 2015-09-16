package org.openshift.quickstarts.todolist.dao;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 *
 */
public class TodoListDAOFactory {
    public static TodoListDAO getTodoListDAO() {
        ServiceLoader<TodoListDAO> serviceLoader = ServiceLoader.load(TodoListDAO.class);
        Iterator<TodoListDAO> iterator = serviceLoader.iterator();
        if (!iterator.hasNext()) {
            throw new IllegalStateException("Can't find TodoListDAO service");
        }
        return iterator.next();
    }
}
