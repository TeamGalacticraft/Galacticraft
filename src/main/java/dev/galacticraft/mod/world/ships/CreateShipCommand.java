package dev.galacticraft.mod.world.ships;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.command.DimensionTpCommand;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;

import java.util.Collections;

public class CreateShipCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandRegistryAccess) {
        dispatcher.register(Commands.literal("ship")
                .requires(stack -> stack.hasPermission(2))
                .then(Commands.literal("new")
                        .then(Commands.argument("block", BlockStateArgument.block(commandRegistryAccess))
                        .executes(ctx -> BlockGroupRenderer.createNewGroup(BlockStateArgument.getBlock(ctx, "block")))))
                .then(Commands.literal("list")
                        .executes(ctx -> BlockGroupRenderer.listGroups()))
                .then(Commands.literal("transform")
                        .then(Commands.argument("ship name", StringArgumentType.word())
                                .suggests(new NameSuggestionProvider())
                                .then(Commands.argument("pos", Vec3Argument.vec3(false))
                                        .executes(ctx -> BlockGroupRenderer.translateShip(StringArgumentType.getString(ctx, "ship name"), new Vec3d(Vec3Argument.getVec3(ctx, "pos"))))

                                )
                        )
                )
                .then(Commands.literal("rotate")
                        .then(Commands.argument("ship name", StringArgumentType.word())
                                .suggests(new NameSuggestionProvider())
                                .then(Commands.argument("rotation", Vec3Argument.vec3(false))
                                        .executes(ctx -> BlockGroupRenderer.rotateShip(StringArgumentType.getString(ctx, "ship name"), new Vec3d(Vec3Argument.getVec3(ctx, "rotation"))))

                                )
                        )
                )
        );
    }
}
