package org.openshift.quickstarts.todolist.service;

import org.openshift.quickstarts.todolist.dao.TodoListDAO;
import org.openshift.quickstarts.todolist.dao.TodoListDAOFactory;
import org.openshift.quickstarts.todolist.model.TodoEntry;

import java.util.List;

/**
 *
 */
public class TodoListService {

    private TodoListDAO dao = TodoListDAOFactory.getTodoListDAO();

    public void addEntry(TodoEntry entry) {
        dao.save(entry);
    }

    public List<TodoEntry> getAllEntries() {
        return dao.list();
    }
}
