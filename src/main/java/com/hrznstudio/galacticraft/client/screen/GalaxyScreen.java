package com.hrznstudio.galacticraft.client.screen;

import com.hrznstudio.galacticraft.api.space.CelestialBody;
import com.hrznstudio.galacticraft.api.space.RocketEntity;
import com.hrznstudio.galacticraft.api.space.RocketTier;
import com.hrznstudio.galacticraft.misc.RocketTiers;
import com.hrznstudio.galacticraft.util.registry.CelestialBodyRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalaxyScreen extends Screen {

    private RocketTier currentRocketTier = RocketTiers.noRocket;
    private List<CelestialBody> bodiesToDraw = new ArrayList<>();
    private CelestialBody selectedBody = null;
    private RocketEntity rocket = null;

    public GalaxyScreen(PlayerEntity playerEntity, Text component) {
        super(component);
        if (playerEntity.getVehicle() instanceof RocketEntity) {
            this.currentRocketTier = ((RocketEntity) playerEntity.getVehicle()).getRocketTier();
            this.rocket = ((RocketEntity) playerEntity.getVehicle());
        }
    }

    @Override
    protected void init() {

    }

    @Override
    public void render(int mouseX, int mouseY, float float_1) {
        super.render(mouseX, mouseY, float_1);

        for (int i = 0; i < CelestialBodyRegistry.bodies.size(); i++) {
            CelestialBody body = CelestialBodyRegistry.bodies.get(i);
            this.minecraft.getTextureManager().bindTexture(body.getIcon().getTexture());
            this.blit(0, 1 + (i * 16), body.getIcon().getX(), body.getIcon().getY(), body.getIcon().getWidth(), body.getIcon().getHeight());
            this.drawString(this.minecraft.textRenderer, body.getName(), 20, 1 + (i * 16), Formatting.DARK_GRAY.getColorValue());
        }
    }

    private void drawBodyOrbits() {
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glLineWidth(3);
        int count = 0;

        final float theta = (float) (2 * Math.PI / 90);
        final float cos = (float) Math.cos(theta);
        final float sin = (float) Math.sin(theta);

        for (CelestialBody body : this.bodiesToDraw) {

        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
