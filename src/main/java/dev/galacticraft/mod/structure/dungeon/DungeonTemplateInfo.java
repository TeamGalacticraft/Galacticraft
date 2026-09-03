/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.structure.dungeon;

import dev.galacticraft.mod.Constant;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class DungeonTemplateInfo {
    //TODO remove this when dungeons are working properly
    private static final boolean DEBUG_TEMPLATE_LOADING = true;

    /**
     * We only print each template once per game/server launch.
     *
     * Without this, /locate would cause the same template information to be
     * printed repeatedly for every candidate structure position.
     */
    private static final Set<ResourceLocation> DEBUGGED_TEMPLATES =
            ConcurrentHashMap.newKeySet();

    private final ResourceLocation id;
    private final StructureTemplate template;
    private final List<DungeonConnector> connectors;

    private DungeonTemplateInfo(
            ResourceLocation id,
            StructureTemplate template,
            List<DungeonConnector> connectors
    ) {
        this.id = id;
        this.template = template;
        this.connectors = List.copyOf(connectors);
    }

    public static Optional<DungeonTemplateInfo> load(
            StructureTemplateManager manager,
            ResourceLocation id
    ) {
        Optional<StructureTemplate> optionalTemplate = manager.get(id);

        if (optionalTemplate.isEmpty()) {
            if (shouldDebug(id)) {
                Constant.LOGGER.warn(
                        "[LunarDungeonDebug] Template DOES NOT EXIST: {}",
                        id
                );
            }

            return Optional.empty();
        }

        StructureTemplate template = optionalTemplate.get();

        List<StructureTemplate.StructureBlockInfo> structureBlocks =
                template.filterBlocks(
                        BlockPos.ZERO,
                        new StructurePlaceSettings(),
                        Blocks.STRUCTURE_BLOCK
                );

        boolean debug = shouldDebug(id);

        if (debug) {
            Constant.LOGGER.info(
                    "[LunarDungeonDebug] --------------------------------------------------"
            );

            Constant.LOGGER.info(
                    "[LunarDungeonDebug] Loading template {}",
                    id
            );

            Constant.LOGGER.info(
                    "[LunarDungeonDebug] Template size = {}",
                    template.getSize()
            );

            Constant.LOGGER.info(
                    "[LunarDungeonDebug] Found {} structure blocks",
                    structureBlocks.size()
            );
        }

        List<DungeonConnector> connectors = new ArrayList<>();

        for (StructureTemplate.StructureBlockInfo info : structureBlocks) {
            CompoundTag nbt = info.nbt();

            if (nbt == null) {
                if (debug) {
                    Constant.LOGGER.info(
                            "[LunarDungeonDebug] Structure block at {} has NO NBT",
                            info.pos()
                    );
                }

                continue;
            }

            String mode = nbt.getString("mode");
            String metadata = nbt.getString("metadata");

            if (debug) {
                Constant.LOGGER.info(
                        "[LunarDungeonDebug] Structure block at {} mode='{}' metadata='{}'",
                        info.pos(),
                        mode,
                        metadata
                );
            }

            if (!metadata.startsWith(DungeonConnector.PREFIX)) {
                continue;
            }

            try {
                DungeonConnector connector =
                        DungeonConnector.parse(
                                info.pos(),
                                metadata
                        );

                connectors.add(connector);

                if (debug) {
                    Constant.LOGGER.info(
                            "[LunarDungeonDebug]   -> CONNECTOR pos={} direction={} type={} name='{}'",
                            connector.localPosition(),
                            connector.direction(),
                            connector.type(),
                            connector.name()
                    );
                }
            } catch (RuntimeException exception) {
                Constant.LOGGER.error(
                        "[LunarDungeonDebug] Failed parsing connector '{}' in template {} at {}",
                        metadata,
                        id,
                        info.pos(),
                        exception
                );

                throw exception;
            }
        }

        if (debug) {
            Constant.LOGGER.info(
                    "[LunarDungeonDebug] {} has {} parsed connectors:",
                    id,
                    connectors.size()
            );

            for (DungeonConnector connector : connectors) {
                Constant.LOGGER.info(
                        "[LunarDungeonDebug]     {} {} {} name='{}'",
                        connector.localPosition(),
                        connector.direction(),
                        connector.type(),
                        connector.name()
                );
            }

            Constant.LOGGER.info(
                    "[LunarDungeonDebug] --------------------------------------------------"
            );
        }

        return Optional.of(
                new DungeonTemplateInfo(
                        id,
                        template,
                        connectors
                )
        );
    }

    private static boolean shouldDebug(ResourceLocation id) {
        return DEBUG_TEMPLATE_LOADING
                && DEBUGGED_TEMPLATES.add(id);
    }

    public ResourceLocation id() {
        return this.id;
    }

    public StructureTemplate template() {
        return this.template;
    }

    public List<DungeonConnector> connectors() {
        return this.connectors;
    }
}