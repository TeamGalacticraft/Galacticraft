package dev.galacticraft.mod.recipe;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.gson.*;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.attribute.GalacticraftAttribute;
import dev.galacticraft.mod.attribute.oxygen.OxygenTank;
import dev.galacticraft.mod.item.GalacticraftItems;
import dev.galacticraft.mod.item.OxygenTankItem;
import dev.galacticraft.mod.util.OxygenTankUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.*;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PotionRecipe implements GCraftingRecipe {


    private final int width;
    private final int height;
    private final DefaultedList<Ingredient> inputs;
    private final ItemStack output;
    private final Identifier id;
    private final String group;


    public PotionRecipe(Identifier id, String group, int width, int height, DefaultedList<Ingredient> ingredients, ItemStack output) {
        this.id = id;
        this.group = group;
        this.width = width;
        this.height = height;
        this.inputs = ingredients;
        this.output = output;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GalacticraftRecipe.POTION_RECIPE_SERIALIZER;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getOutput() {
        return this.output;
    }

    @Override
    public DefaultedList<Ingredient> getPreviewInputs() {
        return this.inputs;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean fits(int width, int height) {
        return width >= this.width && height >= this.height;
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        if (inv.size() != 9) throw new AssertionError();
        for(int i = 0; i <= 3 - this.width; ++i) {
            for(int j = 0; j <= 3 - this.height; ++j) {
                if (this.matchesSmall(inv, i, j, true)) {
                    return true;
                }

                if (this.matchesSmall(inv, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public ItemStack craft(CraftingInventory inv) {
        return this.getOutput().copy();
    }

    private boolean matchesSmall(CraftingInventory inv, int offsetX, int offsetY, boolean bl) {
        if (inv.size() != 9) throw new AssertionError();
        for(int x = 0; x < 3; ++x) {
            for(int y = 0; y < 3; ++y) {
                int k = x - offsetX;
                int l = y - offsetY;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < this.width && l < this.height) {
                    if (bl) {
                        ingredient = this.inputs.get(this.width - k - 1 + l * this.width);
                    } else {
                        ingredient = this.inputs.get(k + l * this.width);
                    }
                }
                if (!ingredient.test(inv.getStack(x + y * 3))) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    private static DefaultedList<Ingredient> getIngredients(String[] pattern, Map<String, Ingredient> key, int width, int height) {
        DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(width * height, Ingredient.EMPTY);
        Set<String> set = new HashSet<>(key.keySet());
        set.remove(" ");

        for(int i = 0; i < pattern.length; ++i) {
            for(int j = 0; j < pattern[i].length(); ++j) {
                String string = pattern[i].substring(j, j + 1);
                Ingredient ingredient = key.get(string);
                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + string + "' but it's not defined in the key");
                }

                set.remove(string);
                defaultedList.set(j + width * i, ingredient);
            }
        }

        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        } else {
            return defaultedList;
        }
    }

    @VisibleForTesting
    static String[] combinePattern(String... lines) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for(int m = 0; m < lines.length; ++m) {
            String string = lines[m];
            i = Math.min(i, findNextIngredient(string));
            int n = findNextIngredientReverse(string);
            j = Math.max(j, n);
            if (n < 0) {
                if (k == m) {
                    ++k;
                }

                ++l;
            } else {
                l = 0;
            }
        }

        if (lines.length == l) {
            return new String[0];
        } else {
            String[] strings = new String[lines.length - l - k];

            for(int o = 0; o < strings.length; ++o) {
                strings[o] = lines[o + k].substring(i, j + 1);
            }

            return strings;
        }
    }

    private static int findNextIngredient(String pattern) {
        int i = 0;
        while (i < pattern.length() && pattern.charAt(i) == ' ') {
            ++i;
        }

        return i;
    }

    private static int findNextIngredientReverse(String pattern) {
        int i = pattern.length() - 1;
        while (i >= 0 && pattern.charAt(i) == ' ') {
            --i;
        }

        return i;
    }


    private static Map<String, Ingredient> getComponents(JsonObject json) {
        Map<String, Ingredient> map = Maps.newHashMap();

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put(entry.getKey(), GCIngredient.fromJson(entry.getValue()));
        }

        map.put(" ", Ingredient.EMPTY);
        return map;
    }


    // borrowed from vanilla... SharedCompressingRecipe does something similar
    // it'd be ideal making a util class later maybe but this should hopefully work???
    private static String[] getPattern(JsonArray json) {
        String[] strings = new String[json.size()];
        if (strings.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        } else if (strings.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for(int i = 0; i < strings.length; ++i) {
                String string = JsonHelper.asString(json.get(i), "pattern[" + i + "]");
                if (string.length() > 3) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
                }

                if (i > 0 && strings[0].length() != string.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                strings[i] = string;
            }

            return strings;
        }
    }

    public static ItemStack getItemStack(JsonObject json) {

        Item item;
        ItemStack itemStack = null;
        if (json.has("potion")) {
            String identifier = JsonHelper.getString(json, "potion");
            Potion potion = (Potion) Registry.POTION.getOrEmpty(new Identifier(identifier)).orElseThrow(() -> {
                return new JsonSyntaxException("Unknown potion '" + identifier + "'");
            });
            // item = potion
            itemStack = PotionUtil.setPotion(new ItemStack(Items.POTION), potion);

        } else if (json.has("item")) {
            String identifier = JsonHelper.getString(json, "item");
            item = (Item) Registry.ITEM.getOrEmpty(new Identifier(identifier)).orElseThrow(() -> {
                return new JsonSyntaxException("Unknown item '" + identifier + "'");
            });
            itemStack = new ItemStack(item);

        }
        if (json.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        } else {
            int i = JsonHelper.getInt(json, "count", 1);
            itemStack.setCount(i);
            return itemStack;
        }
    }



    public enum Serializer implements RecipeSerializer<PotionRecipe> {
        INSTANCE;

        @Override
        public PotionRecipe read(Identifier id, JsonObject object) {
            String string = JsonHelper.getString(object, "group", "");
            Map<String, Ingredient> map = PotionRecipe.getComponents(JsonHelper.getObject(object, "key"));
            String[] pattern = PotionRecipe.combinePattern(PotionRecipe.getPattern(JsonHelper.getArray(object, "pattern")));
            int width = pattern[0].length();
            int height = pattern.length;
            DefaultedList<Ingredient> defaultedList = PotionRecipe.getIngredients(pattern, map, width, height);
            ItemStack itemStack = PotionRecipe.getItemStack(JsonHelper.getObject(object, "result"));
            return new PotionRecipe(id, string, width, height, defaultedList, itemStack);
        }

        @Override
        public PotionRecipe read(Identifier id, PacketByteBuf buf) {
            int width = buf.readVarInt();
            int height = buf.readVarInt();
            String string = buf.readString(Constant.Misc.MAX_STRING_READ);
            DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(width * height, Ingredient.EMPTY);

            for(int k = 0; k < ingredients.size(); ++k) {
                ingredients.set(k, Ingredient.fromPacket(buf));
            }

            ItemStack output = buf.readItemStack();
            return new PotionRecipe(id, string, width, height, ingredients, output);
        }

        @Override
        public void write(PacketByteBuf buf, PotionRecipe recipe) {
            buf.writeVarInt(recipe.width);
            buf.writeVarInt(recipe.height);
            buf.writeString(recipe.group);

            for (Ingredient ingredient : recipe.inputs) {
                ingredient.write(buf);
            }

            buf.writeItemStack(recipe.output);
        }
    }

    // I took this from vanilla :trolley:
    public static class GCIngredient extends Ingredient {

        public GCIngredient(Stream<? extends Ingredient.Entry> entries) {
            super(entries);
        }

        private static Ingredient.Entry entryFromJson(JsonObject json) {
            if (json.has("item") && json.has("tag")) {
                throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
            } else {
                Identifier identifier;
                if (json.has("item")) {
                    identifier = new Identifier(JsonHelper.getString(json, "item"));
                    Item item = (Item)Registry.ITEM.getOrEmpty(identifier).orElseThrow(() -> {
                        return new JsonSyntaxException("Unknown item '" + identifier + "'");
                    });
                    return new Ingredient.StackEntry(new ItemStack(item));
                } else if (json.has("potion")) {
                    identifier = new Identifier(JsonHelper.getString(json, "potion"));
                    Potion potion = (Potion) Registry.POTION.getOrEmpty(identifier).orElseThrow(() -> {
                        return new JsonSyntaxException("Unknown potion '" + identifier + "'");
                    });

                    return new Ingredient.StackEntry(PotionUtil.setPotion(new ItemStack(Items.POTION), potion));
                } else if (json.has("oxygen")) {
                    identifier = new Identifier(JsonHelper.getString(json, "oxygen"));
                    Item item = (Item)Registry.ITEM.getOrEmpty(identifier).orElseThrow(() -> {
                        return new JsonSyntaxException("Unknown item '" + identifier + "'");
                    });
                    ItemStack tankItemstack = new ItemStack(item);
                    if (!OxygenTankUtil.isOxygenTank(tankItemstack)) {
                        throw new JsonSyntaxException("Item '" + identifier + "' is not a oxygen tank");
                    }
                    if (json.has("o2level")) {
                        OxygenTank oxygenTank = OxygenTankUtil.getOxygenTank(tankItemstack);
                        int o2level = JsonHelper.getInt(json, "o2level", oxygenTank.getCapacity());
                        oxygenTank.setAmount(o2level);
                    }
                    return new Ingredient.StackEntry(tankItemstack);
                }

                else if (json.has("tag")) {
                    identifier = new Identifier(JsonHelper.getString(json, "tag"));
                    Tag<Item> tag = ServerTagManagerHolder.getTagManager().getItems().getTag(identifier);
                    if (tag == null) {
                        throw new JsonSyntaxException("Unknown item tag '" + identifier + "'");
                    } else {
                        return new Ingredient.TagEntry(tag);
                    }
                } else {
                    throw new JsonParseException("An ingredient entry needs either a tag or an item");
                }
            }

        }

        public static Ingredient fromJson(@Nullable JsonElement json) {
            if (json != null && !json.isJsonNull()) {
                if (json.isJsonObject()) {
                    return ofEntries(Stream.of(entryFromJson(json.getAsJsonObject())));
                } else if (json.isJsonArray()) {
                    JsonArray jsonArray = json.getAsJsonArray();
                    if (jsonArray.size() == 0) {
                        throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
                    } else {
                        return ofEntries(StreamSupport.stream(jsonArray.spliterator(), false).map((jsonElement) -> {
                            return entryFromJson(JsonHelper.asObject(jsonElement, "item"));
                        }));
                    }
                } else {
                    throw new JsonSyntaxException("Expected item to be object or array of objects");
                }
            } else {
                throw new JsonSyntaxException("Item cannot be null");
            }
        }

    }

}