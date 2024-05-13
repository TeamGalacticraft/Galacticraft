/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.api.data;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class TranslationProvider implements DataProvider {
    private final Map<String, String> translations = new HashMap<>(512);
    private final CompletableFuture<HolderLookup.Provider> registriesFuture;
    private final String modId;
    private final Path path;

    public TranslationProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        this.registriesFuture = registriesFuture;
        this.modId = output.getModId();
        this.path = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "lang").json(new ResourceLocation(modId, "en_us"));
    }

    protected void generateDefaultTranslations(@NotNull HolderLookup.Provider registries) {
        registries.lookupOrThrow(Registries.BLOCK).listElements().filter(k -> k.key().location().getNamespace().equals(this.modId)).forEach(this::defaultedBlock);
        registries.lookupOrThrow(Registries.ITEM).listElements().filter(k -> k.key().location().getNamespace().equals(this.modId)).filter(k -> !(k.value() instanceof BlockItem)).forEach(this::defaultedItem);
        registries.lookupOrThrow(Registries.ENTITY_TYPE).listElements().filter(k -> k.key().location().getNamespace().equals(this.modId)).forEach(this::defaultedEntity);
        registries.lookupOrThrow(Registries.BIOME).listElements().filter(k -> k.key().location().getNamespace().equals(this.modId)).forEach(this::addDefaulted);
    }

    protected abstract void generateTranslations(@NotNull HolderLookup.Provider registries);

    protected void add(String key, String translation) {
        this.translations.put(key, translation);
    }

    protected <T> void add(Holder.Reference<T> reference, String translation) {
        this.add(reference.key(), translation);
    }

    protected <T> void add(ResourceKey<T> key, String translation) {
        this.add(getBaseKey(key.registry()) + '.' + key.location().toLanguageKey(), translation);
    }

    protected void block(Block block, String translation) {
        this.add(block.getDescriptionId(), translation);
    }

    protected void biome(ResourceLocation id, String translation) {
        this.add("biome." + id.toLanguageKey(), translation);
    }

    protected void item(Item item, String translation) {
        this.add(item.getDescriptionId(), translation);
    }

    protected void entity(EntityType<?> entity, String translation) {
        this.add(entity.getDescriptionId(), translation);
    }

    protected void mobEffect(MobEffect effect, String translation) {
        this.add(effect.getDescriptionId(), translation);
    }

    protected void enchantment(Enchantment enchantment, String translation) {
        this.add(enchantment.getDescriptionId(), translation);
    }

    protected <T> void addDefaulted(Holder.Reference<T> reference) {
        ResourceKey<T> key = reference.key();
        this.add(getBaseKey(key.registry()) + '.' + key.location().toLanguageKey(), createDefaulted(key.location()));
    }

    protected void defaultedItem(Holder.Reference<Item> reference) {
        this.add(reference.value().getDescriptionId(), createDefaulted(reference.key().location()));
    }

    protected void defaultedBlock(Holder.Reference<Block> reference) {
        this.add(reference.value().getDescriptionId(), createDefaulted(reference.key().location()));
    }

    protected void defaultedEntity(Holder.Reference<EntityType<?>> reference) {
        this.add(reference.value().getDescriptionId(), createDefaulted(reference.key().location()));
    }

    protected static String createDefaulted(ResourceLocation id) {
        String path = id.getPath();
        if (path.endsWith("_block")) {
            return "Block of " + normalizeName(path.substring(0, path.length() - 6));
        }

        return normalizeName(path);
    }

    private static @NotNull String getBaseKey(ResourceLocation id) {
        String baseKey = id.toShortLanguageKey();
        baseKey = baseKey.substring(baseKey.lastIndexOf('/') + 1);
        return baseKey;
    }

    private static @NotNull String normalizeName(String id) {
        char[] chars = id.toCharArray();
        boolean capitalize = true;
        for (int i = 0; i < chars.length; i++) {
            if (capitalize) {
                chars[i] = Character.toUpperCase(chars[i]);
                capitalize = false;
            }

            if (chars[i] == '_') {
                chars[i] = ' ';
                capitalize = true;
            }
        }
        return new String(chars);
    }

    @Override
    public @NotNull CompletableFuture<?> run(CachedOutput writer) {
        return this.registriesFuture.thenApply(registries -> {
            this.translations.clear();
            this.generateDefaultTranslations(registries);
            this.generateTranslations(registries);
            return registries;
        }).thenCompose(registries -> {
            JsonObject object = new JsonObject();
            this.translations.forEach(object::addProperty);
            return DataProvider.saveStable(writer, object, this.path);
        });
    }
}
