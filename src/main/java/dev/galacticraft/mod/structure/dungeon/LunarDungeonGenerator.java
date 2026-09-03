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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public final class LunarDungeonGenerator {
    private static final int MAX_LAYOUT_ATTEMPTS = 12;

    private static final int MIN_MAIN_WING_LENGTH = 2;
    private static final int MAX_MAIN_WING_LENGTH = 4;

    private static final int MIN_FINAL_LENGTH = 1;
    private static final int MAX_FINAL_LENGTH = 3;

    private static final int MIN_OPTIONAL_BRANCH_LENGTH = 2;
    private static final int MAX_OPTIONAL_BRANCH_LENGTH = 5;

    private static final AtomicInteger GENERATION_CALL_COUNTER =
            new AtomicInteger();

    private LunarDungeonGenerator() {
    }

    public static Optional<List<DungeonPiecePlacement>> generate(
            StructureTemplateManager manager,
            BlockPos hubCenter,
            int minimumBuildHeight,
            int maximumBuildHeight,
            RandomSource random
    ) {
        int generationCall =
                GENERATION_CALL_COUNTER.incrementAndGet();

        for (int attempt = 0;
             attempt < MAX_LAYOUT_ATTEMPTS;
             attempt++) {

            int attemptNumber = attempt + 1;

            RandomSource attemptRandom =
                    RandomSource.create(
                            random.nextLong()
                    );

            Rotation globalRotation =
                    Rotation.getRandom(
                            attemptRandom
                    );

            GenerationState state =
                    new GenerationState(
                            manager,
                            minimumBuildHeight,
                            maximumBuildHeight,
                            generationCall,
                            attemptNumber
                    );

            boolean success =
                    generateAttempt(
                            state,
                            hubCenter,
                            globalRotation,
                            attemptRandom
                    );

            if (success) {

                return Optional.of(
                        state.placements()
                );
            }
        }

        /*
         * Do NOT log this normally.
         *
         * /locate calls this method many times while searching and a failed
         * candidate structure position is not inherently an error.
         */
        return Optional.empty();
    }

    public static void addPieces(
            StructureTemplateManager manager,
            List<DungeonPiecePlacement> placements,
            StructurePieceAccessor pieces
    ) {
        for (DungeonPiecePlacement placement : placements) {
            pieces.addPiece(
                    new LunarDungeonPiece(
                            manager,
                            placement.definition().template(),
                            placement.origin(),
                            placement.rotation()
                    )
            );
        }
    }

    private static boolean generateAttempt(
            GenerationState state,
            BlockPos hubCenter,
            Rotation globalRotation,
            RandomSource random
    ) {
        DungeonPiecePlacement hub =
                state.placeCentered(
                        LunarDungeonPieces.HUB,
                        hubCenter,
                        globalRotation
                );

        if (hub == null) {
            return state.fail(
                    "Could not place HUB: "
                            + state.lastAttachFailure()
            );
        }

        Optional<DungeonPiecePlacement.OpenConnector>
                securityConnector =
                hub.openConnectorNamed("security");

        Optional<DungeonPiecePlacement.OpenConnector>
                reactorConnector =
                hub.openConnectorNamed("reactor");

        Optional<DungeonPiecePlacement.OpenConnector>
                excavationConnector =
                hub.openConnectorNamed("excavation");

        Optional<DungeonPiecePlacement.OpenConnector>
                entranceConnector =
                hub.openConnectorNamed("entrance");

        Optional<DungeonPiecePlacement.OpenConnector>
                finalConnector =
                hub.openConnectorNamed("final");

        if (securityConnector.isEmpty()
                || reactorConnector.isEmpty()
                || excavationConnector.isEmpty()
                || entranceConnector.isEmpty()
                || finalConnector.isEmpty()) {

            return state.fail(
                    "Hub is missing required named connector(s). "
                            + "security=" + securityConnector.isPresent()
                            + ", reactor=" + reactorConnector.isPresent()
                            + ", excavation=" + excavationConnector.isPresent()
                            + ", entrance=" + entranceConnector.isPresent()
                            + ", final=" + finalConnector.isPresent()
                            + ". Parsed connectors: "
                            + describeConnectors(hub)
            );
        }

        // ---------------------------------------------------------------------
        // ENTRANCE
        // ---------------------------------------------------------------------

        if (state.tryAttach(
                LunarDungeonPieces.ENTRANCE,
                entranceConnector.get(),
                0,
                null,
                random,
                false
        ) == null) {

            return state.fail(
                    "Failed attaching ENTRANCE: "
                            + state.lastAttachFailure()
            );
        }

        // ---------------------------------------------------------------------
        // BRANCH STORAGE
        // ---------------------------------------------------------------------

        Map<DungeonWing, List<BranchSeed>> branchSeeds =
                new EnumMap<>(
                        DungeonWing.class
                );

        for (DungeonWing wing :
                DungeonWing.values()) {

            branchSeeds.put(
                    wing,
                    new ArrayList<>()
            );
        }

        // ---------------------------------------------------------------------
        // SECURITY
        // ---------------------------------------------------------------------

        if (!generateWing(
                state,
                DungeonWing.SECURITY,
                securityConnector.get(),
                branchSeeds.get(
                        DungeonWing.SECURITY
                ),
                random
        )) {
            return false;
        }

        // ---------------------------------------------------------------------
        // REACTOR
        // ---------------------------------------------------------------------

        if (!generateWing(
                state,
                DungeonWing.REACTOR,
                reactorConnector.get(),
                branchSeeds.get(
                        DungeonWing.REACTOR
                ),
                random
        )) {
            return false;
        }

        // ---------------------------------------------------------------------
        // EXCAVATION
        // ---------------------------------------------------------------------

        if (!generateWing(
                state,
                DungeonWing.EXCAVATION,
                excavationConnector.get(),
                branchSeeds.get(
                        DungeonWing.EXCAVATION
                ),
                random
        )) {
            return false;
        }

        // ---------------------------------------------------------------------
        // FINAL
        // ---------------------------------------------------------------------

        if (!generateFinalSection(
                state,
                finalConnector.get(),
                random
        )) {
            return false;
        }

        // ---------------------------------------------------------------------
        // OPTIONAL BRANCHES
        // ---------------------------------------------------------------------

        generateOptionalBranches(
                state,
                DungeonWing.SECURITY,
                branchSeeds.get(
                        DungeonWing.SECURITY
                ),
                random
        );

        generateOptionalBranches(
                state,
                DungeonWing.REACTOR,
                branchSeeds.get(
                        DungeonWing.REACTOR
                ),
                random
        );

        generateOptionalBranches(
                state,
                DungeonWing.EXCAVATION,
                branchSeeds.get(
                        DungeonWing.EXCAVATION
                ),
                random
        );

        state.closeRemainingConnectors(
                random
        );

        return true;
    }

    private static boolean generateWing(
            GenerationState state,
            DungeonWing wing,
            DungeonPiecePlacement.OpenConnector startConnector,
            List<BranchSeed> branchSeeds,
            RandomSource random
    ) {
        DungeonPiecePool pool =
                LunarDungeonPieces.poolFor(
                        wing
                );

        int targetLength =
                randomBetween(
                        random,
                        MIN_MAIN_WING_LENGTH,
                        MAX_MAIN_WING_LENGTH
                );

        DungeonPiecePlacement.OpenConnector
                currentConnector =
                startConnector;

        DungeonPieceDefinition previousDefinition =
                LunarDungeonPieces.HUB;

        for (int depth = 1;
             depth <= targetLength;
             depth++) {

            Direction preferredDirection =
                    currentConnector.direction();

            DungeonPiecePlacement piece =
                    state.attachFromPool(
                            pool,
                            currentConnector,
                            depth,
                            previousDefinition,
                            random,
                            true
                    );

            if (piece == null) {
                return state.fail(
                        wing
                                + " wing failed at main-path depth "
                                + depth
                                + ": "
                                + state.lastAttachFailure()
                );
            }

            DungeonPiecePlacement.OpenConnector
                    continuation =
                    chooseContinuation(
                            piece,
                            preferredDirection,
                            random
                    );

            if (continuation == null) {
                return state.fail(
                        wing
                                + " wing piece "
                                + piece.definition().template()
                                + " at depth "
                                + depth
                                + " has no usable continuation connector. "
                                + "Open connectors: "
                                + describeConnectors(piece)
                );
            }

            for (DungeonPiecePlacement.OpenConnector connector :
                    piece.openConnectors()) {

                if (sameConnector(
                        connector,
                        continuation
                )) {
                    continue;
                }

                if (!connector.name().isEmpty()) {
                    continue;
                }

                branchSeeds.add(
                        new BranchSeed(
                                connector,
                                depth
                        )
                );
            }

            currentConnector =
                    continuation;

            previousDefinition =
                    piece.definition();
        }

        DungeonPiecePlacement objective =
                state.tryAttach(
                        LunarDungeonPieces.objectiveFor(
                                wing
                        ),
                        currentConnector,
                        targetLength + 1,
                        previousDefinition,
                        random,
                        false
                );

        if (objective == null) {
            return state.fail(
                    wing
                            + " OBJECTIVE failed to attach: "
                            + state.lastAttachFailure()
            );
        }

        return true;
    }

    private static boolean generateFinalSection(
            GenerationState state,
            DungeonPiecePlacement.OpenConnector hubConnector,
            RandomSource random
    ) {

        DungeonPiecePlacement descent =
                state.tryAttach(
                        LunarDungeonPieces.FINAL_DESCENT,
                        hubConnector,
                        0,
                        LunarDungeonPieces.HUB,
                        random,
                        true
                );

        if (descent == null) {
            return state.fail(
                    "FINAL DESCENT failed to attach: "
                            + state.lastAttachFailure()
            );
        }

        DungeonPiecePlacement.OpenConnector current =
                descent.openConnectorNamed(
                        "route"
                ).orElseGet(
                        () -> chooseContinuation(
                                descent,
                                Direction.SOUTH,
                                random
                        )
                );

        if (current == null) {
            return state.fail(
                    "FINAL DESCENT has no route connector. "
                            + "Connectors: "
                            + describeConnectors(descent)
            );
        }

        DungeonPieceDefinition previous =
                descent.definition();

        int length =
                randomBetween(
                        random,
                        MIN_FINAL_LENGTH,
                        MAX_FINAL_LENGTH
                );

        for (int depth = 1;
             depth <= length;
             depth++) {

            Direction preferred =
                    current.direction();

            DungeonPiecePlacement piece =
                    state.attachFromPool(
                            LunarDungeonPieces.FINAL_POOL,
                            current,
                            depth,
                            previous,
                            random,
                            true
                    );

            if (piece == null) {
                return state.fail(
                        "FINAL route failed at depth "
                                + depth
                                + ": "
                                + state.lastAttachFailure()
                );
            }

            current =
                    chooseContinuation(
                            piece,
                            preferred,
                            random
                    );

            if (current == null) {
                return state.fail(
                        "FINAL piece "
                                + piece.definition().template()
                                + " has no continuation at depth "
                                + depth
                );
            }

            previous =
                    piece.definition();
        }

        DungeonPiecePlacement boss =
                state.tryAttach(
                        LunarDungeonPieces.BOSS,
                        current,
                        length + 1,
                        previous,
                        random,
                        true
                );

        if (boss == null) {
            return state.fail(
                    "BOSS failed to attach: "
                            + state.lastAttachFailure()
            );
        }

        DungeonPiecePlacement.OpenConnector finalCurrent = current;
        DungeonPiecePlacement.OpenConnector
                treasureConnector =
                boss.openConnectorNamed(
                        "treasure"
                ).orElseGet(
                        () -> chooseContinuation(
                                boss,
                                finalCurrent.direction(),
                                random
                        )
                );

        if (treasureConnector == null) {
            return state.fail(
                    "BOSS has no treasure connector. "
                            + "Connectors: "
                            + describeConnectors(boss)
            );
        }

        DungeonPiecePlacement treasure =
                state.tryAttach(
                        LunarDungeonPieces.TREASURE,
                        treasureConnector,
                        length + 2,
                        boss.definition(),
                        random,
                        false
                );

        if (treasure == null) {
            return state.fail(
                    "TREASURE failed to attach: "
                            + state.lastAttachFailure()
            );
        }

        return true;
    }

    private static void generateOptionalBranches(
            GenerationState state,
            DungeonWing wing,
            List<BranchSeed> branchSeeds,
            RandomSource random
    ) {
        if (branchSeeds.isEmpty()) {

            return;
        }

        shuffle(
                branchSeeds,
                random
        );

        int wantedBranches =
                Math.min(
                        branchSeeds.size(),
                        1 + random.nextInt(2)
                );

        for (int i = 0;
             i < wantedBranches;
             i++) {

            BranchSeed seed =
                    branchSeeds.get(i);

            if (seed.connector().isUsed()) {
                continue;
            }

            generateOptionalBranch(
                    state,
                    wing,
                    seed,
                    random
            );
        }
    }

    private static void generateOptionalBranch(
            GenerationState state,
            DungeonWing wing,
            BranchSeed seed,
            RandomSource random
    ) {
        DungeonPiecePool pool =
                LunarDungeonPieces.poolFor(
                        wing
                );

        DungeonPiecePlacement.OpenConnector current =
                seed.connector();

        DungeonPieceDefinition previous =
                current.owner().definition();

        int branchLength =
                randomBetween(
                        random,
                        MIN_OPTIONAL_BRANCH_LENGTH,
                        MAX_OPTIONAL_BRANCH_LENGTH
                );

        for (int i = 0;
             i < branchLength;
             i++) {

            if (current.isUsed()) {
                return;
            }

            Direction preferred =
                    current.direction();

            DungeonPiecePlacement piece =
                    state.attachFromPool(
                            pool,
                            current,
                            seed.depth() + i + 1,
                            previous,
                            random,
                            true
                    );

            if (piece == null) {
                state.closeConnector(
                        current,
                        random
                );

                return;
            }

            current =
                    chooseContinuation(
                            piece,
                            preferred,
                            random
                    );

            if (current == null) {
                return;
            }

            previous =
                    piece.definition();
        }

        DungeonPiecePlacement loot =
                state.tryAttach(
                        LunarDungeonPieces.lootFor(
                                wing
                        ),
                        current,
                        seed.depth()
                                + branchLength
                                + 1,
                        previous,
                        random,
                        false
                );

        if (loot == null) {
            state.closeConnector(
                    current,
                    random
            );
        }
    }

    private static DungeonPiecePlacement.OpenConnector
    chooseContinuation(
            DungeonPiecePlacement piece,
            Direction preferredDirection,
            RandomSource random
    ) {
        List<DungeonPiecePlacement.OpenConnector>
                candidates =
                new ArrayList<>();

        for (DungeonPiecePlacement.OpenConnector connector :
                piece.openConnectors()) {

            if (connector.direction().getAxis()
                    == Direction.Axis.Y) {
                continue;
            }

            candidates.add(
                    connector
            );
        }

        if (candidates.isEmpty()) {
            return null;
        }

        int bestScore =
                Integer.MIN_VALUE;

        List<DungeonPiecePlacement.OpenConnector>
                best =
                new ArrayList<>();

        for (DungeonPiecePlacement.OpenConnector connector :
                candidates) {

            int score;

            if (connector.direction()
                    == preferredDirection) {

                score = 4;

            } else if (
                    connector.direction()
                            == preferredDirection
                            .getOpposite()
            ) {

                score = 1;

            } else {
                score = 3;
            }

            if (score > bestScore) {
                bestScore =
                        score;

                best.clear();

                best.add(
                        connector
                );

            } else if (score == bestScore) {

                best.add(
                        connector
                );
            }
        }

        return best.get(
                random.nextInt(
                        best.size()
                )
        );
    }

    private static boolean sameConnector(
            DungeonPiecePlacement.OpenConnector first,
            DungeonPiecePlacement.OpenConnector second
    ) {
        return first.owner() == second.owner()
                && first.index() == second.index();
    }

    private static int randomBetween(
            RandomSource random,
            int minimum,
            int maximum
    ) {
        return minimum
                + random.nextInt(
                maximum
                        - minimum
                        + 1
        );
    }

    private static <T> void shuffle(
            List<T> list,
            RandomSource random
    ) {
        for (int i = list.size() - 1;
             i > 0;
             i--) {

            int other =
                    random.nextInt(
                            i + 1
                    );

            T value =
                    list.get(i);

            list.set(
                    i,
                    list.get(other)
            );

            list.set(
                    other,
                    value
            );
        }
    }

    private static String describeBox(
            DungeonPiecePlacement placement
    ) {
        return "["
                + placement.boundingBox().minX()
                + ","
                + placement.boundingBox().minY()
                + ","
                + placement.boundingBox().minZ()
                + " -> "
                + placement.boundingBox().maxX()
                + ","
                + placement.boundingBox().maxY()
                + ","
                + placement.boundingBox().maxZ()
                + "]";
    }

    private static String describeConnector(
            DungeonPiecePlacement.OpenConnector connector
    ) {
        return "{"
                + "owner="
                + connector.owner()
                .definition()
                .template()
                + ", pos="
                + connector.position()
                + ", dir="
                + connector.direction()
                + ", type="
                + connector.type()
                + ", name='"
                + connector.name()
                + "'}";
    }

    private static String describeConnectors(
            DungeonPiecePlacement placement
    ) {
        List<String> descriptions =
                new ArrayList<>();

        for (DungeonPiecePlacement.OpenConnector connector :
                placement.openConnectors()) {

            descriptions.add(
                    describeConnector(
                            connector
                    )
            );
        }

        return descriptions.toString();
    }

    private record BranchSeed(
            DungeonPiecePlacement.OpenConnector connector,
            int depth
    ) {
    }

    // =========================================================================
    // GENERATION STATE
    // =========================================================================

    private static final class GenerationState {
        private final StructureTemplateManager manager;

        private final int minimumBuildHeight;
        private final int maximumBuildHeight;

        private final int generationCall;
        private final int attemptNumber;

        private final List<DungeonPiecePlacement> placements =
                new ArrayList<>();

        private final Map<ResourceLocation, Integer> usageCounts =
                new HashMap<>();

        private final Map<
                ResourceLocation,
                Optional<DungeonTemplateInfo>
                > templateCache =
                new HashMap<>();

        private String failureReason =
                "No failure reason was recorded";

        private String lastAttachFailure =
                "No attach attempt has failed yet";

        private GenerationState(
                StructureTemplateManager manager,
                int minimumBuildHeight,
                int maximumBuildHeight,
                int generationCall,
                int attemptNumber
        ) {
            this.manager =
                    manager;

            this.minimumBuildHeight =
                    minimumBuildHeight;

            this.maximumBuildHeight =
                    maximumBuildHeight;

            this.generationCall =
                    generationCall;

            this.attemptNumber =
                    attemptNumber;
        }

        private List<DungeonPiecePlacement> placements() {
            return List.copyOf(
                    this.placements
            );
        }

        private String failureReason() {
            return this.failureReason;
        }

        private String lastAttachFailure() {
            return this.lastAttachFailure;
        }

        private boolean fail(String reason) {
            this.failureReason =
                    reason;
            return false;
        }

        private Optional<DungeonTemplateInfo>
        templateInfo(
                DungeonPieceDefinition definition
        ) {
            return this.templateCache.computeIfAbsent(
                    definition.template(),
                    id -> DungeonTemplateInfo.load(
                            this.manager,
                            id
                    )
            );
        }

        private DungeonPiecePlacement placeCentered(
                DungeonPieceDefinition definition,
                BlockPos desiredCenter,
                Rotation rotation
        ) {
            Optional<DungeonTemplateInfo>
                    optionalInfo =
                    this.templateInfo(
                            definition
                    );

            if (optionalInfo.isEmpty()) {
                this.lastAttachFailure =
                        "Template not found: "
                                + definition.template();

                return null;
            }

            DungeonTemplateInfo info =
                    optionalInfo.get();

            int centerX =
                    info.template()
                            .getSize()
                            .getX()
                            / 2;

            int centerZ =
                    info.template()
                            .getSize()
                            .getZ()
                            / 2;

            BlockPos localCenter =
                    new BlockPos(
                            centerX,
                            0,
                            centerZ
                    );

            BlockPos transformedCenter =
                    StructureTemplate.transform(
                            localCenter,
                            Mirror.NONE,
                            rotation,
                            BlockPos.ZERO
                    );

            BlockPos origin =
                    desiredCenter.offset(
                            -transformedCenter.getX(),
                            -transformedCenter.getY(),
                            -transformedCenter.getZ()
                    );

            DungeonPiecePlacement placement =
                    DungeonPiecePlacement.create(
                            definition,
                            info,
                            origin,
                            rotation
                    );

            if (!this.withinWorld(
                    placement
            )) {
                this.lastAttachFailure =
                        "Centered piece "
                                + definition.template()
                                + " would be outside build height. "
                                + "box="
                                + describeBox(placement);

                return null;
            }

            this.commit(
                    placement
            );

            return placement;
        }

        private DungeonPiecePlacement attachFromPool(
                DungeonPiecePool pool,
                DungeonPiecePlacement.OpenConnector target,
                int depth,
                DungeonPieceDefinition previous,
                RandomSource random,
                boolean requireContinuation
        ) {
            List<DungeonPieceDefinition> candidates =
                    pool.weightedOrder(
                            random,
                            definition ->
                                    this.canUse(
                                            definition,
                                            depth,
                                            previous
                                    )
                                            && this.templateInfo(
                                            definition
                                    ).isPresent()
                    );

            if (candidates.isEmpty()) {
                this.lastAttachFailure =
                        "Piece pool has no eligible loaded definitions "
                                + "at depth "
                                + depth
                                + ". Target="
                                + describeConnector(target);

                return null;
            }

            List<String> failures =
                    new ArrayList<>();

            for (DungeonPieceDefinition definition :
                    candidates) {

                DungeonPiecePlacement placement =
                        this.tryAttach(
                                definition,
                                target,
                                depth,
                                previous,
                                random,
                                requireContinuation
                        );

                if (placement != null) {
                    return placement;
                }

                failures.add(
                        definition.template()
                                + " -> "
                                + this.lastAttachFailure
                );
            }

            this.lastAttachFailure =
                    "Every candidate in pool failed. "
                            + failures;

            return null;
        }

        private DungeonPiecePlacement tryAttach(
                DungeonPieceDefinition definition,
                DungeonPiecePlacement.OpenConnector target,
                int depth,
                DungeonPieceDefinition previous,
                RandomSource random,
                boolean requireContinuation
        ) {
            if (target.isUsed()) {
                this.lastAttachFailure =
                        "Target connector is already used: "
                                + describeConnector(
                                target
                        );

                return null;
            }

            if (!this.canUse(
                    definition,
                    depth,
                    previous
            )) {
                this.lastAttachFailure =
                        "Definition is not eligible: "
                                + definition.template()
                                + " depth="
                                + depth
                                + " previous="
                                + (
                                previous == null
                                        ? "null"
                                        : previous.template()
                        );

                return null;
            }

            Optional<DungeonTemplateInfo>
                    optionalInfo =
                    this.templateInfo(
                            definition
                    );

            if (optionalInfo.isEmpty()) {
                this.lastAttachFailure =
                        "Template not found: "
                                + definition.template();

                return null;
            }

            DungeonTemplateInfo info =
                    optionalInfo.get();

            if (info.connectors().isEmpty()) {
                this.lastAttachFailure =
                        "Template "
                                + definition.template()
                                + " has ZERO parsed connectors";

                return null;
            }

            List<Rotation> rotations =
                    new ArrayList<>(
                            Rotation.getShuffled(
                                    random
                            )
                    );

            List<Integer> connectorIndexes =
                    new ArrayList<>();

            for (int i = 0;
                 i < info.connectors().size();
                 i++) {

                connectorIndexes.add(
                        i
                );
            }

            shuffle(
                    connectorIndexes,
                    random
            );

            int combinationsChecked = 0;
            int typeMismatchCount = 0;
            int directionMismatchCount = 0;
            int outsideWorldCount = 0;
            int collisionCount = 0;
            int noContinuationCount = 0;

            String firstCollision =
                    null;

            for (Rotation rotation :
                    rotations) {

                for (int connectorIndex :
                        connectorIndexes) {

                    combinationsChecked++;

                    DungeonConnector localConnector =
                            info.connectors()
                                    .get(
                                            connectorIndex
                                    );

                    if (localConnector.type()
                            != target.type()) {

                        typeMismatchCount++;
                        continue;
                    }

                    Direction transformedDirection =
                            rotation.rotate(
                                    localConnector.direction()
                            );

                    if (transformedDirection
                            != target.direction()
                            .getOpposite()) {

                        directionMismatchCount++;
                        continue;
                    }

                    BlockPos requiredWorldPosition =
                            target.position()
                                    .relative(
                                            target.direction()
                                    );

                    BlockPos transformedLocalPosition =
                            StructureTemplate.transform(
                                    localConnector.localPosition(),
                                    Mirror.NONE,
                                    rotation,
                                    BlockPos.ZERO
                            );

                    BlockPos origin =
                            requiredWorldPosition.offset(
                                    -transformedLocalPosition.getX(),
                                    -transformedLocalPosition.getY(),
                                    -transformedLocalPosition.getZ()
                            );

                    DungeonPiecePlacement candidate =
                            DungeonPiecePlacement.create(
                                    definition,
                                    info,
                                    origin,
                                    rotation
                            );

                    if (!this.withinWorld(
                            candidate
                    )) {
                        outsideWorldCount++;
                        continue;
                    }

                    DungeonPiecePlacement collision =
                            this.findCollision(
                                    candidate
                            );

                    if (collision != null) {
                        collisionCount++;

                        if (firstCollision == null) {
                            firstCollision =
                                    definition.template()
                                            + " "
                                            + describeBox(candidate)
                                            + " collided with "
                                            + collision.definition().template()
                                            + " "
                                            + describeBox(collision);
                        }

                        continue;
                    }

                    if (requireContinuation
                            && candidate.connectors()
                            .size() <= 1) {

                        noContinuationCount++;

                        continue;
                    }

                    target.markUsed();

                    candidate.markUsed(
                            connectorIndex
                    );

                    this.commit(
                            candidate
                    );

                    return candidate;
                }
            }

            this.lastAttachFailure =
                    "Could not attach "
                            + definition.template()
                            + " to "
                            + describeConnector(target)
                            + ". combinations="
                            + combinationsChecked
                            + ", typeMismatch="
                            + typeMismatchCount
                            + ", directionMismatch="
                            + directionMismatchCount
                            + ", outsideWorld="
                            + outsideWorldCount
                            + ", collisions="
                            + collisionCount
                            + ", noContinuation="
                            + noContinuationCount
                            + (
                            firstCollision == null
                                    ? ""
                                    : ", firstCollision={"
                                    + firstCollision
                                    + "}"
                    );

            return null;
        }

        private boolean canUse(
                DungeonPieceDefinition definition,
                int depth,
                DungeonPieceDefinition previous
        ) {
            if (!definition.canGenerateAtDepth(
                    depth
            )) {
                return false;
            }

            int count =
                    this.usageCounts.getOrDefault(
                            definition.template(),
                            0
                    );

            if (definition.hasGenerationLimit()
                    && count
                    >= definition.maxPerDungeon()) {

                return false;
            }

            if (previous != null
                    && !definition
                    .allowSameCategoryConsecutively()
                    && previous.category()
                    == definition.category()) {

                return false;
            }

            return true;
        }

        private DungeonPiecePlacement findCollision(
                DungeonPiecePlacement candidate
        ) {
            for (DungeonPiecePlacement existing :
                    this.placements) {

                if (candidate.boundingBox()
                        .intersects(
                                existing.boundingBox()
                        )) {

                    return existing;
                }
            }

            return null;
        }

        private boolean withinWorld(
                DungeonPiecePlacement placement
        ) {
            return placement.boundingBox().minY()
                    >= this.minimumBuildHeight

                    && placement.boundingBox().maxY()
                    < this.maximumBuildHeight;
        }

        private void commit(
                DungeonPiecePlacement placement
        ) {
            this.placements.add(
                    placement
            );

            this.usageCounts.merge(
                    placement.definition()
                            .template(),
                    1,
                    Integer::sum
            );
        }

        private void closeConnector(
                DungeonPiecePlacement.OpenConnector connector,
                RandomSource random
        ) {
            if (connector.isUsed()) {
                return;
            }

            this.attachFromPool(
                    LunarDungeonPieces.TERMINATOR_POOL,
                    connector,
                    0,
                    connector.owner()
                            .definition(),
                    random,
                    false
            );
        }

        private void closeRemainingConnectors(
                RandomSource random
        ) {
            List<DungeonPiecePlacement.OpenConnector>
                    open =
                    new ArrayList<>();

            for (DungeonPiecePlacement placement :
                    this.placements) {

                open.addAll(
                        placement.openConnectors()
                );
            }

            for (DungeonPiecePlacement.OpenConnector connector :
                    open) {

                if (!connector.isUsed()) {
                    this.closeConnector(
                            connector,
                            random
                    );
                }
            }
        }
    }
}