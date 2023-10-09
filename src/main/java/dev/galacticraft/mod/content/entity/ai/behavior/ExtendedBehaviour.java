package dev.galacticraft.mod.content.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An extension of the base Behavior class that is used for tasks in the brain
 * system. <br>
 * This extension auto-handles some boilerplate and adds in some additional
 * auto-handled functions: <br>
 * <ul>
 * <li>Task start and stop callbacks for additional entity-interactions</li>
 * <li>A functional implementation of a duration provider</li>
 * <li>A functional implementation of a cooldown provider</li>
 * </ul>
 * Ideally, all custom behaviours should use at least this class as a base,
 * instead of the core Behavior class
 *
 * @param <E> Your entity
 */
public abstract class ExtendedBehaviour<E extends LivingEntity> extends Behavior<E> {
    protected Predicate<E> startCondition = entity -> true;
    protected Predicate<E> stopCondition = entity -> false;
    protected Consumer<E> taskStartCallback = entity -> {};
    protected Consumer<E> taskStopCallback = entity -> {};

    protected Function<E, Integer> runtimeProvider = entity -> 60;
    protected Function<E, Integer> cooldownProvider = entity -> 0;
    protected long cooldownFinishedAt = 0;

    public ExtendedBehaviour() {
        super(new Object2ObjectOpenHashMap<>());

        for (Pair<MemoryModuleType<?>, MemoryStatus> memoryReq : getMemoryRequirements()) {
            this.entryCondition.put(memoryReq.getFirst(), memoryReq.getSecond());
        }
    }

    /**
     * A callback for when the task begins. Use this to trigger effects or handle
     * things when the entity activates this task.
     *
     * @param callback The callback
     * @return this
     */
    public final ExtendedBehaviour<E> whenStarting(Consumer<E> callback) {
        this.taskStartCallback = callback;

        return this;
    }

    /**
     * A callback for when the task stops. Use this to trigger effects or handle
     * things when the entity ends this task. <br>
     * Note that the task stopping does not necessarily mean it was successful.
     *
     * @param callback The callback
     * @return this
     */
    public final ExtendedBehaviour<E> whenStopping(Consumer<E> callback) {
        this.taskStopCallback = callback;

        return this;
    }

    /**
     * Set the length that the task should run for, once activated. The value used
     * is in <i>ticks</i>.
     *
     * @param timeProvider A function for the tick value
     * @return this
     */
    public final ExtendedBehaviour<E> runFor(Function<E, Integer> timeProvider) {
        this.runtimeProvider = timeProvider;

        return this;
    }

    /**
     * Set the length that the task should wait for between activations. This is the
     * time between when the task stops, and it is able to start again. The value
     * used is in <i>ticks</i>
     *
     * @param timeProvider A function for the tick value
     * @return this
     */
    public final ExtendedBehaviour<E> cooldownFor(Function<E, Integer> timeProvider) {
        this.cooldownProvider = timeProvider;

        return this;
    }

    /**
     * Set an additional condition for the behaviour to be able to start. Useful for
     * dynamically predicating behaviours.<br>
     * Prevents this behaviour starting unless this predicate returns true.
     *
     * @param predicate The predicate
     * @return this
     */
    public final ExtendedBehaviour<E> startCondition(Predicate<E> predicate) {
        this.startCondition = predicate;

        return this;
    }

    /**
     * Set an automatic condition for the behavior to stop. Useful for dynamically
     * stopping behaviours. Has no effect on one-shot behaviours that don't have a
     * runtime.<br>
     * Stops the behaviour if it is active and this predicate returns true.
     *
     * @param predicate The predicate
     * @return this
     */
    public final ExtendedBehaviour<E> stopIf(Predicate<E> predicate) {
        this.stopCondition = predicate;

        return this;
    }

    @Override
    public final boolean tryStart(ServerLevel level, E entity, long gameTime) {
        if (!doStartCheck(level, entity, gameTime))
            return false;

        this.status = Status.RUNNING;
        this.endTimestamp = gameTime + this.runtimeProvider.apply(entity);

        start(level, entity, gameTime);

        return true;
    }

    //   @APIOnly
    protected boolean doStartCheck(ServerLevel level, E entity, long gameTime) {
        return this.cooldownFinishedAt <= gameTime && hasRequiredMemories(entity) && this.startCondition.test(entity)
                && checkExtraStartConditions(level, entity);
    }

