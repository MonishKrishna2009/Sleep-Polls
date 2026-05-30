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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

public final class SleepMessageFactory {

    private SleepMessageFactory() {
    }

    public static Component createPrefix() {
        return Component.text(
                "[SleepPoll] ",
                Style.style(
                        NamedTextColor.GOLD,
                        TextDecoration.BOLD
                )
        );
    }

    public static Component createPollDivider() {
        return Component.text(
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                Style.style(
                        NamedTextColor.DARK_GRAY,
                        TextDecoration.STRIKETHROUGH
                )
        );
    }

    public static Component createYesButton() {
        return Component.text(
                "[✔ YES]",
                Style.style(
                        NamedTextColor.GREEN,
                        TextDecoration.BOLD
                )
        ).clickEvent(
                ClickEvent.runCommand("/sp yes")
        ).hoverEvent(
                HoverEvent.showText(
                        Component.text("Vote YES", NamedTextColor.GREEN)
                )
        );
    }

    public static Component createNoButton() {
        return Component.text(
                "[✖ NO]",
                Style.style(
                        NamedTextColor.RED,
                        TextDecoration.BOLD
                )
        ).clickEvent(
                ClickEvent.runCommand("/sp no")
        ).hoverEvent(
                HoverEvent.showText(
                        Component.text("Vote NO", NamedTextColor.RED)
                )
        );
    }

    public static Component createPollStartedMessage() {
        return Component.text(
                "A player is sleeping!",
                NamedTextColor.YELLOW
        );
    }

    public static Component createPollVoteOptionsMessage(int remainingSeconds, Component yesButton, Component noButton) {
        return Component.text(
                "Vote within ",
                NamedTextColor.GRAY
        ).append(
                Component.text(
                        remainingSeconds + " seconds",
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
        );
    }

    public static Component createVotesNeededMessage(int needed, int eligible) {
        return Component.text(
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
                        String.valueOf(eligible),
                        NamedTextColor.AQUA
                )
        );
    }

    public static Component createVoteRecordedMessage(boolean accepted) {
        return Component.text(
                "Vote recorded: ",
                NamedTextColor.GRAY
        ).append(
                Component.text(
                        accepted ? "YES" : "NO",
                        Style.style(
                                accepted ? NamedTextColor.GREEN : NamedTextColor.RED,
                                TextDecoration.BOLD
                        )
                )
        );
    }

    public static Component createPollSuccessMessage() {
        return Component.text(
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
        );
    }

    public static Component createPollFailedMessage() {
        return Component.text(
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
    }
}
