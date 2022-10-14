package org.nekosoft.shlink.dao.impl;

import org.nekosoft.shlink.sec.delegation.RunAs;

public class JavaSampleBlockDelegation {

    public Object partlyDelegatedMethod() throws Exception {

        try (RunAs ignored = RunAs.userWithRoles("Viewer", "Stats")) {
            // Your delegated business logic here
        }

        try (RunAs ignored = RunAs.anonymousWithRoles("Viewer", "Stats")) {
            // Your delegated business logic here
        }

        return null;
    }

}
