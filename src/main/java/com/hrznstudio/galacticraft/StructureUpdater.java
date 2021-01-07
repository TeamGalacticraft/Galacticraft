/*
 * Copyright (c) 2020 HRZN LTD
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR a\\ PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hrznstudio.galacticraft;

import com.mojang.datafixers.DataFixer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.world.level.storage.LevelStorage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StructureUpdater {
    public static void update(DataFixer dataFixer) {
        try (Stream<Path> walk = Files.walk((new File("../src/main/resources/data/galacticraft-rewoven/").toPath()))) {
            List<String> fileNamesList = walk.filter(Files::isRegularFile).filter(path -> path.toString().endsWith("nbt")).map(Path::toString).collect(Collectors.toList());
            LevelStorage storage = new LevelStorage(new File("../run/structure_update/").toPath(), new File("../run/structure_update/backup").toPath(), dataFixer);
            StructureManager manager = new StructureManager(null, storage.createSession("session"), dataFixer);

            for (String s : fileNamesList) {
                File file = new File(s);
                Structure structure = manager.createStructure(NbtIo.readCompressed(file));
                CompoundTag tag = structure.toTag(new CompoundTag());
                File file1 = new File("../run/structure_update/out/" + s.replace("../src/main/resources/data/galacticraft-rewoven/", ""));
                file1.getParentFile().mkdirs();
                NbtIo.writeCompressed(tag, file1);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
