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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;

public final class DungeonPiecePlacement {
    private final DungeonPieceDefinition definition;
    private final DungeonTemplateInfo templateInfo;
    private final BlockPos origin;
    private final Rotation rotation;
    private final BoundingBox boundingBox;

    private final List<PlacedConnector> connectors;
    private final BitSet usedConnectors;

    private DungeonPiecePlacement(
            DungeonPieceDefinition definition,
            DungeonTemplateInfo templateInfo,
            BlockPos origin,
            Rotation rotation,
            BoundingBox boundingBox,
            List<PlacedConnector> connectors
    ) {
        this.definition = definition;
        this.templateInfo = templateInfo;
        this.origin = origin;
        this.rotation = rotation;
        this.boundingBox = boundingBox;
        this.connectors = List.copyOf(connectors);
        this.usedConnectors = new BitSet(connectors.size());
    }

    public static DungeonPiecePlacement create(
            DungeonPieceDefinition definition,
            DungeonTemplateInfo templateInfo,
            BlockPos origin,
            Rotation rotation
    ) {
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setMirror(Mirror.NONE)
                .setRotation(rotation);

        BoundingBox boundingBox = templateInfo.template().getBoundingBox(settings, origin);

        List<PlacedConnector> connectors = new ArrayList<>();

        List<DungeonConnector> localConnectors = templateInfo.connectors();

        for (int i = 0; i < localConnectors.size(); i++) {
            DungeonConnector connector = localConnectors.get(i);

            BlockPos rotatedLocalPosition = StructureTemplate.transform(
                    connector.localPosition(),
                    Mirror.NONE,
                    rotation,
                    BlockPos.ZERO
            );

            BlockPos worldPosition = origin.offset(rotatedLocalPosition);

            Direction worldDirection = rotation.rotate(connector.direction());

            connectors.add(
                    new PlacedConnector(
                            i,
                            worldPosition,
                            worldDirection,
                            connector.type(),
                            connector.name()
                    )
            );
        }

        return new DungeonPiecePlacement(
                definition,
                templateInfo,
                origin,
                rotation,
                boundingBox,
                connectors
        );
    }

    public DungeonPieceDefinition definition() {
        return this.definition;
    }

    public DungeonTemplateInfo templateInfo() {
        return this.templateInfo;
    }

    public BlockPos origin() {
        return this.origin;
    }

    public Rotation rotation() {
        return this.rotation;
    }

    public BoundingBox boundingBox() {
        return this.boundingBox;
    }

    public List<PlacedConnector> connectors() {
        return this.connectors;
    }

    public void markUsed(int connectorIndex) {
        this.usedConnectors.set(connectorIndex);
    }

    public boolean isUsed(int connectorIndex) {
        return this.usedConnectors.get(connectorIndex);
    }

    public List<OpenConnector> openConnectors() {
        List<OpenConnector> result = new ArrayList<>();

        for (PlacedConnector connector : this.connectors) {
            if (!this.isUsed(connector.index())) {
                result.add(
                        new OpenConnector(
                                this,
                                connector.index(),
                                connector.position(),
                                connector.direction(),
                                connector.type(),
                                connector.name()
                        )
                );
            }
        }

        return result;
    }

    public Optional<OpenConnector> openConnectorNamed(String name) {
        for (OpenConnector connector : this.openConnectors()) {
            if (connector.name().equalsIgnoreCase(name)) {
                return Optional.of(connector);
            }
        }

        return Optional.empty();
    }

    public record PlacedConnector(
            int index,
            BlockPos position,
            Direction direction,
            DungeonConnectorType type,
            String name
    ) {
    }

    public record OpenConnector(
            DungeonPiecePlacement owner,
            int index,
            BlockPos position,
            Direction direction,
            DungeonConnectorType type,
            String name
    ) {
        public boolean isUsed() {
            return this.owner.isUsed(this.index);
        }

        public void markUsed() {
            this.owner.markUsed(this.index);
        }
    }
}