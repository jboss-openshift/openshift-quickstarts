package org.openshift.quickstarts.procexecserver.library;

import org.openshift.quickstarts.procexecserver.library.types.LoanRequest;
import org.openshift.quickstarts.procexecserver.library.types.LoanResponse;
import org.openshift.quickstarts.procexecserver.library.types.ReturnRequest;
import org.openshift.quickstarts.procexecserver.library.types.ReturnResponse;

public interface LoanProcess {

    public LoanResponse loanRequest(LoanRequest request);

    public ReturnResponse returnRequest(ReturnRequest request);

}
