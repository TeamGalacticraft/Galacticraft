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

package dev.galacticraft.mod.content.block.entity;

import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.configuration.RedstoneMode;
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.storage.MachineEnergyStorage;
import dev.galacticraft.machinelib.api.storage.StorageSpec;
import dev.galacticraft.mod.content.AirlockState;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCSounds;
import dev.galacticraft.mod.content.ProximityAccess;
import dev.galacticraft.mod.content.block.machine.airlock.AirlockFrameScanner;
import dev.galacticraft.mod.content.block.special.AirlockSealBlock;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.screen.AirlockControllerMenu;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AirlockControllerBlockEntity extends MachineBlockEntity {
    public static final int MIN_KEYCARD_OPEN_SECONDS = 1;
    public static final int MAX_KEYCARD_OPEN_SECONDS = 9;
    public static final int DEFAULT_KEYCARD_OPEN_SECONDS = 3;
    private static final String NBT_PROXIMITY_OPEN =
            "ProximityOpen";
    private static final String NBT_SEALED_FRAMES =
            "SealedFrames";
    private static final String NBT_PROXIMITY_ACCESS =
            "ProximityAccess";
    private static final String NBT_ACCESS_ID =
            "AccessId";
    private static final String NBT_STRUCTURE_MANAGED =
            "StructureManaged";
    private static final String NBT_KEYCARD_OPEN_SECONDS =
            "KeycardOpenSeconds";
    private static final String NBT_PERMANENT_OPEN_ON_KEYCARD =
            "PermanentOpenOnKeycard";
    private static final String NBT_PERMANENTLY_UNLOCKED =
            "PermanentlyUnlocked";
    private static final StorageSpec SPEC =
            StorageSpec.of(
                    MachineEnergyStorage.spec(
                            0,
                            0
                    )
            );
    private final Set<Long> sealedFrames =
            new HashSet<>();
    private byte proximityOpen = 0;
    private ProximityAccess proximityAccess =
            ProximityAccess.PUBLIC;
    /*
     * A card stores this value.
     *
     * Multiple controllers may intentionally share the same accessId.
     */
    private String accessId =
            UUID.randomUUID().toString();
    /*
     * Structure-managed controllers:
     *
     * - have no player owner
     * - cannot be configured
     * - ignore normal proximity opening
     * - ignore normal redstone opening
     * - require a matching keycard
     */
    private boolean structureManaged = false;
    private int keycardOpenSeconds =
            DEFAULT_KEYCARD_OPEN_SECONDS;
    /*
     * Temporary card activation is deliberately not persisted.
     *
     * If the server stops while a normal airlock is temporarily open,
     * restarting the server closes it again.
     */
    private long keycardOpenUntil = 0L;
    private boolean permanentOpenOnKeycard = false;
    private boolean permanentlyUnlocked = false;
    private List<AirlockFrameScanner.Result> lastFrames =
            Collections.emptyList();
    private Map<Long, AirlockFrameScanner.Result> lastFrameMap =
            Collections.emptyMap();
    private AirlockState state =
            AirlockState.NONE;

    private int ticks = 0;

    public AirlockControllerBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState state
    ) {
        super(
                (BlockEntityType<? extends MachineBlockEntity>) type,
                pos,
                state,
                SPEC
        );
    }

    private static Map<Long, AirlockFrameScanner.Result> indexFrames(
            List<AirlockFrameScanner.Result> list
    ) {
        Map<Long, AirlockFrameScanner.Result> output =
                new HashMap<>(list.size());

        for (AirlockFrameScanner.Result result
                : list) {

            output.put(
                    frameId(result),
                    result
            );
        }

        return output;
    }

    private static long frameId(
            AirlockFrameScanner.Result frame
    ) {
        int hash = 1;

        hash = 31 * hash
                + frame.plane.ordinal();

        hash = 31 * hash + frame.minX;
        hash = 31 * hash + frame.minY;
        hash = 31 * hash + frame.minZ;

        hash = 31 * hash + frame.maxX;
        hash = 31 * hash + frame.maxY;
        hash = 31 * hash + frame.maxZ;

        return hash & 0xffffffffL;
    }

    private static AABB expandedInterior(
            AirlockFrameScanner.Result frame,
            double radius
    ) {
        AABB interior =
                switch (frame.plane) {
                    case XY -> new AABB(
                            frame.minX + 1,
                            frame.minY + 1,
                            frame.minZ,
                            frame.maxX,
                            frame.maxY,
                            frame.maxZ
                    );

                    case XZ -> new AABB(
                            frame.minX + 1,
                            frame.minY,
                            frame.minZ + 1,
                            frame.maxX,
                            frame.maxY,
                            frame.maxZ
                    );

                    case YZ -> new AABB(
                            frame.minX,
                            frame.minY + 1,
                            frame.minZ + 1,
                            frame.maxX,
                            frame.maxY,
                            frame.maxZ
                    );
                };

        return interior.inflate(
                Math.max(
                        radius,
                        0
                ) + 1.0e-4
        );
    }

    private static boolean sameFrames(
            List<AirlockFrameScanner.Result> first,
            List<AirlockFrameScanner.Result> second
    ) {
        if (first == second) {
            return true;
        }

        if (first == null
                || second == null
                || first.size() != second.size()) {

            return false;
        }

        for (int i = 0;
             i < first.size();
             i++) {

            AirlockFrameScanner.Result x =
                    first.get(i);

            AirlockFrameScanner.Result y =
                    second.get(i);

            if (x.plane != y.plane) {
                return false;
            }

            if (x.minX != y.minX
                    || x.minY != y.minY
                    || x.minZ != y.minZ) {

                return false;
            }

            if (x.maxX != y.maxX
                    || x.maxY != y.maxY
                    || x.maxZ != y.maxZ) {

                return false;
            }
        }

        return true;
    }

    public byte getProximityOpen() {
        return this.proximityOpen;
    }

    public void setProximityOpen(byte proximityOpen) {
        if (this.structureManaged) {
            return;
        }

        this.proximityOpen =
                (byte) Math.max(
                        0,
                        Math.min(
                                5,
                                proximityOpen
                        )
                );

        setChanged();
    }

    public ProximityAccess getProximityAccess() {
        return this.proximityAccess;
    }

    public void setProximityAccess(
            ProximityAccess access
    ) {
        if (this.structureManaged) {
            return;
        }

        if (access == null) {
            access = ProximityAccess.PUBLIC;
        }

        if (this.proximityAccess != access) {
            this.proximityAccess = access;
            setChanged();
        }
    }

    public String getAccessId() {
        return this.accessId;
    }

    public boolean acceptsKeycard(
            @Nullable String cardAccessId
    ) {
        return cardAccessId != null
                && Objects.equals(
                this.accessId,
                cardAccessId
        );
    }

    public boolean isStructureManaged() {
        return this.structureManaged;
    }

    public int getKeycardOpenSeconds() {
        return this.keycardOpenSeconds;
    }

    public void setKeycardOpenSeconds(
            int seconds
    ) {
        if (this.structureManaged) {
            return;
        }

        int clamped =
                Math.max(
                        MIN_KEYCARD_OPEN_SECONDS,
                        Math.min(
                                MAX_KEYCARD_OPEN_SECONDS,
                                seconds
                        )
                );

        if (this.keycardOpenSeconds != clamped) {
            this.keycardOpenSeconds = clamped;
            setChanged();
        }
    }

    public boolean isPermanentOpenOnKeycard() {
        return this.permanentOpenOnKeycard;
    }

    public boolean isPermanentlyUnlocked() {
        return this.permanentlyUnlocked;
    }

    public void initializeStructureManagedDefaults() {
        this.structureManaged = true;

        this.proximityOpen = 0;
        this.proximityAccess = ProximityAccess.PRIVATE;

        this.permanentOpenOnKeycard = true;
    }

    public void configureAsStructureManaged(
            String accessId
    ) {
        configureAsStructureManaged(
                accessId,
                true
        );
    }

    public void configureAsStructureManaged(
            String accessId,
            boolean permanentOpenOnKeycard
    ) {
        this.structureManaged = true;

        this.proximityOpen = 0;
        this.proximityAccess =
                ProximityAccess.PRIVATE;

        this.permanentOpenOnKeycard =
                permanentOpenOnKeycard;

        if (accessId != null
                && !accessId.isBlank()) {

            this.accessId = accessId;
        }

        setChanged();

        if (this.level instanceof ServerLevel server) {
            updateAirlockState(server);
        }
    }

    public boolean canConfigure(
            Player player
    ) {
        return !this.structureManaged
                && this.getSecurity().hasAccess(player);
    }

    public boolean canBindKeycard(
            Player player
    ) {
        if (this.structureManaged) {
            return false;
        }

        return switch (this.proximityAccess) {
            case PUBLIC -> true;

            case TEAM -> this.getSecurity()
                    .hasAccess(player);

            case PRIVATE -> this.getSecurity()
                    .isOwner(player);
        };
    }

    /**
     * Activates this controller using a card that has already passed its
     * access-ID check.
     * <p>
     * No normal player permission check is performed here. The keycard itself
     * is the credential.
     *
     * @return true when this card caused a new activation
     */
    public boolean activateKeycard(
            Player player
    ) {
        if (!(this.level instanceof ServerLevel server)) {
            return false;
        }

        if (this.permanentlyUnlocked) {
            return false;
        }

        if (this.permanentOpenOnKeycard) {
            this.permanentlyUnlocked = true;
        } else {
            long duration =
                    this.keycardOpenSeconds
                            * 20L;

            this.keycardOpenUntil =
                    Math.max(
                            this.keycardOpenUntil,
                            server.getGameTime()
                                    + duration
                    );
        }

        setChanged();

        updateAirlockState(server);

        return true;
    }

    private boolean keycardRequestsOpen(
            ServerLevel server
    ) {
        return this.permanentlyUnlocked
                || server.getGameTime()
                < this.keycardOpenUntil;
    }

    @Override
    protected void saveAdditional(
            CompoundTag tag,
            HolderLookup.Provider lookup
    ) {
        super.saveAdditional(
                tag,
                lookup
        );

        tag.putByte(
                NBT_PROXIMITY_OPEN,
                this.proximityOpen
        );

        tag.putInt(
                NBT_PROXIMITY_ACCESS,
                this.proximityAccess.ordinal()
        );

        tag.putString(
                NBT_ACCESS_ID,
                this.accessId
        );

        tag.putBoolean(
                NBT_STRUCTURE_MANAGED,
                this.structureManaged
        );

        tag.putInt(
                NBT_KEYCARD_OPEN_SECONDS,
                this.keycardOpenSeconds
        );

        tag.putBoolean(
                NBT_PERMANENT_OPEN_ON_KEYCARD,
                this.permanentOpenOnKeycard
        );

        tag.putBoolean(
                NBT_PERMANENTLY_UNLOCKED,
                this.permanentlyUnlocked
        );

        long[] sealed =
                this.sealedFrames
                        .stream()
                        .mapToLong(Long::longValue)
                        .toArray();

        tag.putLongArray(
                NBT_SEALED_FRAMES,
                sealed
        );
    }

    @Override
    public void loadAdditional(
            CompoundTag tag,
            HolderLookup.Provider lookup
    ) {
        super.loadAdditional(
                tag,
                lookup
        );

        if (tag.contains(
                NBT_PROXIMITY_OPEN
        )) {
            this.proximityOpen =
                    tag.getByte(
                            NBT_PROXIMITY_OPEN
                    );
        }

        if (tag.contains(
                NBT_PROXIMITY_ACCESS
        )) {
            int ordinal =
                    tag.getInt(
                            NBT_PROXIMITY_ACCESS
                    );

            this.proximityAccess =
                    ordinal >= 0
                            && ordinal
                            < ProximityAccess.values().length
                            ? ProximityAccess.values()[ordinal]
                            : ProximityAccess.PUBLIC;
        }

        if (tag.contains(
                NBT_ACCESS_ID
        )) {
            String loadedId =
                    tag.getString(
                            NBT_ACCESS_ID
                    );

            if (!loadedId.isBlank()) {
                this.accessId = loadedId;
            }
        }

        if (tag.contains(
                NBT_STRUCTURE_MANAGED
        )) {
            this.structureManaged =
                    tag.getBoolean(
                            NBT_STRUCTURE_MANAGED
                    );
        }

        if (tag.contains(
                NBT_KEYCARD_OPEN_SECONDS
        )) {
            this.keycardOpenSeconds =
                    Math.max(
                            MIN_KEYCARD_OPEN_SECONDS,
                            Math.min(
                                    MAX_KEYCARD_OPEN_SECONDS,
                                    tag.getInt(
                                            NBT_KEYCARD_OPEN_SECONDS
                                    )
                            )
                    );
        }

        if (tag.contains(
                NBT_PERMANENT_OPEN_ON_KEYCARD
        )) {
            this.permanentOpenOnKeycard =
                    tag.getBoolean(
                            NBT_PERMANENT_OPEN_ON_KEYCARD
                    );
        }

        if (tag.contains(
                NBT_PERMANENTLY_UNLOCKED
        )) {
            this.permanentlyUnlocked =
                    tag.getBoolean(
                            NBT_PERMANENTLY_UNLOCKED
                    );
        }

        if (tag.contains(
                NBT_SEALED_FRAMES
        )) {
            this.sealedFrames.clear();

            for (long id : tag.getLongArray(
                    NBT_SEALED_FRAMES
            )) {
                this.sealedFrames.add(id);
            }
        }

        /*
         * Never persist a temporary card opening.
         */
        this.keycardOpenUntil = 0L;

        if (this.structureManaged) {
            this.proximityOpen = 0;
            this.proximityAccess =
                    ProximityAccess.PRIVATE;
        }

        onLoad();
    }

    public void onLoad() {
        if (!(this.level instanceof ServerLevel server)) {
            return;
        }

        updateAirlockState(server);
    }

    private void serverTick() {
        if (!(this.level instanceof ServerLevel server)) {
            return;
        }

        if ((++this.ticks % 5) != 0) {
            return;
        }

        updateAirlockState(server);
    }

    private void updateAirlockState(
            ServerLevel server
    ) {
        List<AirlockFrameScanner.Result> frames =
                AirlockFrameScanner.scanAll(
                        server,
                        this.worldPosition
                );

        Map<Long, AirlockFrameScanner.Result> frameMap =
                indexFrames(frames);

        boolean framesChanged =
                !sameFrames(
                        frames,
                        this.lastFrames
                );

        boolean keycardOpen =
                keycardRequestsOpen(server);

        boolean powered =
                server.getBestNeighborSignal(
                        this.worldPosition
                ) > 0;

        RedstoneMode mode =
                this.getRedstoneMode();

        boolean redstoneAllows =
                mode.isActive(powered);

        Set<Long> nextSealed =
                new HashSet<>();

        for (AirlockFrameScanner.Result frame
                : frames) {

            boolean shouldSeal;

            if (this.structureManaged) {
                shouldSeal =
                        !keycardOpen;
            } else if (keycardOpen) {
                shouldSeal = false;
            } else if (!redstoneAllows) {
                shouldSeal = false;
            } else {
                shouldSeal =
                        !hasAuthorizedPlayerNear(
                                server,
                                frame
                        );
            }

            if (shouldSeal) {
                nextSealed.add(
                        frameId(frame)
                );
            }
        }

        boolean anyChange = false;

        for (long id
                : new HashSet<>(
                this.sealedFrames
        )) {
            if (nextSealed.contains(id)) {
                continue;
            }

            AirlockFrameScanner.Result frame =
                    this.lastFrameMap.getOrDefault(
                            id,
                            frameMap.get(id)
                    );

            if (frame != null) {
                unseal(frame);

                this.sealedFrames.remove(id);

                anyChange = true;
            }
        }

        for (long id : nextSealed) {
            if (this.sealedFrames.contains(id)) {
                continue;
            }

            AirlockFrameScanner.Result frame =
                    frameMap.get(id);

            if (frame != null) {
                seal(frame);

                this.sealedFrames.add(id);

                anyChange = true;
            }
        }

        AirlockState newState;

        if (frames.isEmpty()
                || this.sealedFrames.isEmpty()) {

            newState =
                    AirlockState.NONE;

        } else if (this.sealedFrames.size()
                == frames.size()) {

            newState =
                    AirlockState.ALL;

        } else {
            newState =
                    AirlockState.PARTIAL;
        }

        boolean stateChanged =
                newState != this.state;

        this.state =
                newState;

        this.lastFrames =
                frames;

        this.lastFrameMap =
                frameMap;

        if (anyChange
                || framesChanged
                || stateChanged) {

            BlockState blockState =
                    server.getBlockState(
                            this.worldPosition
                    );

            server.sendBlockUpdated(
                    this.worldPosition,
                    blockState,
                    blockState,
                    Block.UPDATE_CLIENTS
            );

            setChanged();
        }
    }

    private boolean hasAuthorizedPlayerNear(
            ServerLevel server,
            AirlockFrameScanner.Result frame
    ) {
        double radius =
                this.proximityOpen;

        if (radius <= 0) {
            return false;
        }

        AABB expanded =
                expandedInterior(
                        frame,
                        radius
                );

        for (Player player
                : server.getEntitiesOfClass(
                Player.class,
                expanded
        )) {
            boolean authorized =
                    switch (this.proximityAccess) {
                        case PUBLIC -> true;

                        case TEAM -> this.getSecurity()
                                .hasAccess(player);

                        case PRIVATE -> this.getSecurity()
                                .isOwner(player);
                    };

            if (authorized) {
                return true;
            }
        }

        return false;
    }

    private void seal(
            AirlockFrameScanner.Result frame
    ) {
        if (!(this.level instanceof ServerLevel server)) {
            return;
        }

        boolean anyAir = false;

        switch (frame.plane) {
            case XY -> {
                int z = frame.minZ;

                for (int x = frame.minX + 1;
                     x <= frame.maxX - 1;
                     x++) {

                    for (int y = frame.minY + 1;
                         y <= frame.maxY - 1;
                         y++) {

                        if (server.getBlockState(
                                new BlockPos(
                                        x,
                                        y,
                                        z
                                )
                        ).isAir()) {
                            anyAir = true;
                        }
                    }
                }
            }

            case XZ -> {
                int y = frame.minY;

                for (int x = frame.minX + 1;
                     x <= frame.maxX - 1;
                     x++) {

                    for (int z = frame.minZ + 1;
                         z <= frame.maxZ - 1;
                         z++) {

                        if (server.getBlockState(
                                new BlockPos(
                                        x,
                                        y,
                                        z
                                )
                        ).isAir()) {
                            anyAir = true;
                        }
                    }
                }
            }

            case YZ -> {
                int x = frame.minX;

                for (int y = frame.minY + 1;
                     y <= frame.maxY - 1;
                     y++) {

                    for (int z = frame.minZ + 1;
                         z <= frame.maxZ - 1;
                         z++) {

                        if (server.getBlockState(
                                new BlockPos(
                                        x,
                                        y,
                                        z
                                )
                        ).isAir()) {
                            anyAir = true;
                        }
                    }
                }
            }
        }

        if (anyAir) {
            BlockPos center =
                    new BlockPos(
                            (
                                    frame.minX
                                            + frame.maxX
                            ) / 2,
                            (
                                    frame.minY
                                            + frame.maxY
                            ) / 2,
                            (
                                    frame.minZ
                                            + frame.maxZ
                            ) / 2
                    );

            server.playSound(
                    null,
                    center,
                    GCSounds.AIRLOCK_CLOSE,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F
            );
        }

        switch (frame.plane) {
            case XY -> {
                int z = frame.minZ;

                for (int x = frame.minX + 1;
                     x <= frame.maxX - 1;
                     x++) {

                    for (int y = frame.minY + 1;
                         y <= frame.maxY - 1;
                         y++) {

                        BlockPos pos =
                                new BlockPos(
                                        x,
                                        y,
                                        z
                                );

                        if (server.getBlockState(pos).isAir()) {
                            server.setBlock(
                                    pos,
                                    GCBlocks.AIR_LOCK_SEAL
                                            .defaultBlockState()
                                            .setValue(
                                                    AirlockSealBlock.FACING,
                                                    frame.sealFacing
                                            ),
                                    Block.UPDATE_ALL
                            );
                        }
                    }
                }
            }

            case XZ -> {
                int y = frame.minY;

                for (int x = frame.minX + 1;
                     x <= frame.maxX - 1;
                     x++) {

                    for (int z = frame.minZ + 1;
                         z <= frame.maxZ - 1;
                         z++) {

                        BlockPos pos =
                                new BlockPos(
                                        x,
                                        y,
                                        z
                                );

                        if (server.getBlockState(pos).isAir()) {
                            server.setBlock(
                                    pos,
                                    GCBlocks.AIR_LOCK_SEAL
                                            .defaultBlockState()
                                            .setValue(
                                                    AirlockSealBlock.FACING,
                                                    frame.sealFacing
                                            ),
                                    Block.UPDATE_ALL
                            );
                        }
                    }
                }
            }

            case YZ -> {
                int x = frame.minX;

                for (int y = frame.minY + 1;
                     y <= frame.maxY - 1;
                     y++) {

                    for (int z = frame.minZ + 1;
                         z <= frame.maxZ - 1;
                         z++) {

                        BlockPos pos =
                                new BlockPos(
                                        x,
                                        y,
                                        z
                                );

                        if (server.getBlockState(pos).isAir()) {
                            server.setBlock(
                                    pos,
                                    GCBlocks.AIR_LOCK_SEAL
                                            .defaultBlockState()
                                            .setValue(
                                                    AirlockSealBlock.FACING,
                                                    frame.sealFacing
                                            ),
                                    Block.UPDATE_ALL
                            );
                        }
                    }
                }
            }
        }
    }

    private void unseal(
            AirlockFrameScanner.Result frame
    ) {
        if (!(this.level instanceof ServerLevel server)) {
            return;
        }

        boolean hadSeal = false;

        switch (frame.plane) {
            case XY -> {
                int z = frame.minZ;

                for (int x = frame.minX + 1;
                     x <= frame.maxX - 1;
                     x++) {

                    for (int y = frame.minY + 1;
                         y <= frame.maxY - 1;
                         y++) {

                        BlockPos pos =
                                new BlockPos(
                                        x,
                                        y,
                                        z
                                );

                        if (server.getBlockState(pos)
                                .is(GCBlocks.AIR_LOCK_SEAL)) {

                            hadSeal = true;

                            server.setBlock(
                                    pos,
                                    Blocks.AIR.defaultBlockState(),
                                    Block.UPDATE_ALL
                            );
                        }
                    }
                }
            }

            case XZ -> {
                int y = frame.minY;

                for (int x = frame.minX + 1;
                     x <= frame.maxX - 1;
                     x++) {

                    for (int z = frame.minZ + 1;
                         z <= frame.maxZ - 1;
                         z++) {

                        BlockPos pos =
                                new BlockPos(
                                        x,
                                        y,
                                        z
                                );

                        if (server.getBlockState(pos)
                                .is(GCBlocks.AIR_LOCK_SEAL)) {

                            hadSeal = true;

                            server.setBlock(
                                    pos,
                                    Blocks.AIR.defaultBlockState(),
                                    Block.UPDATE_ALL
                            );
                        }
                    }
                }
            }

            case YZ -> {
                int x = frame.minX;

                for (int y = frame.minY + 1;
                     y <= frame.maxY - 1;
                     y++) {

                    for (int z = frame.minZ + 1;
                         z <= frame.maxZ - 1;
                         z++) {

                        BlockPos pos =
                                new BlockPos(
                                        x,
                                        y,
                                        z
                                );

                        if (server.getBlockState(pos)
                                .is(GCBlocks.AIR_LOCK_SEAL)) {

                            hadSeal = true;

                            server.setBlock(
                                    pos,
                                    Blocks.AIR.defaultBlockState(),
                                    Block.UPDATE_ALL
                            );
                        }
                    }
                }
            }
        }

        if (hadSeal) {
            BlockPos center =
                    new BlockPos(
                            (
                                    frame.minX
                                            + frame.maxX
                            ) / 2,
                            (
                                    frame.minY
                                            + frame.maxY
                            ) / 2,
                            (
                                    frame.minZ
                                            + frame.maxZ
                            ) / 2
                    );

            server.playSound(
                    null,
                    center,
                    GCSounds.AIRLOCK_OPEN,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F
            );
        }
    }

    public AirlockState getAirlockState() {
        return this.state;
    }

    public List<AirlockFrameScanner.Result> getLastFrames() {
        return this.lastFrames;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(
                Translations.Ui.AIRLOCK_DEFAULT_NAME
        );
    }

    @Override
    protected void tickConstant(
            @NotNull ServerLevel level,
            @NotNull BlockPos pos,
            @NotNull BlockState state,
            @NotNull ProfilerFiller profiler
    ) {
        serverTick();

        super.tickConstant(
                level,
                pos,
                state,
                profiler
        );
    }

    @Override
    protected @NotNull MachineStatus tick(
            @NotNull ServerLevel level,
            @NotNull BlockPos pos,
            @NotNull BlockState state,
            @NotNull ProfilerFiller profiler
    ) {
        return switch (this.state) {
            case ALL -> GCMachineStatuses.AIRLOCK_ENABLED;

            case PARTIAL -> GCMachineStatuses.AIRLOCK_PARTIAL;

            case NONE -> GCMachineStatuses.AIRLOCK_DISABLED;
        };
    }

    @Override
    public @Nullable MachineMenu<? extends MachineBlockEntity> createMenu(
            int syncId,
            Inventory inventory,
            Player player
    ) {
        return new AirlockControllerMenu(
                syncId,
                player,
                this
        );
    }

    @Override
    public void setRemoved() {
        try {
            if (this.level instanceof ServerLevel server) {
                boolean shouldUnseal = false;

                boolean chunkLoaded =
                        server.isLoaded(
                                this.worldPosition
                        );

                if (chunkLoaded) {
                    BlockEntity current =
                            server.getBlockEntity(
                                    this.worldPosition
                            );

                    if (current == null
                            || current != this) {

                        shouldUnseal = true;

                    } else {
                        BlockState stateAtPos =
                                server.getBlockState(
                                        this.worldPosition
                                );

                        if (stateAtPos.isAir()) {
                            shouldUnseal = true;
                        }
                    }
                }

                if (shouldUnseal) {
                    for (long id
                            : this.sealedFrames) {

                        AirlockFrameScanner.Result frame =
                                this.lastFrameMap.get(id);

                        if (frame != null) {
                            unseal(frame);
                        }
                    }

                    this.sealedFrames.clear();
                }
            }
        } finally {
            super.setRemoved();
        }
    }
}