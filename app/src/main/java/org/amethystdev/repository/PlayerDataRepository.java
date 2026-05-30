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

package org.amethystdev.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.amethystdev.database.DatabaseExecutor;
import org.amethystdev.database.DatabaseManager;
import org.amethystdev.database.DatabaseRetry;

import org.amethystdev.model.PlayerData;

public final class PlayerDataRepository {

    private final DatabaseManager databaseManager;

    /*
     * Runtime player cache
     */
    private final Map<UUID, PlayerData> cache =
            new ConcurrentHashMap<>();

    public PlayerDataRepository(
            DatabaseManager databaseManager
    ) {

        this.databaseManager = databaseManager;
    }

    /*
     * Lazy-loaded cache access
     */
    public PlayerData getOrCreatePlayer(
            UUID uuid
    ) {

        // Cache hit
        PlayerData cached =
                cache.get(uuid);

        if (cached != null) {
            return cached;
        }

        // Database lookup
        PlayerData existing =
                getPlayer(uuid);

        if (existing != null) {

            cache.put(
                    uuid,
                    existing
            );

            return existing;
        }

        // Create player if missing
        createPlayer(uuid);

        PlayerData created =
                getPlayer(uuid);

        if (created != null) {

            cache.put(
                    uuid,
                    created
            );
        }

        return created;
    }

    /*
     * Async cache access
     */
    public CompletableFuture<PlayerData> getOrCreatePlayerAsync(
            UUID uuid
    ) {

        return CompletableFuture.supplyAsync(
                () -> getOrCreatePlayer(uuid),
                DatabaseExecutor.getExecutor()
        );
    }

    /*
     * Direct DB fetch
     */
    public PlayerData getPlayer(
            UUID uuid
    ) {

        String sql = """
                SELECT *
                FROM player_data
                WHERE uuid = ?;
                """;

        try (

                Connection connection =
                        databaseManager.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)

        ) {

            if (connection == null) {
                return null;
            }

            statement.setQueryTimeout(10);

            statement.setString(
                    1,
                    uuid.toString()
            );

            try (

                    ResultSet result =
                            statement.executeQuery()

            ) {

                if (!result.next()) {
                    return null;
                }

                PlayerData data =
                        mapPlayer(result);

                // Cache loaded player
                cache.put(
                        uuid,
                        data
                );

                return data;
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }

    /*
     * Async DB fetch
     */
    public CompletableFuture<PlayerData> getPlayerAsync(
            UUID uuid
    ) {

        return CompletableFuture.supplyAsync(
                () -> getPlayer(uuid),
                DatabaseExecutor.getExecutor()
        );
    }

    /*
     * Create new player row
     */
    public void createPlayer(
            UUID uuid
    ) {

        String sql = """
                INSERT INTO player_data (

                    uuid,
                    bossbar_enabled,
                    total_votes,
                    successful_votes,
                    failed_votes,
                    polls_started,
                    nights_skipped

                ) VALUES (?, ?, ?, ?, ?, ?, ?);
                """;

        DatabaseRetry.runWithRetry(() -> {

            try (

                    Connection connection =
                            databaseManager.getConnection();

                    PreparedStatement statement =
                            connection.prepareStatement(sql)

            ) {

                if (connection == null) {
                    return;
                }

                statement.setQueryTimeout(10);

                statement.setString(
                        1,
                        uuid.toString()
                );

                statement.setInt(
                        2,
                        1
                );

                statement.setInt(
                        3,
                        0
                );

                statement.setInt(
                        4,
                        0
                );

                statement.setInt(
                        5,
                        0
                );

                statement.setInt(
                        6,
                        0
                );

                statement.setInt(
                        7,
                        0
                );

                statement.executeUpdate();

            } catch (SQLException e) {

                throw new RuntimeException(e);
            }
        });
    }

    /*
     * Async write-through save
     */
    public void savePlayer(
            PlayerData data
    ) {

        // Update cache immediately
        cache.put(
                data.getUuid(),
                data
        );

        DatabaseExecutor.executeAsync(() -> {

            DatabaseRetry.runWithRetry(() -> {

                String sql = """
                        UPDATE player_data
                        SET

                            bossbar_enabled = ?,
                            total_votes = ?,
                            successful_votes = ?,
                            failed_votes = ?,
                            polls_started = ?,
                            nights_skipped = ?

                        WHERE uuid = ?;
                        """;

                try (

                        Connection connection =
                                databaseManager.getConnection();

                        PreparedStatement statement =
                                connection.prepareStatement(sql)

                ) {

                    if (connection == null) {
                        return;
                    }

                    statement.setQueryTimeout(10);

                    statement.setInt(
                            1,
                            data.isBossBarEnabled()
                                    ? 1
                                    : 0
                    );

                    statement.setInt(
                            2,
                            data.getTotalVotes()
                    );

                    statement.setInt(
                            3,
                            data.getSuccessfulVotes()
                    );

                    statement.setInt(
                            4,
                            data.getFailedVotes()
                    );

                    statement.setInt(
                            5,
                            data.getPollsStarted()
                    );

                    statement.setInt(
                            6,
                            data.getNightsSkipped()
                    );

                    statement.setString(
                            7,
                            data.getUuid().toString()
                    );

                    statement.executeUpdate();

                } catch (SQLException e) {

                    throw new RuntimeException(e);
                }
            });
        });
    }

    /*
     * Leaderboard query
     */
    public List<PlayerData> getTopPlayers(
            int limit
    ) {

        List<PlayerData> players =
                new ArrayList<>();

        String sql = """
                SELECT *
                FROM player_data
                ORDER BY successful_votes DESC
                LIMIT ?;
                """;

        try (

                Connection connection =
                        databaseManager.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)

        ) {

            if (connection == null) {
                return players;
            }

            statement.setQueryTimeout(15);

            statement.setInt(
                    1,
                    limit
            );

            try (

                    ResultSet result =
                            statement.executeQuery()

            ) {

                while (result.next()) {

                    PlayerData data =
                            mapPlayer(result);

                    players.add(data);

                    // Refresh cache
                    cache.put(
                            data.getUuid(),
                            data
                    );
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return players;
    }

    /*
     * Async leaderboard query
     */
    public CompletableFuture<List<PlayerData>> getTopPlayersAsync(
            int limit
    ) {

        return CompletableFuture.supplyAsync(
                () -> getTopPlayers(limit),
                DatabaseExecutor.getExecutor()
        );
    }

    /*
     * Shared ResultSet mapper
     */
    private PlayerData mapPlayer(
            ResultSet result
    ) throws SQLException {

        return new PlayerData(
                UUID.fromString(
                        result.getString("uuid")
                ),
                result.getInt(
                        "bossbar_enabled"
                ) == 1,
                result.getInt(
                        "total_votes"
                ),
                result.getInt(
                        "successful_votes"
                ),
                result.getInt(
                        "failed_votes"
                ),
                result.getInt(
                        "polls_started"
                ),
                result.getInt(
                        "nights_skipped"
                )
        );
    }

    /*
     * Cache invalidation
     */
    public void invalidatePlayer(
            UUID uuid
    ) {

        cache.remove(uuid);
    }

    /*
     * Full cache clear
     */
    public void clearCache() {

        cache.clear();
    }

    /*
     * Cache size
     */
    public int getCachedPlayerCount() {

        return cache.size();
    }
}