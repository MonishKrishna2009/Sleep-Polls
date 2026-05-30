package org.amethystdev.sleep;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.amethystdev.Main;
import org.amethystdev.model.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

public final class SleepPollManager {

    private final Plugin plugin;

    private final Map<String, Poll> polls =
            new HashMap<>();

    private final Map<String, BossBar> bossBars =
            new HashMap<>();

    private final Map<String, BukkitTask> tasks =
            new HashMap<>();

    public SleepPollManager(
            Plugin plugin
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
                        ((Main) plugin)
                                .getPollDurationSeconds(),
                        ((Main) plugin)
                                .getRequiredPercentage()
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

    private void startPollLifecycle(
            Poll poll
    ) {

        Bukkit.getScheduler().runTask(plugin, () -> {

            int needed =
                    poll.getNeededVotes();

            for (UUID id : poll.getEligible()) {

                Player p =
                        Bukkit.getPlayer(id);

                if (p == null)
                    continue;

                Component yesButton =
                        Component.text(
                                "[✔ YES]",
                                Style.style(
                                        NamedTextColor.GREEN,
                                        TextDecoration.BOLD
                                )
                        )
                        .clickEvent(
                                ClickEvent.runCommand(
                                        "/sp yes"
                                )
                        )
                        .hoverEvent(
                                HoverEvent.showText(
                                        Component.text(
                                                "Vote YES",
                                                NamedTextColor.GREEN
                                        )
                                )
                        );

                Component noButton =
                        Component.text(
                                "[✖ NO]",
                                Style.style(
                                        NamedTextColor.RED,
                                        TextDecoration.BOLD
                                )
                        )
                        .clickEvent(
                                ClickEvent.runCommand(
                                        "/sp no"
                                )
                        )
                        .hoverEvent(
                                HoverEvent.showText(
                                        Component.text(
                                                "Vote NO",
                                                NamedTextColor.RED
                                        )
                                )
                        );

                p.sendMessage(
                        Component.text(
                                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                                Style.style(
                                        NamedTextColor.DARK_GRAY,
                                        TextDecoration.STRIKETHROUGH
                                )
                        )
                );

                p.sendMessage(
                        Component.text(
                                "[SleepPoll] ",
                                Style.style(
                                        NamedTextColor.GOLD,
                                        TextDecoration.BOLD
                                )
                        ).append(
                                Component.text(
                                        "A player is sleeping!",
                                        NamedTextColor.YELLOW
                                )
                        )
                );

                p.sendMessage(
                        Component.text(
                                "Vote within ",
                                NamedTextColor.GRAY
                        ).append(
                                Component.text(
                                        poll.getRemainingSeconds()
                                                + " seconds",
                                        NamedTextColor.AQUA
                                )
                        ).append(
                                Component.text(
                                        ": ",
                                        NamedTextColor.GRAY
                                )
                        ).append(
                                yesButton
                        ).append(
                                Component.text(
                                        "   ",
                                        NamedTextColor.DARK_GRAY
                                )
                        ).append(
                                noButton
                        )
                );

                p.sendMessage(
                        Component.text(
                                "Votes needed: ",
                                NamedTextColor.GRAY
                        ).append(
                                Component.text(
                                        String.valueOf(needed),
                                        NamedTextColor.AQUA
                                )
                        ).append(
                                Component.text(
                                        "/",
                                        NamedTextColor.GRAY
                                )
                        ).append(
                                Component.text(
                                        String.valueOf(
                                                poll.getEligible().size()
                                        ),
                                        NamedTextColor.AQUA
                                )
                        )
                );

                if (((Main) plugin)
                        .areSoundsEnabled()) {

                    p.playSound(
                            p.getLocation(),
                            Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                            1f,
                            1f
                    );
                }
            }

            if (((Main) plugin)
                    .isBossBarEnabled()) {

                BossBar bossBar =
                        Bukkit.createBossBar(
                                "§6🌙 Sleep Poll",
                                BarColor.GREEN,
                                BarStyle.SOLID
                        );

                for (UUID id : poll.getEligible()) {

                    Player p =
                            Bukkit.getPlayer(id);

                    if (p == null)
                        continue;

                    if (((Main) plugin)
                            .hasBossBarEnabled(p)) {

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

                                        for (UUID id :
                                                poll.getEligible()) {

                                            Player p =
                                                    Bukkit.getPlayer(id);

                                            if (p == null)
                                                continue;

                                            if (((Main) plugin)
                                                    .areSoundsEnabled()) {

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
                ((Main) plugin)
                        .getPlayerDataRepository()
                        .getOrCreatePlayer(
                                player.getUniqueId()
                        );

        if (data != null) {

            data.incrementVotes();

            ((Main) plugin)
                    .getPlayerDataRepository()
                    .savePlayer(data);
        }

        updateBossBar(poll);

        updateActionBars(poll);

        player.sendMessage(
                Component.text(
                        "[SleepPoll] ",
                        Style.style(
                                NamedTextColor.GOLD,
                                TextDecoration.BOLD
                        )
                ).append(
                        Component.text(
                                "Vote recorded: ",
                                NamedTextColor.GRAY
                        )
                ).append(
                        Component.text(
                                accept
                                        ? "YES"
                                        : "NO",
                                Style.style(
                                        accept
                                                ? NamedTextColor.GREEN
                                                : NamedTextColor.RED,
                                        TextDecoration.BOLD
                                )
                        )
                )
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
                        ((Main) plugin)
                                .getPlayerDataRepository()
                                .getOrCreatePlayer(id);

                if (data == null)
                    continue;

                data.incrementSuccessfulVotes();

                data.incrementNightsSkipped();

                ((Main) plugin)
                        .getPlayerDataRepository()
                        .savePlayer(data);
            }

        } else {

            for (UUID id :
                    poll.getYesVotes()) {

                PlayerData data =
                        ((Main) plugin)
                                .getPlayerDataRepository()
                                .getOrCreatePlayer(id);

                if (data == null)
                    continue;

                data.incrementFailedVotes();

                ((Main) plugin)
                        .getPlayerDataRepository()
                        .savePlayer(data);
            }
        }

        Bukkit.getScheduler().runTask(plugin, () -> {

            Component msg =
                    succeeded
                            ? Component.text(
                            "Sleep poll succeeded! ",
                            Style.style(
                                    NamedTextColor.GREEN,
                                    TextDecoration.BOLD
                            )
                    ).append(
                            Component.text(
                                    "Skipping to day...",
                                    NamedTextColor.GRAY
                            )
                    )
                            : Component.text(
                            "Sleep poll ended. ",
                            Style.style(
                                    NamedTextColor.RED,
                                    TextDecoration.BOLD
                            )
                    ).append(
                            Component.text(
                                    "Not enough yes votes.",
                                    NamedTextColor.GRAY
                            )
                    );

            for (UUID id :
                    poll.getEligible()) {

                Player p =
                        Bukkit.getPlayer(id);

                if (p == null)
                    continue;

                p.sendMessage(
                        Component.text(
                                "[SleepPoll] ",
                                Style.style(
                                        NamedTextColor.GOLD,
                                        TextDecoration.BOLD
                                )
                        ).append(msg)
                );

                if (((Main) plugin)
                        .areSoundsEnabled()) {

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

                if (Bukkit.getWorld(
                        poll.getWorld()
                ) != null) {

                    Bukkit.getWorld(
                            poll.getWorld()
                    ).setTime(1000L);
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

        bossBar.setTitle(
                "§6🌙 Sleep Poll §8• §e"
                        + poll.getRemainingSeconds()
                        + "s §7remaining §8| §aYES "
                        + poll.getYesVotes().size()
                        + "/"
                        + poll.getNeededVotes()
        );

        double progress =
                poll.getRemainingSeconds()
                        / (double) ((Main) plugin)
                        .getPollDurationSeconds();

        bossBar.setProgress(
                Math.max(
                        0.0,
                        Math.min(
                                1.0,
                                progress
                        )
                )
        );
    }

    private void updateActionBars(
            Poll poll
    ) {

        Component actionBar =
                Component.text(
                        "🌙 Sleep Poll ",
                        NamedTextColor.GOLD
                ).append(
                        Component.text(
                                "• ",
                                NamedTextColor.DARK_GRAY
                        )
                ).append(
                        Component.text(
                                "YES ",
                                NamedTextColor.GREEN
                        )
                ).append(
                        Component.text(
                                poll.getYesVotes().size()
                                        + "/"
                                        + poll.getNeededVotes(),
                                NamedTextColor.AQUA
                        )
                ).append(
                        Component.text(
                                " • ",
                                NamedTextColor.DARK_GRAY
                        )
                ).append(
                        Component.text(
                                poll.getRemainingSeconds()
                                        + "s",
                                poll.getRemainingSeconds()
                                        <= 5
                                                ? NamedTextColor.RED
                                                : NamedTextColor.YELLOW
                        )
                );

        for (UUID id :
                poll.getEligible()) {

            Player p =
                    Bukkit.getPlayer(id);

            if (p == null)
                continue;

            p.sendActionBar(actionBar);
        }
    }
}