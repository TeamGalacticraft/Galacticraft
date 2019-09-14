/*
 * Copyright (c) 2018-2019 Horizon Studio
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

package com.hrznstudio.galacticraft.misc.capes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hrznstudio.galacticraft.api.capes.CapeListener;
import com.hrznstudio.galacticraft.api.capes.models.CapesModel;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CapeLoader {

    private List<CapeListener> listeners;
    private Gson gson = new GsonBuilder().create();

    public CapeLoader() {
        this.listeners = new ArrayList<>();
    }

    public boolean register(CapeListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
            return true;
        } else {
            return false;
        }
    }

    public void load() {
        new Thread(() -> {
            CapesModel capesModel = null;
            try {
                capesModel = this.gson.fromJson(IOUtils.toString(new URL("https://raw.githubusercontent.com/StellarHorizons/Galacticraft-Rewoven/master/capes.json"), Charset.defaultCharset()), CapesModel.class);
            } catch (IOException e) {
                Thread.currentThread().interrupt();
            }

            while (!Thread.currentThread().isInterrupted()) {
                if (capesModel != null && this.listeners != null) {
                    CapesModel finalCapesModel = capesModel;
                    this.listeners.forEach(l -> l.loadCapes(finalCapesModel));
                    Thread.currentThread().interrupt();
                }
            }
        }, "capeLoader").start();
    }
}
