package com.hrznstudio.galacticraft.mixin;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityAttributeInstance.class)
public interface EntityAttributeInstanceAccessor {
    @Invoker
    void callAddModifier(EntityAttributeModifier modifier);
}
