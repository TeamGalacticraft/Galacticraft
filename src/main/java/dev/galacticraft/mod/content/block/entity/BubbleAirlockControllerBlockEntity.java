package dev.galacticraft.mod.content.block.entity;

import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.configuration.RedstoneMode;
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.storage.MachineEnergyStorage;
import dev.galacticraft.machinelib.api.storage.StorageSpec;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.client.model.GCModelLoader;
import dev.galacticraft.mod.content.AirlockState;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.ProximityAccess;
import dev.galacticraft.mod.content.block.machine.airlock.AirlockFrameScanner;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.screen.BubbleAirlockControllerMenu;
import dev.maximus.glasswork.api.GlassworkAPI;
import dev.maximus.glasswork.api.InjectedQuad;
import dev.maximus.glasswork.api.QuadVertex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BubbleAirlockControllerBlockEntity extends MachineBlockEntity {
    private static final long PER_TICK = Galacticraft.CONFIG.oxygenCollectorEnergyConsumptionRate();

    private static final int COLOR_ARGB   = 0x50296BF2;
    private static final int PACKED_LIGHT = 0x00F000F0;

    private static final StorageSpec SPEC = StorageSpec.of(
            MachineEnergyStorage.spec(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.oxygenCollectorEnergyConsumptionRate() * 2,
                    0
            )
    );

    private static final String NBT_ACCESS = "Access";
    private static final String NBT_ACTIVE = "Active";

    private ProximityAccess access = ProximityAccess.PUBLIC;
    private boolean active = false;

    private AirlockState displayState = AirlockState.NONE;
    private List<AirlockFrameScanner.Result> frames = Collections.emptyList();
    private Map<Long, AirlockFrameScanner.Result> frameIndex = Collections.emptyMap();

    private final Set<SectionPos> uploadedSections = new HashSet<>();

    public BubbleAirlockControllerBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.BUBBLE_AIRLOCK_CONTROLLER, pos, state, SPEC);
    }

    public ProximityAccess getAccess() { return access; }
    public void setAccess(ProximityAccess a) {
        if (a == null) a = ProximityAccess.PUBLIC;
        if (a != this.access) { this.access = a; setChanged(); }
    }
    public AirlockState getDisplayState() { return displayState; }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        tag.putInt(NBT_ACCESS, access.ordinal());
        tag.putBoolean(NBT_ACTIVE, active);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        if (tag.contains(NBT_ACCESS)) {
            int i = tag.getInt(NBT_ACCESS);
            access = (i >= 0 && i < ProximityAccess.values().length) ? ProximityAccess.values()[i] : ProximityAccess.PUBLIC;
        }
        if (tag.contains(NBT_ACTIVE)) {
            active = tag.getBoolean(NBT_ACTIVE);
        }
        onLoad();
    }

    public void onLoad() {
        if (!(this.level instanceof ServerLevel server)) return;
        rescanFrames(server);
        if (active) pushAllQuads();
    }

    @Override
    public void setRemoved() {
        try {
            clearAllQuads();
        } finally {
            super.setRemoved();
        }
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        serverTick(level);
        super.tickConstant(level, pos, state, profiler);
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        return switch (displayState) {
            case ALL     -> GCMachineStatuses.AIRLOCK_ENABLED;
            case PARTIAL -> GCMachineStatuses.AIRLOCK_PARTIAL;
            case NONE    -> GCMachineStatuses.AIRLOCK_DISABLED;
        };
    }

    private void serverTick(ServerLevel server) {
        boolean framesChanged = rescanFrames(server);

        boolean powered = server.getBestNeighborSignal(this.worldPosition) > 0;
        RedstoneMode rs = this.getRedstoneMode();
        boolean rsAllows = rs.isActive(powered);

        boolean hasEnergy = this.energyStorage().canExtract(PER_TICK);

        boolean shouldBeActive = rsAllows && !frames.isEmpty() && hasEnergy;

        if (shouldBeActive != this.active || framesChanged) {
            this.active = shouldBeActive;
            if (this.active) {
                pushAllQuads();
                displayState = AirlockState.ALL;
            } else {
                clearAllQuads();
                displayState = AirlockState.NONE;
            }
            setChanged();
            server.sendBlockUpdated(this.worldPosition, server.getBlockState(this.worldPosition), server.getBlockState(this.worldPosition), Block.UPDATE_CLIENTS);
        }

        if (this.active) {
            this.energyStorage().extract(PER_TICK);
            applyBarrier(server);
        }
    }

    private boolean rescanFrames(ServerLevel server) {
        List<AirlockFrameScanner.Result> next = AirlockFrameScanner.scanAll(server, this.worldPosition);
        if (!sameFrames(frames, next)) {
            frames = next;
            frameIndex = indexFrames(next);
            return true;
        }
        return false;
    }

    private static boolean sameFrames(List<AirlockFrameScanner.Result> a, List<AirlockFrameScanner.Result> b) {
        if (a == b) return true;
        if (a == null || b == null || a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) {
            var x = a.get(i); var y = b.get(i);
            if (x.plane != y.plane) return false;
            if (x.minX != y.minX || x.minY != y.minY || x.minZ != y.minZ) return false;
            if (x.maxX != y.maxX || x.maxY != y.maxY || x.maxZ != y.maxZ) return false;
        }
        return true;
    }

    private static Map<Long, AirlockFrameScanner.Result> indexFrames(List<AirlockFrameScanner.Result> list) {
        Map<Long, AirlockFrameScanner.Result> out = new HashMap<>(list.size());
        for (AirlockFrameScanner.Result r : list) out.put(frameId(r), r);
        return out;
    }

    private static long frameId(AirlockFrameScanner.Result f) {
        int h = 1;
        h = 31 * h + f.plane.ordinal();
        h = 31 * h + f.minX; h = 31 * h + f.minY; h = 31 * h + f.minZ;
        h = 31 * h + f.maxX; h = 31 * h + f.maxY; h = 31 * h + f.maxZ;
        return (h & 0xffffffffL);
    }

    private void pushAllQuads() {
        if (level == null) return;
        clearAllQuads();

        for (var f : frames) {
            var quads = buildPlaneQuads(f, COLOR_ARGB, PACKED_LIGHT);
            if (!quads.isEmpty()) {
                var q = quads.get(0);
                SectionPos sec = SectionPos.of(new BlockPos((int) q.v1().x(), (int) q.v1().y(), (int) q.v1().z()));
                if (level instanceof ServerLevel server) {
                    GlassworkAPI.serverPut(server, sec, quads);
                }
                uploadedSections.add(sec);
            }
        }
    }

    private void clearAllQuads() {
        if (level == null) return;
        if (level instanceof ServerLevel server) {
            for (SectionPos sec : uploadedSections) {
                GlassworkAPI.serverRemoveAll(server, sec);
            }
        }
        uploadedSections.clear();
    }

    private List<InjectedQuad> buildPlaneQuads(AirlockFrameScanner.Result f, int argb, int light) {
        List<InjectedQuad> out = new ArrayList<>(2);

        switch (f.plane) {
            case XY -> {
                int z = f.minZ;
                float x1 = f.minX + 1, x2 = f.maxX;
                float y1 = f.minY + 1, y2 = f.maxY;

                float zPlus  = z + 0.5f;
                float zMinus = z + 0.5f;

                out.add(quad(x1, y1, zPlus,  x2, y1, zPlus,  x2, y2, zPlus,  x1, y2, zPlus,
                        argb, light, 0, 0, +1));
                out.add(quad(x1, y2, zMinus, x2, y2, zMinus, x2, y1, zMinus, x1, y1, zMinus,
                        argb, light, 0, 0, -1));
            }

            case XZ -> {
                int y = f.minY;
                float x1 = f.minX + 1, x2 = f.maxX;
                float z1 = f.minZ + 1, z2 = f.maxZ;

                float yPlus  = y + 0.5f;
                float yMinus = y + 0.5f;

                out.add(quad(x1, yPlus, z1,  x2, yPlus, z1,  x2, yPlus, z2,  x1, yPlus, z2,
                        argb, light, 0, +1, 0));
                out.add(quad(x1, yMinus, z2, x2, yMinus, z2, x2, yMinus, z1, x1, yMinus, z1,
                        argb, light, 0, -1, 0));
            }

            case YZ -> {
                int x = f.minX;
                float y1 = f.minY + 1, y2 = f.maxY;
                float z1 = f.minZ + 1, z2 = f.maxZ;

                float xPlus  = x + 0.5f;
                float xMinus = x + 0.5f;

                out.add(quad(xPlus, y1, z1,  xPlus, y1, z2,  xPlus, y2, z2,  xPlus, y2, z1,
                        argb, light, +1, 0, 0));
                out.add(quad(xMinus, y2, z1, xMinus, y2, z2, xMinus, y1, z2, xMinus, y1, z1,
                        argb, light, -1, 0, 0));
            }
        }
        return out;
    }

    private static final ResourceLocation WHITE_SPRITE_ID = Constant.id("block/white");

    private static TextureAtlasSprite whiteSprite() {
        var mc = Minecraft.getInstance();
        return mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(WHITE_SPRITE_ID);
    }

    private static InjectedQuad quad(
            float x1,float y1,float z1,
            float x2,float y2,float z2,
            float x3,float y3,float z3,
            float x4,float y4,float z4,
            int argb, int light,
            float nx,float ny,float nz
    ) {
        var s = whiteSprite();
        float u0 = s != null ? s.getU0() : 0f, v0 = s != null ? s.getV0() : 0f;
        float u1 = s != null ? s.getU1() : 1f, v1 = s != null ? s.getV1() : 1f;

        var v1q = new QuadVertex(x1,y1,z1, u0,v0, argb, light, 0, nx,ny,nz);
        var v2q = new QuadVertex(x2,y2,z2, u1,v0, argb, light, 0, nx,ny,nz);
        var v3q = new QuadVertex(x3,y3,z3, u1,v1, argb, light, 0, nx,ny,nz);
        var v4q = new QuadVertex(x4,y4,z4, u0,v1, argb, light, 0, nx,ny,nz);
        return new InjectedQuad(v1q,v2q,v3q,v4q);
    }

    private void applyBarrier(ServerLevel level) {
        for (var f : frames) {
            AABB aabb = interiorSlab(f).inflate(0.2);
            for (Player p : level.getEntitiesOfClass(Player.class, aabb)) {
                if (isAuthorized(p)) continue;
                Direction d = f.sealFacing;
                p.setDeltaMovement(p.getDeltaMovement().add(
                        d.getStepX() * 0.2,
                        d.getStepY() * 0.2,
                        d.getStepZ() * 0.2
                ));
                p.hurtMarked = true;
            }
        }
    }

    private boolean isAuthorized(Player p) {
        return switch (access) {
            case PUBLIC  -> true;
            case TEAM    -> this.getSecurity().hasAccess(p);
            case PRIVATE -> this.getSecurity().isOwner(p);
        };
    }

    private static AABB interiorSlab(AirlockFrameScanner.Result f) {
        return switch (f.plane) {
            case XY -> new AABB(f.minX + 1, f.minY + 1, f.minZ,     f.maxX,     f.maxY,     f.maxZ);
            case XZ -> new AABB(f.minX + 1, f.minY,     f.minZ + 1, f.maxX,     f.maxY,     f.maxZ);
            case YZ -> new AABB(f.minX,     f.minY + 1, f.minZ + 1, f.maxX,     f.maxY,     f.maxZ);
        };
    }

    @Override
    public @Nullable MachineMenu<? extends MachineBlockEntity> createMenu(int syncId, Inventory inventory, Player player) {
        return new BubbleAirlockControllerMenu(syncId, player, this);
    }
}