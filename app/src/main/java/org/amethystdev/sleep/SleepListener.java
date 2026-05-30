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

package org.amethystdev.sleep;

import java.util.Set;
import java.util.stream.Collectors;

import org.amethystdev.Main;
import org.amethystdev.model.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import net.ess3.api.IEssentials;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

public final class SleepListener implements Listener {

    private static final Component PREFIX =
            Component.text(
                    "[SleepPoll] ",
                    Style.style(
                            NamedTextColor.GOLD,
                            TextDecoration.BOLD
                    )
            );

    private final Main plugin;

    private final SleepPollManager manager;

    public SleepListener(
            Main plugin,
            SleepPollManager manager
    ) {

        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerBedEnter(
            PlayerBedEnterEvent event
    ) {

        // Bed entry failed
        if (event.getBedEnterResult()
                != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }

        Player sleeper =
                event.getPlayer();

        World world =
                sleeper.getWorld();

        String worldName =
                world.getName();

        // Ignore blacklisted worlds
        if (plugin.isWorldBlocked(worldName)) {
            return;
        }

        long time =
                world.getTime();

        // Only allow at night
        if (time < 13000
                || time > 23000) {

            sleeper.sendMessage(
                    PREFIX.append(
                            Component.text(
                                    "Sleep polls are only available during night time!",
                                    NamedTextColor.RED
                            )
                    )
            );

            return;
        }

        // Prevent duplicate polls
        if (manager.isPollActive(worldName)) {

            event.setCancelled(true);

            sleeper.sendMessage(
                    PREFIX.append(
                            Component.text(
                                    "A poll is already active! ",
                                    NamedTextColor.YELLOW
                            )
                    ).append(
                            Component.text(
                                    "Please vote with ",
                                    NamedTextColor.GRAY
                            )
                    ).append(
                            Component.text(
                                    "/sleeppoll yes ",
                                    NamedTextColor.GREEN
                            )
                    ).append(
                            Component.text(
                                    "or ",
                                    NamedTextColor.GRAY
                            )
                    ).append(
                            Component.text(
                                    "/sleeppoll no",
                                    NamedTextColor.RED
                            )
                    ).append(
                            Component.text(
                                    ".",
                                    NamedTextColor.GRAY
                            )
                    )
            );

            return;
        }

        Set<Player> voters = world.getPlayers()
                .stream()

                // Online only
                .filter(Player::isOnline)

                // Ignore AFK players
                .filter(p -> !isAfk(p))

                // Ignore spectators
                .filter(p ->
                        p.getGameMode()
                                != GameMode.SPECTATOR
                )

                // Ignore dead players
                .filter(p -> !p.isDead())

                .collect(Collectors.toSet());

        // Minimum players check
        if (voters.size() < 2) {

            sleeper.sendMessage(
                    PREFIX.append(
                            Component.text(
                                    "Not enough active players online to start a sleep poll.",
                                    NamedTextColor.RED
                            )
                    )
            );

            return;
        }

        event.setCancelled(true);

        manager.startPoll(
                worldName,
                voters
        );

        // Track statistics
        PlayerData data =
                plugin.getPlayerDataRepository()
                        .getOrCreatePlayer(
                                sleeper.getUniqueId()
                        );

        if (data != null) {

            data.incrementPollsStarted();

            plugin.getPlayerDataRepository()
                    .savePlayer(data);
        }
    }

    private boolean isAfk(
            Player player
    ) {

        if (Bukkit.getPluginManager()
                .getPlugin("Essentials") == null) {

            return false;
        }

        try {

            IEssentials essentials =
                    (IEssentials) Bukkit.getPluginManager()
                            .getPlugin("Essentials");

            return essentials
                    .getUser(player)
                    .isAfk();

        } catch (Exception ignored) {

            return false;
        }
    }
}