/*
 * This file is part of Sleep-Polls - https://github.com/Amethyst-Developers/Sleep-Polls
 * Copyright (C) 2026  Monk (Monish), The Amethyst Team and contributors
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.amethystdev.commands;

import org.amethystdev.Main;

import org.amethystdev.database.DatabaseExecutor;

import org.amethystdev.model.PlayerData;

import org.amethystdev.sleep.Poll;
import org.amethystdev.sleep.SleepPollManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;

import revxrsal.commands.bukkit.actor.BukkitCommandActor;

@Command({ "sleeppoll", "sp" })
public final class SleepPollsCommand {

    private static final Component PREFIX =
            Component.text(
                    "[SleepPoll] ",
                    Style.style(
                            NamedTextColor.GOLD,
                            TextDecoration.BOLD
                    )
            );

    private final Main plugin;

    public SleepPollsCommand(
            Main plugin
    ) {

        this.plugin = plugin;
    }

    @Subcommand("help")
    public void help(
            BukkitCommandActor actor
    ) {

        actor.reply(
                Component.text(
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━",
                        NamedTextColor.DARK_GRAY
                )
        );

        actor.reply(
                Component.text(
                        "SleepPolls Commands",
                        NamedTextColor.GOLD
                ).decorate(
                        TextDecoration.BOLD
                )
        );

        actor.reply(Component.empty());

        actor.reply(
                Component.text(
                        "▪ ",
                        NamedTextColor.DARK_GRAY
                ).append(
                        Component.text(
                                "/sp help",
                                NamedTextColor.GOLD
                        )
                ).append(
                        Component.text(
                                " • Show this help menu",
                                NamedTextColor.GRAY
                        )
                )
        );

        actor.reply(
                Component.text(
                        "▪ ",
                        NamedTextColor.DARK_GRAY
                ).append(
                        Component.text(
                                "/sp version",
                                NamedTextColor.YELLOW
                        )
                ).append(
                        Component.text(
                                " • Show plugin version",
                                NamedTextColor.GRAY
                        )
                )
        );

        actor.reply(
                Component.text(
                        "▪ ",
                        NamedTextColor.DARK_GRAY
                ).append(
                        Component.text(
                                "/sp status",
                                NamedTextColor.AQUA
                        )
                ).append(
                        Component.text(
                                " • View current poll status",
                                NamedTextColor.GRAY
                        )
                )
        );

        actor.reply(
                Component.text(
                        "▪ ",
                        NamedTextColor.DARK_GRAY
                ).append(
                        Component.text(
                                "/sp stats",
                                NamedTextColor.AQUA
                        )
                ).append(
                        Component.text(
                                " • View your statistics",
                                NamedTextColor.GRAY
                        )
                )
        );

        actor.reply(
                Component.text(
                        "▪ ",
                        NamedTextColor.DARK_GRAY
                ).append(
                        Component.text(
                                "/sp top",
                                NamedTextColor.GOLD
                        )
                ).append(
                        Component.text(
                                " • View leaderboards",
                                NamedTextColor.GRAY
                        )
                )
        );

        actor.reply(
                Component.text(
                        "▪ ",
                        NamedTextColor.DARK_GRAY
                ).append(
                        Component.text(
                                "/sp yes",
                                NamedTextColor.GREEN
                        )
                ).append(
                        Component.text(
                                " • Vote yes in the active poll",
                                NamedTextColor.GRAY
                        )
                )
        );

        actor.reply(
                Component.text(
                        "▪ ",
                        NamedTextColor.DARK_GRAY
                ).append(
                        Component.text(
                                "/sp no",
                                NamedTextColor.RED
                        )
                ).append(
                        Component.text(
                                " • Vote no in the active poll",
                                NamedTextColor.GRAY
                        )
                )
        );

        actor.reply(
                Component.text(
                        "▪ ",
                        NamedTextColor.DARK_GRAY
                ).append(
                        Component.text(
                                "/sp bossbar",
                                NamedTextColor.AQUA
                        )
                ).append(
                        Component.text(
                                " • Toggle boss bar notifications",
                                NamedTextColor.GRAY
                        )
                )
        );

        actor.reply(
                Component.text(
                        "▪ ",
                        NamedTextColor.DARK_GRAY
                ).append(
                        Component.text(
                                "/sp reload",
                                NamedTextColor.YELLOW
                        )
                ).append(
                        Component.text(
                                " • Reload plugin configuration",
                                NamedTextColor.GRAY
                        )
                )
        );

        actor.reply(Component.empty());

        actor.reply(
                Component.text(
                        "Version: ",
                        NamedTextColor.DARK_GRAY
                ).append(
                        Component.text(
                                plugin.getPluginMeta()
                                        .getVersion(),
                                NamedTextColor.WHITE
                        )
                )
        );

        actor.reply(
                Component.text(
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━",
                        NamedTextColor.DARK_GRAY
                )
        );
    }

    @Subcommand("reload")
    public void reload(
            BukkitCommandActor actor
    ) {

        if (!actor.sender()
                .hasPermission(
                        "sleeppolls.reload"
                )) {

            actor.reply(
                    PREFIX.append(
                            Component.text(
                                    "You do not have permission.",
                                    NamedTextColor.RED
                            )
                    )
            );

            return;
        }

        actor.reply(
                PREFIX.append(
                        Component.text(
                                "Reloading SleepPolls...",
                                NamedTextColor.YELLOW
                        )
                )
        );

        DatabaseExecutor.executeAsync(() -> {

            try {

                plugin.reloadPlugin();

                Bukkit.getScheduler()
                        .runTask(plugin, () -> {

                            actor.reply(
                                    PREFIX.append(
                                            Component.text(
                                                    "SleepPolls configuration reloaded successfully.",
                                                    NamedTextColor.GREEN
                                            )
                                    )
                            );
                        });

            } catch (Exception e) {

                e.printStackTrace();

                Bukkit.getScheduler()
                        .runTask(plugin, () -> {

                            actor.reply(
                                    PREFIX.append(
                                            Component.text(
                                                    "Failed to reload SleepPolls.",
                                                    NamedTextColor.RED
                                            )
                                    )
                            );
                        });
            }
        });
    }

    @Subcommand("version")
    public void version(
            BukkitCommandActor actor
    ) {

        actor.reply(
                Component.text(
                        "Running SleepPolls ",
                        NamedTextColor.GREEN,
                        TextDecoration.BOLD
                ).append(
                        Component.text(
                                plugin.getPluginMeta()
                                        .getVersion(),
                                NamedTextColor.GRAY,
                                TextDecoration.ITALIC
                        )
                )
        );
    }

    @Subcommand("status")
    public void status(
            BukkitCommandActor actor
    ) {

        if (!actor.isPlayer()) {

            actor.reply(
                    Component.text(
                            "This command can only be used by players!",
                            NamedTextColor.RED
                    )
            );

            return;
        }

        Player player =
                actor.asPlayer();

        SleepPollManager manager =
                plugin.getPollManager();

        Poll poll =
                manager.getPoll(
                        player.getWorld()
                                .getName()
                );

        if (poll == null) {

            actor.reply(
                    PREFIX.append(
                            Component.text(
                                    "There is no active sleep poll in this world.",
                                    NamedTextColor.RED
                            )
                    )
            );

            return;
        }

        actor.reply(
                Component.text(
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━",
                        NamedTextColor.DARK_GRAY
                )
        );

        actor.reply(
                Component.text(
                        "🌙 Sleep Poll Status",
                        NamedTextColor.GOLD
                ).decorate(
                        TextDecoration.BOLD
                )
        );

        actor.reply(
                Component.text(
                        "World: ",
                        NamedTextColor.GRAY
                ).append(
                        Component.text(
                                poll.getWorld(),
                                NamedTextColor.AQUA
                        )
                )
        );

        actor.reply(
                Component.text(
                        "YES Votes: ",
                        NamedTextColor.GRAY
                ).append(
                        Component.text(
                                poll.getYesVoteCount(),
                                NamedTextColor.GREEN
                        )
                )
        );

        actor.reply(
                Component.text(
                        "NO Votes: ",
                        NamedTextColor.GRAY
                ).append(
                        Component.text(
                                poll.getNoVoteCount(),
                                NamedTextColor.RED
                        )
                )
        );

        actor.reply(
                Component.text(
                        "Required: ",
                        NamedTextColor.GRAY
                ).append(
                        Component.text(
                                poll.getNeededVotes(),
                                NamedTextColor.AQUA
                        )
                )
        );

        actor.reply(
                Component.text(
                        "Time Remaining: ",
                        NamedTextColor.GRAY
                ).append(
                        Component.text(
                                poll.getRemainingSeconds()
                                        + "s",
                                poll.getRemainingSeconds()
                                        <= 5
                                                ? NamedTextColor.RED
                                                : NamedTextColor.YELLOW
                        )
                )
        );

        actor.reply(
                Component.text(
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━",
                        NamedTextColor.DARK_GRAY
                )
        );
    }

    @Subcommand("stats")
    public void stats(
            BukkitCommandActor actor
    ) {

        if (!actor.isPlayer()) {

            actor.reply(
                    Component.text(
                            "This command can only be used by players!",
                            NamedTextColor.RED
                    )
            );

            return;
        }

        Player player =
                actor.asPlayer();

        plugin.getPlayerDataRepository()
                .getOrCreatePlayerAsync(
                        player.getUniqueId()
                )
                .thenAccept(data -> {

                    if (data == null) {

                        Bukkit.getScheduler()
                                .runTask(plugin, () -> {

                                    player.sendMessage(
                                            PREFIX.append(
                                                    Component.text(
                                                            "Failed to load your statistics.",
                                                            NamedTextColor.RED
                                                    )
                                            )
                                    );
                                });

                        return;
                    }

                    double successRate =
                            data.getTotalVotes() == 0
                                    ? 0
                                    : (
                                    (double)
                                            data.getSuccessfulVotes()
                                            / data.getTotalVotes()
                            ) * 100;

                    Bukkit.getScheduler()
                            .runTask(plugin, () -> {

                                actor.reply(
                                        Component.text(
                                                "━━━━━━━━━━━━━━━━━━━━━━━━━━",
                                                NamedTextColor.DARK_GRAY
                                        )
                                );

                                actor.reply(
                                        Component.text(
                                                "📊 SleepPoll Statistics",
                                                NamedTextColor.GOLD
                                        ).decorate(
                                                TextDecoration.BOLD
                                        )
                                );

                                actor.reply(
                                        Component.text(
                                                "Total Votes: ",
                                                NamedTextColor.GRAY
                                        ).append(
                                                Component.text(
                                                        data.getTotalVotes(),
                                                        NamedTextColor.AQUA
                                                )
                                        )
                                );

                                actor.reply(
                                        Component.text(
                                                "Successful Votes: ",
                                                NamedTextColor.GRAY
                                        ).append(
                                                Component.text(
                                                        data.getSuccessfulVotes(),
                                                        NamedTextColor.GREEN
                                                )
                                        )
                                );

                                actor.reply(
                                        Component.text(
                                                "Failed Votes: ",
                                                NamedTextColor.GRAY
                                        ).append(
                                                Component.text(
                                                        data.getFailedVotes(),
                                                        NamedTextColor.RED
                                                )
                                        )
                                );

                                actor.reply(
                                        Component.text(
                                                "Polls Started: ",
                                                NamedTextColor.GRAY
                                        ).append(
                                                Component.text(
                                                        data.getPollsStarted(),
                                                        NamedTextColor.YELLOW
                                                )
                                        )
                                );

                                actor.reply(
                                        Component.text(
                                                "Nights Skipped: ",
                                                NamedTextColor.GRAY
                                        ).append(
                                                Component.text(
                                                        data.getNightsSkipped(),
                                                        NamedTextColor.GOLD
                                                )
                                        )
                                );

                                actor.reply(
                                        Component.text(
                                                "Success Rate: ",
                                                NamedTextColor.GRAY
                                        ).append(
                                                Component.text(
                                                        String.format(
                                                                "%.1f%%",
                                                                successRate
                                                        ),
                                                        NamedTextColor.GREEN
                                                )
                                        )
                                );

                                actor.reply(
                                        Component.text(
                                                "━━━━━━━━━━━━━━━━━━━━━━━━━━",
                                                NamedTextColor.DARK_GRAY
                                        )
                                );
                            });
                });
    }

    @Subcommand("top")
    public void top(
            BukkitCommandActor actor
    ) {

        plugin.getPlayerDataRepository()
                .getTopPlayersAsync(10)
                .thenAccept(topPlayers -> {

                    Bukkit.getScheduler()
                            .runTask(plugin, () -> {

                                actor.reply(
                                        Component.text(
                                                "━━━━━━━━━━━━━━━━━━━━━━━━━━",
                                                NamedTextColor.DARK_GRAY
                                        )
                                );

                                actor.reply(
                                        Component.text(
                                                "🏆 SleepPoll Leaderboards",
                                                NamedTextColor.GOLD
                                        ).decorate(
                                                TextDecoration.BOLD
                                        )
                                );

                                if (topPlayers.isEmpty()) {

                                    actor.reply(
                                            Component.text(
                                                    "No statistics available yet.",
                                                    NamedTextColor.RED
                                            )
                                    );

                                    return;
                                }

                                int position = 1;

                                for (PlayerData data :
                                        topPlayers) {

                                    String playerName =
                                            "Unknown";

                                    var offlinePlayer =
                                            Bukkit.getOfflinePlayer(
                                                    data.getUuid()
                                            );

                                    if (offlinePlayer.getName()
                                            != null) {

                                        playerName =
                                                offlinePlayer.getName();
                                    }

                                    actor.reply(
                                            Component.text(
                                                    "#" + position + " ",
                                                    NamedTextColor.GOLD
                                            ).append(
                                                    Component.text(
                                                            playerName,
                                                            NamedTextColor.YELLOW
                                                    )
                                            ).append(
                                                    Component.text(
                                                            " • ",
                                                            NamedTextColor.DARK_GRAY
                                                    )
                                            ).append(
                                                    Component.text(
                                                            data.getSuccessfulVotes()
                                                                    + " successful votes",
                                                            NamedTextColor.GREEN
                                                    )
                                            )
                                    );

                                    position++;
                                }

                                actor.reply(
                                        Component.text(
                                                "━━━━━━━━━━━━━━━━━━━━━━━━━━",
                                                NamedTextColor.DARK_GRAY
                                        )
                                );
                            });
                });
    }

    @Subcommand("yes")
    public void voteYes(
            BukkitCommandActor actor
    ) {

        vote(actor, true);
    }

    @Subcommand("no")
    public void voteNo(
            BukkitCommandActor actor
    ) {

        vote(actor, false);
    }

    @Subcommand("bossbar")
    public void bossbar(
            BukkitCommandActor actor
    ) {

        if (!actor.isPlayer()) {

            actor.reply(
                    Component.text(
                            "This command can only be used by players!",
                            NamedTextColor.RED
                    )
            );

            return;
        }

        Player player =
                actor.asPlayer();

        if (!player.hasPermission(
                "sleeppolls.bossbar"
        )) {

            player.sendMessage(
                    PREFIX.append(
                            Component.text(
                                    "You do not have permission to use this command.",
                                    NamedTextColor.RED
                            )
                    )
            );

            return;
        }

        boolean currentlyEnabled =
                plugin.hasBossBarEnabled(player);

        plugin.setBossBarEnabled(
                player,
                !currentlyEnabled
        );

        player.sendMessage(
                PREFIX.append(
                        Component.text(
                                "BossBar notifications ",
                                NamedTextColor.GRAY
                        )
                ).append(
                        Component.text(
                                currentlyEnabled
                                        ? "disabled"
                                        : "enabled",
                                currentlyEnabled
                                        ? NamedTextColor.RED
                                        : NamedTextColor.GREEN
                        )
                ).append(
                        Component.text(
                                ".",
                                NamedTextColor.GRAY
                        )
                )
        );
    }

    private void vote(
            BukkitCommandActor actor,
            boolean vote
    ) {

        if (!actor.isPlayer()) {

            actor.reply(
                    Component.text(
                            "This command can only be used by players!",
                            NamedTextColor.RED
                    )
            );

            return;
        }

        Player player =
                actor.asPlayer();

        SleepPollManager manager =
                plugin.getPollManager();

        Poll poll =
                manager.getPoll(
                        player.getWorld()
                                .getName()
                );

        if (poll == null) {

            player.sendMessage(
                    PREFIX.append(
                            Component.text(
                                    "There is no active poll in your world.",
                                    NamedTextColor.RED
                            )
                    )
            );

            return;
        }

        manager.vote(
                poll,
                player,
                vote
        );
    }
}