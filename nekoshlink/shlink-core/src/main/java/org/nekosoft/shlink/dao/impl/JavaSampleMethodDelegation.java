package org.nekosoft.shlink.dao.impl;

import org.nekosoft.shlink.sec.delegation.annotation.RunAs;

public class JavaSampleMethodDelegation {

    @RunAs(roles = {"Editor", "Domains"}, allowAnonymous = true)
    public Object whollyDelegatedMethod() {
        // Your delegated business logic here
        return null;
    }

}
