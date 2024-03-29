package com.guflimc.clans.common.commands.arguments;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import com.guflimc.clans.api.ClanAPI;
import com.guflimc.clans.api.domain.Clan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

public final class ClanArgument<C> extends CommandArgument<C, Clan> {

    private ClanArgument(
            final boolean required,
            final @NotNull String name,
            final @NotNull String defaultValue,
            final @Nullable BiFunction<CommandContext<C>, String,
                    List<String>> suggestionsProvider
    ) {
        super(required, name, new ClanParser<>(), defaultValue, Clan.class, suggestionsProvider);
    }

    public static final class ClanParser<C> implements ArgumentParser<C, Clan> {

        @Override
        public @NotNull ArgumentParseResult<Clan> parse(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull Queue<String> inputQueue
        ) {
            final String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(
                        ClanParser.class,
                        commandContext
                ));
            }
            inputQueue.remove();

            Clan clan = ClanAPI.get().findClan(input).orElse(null);

            if (clan == null) {
                return ArgumentParseResult.failure(new ClanParseException(input, commandContext));
            }

            return ArgumentParseResult.success(clan);
        }

        @Override
        public @NotNull List<String> suggestions(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull String input
        ) {
            List<String> output = new ArrayList<>();

            ClanAPI.get().clans().stream().filter(rg -> rg.name() != null)
                    .forEach(rg -> output.add(rg.name()));

            return output;
        }

    }

    public static final class ClanParseException extends ParserException {

        @Serial
        private static final long serialVersionUID = -2563079642852029296L;

        private final String input;

        public ClanParseException(
                final @NotNull String input,
                final @NotNull CommandContext<?> context
        ) {
            super(
                    ClanParser.class,
                    context,
                    Caption.of("cmd.args.error.Clan"),
                    CaptionVariable.of("0", input)
            );
            this.input = input;
        }

        public @NotNull String getInput() {
            return input;
        }

    }

}