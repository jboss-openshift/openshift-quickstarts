package org.openshift.quickstarts.todolist.dao;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.openshift.quickstarts.todolist.model.TodoEntry;

/**
 *
 */
public class TodoMapper {

    private static final String ATTR_ID = "_id";
    private static final String ATTR_SUMMARY = "summary";
    private static final String ATTR_DESCRIPTION = "description";

    public TodoEntry fromDocument(Document document) {
        TodoEntry entry = new TodoEntry();
        entry.setId(document.getObjectId(ATTR_ID).toHexString());
        entry.setSummary(document.getString(ATTR_SUMMARY));
        entry.setDescription(document.getString(ATTR_DESCRIPTION));
        return entry;
    }

    public Document toDocument(TodoEntry entry) {
        Document document = new Document();
        if (entry.getId() != null) {
            document.append(ATTR_ID, new ObjectId((String)entry.getId()));
        }
        return document
                .append(ATTR_SUMMARY, entry.getSummary())
                .append(ATTR_DESCRIPTION, entry.getDescription());
    }
}
