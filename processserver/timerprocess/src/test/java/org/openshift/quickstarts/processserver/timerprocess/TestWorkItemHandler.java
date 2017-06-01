package org.openshift.quickstarts.processserver.timerprocess;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import java.util.ArrayList;
import java.util.List;

public class TestWorkItemHandler implements WorkItemHandler {

    private List<WorkItem> workItems = new ArrayList<WorkItem>();

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        workItems.add(workItem);
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
    }

    public WorkItem getWorkItem() {
        if (workItems.size() == 0) {
            return null;
        }
        if (workItems.size() == 1) {
            WorkItem result = workItems.get(0);
            this.workItems.clear();
            return result;
        } else {
            throw new IllegalArgumentException("More than one work item active");
        }
    }

    public List<WorkItem> getWorkItems() {
        List<WorkItem> result = new ArrayList<WorkItem>(workItems);
        workItems.clear();
        return result;
    }
}