    /**
     * Check any extra conditions required for this behaviour to start. <br>
     * By this stage, memory conditions from
     * {@link ExtendedBehaviour#getMemoryRequirements()} have already been checked.
     *
     * @param level  The level the entity is in
     * @param entity The owner of the brain
     * @return Whether the conditions have been met to start the behaviour
     */
    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        return true;
    }

    /**
     * The root stop method for when this behaviour stops. This method should only
     * be overridden by other abstract subclasses. <br>
     * If overriding, ensure you either call {@code super} or manually call
     * {@code stop(E)} yourself.
     *
     * @param level    The level the entity is in
     * @param entity   The entity the brain belongs to
     * @param gameTime The current gameTime (in ticks) of the level
     */
    // @APIOnly
    @Override
    protected void start(ServerLevel level, E entity, long gameTime) {
        this.taskStartCallback.accept(entity);
        start(entity);
    }

    /**
     * Override this for custom behaviour implementations. This is a safe endpoint
     * for behaviours so that all required auto-handling is safely contained without
     * super calls.<br>
     * This is called when the behaviour is to start. Set up any instance variables
     * needed or perform the required actions.<br>
     * By this stage any memory requirements set in
     * {@link ExtendedBehaviour#getMemoryRequirements()} are true, so any memories
     * paired with {@link MemoryStatus#VALUE_PRESENT} are safe to retrieve.
     *
     * @param entity The entity being handled (I.E. the owner of the brain)
     */
    protected void start(E entity) {
    }

    /**
     * The root stop method for when this behaviour stops. This method should only
     * be overridden by other abstract subclasses. <br>
     * If overriding, ensure you either call {@code super} or manually call
     * {@code stop(E)} yourself.
     *
     * @param level    The level the entity is in
     * @param entity   The entity the brain belongs to
     * @param gameTime The current gameTime (in ticks) of the level
     */
    // @APIOnly
    @Override
    protected void stop(ServerLevel level, E entity, long gameTime) {
        this.cooldownFinishedAt = gameTime + cooldownProvider.apply(entity);

        this.taskStopCallback.accept(entity);
        stop(entity);
    }

    /**
     * Override this for custom behaviour implementations. This is a safe endpoint
     * for behaviours so that all required auto-handling is safely contained without
     * super calls.<br>
     * This is called when the behaviour is to stop. Close off any instanced
     * variables and such here, ready for the next start.
     *
     * @param entity The entity being handled (I.E. the owner of the brain)
     */
    protected void stop(E entity) {
    }

    /**
     * The root method to check if this behaviour should continue running. This
     * method should only be overridden by other abstract subclasses.<br>
     * If overriding, ensure you either call super or manually call the
     * {@link ExtendedBehaviour#stopCondition} check yourself.
     *
     * @param level    The level the entity is in
     * @param entity   The entity the brain belongs to
     * @param gameTime The current gameTime (in ticks) of the level
     * @return Whether the behaviour should continue ticking
     */
    @Override
    protected boolean canStillUse(ServerLevel level, E entity, long gameTime) {
        return shouldKeepRunning(entity) && !this.stopCondition.test(entity);
    }

    /**
     * Check whether the behaviour should continue running. This is checked before
     * {@link ExtendedBehaviour#tick(E)}. <br>
     * Memories are not guaranteed to be in their required state here, so if you
     * have required memories, it might be worth checking them here.
     *
     * @param entity The owner of the brain
     * @return Whether the behaviour should continue ticking
     */
    protected boolean shouldKeepRunning(E entity) {
        return false;
    }

    /**
     * The root tick method for when this behaviour ticks. This method should only
     * be overridden by other abstract subclasses. <br>
     * If overriding, ensure you either call {@code super} or manually call
     * {@code tick(E)} yourself.
     *
     * @param level    The level the entity is in
     * @param entity   The entity the brain belongs to
     * @param gameTime The current gameTime (in ticks) of the level
     */
    // @APIOnly
    @Override
    protected void tick(ServerLevel level, E entity, long gameTime) {
        tick(entity);
    }

    /**
     * Override this for custom behaviour implementations. This is a safe endpoint
     * for behaviours so that all required auto-handling is safely contained without
     * super calls.<br>
     * This is called when the behaviour is ticked. Be aware this is called <i>every
     * tick</i>, so use tick reduction if needed to minimise performance impacts of
     * goals. <br>
     * NOTE: Memory requirements are <i>not</i> guaranteed at this stage. If you are
     * retrieving brain memories, you'll need to check their presence before use.
     *
     * @param entity The entity being handled (I.E. the owner of the brain)
     */
    protected void tick(E entity) {
    }

    @Override
    protected boolean timedOut(long gameTime) {
        return super.timedOut(gameTime);
    }

    // @APIOnly
    @Override
    public final boolean hasRequiredMemories(E entity) {
        Brain<?> brain = entity.getBrain();

        for (Pair<MemoryModuleType<?>, MemoryStatus> memoryPair : getMemoryRequirements()) {
            if (!brain.checkMemory(memoryPair.getFirst(), memoryPair.getSecond()))
                return false;
        }

        return true;
    }

    /**
     * The list of memory requirements this task has prior to starting. This
     * outlines the approximate state the brain should be in, in order to allow this
     * behaviour to run. <br>
     * Bonus points if it's a statically-initialised list.
     *
     * @return The {@link List} of {@link MemoryModuleType Memories} and their
     *         associated required {@link MemoryStatus status}
     */
    protected abstract List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements();
}
