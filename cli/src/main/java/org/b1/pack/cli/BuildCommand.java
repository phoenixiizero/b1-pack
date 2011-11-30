/*
 * Copyright 2011 b1.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.b1.pack.cli;

import org.b1.pack.api.builder.PackBuilder;
import org.b1.pack.api.builder.PbFactory;
import org.b1.pack.api.builder.PbVolume;
import org.b1.pack.api.common.PackException;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class BuildCommand implements PackCommand {

    @Override
    public void execute(ArgSet argSet) throws IOException {
        System.out.println("Starting");
        File outputFolder = FileTools.getOutputFolder(argSet);
        Set<FsObject> fsObjects = FileTools.getFsObjects(argSet.getFileNames());
        PackBuilder builder = PbFactory.newInstance(argSet.getTypeFormat()).createPackBuilder(argSet.getPackName(), argSet.getVolumeSize());
        for (FsObject fsObject : fsObjects) {
            File file = fsObject.getFile();
            if (file.isFile()) {
                builder.addFile(new FsPbFile(fsObject));
            } else if (file.isDirectory()) {
                builder.addFolder(new FsPbFolder(fsObject));
            } else {
                throw new PackException("Not found: " + file);
            }
        }
        for (PbVolume volume : builder.getVolumes()) {
            buildVolume(outputFolder, volume);
        }
        System.out.println();
        System.out.println("Done");
    }

    private void buildVolume(File outputFolder, PbVolume volume) throws IOException {
        File volumeFile = new File(outputFolder, volume.getName());
        System.out.println();
        System.out.println("Creating volume " + volumeFile);
        System.out.println();
        if (volumeFile.exists()) {
            throw new PackException("File already exists: " + volumeFile);
        }
        FileTools.saveToFile(volume, volumeFile);
    }
}
