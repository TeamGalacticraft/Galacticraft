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
