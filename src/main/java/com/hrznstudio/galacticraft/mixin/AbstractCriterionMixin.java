package com.hrznstudio.galacticraft.mixin;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.accessor.ServerPlayerEntityAccessor;
import com.hrznstudio.galacticraft.api.research.PlayerResearchTracker;
import com.hrznstudio.galacticraft.api.research.ResearchConditionsContainer;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@Mixin(AbstractCriterion.class)
public class AbstractCriterionMixin<T extends AbstractCriterionConditions> {
    @Unique
    private static final Method getPlayerPredicate = getPPM();
    @Shadow
    @Final
    private Map<PlayerAdvancementTracker, Set<Criterion.ConditionsContainer<T>>> progressions;

    private static Method getPPM() {
        try {
            Method m = AbstractCriterionConditions.class.getDeclaredMethod("getPlayerPredicate");
            m.setAccessible(true);
            return m;
        } catch (NoSuchMethodException e) {
            try {
                Method m = AbstractCriterionConditions.class.getDeclaredMethod("method_27790");
                m.setAccessible(true);
                return m;
            } catch (NoSuchMethodException ex) {
                ex.addSuppressed(e);
                RuntimeException exception = new RuntimeException("GC: Unable to find method AbstractCriterionConditions#getPlayerPredicate");
                exception.addSuppressed(ex);
                throw exception;
            }
        }
    }

    @Unique
    private static final Field owner = getOwner();

    @Unique
    private static Field getOwner() {
        try {
            Field owner = PlayerAdvancementTracker.class.getDeclaredField("owner");
            owner.setAccessible(true);
            return owner;
        } catch (NoSuchFieldException | ClassCastException e) {
            try {
                @SuppressWarnings("JavaReflectionMemberAccess") Field owner = PlayerAdvancementTracker.class.getDeclaredField("field_13391");
                owner.setAccessible(true);
                return owner;
            } catch (NoSuchFieldException | ClassCastException ex) {
                ex.addSuppressed(e);
                ex.printStackTrace();
            }
        }
        throw new RuntimeException("Unable to get field 'owner'!");
    }

    @Inject(method = "grant", at = @At("RETURN"))
    private void grantResearch(PlayerAdvancementTracker tracker, CallbackInfo ci) {
        if (tracker != null) {
            Set<Criterion.ConditionsContainer<T>> set = null;
            owner.setAccessible(true);
            try {
                set = this.progressions.get((((ServerPlayerEntityAccessor) owner.get(tracker)).getResearchTracker()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (set != null && !set.isEmpty()) {

                for (Criterion.ConditionsContainer<T> container : ImmutableSet.copyOf(set)) {
                    container.grant(tracker);
                }
            }
        }
    }

    @Inject(method = "test", at = @At("RETURN"))
    private void testResearch(ServerPlayerEntity player, Predicate<T> tester, CallbackInfo ci) {
        try {
            PlayerResearchTracker tracker = ((ServerPlayerEntityAccessor) player).getResearchTracker();
            Set<Criterion.ConditionsContainer<T>> set = this.progressions.get(tracker);
            if (set != null) {
                LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, player);
                List<ResearchConditionsContainer<T>> list = null;
                Iterator<Criterion.ConditionsContainer<T>> iterator = set.iterator();

                ResearchConditionsContainer<T> container1;
                while (iterator.hasNext()) {
                    container1 = (ResearchConditionsContainer<T>) iterator.next();
                    T abstractCriterionConditions = container1.getConditions();
                    if (((EntityPredicate.Extended) getPlayerPredicate.invoke(abstractCriterionConditions)).test(lootContext) && tester.test(abstractCriterionConditions)) {
                        if (list == null) {
                            list = Lists.newArrayList();
                        }

                        list.add(container1);
                    }

                }

                if (list != null) {
                    for (ResearchConditionsContainer<T> container : list) {
                        container.grant(tracker);
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
