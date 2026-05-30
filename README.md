# 🌙 SleepPolls

<p align="center">
  <img src="assets/logo.png" alt="SleepPolls Logo" width="220">
</p>

<h1 align="center">SleepPolls</h1>

<p align="center">
  Modern sleep voting for Paper servers.
</p>

<p align="center">
  <a href="https://modrinth.com/plugin/sleep-polls">
    <img src="https://img.shields.io/modrinth/dt/sleep-polls?style=for-the-badge&logo=modrinth&label=Downloads&color=00AF5C">
  </a>
  <img src="https://img.shields.io/badge/Platform-Paper-blue?style=for-the-badge">
  <img src="https://img.shields.io/badge/Database-SQLite%20%7C%20MariaDB-green?style=for-the-badge">
  <img src="https://img.shields.io/badge/License-GPL%20v3-red?style=for-the-badge">
</p>

---

## 🚀 Why SleepPolls?

Most sleep voting plugins stop at basic vote counting.

SleepPolls provides a modern, production-ready sleep voting experience with interactive chat components, live vote tracking, persistent statistics, leaderboards, database support, and AFK-aware voting.

### ✨ Highlights

- 🌙 Interactive sleep voting
- 📊 Live BossBar progress tracking
- 💬 ActionBar countdown updates
- 🖱️ Clickable YES / NO vote buttons
- 😴 EssentialsX AFK detection
- 🏆 Statistics & leaderboards
- 💾 SQLite & MariaDB support
- 🌍 World blacklist support
- 🔊 Optional sound effects
- ⚡ Async database operations
- 🚀 HikariCP connection pooling
- 🔓 Open source

---

# 🎯 Overview

When a player enters a bed during the night, SleepPolls automatically creates a vote.

Eligible players can vote using clickable chat buttons or commands.

If enough YES votes are received before the timer expires, the night is skipped.

Unlike traditional sleep plugins, SleepPolls only counts active eligible players, creating a much fairer experience for SMPs and community servers.

---

# ✨ Features

## 🌙 Sleep Voting

- Automatic sleep vote creation
- Configurable vote duration
- Configurable vote requirements
- One active poll per world
- Real-time vote tracking

## 🖱️ Interactive Voting

Players receive clickable buttons directly in chat:

```text
✔ YES    ✖ NO
```

No GUI required.

---

## 📊 BossBar Tracking

Optional BossBar showing:

- Remaining time
- Current YES votes
- Required votes

Players can disable it individually:

```text
/sp bossbar
```

---

## 💬 ActionBar Updates

Live updates every second:

```text
🌙 Sleep Poll • YES 2/3 • 12s
```

---

## 😴 AFK Detection

If EssentialsX is installed:

- AFK players are excluded
- Only active players count toward vote requirements

This prevents inactive players from blocking night skips.

---

## 📈 Statistics System

SleepPolls tracks:

- Total Votes
- Successful Votes
- Failed Votes
- Polls Started
- Nights Skipped

View your statistics:

```text
/sp stats
```

---

## 🏆 Leaderboards

View the most active sleepers:

```text
/sp top
```

Perfect for competitive SMP communities.

---

## 🌧️ Weather Control

After a successful vote:

- Rain can be cleared
- Thunder can be cleared

Fully configurable.

---

## 🌍 World Blacklist

Disable sleep voting in specific worlds.

Example:

```yaml
worlds:
  blacklist:
    - lobby
    - minigames
```

---

# 📦 Installation

## Requirements

- Paper
- Java 25+
- Minecraft 26.1+

Optional:

- EssentialsX (AFK detection)

## Installation

1. Download the latest release.
2. Place the jar in your plugins folder.
3. Start the server.
4. Configure `config.yml`.
5. Run:

```text
/sp reload
```

or restart the server.

---

# 🎮 Commands

| Command | Description |
|----------|-------------|
| `/sp help` | Display help menu |
| `/sp version` | Show plugin version |
| `/sp status` | View current poll |
| `/sp yes` | Vote YES |
| `/sp no` | Vote NO |
| `/sp stats` | View personal statistics |
| `/sp top` | View leaderboard |
| `/sp bossbar` | Toggle BossBar |
| `/sp reload` | Reload configuration |

Aliases:

```text
/sp
/sleeppoll
```

---

# 🔐 Permissions

| Permission | Description |
|------------|-------------|
| `sleeppolls.reload` | Allows configuration reload |
| `sleeppolls.bossbar` | Allows BossBar toggling |

All voting commands are available to regular players.

---

# ⚙️ Configuration

## Poll Settings

```yaml
poll-duration-seconds: 20
required-percentage: 50
```

Example:

- 2 eligible players → 1 YES required
- 3 eligible players → 2 YES required
- 5 eligible players → 3 YES required

---

## BossBar

```yaml
bossbar:
  enabled: true
```

---

## Sounds

```yaml
sounds:
  enabled: true
```

---

## Weather

```yaml
weather:
  clear-rain: true
  clear-thunder: true
```

---

## Database

### SQLite

```yaml
database:
  type: SQLITE
```

### MariaDB

```yaml
database:
  type: MARIADB

  mariadb:
    host: localhost
    port: 3306
    database: sleeppolls
    username: root
    password: password
    pool-size: 5
```

---

# 🔄 How It Works

1. Player enters a bed.
2. SleepPolls calculates eligible voters.
3. Vote starts.
4. Players vote YES or NO.
5. Live BossBar and ActionBar updates are shown.
6. Poll succeeds or fails.
7. Statistics are updated and stored.

---

# 🛠️ Technical Details

Built using:

- Java 25+
- Paper API
- Adventure API
- Lamp Command Framework
- HikariCP
- SQLite
- MariaDB

### Design Goals

- Lightweight
- Async-first
- Modern Paper APIs
- No ProtocolLib dependency
- No NMS
- Production ready

---

# 🗺️ Roadmap

- [ ] Velocity network-wide sleep voting
- [ ] PlaceholderAPI support
- [ ] Localization support
- [ ] Customizable messages
- [ ] GUI voting interface
- [ ] Web statistics dashboard
- [ ] Per-world vote settings

---

# 🤝 Contributing

Contributions are welcome.

1. Fork the repository
2. Create a branch
3. Commit your changes
4. Open a Pull Request

---

# 📜 License

SleepPolls is licensed under the GNU General Public License v3.0.

See the LICENSE file for details.

---

# ❤️ Credits

Developed and maintained by **Amethyst Developers**.

Built using:

- Paper
- Adventure
- Lamp
- HikariCP

Made for modern Minecraft servers.