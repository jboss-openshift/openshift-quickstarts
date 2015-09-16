package org.openshift.quickstarts.todolist.dao;

import org.openshift.quickstarts.todolist.model.TodoEntry;

import java.util.List;

/**
 *
 */
public interface TodoListDAO {

    void save(TodoEntry entry);

    List<TodoEntry> list();
}
