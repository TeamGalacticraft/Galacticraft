package com.hrznstudio.galacticraft.api.research;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class ClientResearchManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ResearchManager manager = new ResearchManager();
    private final Map<ResearchNode, AdvancementProgress> researchProgresses = Maps.newHashMap();
    @Nullable
    private ClientResearchManager.Listener listener;
//   @Nullable
//   private Advancement selectedTab; //fixme not relevant

    public ClientResearchManager() {
    }

    public void onResearch(PacketByteBuf buf) {
        boolean clearCurrent = buf.readBoolean();
        Map<Identifier, ResearchNode.Builder> toEarn = Maps.newHashMap();
        Set<Identifier> toRemove = Sets.newLinkedHashSet();
        Map<Identifier, AdvancementProgress> setProgress = Maps.newHashMap();
        int i = buf.readVarInt();

        int l;
        Identifier identifier3;
        for (l = 0; l < i; ++l) {
            identifier3 = buf.readIdentifier();
            ResearchNode.Builder task = ResearchNode.Builder.fromPacket(buf);
            toEarn.put(identifier3, task);
        }

        i = buf.readVarInt();

        for (l = 0; l < i; ++l) {
            identifier3 = buf.readIdentifier();
            toRemove.add(identifier3);
        }

        i = buf.readVarInt();

        for (l = 0; l < i; ++l) {
            identifier3 = buf.readIdentifier();
            setProgress.put(identifier3, AdvancementProgress.fromPacket(buf));
        }

        if (clearCurrent) {
            this.manager.clear();
            this.researchProgresses.clear();
        }

        this.manager.removeAll(toRemove);
        this.manager.load(toEarn);

        for (Entry<Identifier, AdvancementProgress> entry : setProgress.entrySet()) {
            ResearchNode node = this.manager.get(entry.getKey());
            if (node != null) {
                AdvancementProgress progress = entry.getValue();
                progress.init(node.getCriteria(), node.getRequirements());
                this.researchProgresses.put(node, progress);
                if (this.listener != null) {
                    this.listener.setProgress(node, progress);
                }


                if (!clearCurrent && progress.isDone() && node.getInfo() != null /*&& advancement.ge().shouldShowToast()*/) { //show toast always
                    LOGGER.info("obtained research: " + node.getId());
//               this.client.getToastManager().add(new AdvancementToast(advancement)); //todo toasts
                }
            } else {
                LOGGER.warn("Server informed client about progress for unknown research {}", entry.getKey());
            }
        }

    }

    public ResearchManager getManager() {
        return this.manager;
    }

//   public void selectTab(@Nullable Advancement tab, boolean local) { //not relevant
//      ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
//      if (clientPlayNetworkHandler != null && tab != null && local) {
//         clientPlayNetworkHandler.sendPacket(AdvancementTabC2SPacket.open(tab));
//      }
//
//      if (this.selectedTab != tab) {
//         this.selectedTab = tab;
//         if (this.listener != null) {
//            this.listener.selectTab(tab);
//         }
//      }
//
//   }

    public void addListener(@Nullable ClientResearchManager.Listener listener) {
        this.listener = listener;
        this.manager.listeners.add(listener);
        if (listener == null) {
            this.manager.listeners.clear();
        }
        if (listener != null) {

            for (Entry<ResearchNode, AdvancementProgress> entry : this.researchProgresses.entrySet()) {
                listener.setProgress(entry.getKey(), entry.getValue());
            }

//         listener.selectTab(this.selectedTab);
        }

    }

    @Environment(EnvType.CLIENT)
    public interface Listener extends ResearchManager.Listener {
        void setProgress(ResearchNode node, AdvancementProgress progress);

//      void selectTab(@Nullable ResearchNode advancement);
    }
}
