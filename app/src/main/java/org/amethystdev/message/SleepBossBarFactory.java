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

package org.amethystdev.message;

import org.amethystdev.Main;
import org.amethystdev.sleep.Poll;
import org.bukkit.Bukkit;

public final class SleepBossBarFactory {

    private SleepBossBarFactory() {
    }

    public static String createTitle(
            Poll poll
    ) {

        return "§6🌙 Sleep Poll §8• §e"
                + poll.getRemainingSeconds()
                + "s §7remaining §8| §aYES "
                + poll.getYesVoteCount()
                + "/"
                + poll.getNeededVotes();
    }

    public static double createProgress(
            Poll poll
    ) {

        try {
            var method = poll.getClass().getMethod("getDurationSeconds");
            Object value = method.invoke(poll);
            if (value instanceof Number) {
                double duration = ((Number) value).doubleValue();
                if (duration <= 0.0) {
                    return 0.0;
                }
                double progress = poll.getRemainingSeconds() / duration;
                return Math.max(0.0, Math.min(1.0, progress));
            }
        } catch (ReflectiveOperationException ignored) {
        }

        try {
            var plugin = Bukkit.getPluginManager().getPlugin("Sleep-Polls");
            if (plugin instanceof Main) {
                double duration = ((Main) plugin).getPollDurationSeconds();
                if (duration <= 0.0) {
                    return 0.0;
                }
                double progress = poll.getRemainingSeconds() / duration;
                return Math.max(0.0, Math.min(1.0, progress));
            }
        } catch (Throwable ignored) {
        }

        double progress = poll.getRemainingSeconds() / 20.0;
        return Math.max(0.0, Math.min(1.0, progress));
    }
}