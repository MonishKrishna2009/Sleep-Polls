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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class DatabaseExecutor {

    /*
     * Dedicated database thread pool
     */
    private static final ExecutorService EXECUTOR =
            Executors.newFixedThreadPool(2);

    private DatabaseExecutor() {}

    /*
     * Execute async task
     */
    public static void executeAsync(
            Runnable runnable
    ) {

        EXECUTOR.execute(runnable);
    }

    /*
     * Access executor for CompletableFuture
     */
    public static ExecutorService getExecutor() {

        return EXECUTOR;
    }

    /*
     * Shutdown executor safely
     */
    public static void shutdown() {

        EXECUTOR.shutdown();
    }
}