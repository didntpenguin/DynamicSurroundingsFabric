package org.orecruncher.dsurround.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.scripting.Script;
import org.orecruncher.dsurround.runtime.BiomeConditionEvaluator;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

@Environment(EnvType.CLIENT)
final class BiomeCommand {

    public static void register(@Nullable CommandDispatcher<FabricClientCommandSource> dispatcher) {
        if (dispatcher == null) {
            return;
        }
        dispatcher.register(
                ClientCommandManager.literal("dsbiome")
                        .then(argument("biomeId", IdentifierArgumentType.identifier())
                                .then(argument("script", MessageArgumentType.message()).executes(BiomeCommand::execute))));
    }

    private static int execute(CommandContext<FabricClientCommandSource> ctx) {
        var biomeId = ctx.getArgument("biomeId", Identifier.class);
        var script = ctx.getArgument("script", MessageArgumentType.MessageFormat.class);
        var biome = GameUtils.getRegistryManager().get(Registry.BIOME_KEY).get(biomeId);
        var result = BiomeConditionEvaluator.INSTANCE.eval(biome, new Script(script.getContents()));
        ctx.getSource().sendFeedback(Text.of(result.toString()));
        return 1;
    }
}
