package com.hrznstudio.galacticraft.api.rocket;

import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.rocket.part.RocketPart;
import com.hrznstudio.galacticraft.api.rocket.part.RocketPartType;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Objects;

public class RocketData {
    public static final RocketData EMPTY = new RocketData(-1, 0, 0, 0, 1, null, null, null, null, null, null);

    private final int tier;
    private final int red;
    private final int green;
    private final int blue;
    private final int alpha;
    private final RocketPart cone;
    private final RocketPart body;
    private final RocketPart fin;
    private final RocketPart booster;
    private final RocketPart bottom;
    private final RocketPart upgrade;

    public RocketData(int tier, int red, int green, int blue, int alpha, RocketPart cone, RocketPart body, RocketPart fin, RocketPart booster, RocketPart bottom, RocketPart upgrade) {
        this.tier = tier;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.cone = cone;
        this.body = body;
        this.fin = fin;
        this.booster = booster;
        this.bottom = bottom;
        this.upgrade = upgrade;
    }

    public static RocketData fromItem(ItemStack stack) {
        return fromTag(stack.getOrCreateTag());
    }

    public static RocketData fromTag(CompoundTag tag) {
        if (tag.contains("tier")
                && tag.contains("red")
                && tag.contains("green") && tag.contains("blue")
                && tag.contains("alpha")
                && tag.contains("cone")
                && tag.contains("body") && tag.contains("fin")
                && tag.contains("booster") && tag.contains("bottom")
                && tag.contains("upgrade")) {
            return new RocketData(tag.getInt("tier"), tag.getInt("red"), tag.getInt("green"), tag.getInt("blue"), tag.contains("alpha") ? tag.getInt("alpha") : 1,
                    Galacticraft.ROCKET_PARTS.get(new Identifier(tag.getString("cone"))), Galacticraft.ROCKET_PARTS.get(new Identifier(tag.getString("body"))),
                    Galacticraft.ROCKET_PARTS.get(new Identifier(tag.getString("fin"))), Galacticraft.ROCKET_PARTS.get(new Identifier(tag.getString("booster"))),
                    Galacticraft.ROCKET_PARTS.get(new Identifier(tag.getString("bottom"))), Galacticraft.ROCKET_PARTS.get(new Identifier(tag.getString("upgrade"))));
        } else {
            return EMPTY;
        }
    }

    public ItemStack toSchematic() {
        ItemStack stack = new ItemStack(GalacticraftItems.ROCKET_SCHEMATIC);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("tier", tier);
        tag.putInt("red", red);
        tag.putInt("green", green);
        tag.putInt("blue", blue);
        tag.putInt("alpha", alpha);
        tag.putString("cone", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(cone)).toString());
        tag.putString("body", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(body)).toString());
        tag.putString("fin", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(fin)).toString());
        tag.putString("booster", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(booster)).toString());
        tag.putString("bottom", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(bottom)).toString());
        tag.putString("upgrade", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(upgrade)).toString());
        stack.setTag(tag);
        return stack;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("empty", isEmpty());
        if (!isEmpty()) {
            tag.putInt("tier", tier);
            tag.putInt("red", red);
            tag.putInt("green", green);
            tag.putInt("blue", blue);
            tag.putInt("alpha", alpha);
            tag.putString("cone", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(cone)).toString());
            tag.putString("body", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(body)).toString());
            tag.putString("fin", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(fin)).toString());
            tag.putString("booster", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(booster)).toString());
            tag.putString("bottom", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(bottom)).toString());
            tag.putString("upgrade", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(upgrade)).toString());
        }
        return tag;
    }

    public int getTier() {
        return tier;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public int getAlpha() {
        return alpha;
    }

    public RocketPart getCone() {
        return cone;
    }

    public RocketPart getBody() {
        return body;
    }

    public RocketPart getFin() {
        return fin;
    }

    public RocketPart getBooster() {
        return booster;
    }

    public RocketPart getBottom() {
        return bottom;
    }

    public RocketPart getUpgrade() {
        return upgrade;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RocketData that = (RocketData) o;
        return tier == that.tier &&
                red == that.red &&
                green == that.green &&
                blue == that.blue &&
                alpha == that.alpha &&
                cone == that.cone &&
                body == that.body &&
                fin == that.fin &&
                booster == that.booster &&
                bottom == that.bottom &&
                upgrade == that.upgrade;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tier, red, green, blue, alpha, cone, body, fin, booster, bottom, upgrade);
    }

    @Override
    public String toString() {
        return "RocketData{" +
                "tier=" + tier +
                ", red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                ", alpha=" + alpha +
                ", cone=" + cone +
                ", body=" + body +
                ", fin=" + fin +
                ", booster=" + booster +
                ", bottom=" + bottom +
                ", upgrade=" + upgrade +
                '}';
    }

    public RocketPart getPartForType(RocketPartType value) {
        switch (value) {
            case UPGRADE:
                return getUpgrade();
            case FIN:
                return getFin();
            case BODY:
                return getBody();
            case CONE:
                return getCone();
            case BOTTOM:
                return getBottom();
            case BOOSTER:
                return getBooster();
            default:
                throw new IllegalArgumentException();
        }
    }

    public List<RocketPart> getParts() {
        return Lists.newArrayList(cone, body, fin, booster, bottom, upgrade);
    }
}
