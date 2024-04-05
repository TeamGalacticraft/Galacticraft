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

package dev.galacticraft.mod.client.gl;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.BufferVertexConsumer;
import com.mojang.blaze3d.vertex.DefaultedVertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.logging.LogUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

import java.nio.ByteBuffer;

public class BufferWriter extends DefaultedVertexConsumer implements BufferVertexConsumer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int FLOAT32_SIZE = 4;
    private static final int INT16_SIZE = 2;
    private static final int INT32_SIZE = 4;

    private int nextElementByte;
    private int vertices;
    @Nullable
    private VertexFormatElement currentElement;
    private int elementIndex;
    private VertexFormat format;
    ByteBuffer data;

    public BufferWriter(VertexFormat format, int initialSize) {
        this.data = MemoryUtil.memAlloc(initialSize);
        this.format = format;
        this.currentElement = format.getElements().get(0);
    }

    public BufferWriter(VertexFormat format) {
        this(format, 64);
    }

    public void rewind() {
        this.data.rewind();
    }

    public void int3(int a, int b, int c) {
        this.ensureCapacity(INT32_SIZE * 3);
        this.data.putInt(a).putInt(b).putInt(c);
    }

    public void free() {
        MemoryUtil.memFree(this.data);
    }

    private void ensureCapacity(int bytes) {
        if (this.data.remaining() >= bytes) return;
        this.data = MemoryUtil.memRealloc(this.data, this.data.capacity() * 2);

        LOGGER.debug("Resizing buffer from " + this.data.capacity() + " bytes to " + this.data.capacity() * 2);
    }

    @Override
    public VertexFormatElement currentElement() {
        if (this.currentElement == null) {
            throw new IllegalStateException("BufferWriter not started");
        } else {
            return this.currentElement;
        }
    }

    @Override
    public void nextElement() {
        ImmutableList<VertexFormatElement> elements = this.format.getElements();
        this.elementIndex = (this.elementIndex + 1) % elements.size();
        this.nextElementByte += this.currentElement.getByteSize();
        VertexFormatElement vertexFormatElement = elements.get(this.elementIndex);
        this.currentElement = vertexFormatElement;
        if (vertexFormatElement.getUsage() == VertexFormatElement.Usage.PADDING) { // Why does mojank have this?
            this.nextElement();
        }

        if (this.defaultColorSet && this.currentElement.getUsage() == VertexFormatElement.Usage.COLOR) {
            BufferVertexConsumer.super.color(this.defaultR, this.defaultG, this.defaultB, this.defaultA);
        }
    }

    @Override
    public void putByte(int index, byte byteValue) {
//        this.ensureCapacity(1);
        this.data.put(byteValue);
    }

    @Override
    public void putShort(int index, short shortValue) {
//        this.ensureCapacity(INT16_SIZE);
        this.data.putShort(shortValue);
    }

    @Override
    public void putFloat(int index, float floatValue) {
//        this.ensureCapacity(FLOAT32_SIZE);
        this.data.putFloat(floatValue);
    }

    @Override
    public void endVertex() {
        if (this.elementIndex != 0) {
            throw new IllegalStateException("Not filled all elements of the vertex");
        } else {
            ++this.vertices;
            this.ensureCapacity(format.getVertexSize());
        }
    }
}
