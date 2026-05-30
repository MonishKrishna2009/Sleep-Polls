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

package org.amethystdev.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DatabaseInitializer {

    private static final int LATEST_SCHEMA_VERSION = 1;

    private final DatabaseManager databaseManager;

    public DatabaseInitializer(
            DatabaseManager databaseManager
    ) {

        this.databaseManager = databaseManager;
    }

    public void initialize() {

        createMetaTable();

        runMigrations();
    }

    private void createMetaTable() {

        String sql = """
                CREATE TABLE IF NOT EXISTS database_meta (

                    meta_key VARCHAR(255) PRIMARY KEY,

                    meta_value VARCHAR(255) NOT NULL
                );
                """;

        try (

                Connection connection =
                        databaseManager.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)

        ) {

            statement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    private void runMigrations() {

        int currentVersion =
                getSchemaVersion();

        while (currentVersion
                < LATEST_SCHEMA_VERSION) {

            int nextVersion =
                    currentVersion + 1;

            migrate(nextVersion);

            setSchemaVersion(nextVersion);

            currentVersion = nextVersion;
        }
    }

    private void migrate(
            int version
    ) {

        switch (version) {

            case 1 -> migrationV1();

            default -> throw new IllegalStateException(
                    "Unknown schema version: "
                            + version
            );
        }
    }

    /*
     * Initial schema
     */
    private void migrationV1() {

        String sql = """
                CREATE TABLE IF NOT EXISTS player_data (

                    uuid VARCHAR(36) PRIMARY KEY,

                    bossbar_enabled INTEGER NOT NULL,

                    total_votes INTEGER NOT NULL,

                    successful_votes INTEGER NOT NULL,

                    failed_votes INTEGER NOT NULL,

                    polls_started INTEGER NOT NULL,

                    nights_skipped INTEGER NOT NULL
                );
                """;

        try (

                Connection connection =
                        databaseManager.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)

        ) {

            statement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    private int getSchemaVersion() {

        String sql = """
                SELECT meta_value
                FROM database_meta
                WHERE meta_key = 'schema_version';
                """;

        try (

                Connection connection =
                        databaseManager.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()

        ) {

            if (!result.next()) {
                return 0;
            }

            return Integer.parseInt(
                    result.getString(
                            "meta_value"
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();
        }

        return 0;
    }

    private void setSchemaVersion(
            int version
    ) {

        String sql = """
                INSERT INTO database_meta (

                    meta_key,
                    meta_value

                ) VALUES (?, ?);
                """;

        try (

                Connection connection =
                        databaseManager.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)

        ) {

            statement.setString(
                    1,
                    "schema_version"
            );

            statement.setString(
                    2,
                    String.valueOf(version)
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            // SQLite duplicate protection
            updateSchemaVersion(version);
        }
    }

    private void updateSchemaVersion(
            int version
    ) {

        String sql = """
                UPDATE database_meta
                SET meta_value = ?
                WHERE meta_key = 'schema_version';
                """;

        try (

                Connection connection =
                        databaseManager.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)

        ) {

            statement.setString(
                    1,
                    String.valueOf(version)
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }
}