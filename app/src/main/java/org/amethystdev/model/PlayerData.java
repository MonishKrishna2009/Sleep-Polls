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

package org.amethystdev.model;

import java.util.UUID;

public final class PlayerData {

    private final UUID uuid;

    private boolean bossBarEnabled;

    private int totalVotes;

    private int successfulVotes;

    private int failedVotes;

    private int pollsStarted;

    private int nightsSkipped;

    public PlayerData(
            UUID uuid,
            boolean bossBarEnabled,
            int totalVotes,
            int successfulVotes,
            int failedVotes,
            int pollsStarted,
            int nightsSkipped
    ) {

        this.uuid = uuid;

        this.bossBarEnabled = bossBarEnabled;

        this.totalVotes = totalVotes;

        this.successfulVotes = successfulVotes;

        this.failedVotes = failedVotes;

        this.pollsStarted = pollsStarted;

        this.nightsSkipped = nightsSkipped;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isBossBarEnabled() {
        return bossBarEnabled;
    }

    public void setBossBarEnabled(
            boolean bossBarEnabled
    ) {

        this.bossBarEnabled = bossBarEnabled;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(
            int totalVotes
    ) {

        this.totalVotes = totalVotes;
    }

    public int getSuccessfulVotes() {
        return successfulVotes;
    }

    public void setSuccessfulVotes(
            int successfulVotes
    ) {

        this.successfulVotes = successfulVotes;
    }

    public int getFailedVotes() {
        return failedVotes;
    }

    public void setFailedVotes(
            int failedVotes
    ) {

        this.failedVotes = failedVotes;
    }

    public int getPollsStarted() {
        return pollsStarted;
    }

    public void setPollsStarted(
            int pollsStarted
    ) {

        this.pollsStarted = pollsStarted;
    }

    public int getNightsSkipped() {
        return nightsSkipped;
    }

    public void setNightsSkipped(
            int nightsSkipped
    ) {

        this.nightsSkipped = nightsSkipped;
    }

    public void incrementVotes() {
        this.totalVotes++;
    }

    public void incrementSuccessfulVotes() {
        this.successfulVotes++;
    }

    public void incrementFailedVotes() {
        this.failedVotes++;
    }

    public void incrementPollsStarted() {
        this.pollsStarted++;
    }

    public void incrementNightsSkipped() {
        this.nightsSkipped++;
    }
}