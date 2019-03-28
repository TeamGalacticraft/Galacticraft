package io.github.teamgalacticraft.galacticraft.util;

import io.github.teamgalacticraft.galacticraft.Galacticraft;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class Capes {

    private static final Marker CAPES = MarkerManager.getMarker("Capes");
    private static List<String> capeUsers = new ArrayList<>();

    public static List<String> getCapeUsers() {
        return capeUsers;
    }

    public static void updateCapeList() {
        int timeout = 10000;
        URL capeListUrl;

        try {
            capeListUrl = new URL("https://raw.github.com/teamgalacticraft/Galacticraft-Fabric/master/capes.txt");
        }
        catch (IOException e) {
            Galacticraft.logger.fatal(CAPES, "FAILED TO GET CAPES"); //TODO debug msg not error when config is in
            return;
        }

        URLConnection connection;

        try {
            connection = capeListUrl.openConnection();
        }
        catch (IOException e) {
            Galacticraft.logger.fatal(CAPES, "FAILED TO GET CAPES");
            return;
        }

        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        InputStream stream;

        try {
            stream = connection.getInputStream();
        }
        catch (IOException e) {
            Galacticraft.logger.fatal(CAPES, "FAILED TO GET CAPES");
            return;
        }

        InputStreamReader streamReader = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(streamReader);

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    capeUsers.add(line);
                }
            }
        }
        catch (IOException ignored) {}
        finally
        {
            try {
                reader.close();
            }
            catch (IOException ignored) {}
        }
    }
}
