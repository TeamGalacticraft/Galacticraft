package io.github.teamgalacticraft.galacticraft.misc;

import io.github.teamgalacticraft.galacticraft.Constants;
import io.github.teamgalacticraft.galacticraft.Galacticraft;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class Capes {

    private static final Marker CAPES = MarkerManager.getMarker("Capes");
    private static Map<String, Identifier> capeMap = new HashMap<>();

    public static Map<String, Identifier> getCapeMap() {
        return capeMap;
    }

    public static void updateCapeList() {
        int timeout = 10000;
        URL capeListUrl;

        try {
            capeListUrl = new URL("https://raw.github.com/teamgalacticraft/Galacticraft-Fabric/master/capes.txt");
        } catch (IOException e) {
            Galacticraft.logger.fatal(CAPES, "FAILED TO GET CAPES"); //TODO debug msg not error when config is in
            return;
        }

        URLConnection connection;

        try {
            connection = capeListUrl.openConnection();
        } catch (IOException e) {
            Galacticraft.logger.fatal(CAPES, "FAILED TO GET CAPES");
            return;
        }

        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        InputStream stream;

        try {
            stream = connection.getInputStream();
        } catch (IOException e) {
            Galacticraft.logger.fatal(CAPES, "FAILED TO GET CAPES");
            return;
        }

        InputStreamReader streamReader = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(streamReader);

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (line.contains(":")) {
                    int splitLocation = line.indexOf(":");
                    String uuid = line.substring(0, splitLocation);
                    capeMap.put(uuid, new Identifier(Constants.MOD_ID, "textures/cape/cape_" + convertCapeString(line.substring(splitLocation + 1)) + ".png"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String convertCapeString(String capeName) {
        String s = "";
        for (int i = 0; i < capeName.length(); ++i) {
            char c = capeName.charAt(i);
            if (c == " ".charAt(0)) {
                break;
            }
            c = Character.toLowerCase(c);
            s = String.join("", s, Character.toString(c));
        }
        return s;
    }
}
