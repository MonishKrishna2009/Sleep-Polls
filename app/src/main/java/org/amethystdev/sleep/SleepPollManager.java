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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.amethystdev.Main;
import org.amethystdev.message.ActionBarFactory;
import org.amethystdev.message.SleepBossBarFactory;
import org.amethystdev.message.SleepMessageFactory;
import org.amethystdev.model.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import net.kyori.adventure.text.Component;

public final class SleepPollManager {

    private final Main plugin;

    private final Map<String, Poll> polls =
            new HashMap<>();

    private final Map<String, BossBar> bossBars =
            new HashMap<>();

    private final Map<String, BukkitTask> tasks =
            new HashMap<>();

    public SleepPollManager(
            Main plugin
    ) {

        this.plugin = plugin;
    }

    public boolean isPollActive(
            String worldName
    ) {

        Poll poll =
                polls.get(worldName);

        return poll != null
                && poll.getState()
                == PollState.ACTIVE;
    }

    public Poll startPoll(
            String worldName,
            Set<Player> voters
    ) {

        Set<UUID> eligible =
                new HashSet<>();

        for (Player player : voters) {

            eligible.add(
                    player.getUniqueId()
            );
        }

        Poll poll =
                new Poll(
                        worldName,
                        eligible,
                        plugin.getPollDurationSeconds(),
                        plugin.getRequiredPercentage()
                );

        polls.put(
                worldName,
                poll
        );

        startPollLifecycle(poll);

        return poll;
    }

    public Poll getPoll(
            String worldName
    ) {

        return polls.get(worldName);
    }

    private Set<Player> getOnlineEligiblePlayers(
            Poll poll
    ) {

        Set<Player> players =
                new LinkedHashSet<>();

        for (UUID id :
                poll.getEligible()) {

            Player player =
                    Bukkit.getPlayer(id);

            if (player != null) {

                players.add(player);
            }
        }

        return players;
    }

    private void startPollLifecycle(
            Poll poll
    ) {

        Bukkit.getScheduler().runTask(plugin, () -> {

            int needed =
                    poll.getNeededVotes();

            for (Player p :
                    getOnlineEligiblePlayers(
                            poll
                    )) {

                Component yesButton = SleepMessageFactory.createYesButton();

                Component noButton = SleepMessageFactory.createNoButton();

                p.sendMessage(SleepMessageFactory.createPollDivider());

                p.sendMessage(
                        SleepMessageFactory.createPrefix()
                                .append(SleepMessageFactory.createPollStartedMessage())
                );

                p.sendMessage(
                        SleepMessageFactory.createPollVoteOptionsMessage(
                                poll.getRemainingSeconds(),
                                yesButton,
                                noButton
                        )
                );

                p.sendMessage(
                        SleepMessageFactory.createVotesNeededMessage(
                                needed,
                                poll.getEligibleCount()
                        )
                );
                p.sendMessage(SleepMessageFactory.createPollDivider());

                if (plugin.areSoundsEnabled()) {

                    p.playSound(
                            p.getLocation(),
                            Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                            1f,
                            1f
                    );
                }
            }

            if (plugin.isBossBarEnabled()) {

                BossBar bossBar =
                        Bukkit.createBossBar(
                                "§6🌙 Sleep Poll",
                                BarColor.GREEN,
                                BarStyle.SOLID
                        );

                for (Player p :
                        getOnlineEligiblePlayers(
                                poll
                        )) {

                    if (plugin.hasBossBarEnabled(p)) {

                        bossBar.addPlayer(p);
                    }
                }

                bossBars.put(
                        poll.getWorld(),
                        bossBar
                );

                updateBossBar(poll);
            }

            updateActionBars(poll);
        });

        BukkitTask task =
                Bukkit.getScheduler()
                        .runTaskTimer(
                                plugin,
                                () -> {

                                    if (poll.getState()
                                            != PollState.ACTIVE)
                                        return;

                                    poll.decrementTimer();

                                    updateBossBar(poll);

                                    updateActionBars(poll);

                                    if (poll.getRemainingSeconds()
                                            <= 5
                                            && poll.getRemainingSeconds()
                                            > 0) {

                                        for (Player p :
                                                getOnlineEligiblePlayers(
                                                        poll
                                                )) {

                                            if (plugin.areSoundsEnabled()) {

                                                p.playSound(
                                                        p.getLocation(),
                                                        Sound.BLOCK_NOTE_BLOCK_HAT,
                                                        1f,
                                                        1.8f
                                                );
                                            }
                                        }
                                    }

                                    if (poll.getRemainingSeconds()
                                            <= 0) {

                                        finishPoll(poll);
                                    }

                                },
                                20L,
                                20L
                        );

        tasks.put(
                poll.getWorld(),
                task
        );
    }

