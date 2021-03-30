/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.api.machine;

import com.hrznstudio.galacticraft.api.internal.data.MinecraftServerTeamsGetter;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SecurityInfo {
    private @Nullable GameProfile owner;
    private @Nullable Identifier team;
    private @NotNull Accessibility accessibility;

    public SecurityInfo() {
        this.accessibility = Accessibility.PUBLIC;
        this.team = null;
    }

    @Contract(pure = true)
    public boolean isOwner(PlayerEntity player) {
        return isOwner(player.getGameProfile());
    }

    @Contract(pure = true)
    public boolean isOwner(GameProfile profile) {
        if (this.owner == null) return false;
        return this.owner.equals(profile);
    }

    @Contract(pure = true)
    public boolean hasAccess(PlayerEntity player) {
        if (accessibility == Accessibility.PUBLIC) {
            return true;
        } else if (accessibility == Accessibility.TEAM) {
            return (((MinecraftServerTeamsGetter) player.getServer()).getSpaceRaceTeams().getTeam(player.getUuid()) != null)
                    && ((MinecraftServerTeamsGetter) player.getServer()).getSpaceRaceTeams().getTeam(player.getUuid()).players.containsKey(owner);
        } else if (accessibility == Accessibility.PRIVATE) {
            return isOwner(player);
        }
        return false;
    }

    public @NotNull Accessibility getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(@NotNull Accessibility accessibility) {
        this.accessibility = accessibility;
    }


    public @Nullable GameProfile getOwner() {
        return this.owner;
    }

    public void setOwner(@NotNull PlayerEntity owner) {
        if (this.getOwner() == null) {
            this.owner = owner.getGameProfile();
        }
    }

    public @Nullable Identifier getTeam() {
        return team;
    }

    public CompoundTag toTag(CompoundTag tag) {
        if (this.getOwner() != null) {
            tag.put("owner", NbtHelper.fromGameProfile(new CompoundTag(), this.getOwner()));
        }
        tag.putString("accessibility", this.accessibility.name());
        if (this.getTeam() != null) {
            tag.putString("team", team.toString());
        }
        return tag;
    }

    public void fromTag(CompoundTag tag) {
        if (tag.contains("owner")) {
            this.owner = NbtHelper.toGameProfile(tag.getCompound("owner"));
        }

        if (tag.contains("team")) {
            this.team = new Identifier(tag.getString("team"));
        }

        this.accessibility = Accessibility.valueOf(tag.getString("accessibility"));
    }


    public enum Accessibility implements StringIdentifiable {
        PUBLIC(new TranslatableText("ui.galacticraft-rewoven.machine.security.accessibility.public")),
        TEAM(new TranslatableText("ui.galacticraft-rewoven.machine.security.accessibility.team")),
        PRIVATE(new TranslatableText("ui.galacticraft-rewoven.machine.security.accessibility.private"));

        private final TranslatableText name;

        Accessibility(TranslatableText name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.toString();
        }

        public Text getName() {
            return this.name;
        }
    }
}
