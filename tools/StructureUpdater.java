/*
 * Copyright (c) 2019-2025 Team Galacticraft
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringNbtReader;
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

/**
 * Automatically updates the data format for Galacticraft structures.
 * Also does some string transformations.
 *
 * <code>galacticraft-rewoven</code> -> <code>galacticraft</code>
 * <code>moon_pillager_base</code> -> <code>moon_pillager_outpost</code>
 * <code>:moon_village</code> -> <code>:village/moon/highlands</code>
 */
public class StructureUpdater {
    public static void run() {
        DataFixer dataFixer = Schemas.getFixer();
        try (Stream<Path> walk = Files.walk(new File("../src/main/resources/data/galacticraft/").toPath())) {
            List<String> fileNamesList = walk.filter(Files::isRegularFile).map(Path::toString).filter(path -> path.endsWith("nbt")).toList();
            LevelStorage storage = new LevelStorage(new File("./structure_update/").toPath(), new File("./structure_update/backup").toPath(), dataFixer);
            StructureManager manager = new StructureManager(null, storage.createSession("session"), dataFixer);

            for (String s : fileNamesList) {
                NbtCompound tag = NbtIo.readCompressed(new File(s));
                tag = new StringNbtReader(new StringReader(tag.toString().replaceAll("[- ][Rr]ewoven", "").replaceAll("moon_pillager_base", "moon_pillager_outpost").replaceAll(":moon_village", ":village/moon/highlands"))).parseCompound();
                Structure structure = manager.createStructure(tag);
                tag = structure.writeNbt(new NbtCompound());
                File file1 = new File("./structure_update/out/" + s.replace("../src/main/resources/data/galacticraft/", ""));
                file1.getParentFile().mkdirs();
                NbtIo.writeCompressed(tag, file1);
            }

        } catch (IOException | CommandSyntaxException e) {
            e.printStackTrace();
        }
        System.out.println("Structures updated successfully! Exiting...");
        System.exit(0);
    }
}
