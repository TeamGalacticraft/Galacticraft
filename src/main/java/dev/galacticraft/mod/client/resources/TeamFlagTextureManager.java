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

package dev.galacticraft.mod.client.resources;

import com.mojang.blaze3d.platform.NativeImage;
import dev.galacticraft.mod.Constant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public final class TeamFlagTextureManager {
    public static final TeamFlagTextureManager INSTANCE = new TeamFlagTextureManager();

    public static final int FLAG_WIDTH = 48;
    public static final int FLAG_HEIGHT = 32;
    private static final int FLAG_BYTE_COUNT = FLAG_WIDTH * FLAG_HEIGHT * 3;

    private static final ResourceLocation DEFAULT_FLAG_TEXTURE = Constant.id("space_race/team_flag/default");
    private static final ResourceLocation CURRENT_FLAG_TEXTURE = Constant.id("space_race/team_flag/current");
    private static final ResourceLocation PREVIEW_FLAG_TEXTURE = Constant.id("space_race/team_flag/preview");

    private @Nullable DynamicTexture defaultTexture;
    private @Nullable DynamicTexture currentTexture;
    private @Nullable DynamicTexture previewTexture;
    private @Nullable byte[] currentFlag;
    private @Nullable byte[] previewFlag;

    private TeamFlagTextureManager() {
    }

    public ResourceLocation getCurrentTextureLocation() {
        this.ensureDefaultTexture(false);
        return this.currentFlag == null ? DEFAULT_FLAG_TEXTURE : CURRENT_FLAG_TEXTURE;
    }

    public ResourceLocation getDisplayTextureLocation() {
        return this.previewFlag == null ? this.getCurrentTextureLocation() : PREVIEW_FLAG_TEXTURE;
    }

    public boolean hasCurrentFlag() {
        return this.currentFlag != null;
    }

    public boolean hasPendingPreview() {
        return this.previewFlag != null;
    }

    public void applyPreviewFromFile(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("Unsupported flag file");
        }

        try (InputStream stream = Files.newInputStream(path)) {
            this.applyPreview(loadFlagData(stream));
        }
    }

    public byte[] loadFlagDataFromUrl(String url) throws IOException {
        URI uri = parseHttpUri(url);
        var connection = uri.toURL().openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        try (InputStream stream = connection.getInputStream()) {
            return loadFlagData(stream);
        }
    }

    public void applyPreview(byte[] flagData) {
        this.previewFlag = Arrays.copyOf(validateFlagData(flagData), FLAG_BYTE_COUNT);
        this.previewTexture = this.registerTexture(PREVIEW_FLAG_TEXTURE, this.previewTexture, this.previewFlag);
    }

    public void confirmPreview() {
        if (this.previewFlag == null) {
            return;
        }

        this.currentFlag = Arrays.copyOf(this.previewFlag, FLAG_BYTE_COUNT);
        this.currentTexture = this.registerTexture(CURRENT_FLAG_TEXTURE, this.currentTexture, this.currentFlag);
        this.clearPreview();
    }

    public void clearPreview() {
        this.previewFlag = null;
        this.previewTexture = this.closeTexture(this.previewTexture);
    }

    public @Nullable byte[] getCurrentFlagPayload() {
        return this.currentFlag == null ? null : Arrays.copyOf(this.currentFlag, FLAG_BYTE_COUNT);
    }

    public void setCurrentFlag(@Nullable byte[] flagData) {
        if (flagData == null) {
            this.currentFlag = null;
            this.currentTexture = this.closeTexture(this.currentTexture);
            return;
        }

        this.currentFlag = Arrays.copyOf(validateFlagData(flagData), FLAG_BYTE_COUNT);
        this.currentTexture = this.registerTexture(CURRENT_FLAG_TEXTURE, this.currentTexture, this.currentFlag);
    }

    public void clearAll() {
        this.clearPreview();
        this.setCurrentFlag(null);
    }

    public void restoreTextures() {
        this.ensureDefaultTexture(true);

        if (this.currentFlag != null) {
            this.currentTexture = this.registerTexture(CURRENT_FLAG_TEXTURE, this.currentTexture, this.currentFlag);
        }

        if (this.previewFlag != null) {
            this.previewTexture = this.registerTexture(PREVIEW_FLAG_TEXTURE, this.previewTexture, this.previewFlag);
        }
    }

    private void ensureDefaultTexture(boolean recreate) {
        if (this.defaultTexture != null && !recreate) {
            return;
        }

        this.defaultTexture = this.registerTexture(DEFAULT_FLAG_TEXTURE, this.defaultTexture, buildDefaultFlagData());
    }

    private DynamicTexture registerTexture(ResourceLocation id, @Nullable DynamicTexture existing, byte[] flagData) {
        DynamicTexture texture = new DynamicTexture(createImage(validateFlagData(flagData)));
        this.closeTexture(existing);
        Minecraft.getInstance().getTextureManager().register(id, texture);
        return texture;
    }

    private @Nullable DynamicTexture closeTexture(@Nullable DynamicTexture texture) {
        if (texture != null) {
            texture.close();
        }

        return null;
    }

    private static byte[] loadFlagData(InputStream stream) throws IOException {
        byte[] bytes = stream.readAllBytes();

        try (NativeImage image = NativeImage.read(new ByteArrayInputStream(bytes))) {
            return normalizeImage(image);
        } catch (IOException | RuntimeException nativeImageException) {
            try {
                return normalizeImage(readBufferedImage(bytes));
            } catch (IOException imageIoException) {
                imageIoException.addSuppressed(nativeImageException);
                throw imageIoException;
            }
        }
    }

    private static NativeImage createImage(byte[] flagData) {
        byte[] validated = validateFlagData(flagData);
        NativeImage image = new NativeImage(FLAG_WIDTH, FLAG_HEIGHT, false);
        for (int y = 0; y < FLAG_HEIGHT; y++) {
            for (int x = 0; x < FLAG_WIDTH; x++) {
                int index = (y * FLAG_WIDTH + x) * 3;
                int red = validated[index] & 0xFF;
                int green = validated[index + 1] & 0xFF;
                int blue = validated[index + 2] & 0xFF;
                image.setPixelRGBA(x, y, 0xFF000000 | red << 16 | green << 8 | blue);
            }
        }

        return image;
    }

    private static byte[] normalizeImage(NativeImage image) {
        if (image.getWidth() <= 0 || image.getHeight() <= 0) {
            throw new IllegalArgumentException("Empty team flag image");
        }

        byte[] flagData = new byte[FLAG_BYTE_COUNT];
        for (int y = 0; y < FLAG_HEIGHT; y++) {
            int sourceY = Mth.clamp((int) (((y + 0.5F) * image.getHeight()) / FLAG_HEIGHT), 0, image.getHeight() - 1);
            for (int x = 0; x < FLAG_WIDTH; x++) {
                int sourceX = Mth.clamp((int) (((x + 0.5F) * image.getWidth()) / FLAG_WIDTH), 0, image.getWidth() - 1);
                int color = image.getPixelRGBA(sourceX, sourceY);
                int index = (y * FLAG_WIDTH + x) * 3;
                flagData[index] = (byte) ((color >> 16) & 0xFF);
                flagData[index + 1] = (byte) ((color >> 8) & 0xFF);
                flagData[index + 2] = (byte) (color & 0xFF);
            }
        }

        return flagData;
    }

    private static byte[] normalizeImage(BufferedImage image) {
        if (image.getWidth() <= 0 || image.getHeight() <= 0) {
            throw new IllegalArgumentException("Empty team flag image");
        }

        byte[] flagData = new byte[FLAG_BYTE_COUNT];
        for (int y = 0; y < FLAG_HEIGHT; y++) {
            int sourceY = Mth.clamp((int) (((y + 0.5F) * image.getHeight()) / FLAG_HEIGHT), 0, image.getHeight() - 1);
            for (int x = 0; x < FLAG_WIDTH; x++) {
                int sourceX = Mth.clamp((int) (((x + 0.5F) * image.getWidth()) / FLAG_WIDTH), 0, image.getWidth() - 1);
                int color = image.getRGB(sourceX, sourceY);
                int index = (y * FLAG_WIDTH + x) * 3;
                flagData[index] = (byte) ((color >> 16) & 0xFF);
                flagData[index + 1] = (byte) ((color >> 8) & 0xFF);
                flagData[index + 2] = (byte) (color & 0xFF);
            }
        }

        return flagData;
    }

    private static byte[] buildDefaultFlagData() {
        byte[] flagData = new byte[FLAG_BYTE_COUNT];
        for (int y = 0; y < FLAG_HEIGHT; y++) {
            for (int x = 0; x < FLAG_WIDTH; x++) {
                int index = (y * FLAG_WIDTH + x) * 3;
                boolean border = x == 0 || y == 0 || x == FLAG_WIDTH - 1 || y == FLAG_HEIGHT - 1;
                int red = border ? 0x58 : (((x / 6) + (y / 6)) & 1) == 0 ? 0x22 : 0x18;
                int green = border ? 0x64 : (((x / 6) + (y / 6)) & 1) == 0 ? 0x2E : 0x24;
                int blue = border ? 0x76 : (((x / 6) + (y / 6)) & 1) == 0 ? 0x3B : 0x31;
                flagData[index] = (byte) red;
                flagData[index + 1] = (byte) green;
                flagData[index + 2] = (byte) blue;
            }
        }

        return flagData;
    }

    private static byte[] validateFlagData(byte[] flagData) {
        if (flagData.length != FLAG_BYTE_COUNT) {
            throw new IllegalArgumentException("Invalid team flag payload");
        }

        return flagData;
    }

    private static URI parseHttpUri(String url) {
        String trimmedUrl = url.trim();
        if (trimmedUrl.isEmpty()) {
            throw new IllegalArgumentException("Missing flag URL");
        }

        try {
            URI uri = new URI(trimmedUrl);
            String scheme = uri.getScheme();
            if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
                throw new IllegalArgumentException("Unsupported flag URL scheme");
            }

            return uri;
        } catch (URISyntaxException exception) {
            throw new IllegalArgumentException("Invalid flag URL", exception);
        }
    }

    private static BufferedImage readBufferedImage(byte[] data) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
        if (image == null) {
            throw new IOException("Unable to decode team flag image");
        }

        return image;
    }
}