    public synchronized void vote(
            Poll poll,
            Player player,
            boolean accept
    ) {

        if (poll.getState()
                != PollState.ACTIVE)
            return;

        if (!poll.getEligible()
                .contains(
                        player.getUniqueId()
                ))
            return;

        poll.vote(
                player.getUniqueId(),
                accept
        );

        PlayerData data =
                plugin.getPlayerDataRepository().getOrCreatePlayer(
                                player.getUniqueId()
                        );

        if (data != null) {

            data.incrementVotes();

            plugin.getPlayerDataRepository().savePlayer(data);
        }

        updateBossBar(poll);

        updateActionBars(poll);

        player.sendMessage(
                SleepMessageFactory.createPrefix()
                        .append(SleepMessageFactory.createVoteRecordedMessage(accept))
        );

        if (poll.hasPassed()) {

            finishPoll(poll);

            return;
        }

        if (!poll.canStillPass()) {

            finishPoll(poll);
        }
    }

    public synchronized void finishPoll(
            Poll poll
    ) {

        if (poll.getState()
                != PollState.ACTIVE)
            return;

        boolean succeeded =
                poll.hasPassed();

        poll.setState(
                succeeded
                        ? PollState.SUCCEEDED
                        : PollState.FAILED
        );

        BukkitTask task =
                tasks.remove(
                        poll.getWorld()
                );

        if (task != null) {
            task.cancel();
        }

        BossBar bossBar =
                bossBars.remove(
                        poll.getWorld()
                );

        if (bossBar != null) {
            bossBar.removeAll();
        }

        /*
         * Statistics tracking
         */
        if (succeeded) {

            for (UUID id :
                    poll.getYesVotes()) {

                PlayerData data =
                        plugin.getPlayerDataRepository().getOrCreatePlayer(id);

                if (data == null)
                    continue;

                data.incrementSuccessfulVotes();

                data.incrementNightsSkipped();

                plugin.getPlayerDataRepository().savePlayer(data);
            }

        } else {

            for (UUID id :
                    poll.getYesVotes()) {

                PlayerData data =
                        plugin.getPlayerDataRepository().getOrCreatePlayer(id);

                if (data == null)
                    continue;

                data.incrementFailedVotes();

                plugin.getPlayerDataRepository().savePlayer(data);
            }
        }

        Bukkit.getScheduler().runTask(plugin, () -> {

            Component msg =
                    succeeded
                            ? SleepMessageFactory.createPollSuccessMessage()
                            : SleepMessageFactory.createPollFailedMessage();

            for (Player p :
                    getOnlineEligiblePlayers(
                            poll
                    )) {

                p.sendMessage(
                        SleepMessageFactory.createPrefix().append(msg)
                );

                if (plugin.areSoundsEnabled()) {

                    if (succeeded) {

                        p.playSound(
                                p.getLocation(),
                                Sound.ENTITY_PLAYER_LEVELUP,
                                1f,
                                1f
                        );

                    } else {

                        p.playSound(
                                p.getLocation(),
                                Sound.BLOCK_ANVIL_LAND,
                                0.7f,
                                1.5f
                        );
                    }
                }
            }

            if (succeeded) {

                var world = Bukkit.getWorld(
                        poll.getWorld()
                );

                if (world != null) {

                    world.setTime(1000L);
                }
            }

            polls.remove(
                    poll.getWorld()
            );
        });
    }

    private void updateBossBar(
            Poll poll
    ) {

        BossBar bossBar =
                bossBars.get(
                        poll.getWorld()
                );

        if (bossBar == null)
            return;

        bossBar.setTitle(SleepBossBarFactory.createTitle(poll));

        bossBar.setProgress(SleepBossBarFactory.createProgress(poll));
    }

    private void updateActionBars(
            Poll poll
    ) {

        Component actionBar = ActionBarFactory.createPollActionBar(poll);

        for (Player p :
                getOnlineEligiblePlayers(
                        poll
                )) {

            p.sendActionBar(actionBar);
        }
    }
}