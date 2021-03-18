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
