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
import java.sql.SQLException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class MariaDBDatabaseManager
        implements DatabaseManager {

    private final Plugin plugin;

    private HikariDataSource dataSource;

    public MariaDBDatabaseManager(
            Plugin plugin
    ) {

        this.plugin = plugin;
    }

    @Override
    public synchronized void connect() {

        if (isConnected()) {
            return;
        }

        FileConfiguration config =
                plugin.getConfig();

        String host =
                config.getString(
                        "database.mariadb.host",
                        "localhost"
                );

        int port =
                config.getInt(
                        "database.mariadb.port",
                        3306
                );

        String database =
                config.getString(
                        "database.mariadb.database",
                        "sleeppolls"
                );

        String username =
                config.getString(
                        "database.mariadb.username",
                        "root"
                );

        String password =
                config.getString(
                        "database.mariadb.password",
                        "password"
                );

        int poolSize =
                config.getInt(
                        "database.mariadb.pool-size",
                        5
                );

        String jdbcUrl =
                "jdbc:mariadb://"
                        + host
                        + ":"
                        + port
                        + "/"
                        + database;

        try {

            HikariConfig hikariConfig =
                    new HikariConfig();

            hikariConfig.setJdbcUrl(
                    jdbcUrl
            );

            hikariConfig.setUsername(
                    username
            );

            hikariConfig.setPassword(
                    password
            );

            hikariConfig.setMaximumPoolSize(
                    poolSize
            );

            hikariConfig.setMinimumIdle(
                    1
            );

            hikariConfig.setPoolName(
                    "SleepPolls-Hikari"
            );

            hikariConfig.setAutoCommit(
                    true
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
                    0
            );

            hikariConfig.setDriverClassName(
                    "org.mariadb.jdbc.Driver"
            );

            this.dataSource =
                    new HikariDataSource(
                            hikariConfig
                    );

            plugin.getLogger().info(
                    "Connected to MariaDB database."
            );

        } catch (Exception e) {

            plugin.getLogger().severe(
                    "Failed to connect to MariaDB."
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
                "Disconnected from MariaDB."
        );
    }

    @Override
    public Connection getConnection()
            throws SQLException {

        if (!isConnected()) {

            connect();
        }

        if (!isConnected()) {

            throw new SQLException(
                    "MariaDB database is not connected."
            );
        }

        Connection connection =
                dataSource.getConnection();

        if (!connection.isValid(5)) {

            connection.close();

            disconnect();

            connect();

            if (!isConnected()) {

                throw new SQLException(
                        "Failed to reconnect to MariaDB."
                );
            }

            return dataSource.getConnection();
        }

        return connection;
    }

    @Override
    public boolean isConnected() {

        return dataSource != null
                && !dataSource.isClosed();
    }
}