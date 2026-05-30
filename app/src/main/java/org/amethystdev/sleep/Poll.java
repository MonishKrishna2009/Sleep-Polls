package org.amethystdev.sleep;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class Poll {

    private final String world;

    private final Set<UUID> eligible =
            new HashSet<>();

    private final Set<UUID> yesVotes =
            new HashSet<>();

    private final Set<UUID> noVotes =
            new HashSet<>();

    private final long createdAt;

    private PollState state =
            PollState.ACTIVE;

    private int remainingSeconds;

    private final int requiredPercentage;

    public Poll(
            String world,
            Set<UUID> eligible,
            int durationSeconds,
            int requiredPercentage
    ) {

        this.world = world;

        this.eligible.addAll(
                eligible
        );

        this.remainingSeconds =
                durationSeconds;

        this.requiredPercentage =
                requiredPercentage;

        this.createdAt =
                System.currentTimeMillis();
    }

    public synchronized void vote(
            UUID uuid,
            boolean yes
    ) {

        yesVotes.remove(uuid);

        noVotes.remove(uuid);

        if (yes) {

            yesVotes.add(uuid);

        } else {

            noVotes.add(uuid);
        }
    }

    public synchronized boolean hasPassed() {

        return yesVotes.size()
                >= getNeededVotes();
    }

    public synchronized boolean canStillPass() {

        int remaining =
                eligible.size()
                        - (
                        yesVotes.size()
                                + noVotes.size()
                );

        return yesVotes.size()
                + remaining
                >= getNeededVotes();
    }

    public int getNeededVotes() {

        return (int) Math.ceil(
                eligible.size()
                        * (
                        requiredPercentage
                                / 100.0
                )
        );
    }

    public void decrementTimer() {

        remainingSeconds--;
    }

    public String getWorld() {

        return world;
    }

    public Set<UUID> getEligible() {

        return eligible;
    }

    public Set<UUID> getYesVotes() {

        return yesVotes;
    }

    public Set<UUID> getNoVotes() {

        return noVotes;
    }

    public long getCreatedAt() {

        return createdAt;
    }

    public PollState getState() {

        return state;
    }

    public void setState(
            PollState state
    ) {

        this.state = state;
    }

    public int getRemainingSeconds() {

        return remainingSeconds;
    }

    public void setRemainingSeconds(
            int remainingSeconds
    ) {

        this.remainingSeconds =
                remainingSeconds;
    }

    public int getRequiredPercentage() {

        return requiredPercentage;
    }

    public int getYesVoteCount() {

        return yesVotes.size();
    }

    public int getNoVoteCount() {

        return noVotes.size();
    }

    public int getEligibleCount() {

        return eligible.size();
    }

    public boolean hasVoted(
            UUID uuid
    ) {

        return yesVotes.contains(uuid)
                || noVotes.contains(uuid);
    }

    public boolean votedYes(
            UUID uuid
    ) {

        return yesVotes.contains(uuid);
    }

    public boolean votedNo(
            UUID uuid
    ) {

        return noVotes.contains(uuid);
    }

    public int getRemainingPossibleVotes() {

        return eligible.size()
                - (
                yesVotes.size()
                        + noVotes.size()
        );
    }

    public double getYesPercentage() {

        if (eligible.isEmpty()) {
            return 0;
        }

        return (
                yesVotes.size()
                        / (double) eligible.size()
        ) * 100.0;
    }

    public boolean isExpired() {

        return remainingSeconds <= 0;
    }

    public boolean isActive() {

        return state
                == PollState.ACTIVE;
    }
}