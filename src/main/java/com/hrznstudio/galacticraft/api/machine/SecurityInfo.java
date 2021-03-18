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
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Contract;

public class SecurityInfo {
    private GameProfile owner;
    private Identifier team;
    private Accessibility accessibility;

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

    public Accessibility getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(Accessibility accessibility) {
        this.accessibility = accessibility;
    }

    public boolean hasOwner() {
        return this.owner != null;
    }

    public GameProfile getOwner() {
        return this.owner;
    }

    public void setOwner(PlayerEntity owner) {
        if (!this.hasOwner()) {
            this.owner = owner.getGameProfile();
        }
    }

    public Identifier getTeam() {
        return team;
    }

    public boolean hasTeam() {
        return team != null;
    }

    public CompoundTag toTag(CompoundTag tag) {
        if (this.hasOwner()) {
            tag.put("owner", NbtHelper.fromGameProfile(new CompoundTag(), this.getOwner()));
        }
        tag.putString("accessibility", this.accessibility.asString());
        if (this.hasTeam()) {
            tag.putString("team", team.toString());
        }
        return tag;
    }

    public void fromTag(CompoundTag tag) {

        if (tag.contains("owner")) {
            this.owner = NbtHelper.toGameProfile(tag.getCompound("owner"));
        }

        if (tag.contains("team")) {
            if (!this.hasTeam()) {
                this.team = new Identifier(tag.getString("team"));
            }
        }

        this.accessibility = Accessibility.valueOf(tag.getString("accessibility"));
    }


    public enum Accessibility implements StringIdentifiable {
        PUBLIC(new TranslatableText("ui.galacticraft-rewoven.accessibility.public")),
        TEAM(new TranslatableText("ui.galacticraft-rewoven.accessibility.team")),
        PRIVATE(new TranslatableText("ui.galacticraft-rewoven.accessibility.private"));

        private final TranslatableText name;

        Accessibility(TranslatableText name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.toString();
        }

        public MutableText getName() {
            return this.name;
        }
    }
}
