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

import java.io.File;

import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class SQLiteDatabaseManager
        implements DatabaseManager {

    private final Plugin plugin;

    private HikariDataSource dataSource;

    public SQLiteDatabaseManager(
            Plugin plugin
    ) {

        this.plugin = plugin;
    }

    @Override
    public synchronized void connect() {

        if (isConnected()) {
            return;
        }

        try {

            FileConfiguration config =
                    plugin.getConfig();

            String fileName =
                    config.getString(
                            "database.sqlite.file",
                            "data.db"
                    );

            File dbFile =
                    new File(
                            plugin.getDataFolder(),
                            fileName
                    );

            if (!dbFile.exists()) {

                dbFile.getParentFile()
                        .mkdirs();

                dbFile.createNewFile();
            }

            String jdbcUrl =
                    "jdbc:sqlite:"
                            + dbFile.getAbsolutePath();

            HikariConfig hikariConfig =
                    new HikariConfig();

            hikariConfig.setJdbcUrl(
                    jdbcUrl
            );

            hikariConfig.setPoolName(
                    "SleepPolls-SQLite"
            );

            hikariConfig.setMaximumPoolSize(
                    10
            );

            hikariConfig.setMinimumIdle(
                    1
            );

            hikariConfig.setConnectionTimeout(
                    10000
            );

            hikariConfig.setValidationTimeout(
                    5000
            );

            hikariConfig.setIdleTimeout(
                    600000
            );

            hikariConfig.setMaxLifetime(
                    1800000
            );

            hikariConfig.setKeepaliveTime(
                    30000
            );

            hikariConfig.setLeakDetectionThreshold(
                    15000
            );

            hikariConfig.setConnectionTestQuery(
                    "SELECT 1"
            );

            hikariConfig.setAutoCommit(
                    true
            );

            hikariConfig.setDriverClassName(
                    "org.sqlite.JDBC"
            );

            this.dataSource =
                    new HikariDataSource(
                            hikariConfig
                    );

            plugin.getLogger().info(
                    "Connected to SQLite database."
            );

        } catch (Exception e) {

            plugin.getLogger().severe(
                    "Failed to connect to SQLite database."
            );

            e.printStackTrace();
        }
    }

    @Override
    public synchronized void disconnect() {

        if (dataSource == null) {
            return;
        }

        try {

            dataSource.close();

        } catch (Exception e) {

            e.printStackTrace();
        }

        dataSource = null;

        plugin.getLogger().info(
                "Disconnected from SQLite database."
        );
    }

    @Override
    public Connection getConnection() {

        try {

            if (!isConnected()) {

                connect();
            }

            Connection connection =
                    dataSource.getConnection();

            if (!connection.isValid(5)) {

                connection.close();

                disconnect();

                connect();

                return dataSource.getConnection();
            }

            return connection;

        } catch (SQLException e) {

            plugin.getLogger().severe(
                    "Failed to retrieve SQLite connection."
            );

            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean isConnected() {

        return dataSource != null
                && !dataSource.isClosed();
    }
}