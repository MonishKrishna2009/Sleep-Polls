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

import org.amethystdev.sleep.Poll;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class ActionBarFactory {

    private ActionBarFactory() {
    }

    public static Component createPollActionBar(Poll poll) {
        return Component.text(
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
                        poll.getYesVoteCount()
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
    }
}
