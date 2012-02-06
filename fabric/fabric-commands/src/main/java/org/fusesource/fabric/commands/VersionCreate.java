/*
 * Copyright (C) FuseSource, Inc.
 * http://fusesource.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fusesource.fabric.commands;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.fusesource.fabric.api.Version;
import org.fusesource.fabric.commands.support.FabricCommand;

@Command(name = "version-create", scope = "fabric", description = "Create a new version")
public class VersionCreate extends FabricCommand {

    @Option(name = "--parent", description = "The parent version (will default use latest version as parent)")
    private String parentVersion;

    @Argument(index = 0, required = false)
    private String name;

    @Override
    protected Object doExecute() throws Exception {
        Version latestVersion = null;

        Version[] versions = fabricService.getVersions();
        int vlength = versions.length;
        if (vlength > 0) {
            latestVersion = versions[vlength - 1];
        }
        if (name == null) {
            if (latestVersion == null) {
                throw new IllegalArgumentException("Cannot default the new version name as there are no versions available");
            }
            name = latestVersion.getSequence().next().getName();
        }

        Version parent;
        if (parentVersion == null) {
            parent = latestVersion;
            // TODO we maybe want to choose the version which is less than the 'name' if it was specified
            // e.g. if you create a version 1.1 then it should use 1.0 if there is already a 2.0
        } else {
            parent = fabricService.getVersion(parentVersion);
            if (parent == null) {
                throw new IllegalArgumentException("Cannot find parent version: " + parentVersion);
            }
        }
        if (parent != null) {
            fabricService.createVersion(parent, name);
            System.out.println("Created version: " + name + " as copy of: " + parent.getName());
        } else {
            fabricService.createVersion(name);
            System.out.println("Created version: " + name);
        }
        return null;
    }
}
