package elucent.eidolon.util;

import elucent.eidolon.api.research.Research;
import elucent.eidolon.api.spells.Rune;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.capability.IKnowledge;
import elucent.eidolon.capability.IReputation;
import elucent.eidolon.common.deity.Deities;
import elucent.eidolon.network.KnowledgeUpdatePacket;
import elucent.eidolon.network.Networking;
import elucent.eidolon.registries.AdvancementTriggers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class KnowledgeUtil {
    public static void grantSign(Entity entity, Sign sign) {
        if (!(entity instanceof ServerPlayer player)) return;
        entity.getCapability(IKnowledge.INSTANCE, null).ifPresent((k) -> {
            if (k.knowsSign(sign)) return;
            k.addSign(sign);

            player.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("eidolon.title.new_sign", Component.translatable(sign.getRegistryName().getNamespace() + ".sign." + sign.getRegistryName().getPath()))));
            AdvancementTriggers.triggerSign(sign, player);
            Networking.sendTo(player, new KnowledgeUpdatePacket(player, true));
        });
    }

    public static void grantFact(Entity entity, ResourceLocation fact) {
        if (!(entity instanceof ServerPlayer player)) return;
        entity.getCapability(IKnowledge.INSTANCE, null).ifPresent((k) -> {
            if (k.knowsFact(fact)) return;
            k.addFact(fact);
            player.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("eidolon.title.new_fact")));
            Networking.sendTo(player, new KnowledgeUpdatePacket(player, true));
        });
        AdvancementTriggers.triggerResearch(fact.getPath(), player);
    }

    public static void grantResearch(Entity entity, @NotNull Research research) {
        if (!(entity instanceof ServerPlayer serverPlayer)) return;
        entity.getCapability(IKnowledge.INSTANCE, null).ifPresent((k) -> {
            if (k.knowsResearch(research)) return;
            k.addResearch(research.getRegistryName());
            research.onLearned(serverPlayer);
            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("eidolon.title.new_research", ChatFormatting.GOLD + research.getName())));
            Networking.sendTo(serverPlayer, new KnowledgeUpdatePacket(serverPlayer, true));
        });
        AdvancementTriggers.triggerResearch(research.getRegistryName().toString(), serverPlayer);
    }

    public static void grantResearchNoToast(Entity entity, @NotNull ResourceLocation research) {
        if (!(entity instanceof ServerPlayer serverPlayer)) return;
        entity.getCapability(IKnowledge.INSTANCE, null).ifPresent((k) -> {
            if (k.knowsResearch(research)) return;
            k.addResearch(research);
            Networking.sendTo(serverPlayer, new KnowledgeUpdatePacket(serverPlayer, true));
        });
        AdvancementTriggers.triggerResearch(research.getPath(), serverPlayer);
    }

    public static void grantRune(Entity entity, Rune rune) {
        if (!(entity instanceof ServerPlayer player)) return;
        entity.getCapability(IKnowledge.INSTANCE, null).ifPresent((k) -> {
            if (k.knowsRune(rune)) return;
            k.addRune(rune);

            player.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("eidolon.title.new_rune", Component.translatable(rune.getRegistryName().getNamespace() + ".rune." + rune.getRegistryName().getPath()))));
            Networking.sendTo(player, new KnowledgeUpdatePacket(player, true));
        });
    }

    public static boolean knowsSign(Player player, Sign sign) {
        if (player.getCapability(IKnowledge.INSTANCE).isPresent()) {
            return player.getCapability(IKnowledge.INSTANCE).resolve().get().knowsSign(sign);
        }
        return false;
    }

    public static boolean knowsFact(Player player, ResourceLocation fact) {
        if (player.getCapability(IKnowledge.INSTANCE).isPresent()) {
            return player.getCapability(IKnowledge.INSTANCE).resolve().get().knowsFact(fact);
        }
        return false;
    }

    public static boolean knowsResearch(Player player, ResourceLocation research) {
        if (player.getCapability(IKnowledge.INSTANCE).isPresent()) {
            return player.getCapability(IKnowledge.INSTANCE).resolve().get().knowsResearch(research);
        }
        return false;
    }

    public static boolean knowsRune(Player player, Rune rune) {
        if (player.getCapability(IKnowledge.INSTANCE).isPresent()) {
            return player.getCapability(IKnowledge.INSTANCE).resolve().get().knowsRune(rune);
        }
        return false;
    }

    public static void removeSign(Entity entity, Sign sign) {
        if (!(entity instanceof ServerPlayer player)) return;
        entity.getCapability(IKnowledge.INSTANCE, null).ifPresent((k) -> {
            if (!k.knowsSign(sign)) return;
            k.removeSign(sign);
            Networking.sendTo(player, new KnowledgeUpdatePacket(player, true));
        });
    }

    public static void removeFact(Entity entity, ResourceLocation fact) {
        if (!(entity instanceof ServerPlayer player)) return;
        entity.getCapability(IKnowledge.INSTANCE, null).ifPresent((k) -> {
            if (!k.knowsFact(fact)) return;
            k.removeFact(fact);
            Networking.sendTo(player, new KnowledgeUpdatePacket(player, true));
        });
    }

    public static void removeResearch(Entity entity, ResourceLocation research) {
        if (!(entity instanceof ServerPlayer player)) return;
        entity.getCapability(IKnowledge.INSTANCE, null).ifPresent((k) -> {
            if (!k.knowsResearch(research)) return;
            k.removeResearch(research);
            Networking.sendTo(player, new KnowledgeUpdatePacket(player, true));
        });
    }

    public static void removeRune(Entity entity, Rune rune) {
        if (!(entity instanceof ServerPlayer player)) return;
        entity.getCapability(IKnowledge.INSTANCE, null).ifPresent((k) -> {
            if (!k.knowsRune(rune)) return;
            k.removeRune(rune);
            Networking.sendTo(player, new KnowledgeUpdatePacket(player, true));
        });
    }

    public static void resetSigns(Entity entity) {
        if (!(entity instanceof ServerPlayer player)) return;
        entity.getCapability(IKnowledge.INSTANCE, null).ifPresent((k) -> {
            k.resetSigns();
            Networking.sendTo(player, new KnowledgeUpdatePacket(player, true));
        });
    }

    public static void resetFacts(Entity entity) {
        if (!(entity instanceof ServerPlayer player)) return;
        entity.getCapability(IKnowledge.INSTANCE, null).ifPresent((k) -> {
            k.resetFacts();
            Networking.sendTo(player, new KnowledgeUpdatePacket(player, true));
        });
    }

    public static void resetResearch(Entity entity) {
        if (!(entity instanceof ServerPlayer player)) return;
        entity.getCapability(IKnowledge.INSTANCE, null).ifPresent((k) -> {
            k.resetResearch();
            Networking.sendTo(player, new KnowledgeUpdatePacket(player, true));
        });
    }

    public static void resetRunes(Entity entity) {
        if (!(entity instanceof ServerPlayer player)) return;
        entity.getCapability(IKnowledge.INSTANCE, null).ifPresent((k) -> {
            k.resetRunes();
            Networking.sendTo(player, new KnowledgeUpdatePacket(player, true));
        });
    }

    public static void tryFix(Player player) {
        if (!(player instanceof ServerPlayer && player.level() instanceof ServerLevel server)) return;
        LazyOptional<IReputation> devotion = server.getCapability(IReputation.INSTANCE);
        if (devotion.isPresent()) {
            IReputation d = devotion.resolve().get();
            Deities.getDeities().forEach((deity) -> {
                var rep = d.getReputation(player, deity.getId());
                var curStage = deity.getProgression().last(rep);
                double fakeRep = 1;
                int counter = 0; // Prevent infinite loops, just in case, I don't trust this enough to leave it unchecked
                while (fakeRep < curStage.rep() && counter++ < 20) {
                    var next = deity.getProgression().next(fakeRep);
                    if (next == null) break;
                    deity.onReputationLock(player, next.id());
                    deity.onReputationUnlock(player, next.id());
                    fakeRep = next.rep() + 1;
                }
            });
        }
    }

}
