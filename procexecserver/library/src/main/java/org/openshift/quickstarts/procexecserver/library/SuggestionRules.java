package org.openshift.quickstarts.procexecserver.library;

import org.openshift.quickstarts.procexecserver.library.types.SuggestionRequest;
import org.openshift.quickstarts.procexecserver.library.types.SuggestionResponse;

public interface SuggestionRules {

    public SuggestionResponse suggestionRequest(SuggestionRequest request);

}
