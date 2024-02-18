package com.objecteffects.pinloc.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.entity.Player;
import org.h2.jdbcx.JdbcConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class H2Db {
	private final static Logger log = LoggerFactory.getLogger("PinLoc");

	private static JdbcConnectionPool cp = null;
	private final static String db = "jdbc:h2:";

	public void dbSetup(File dataFolder, String dbName) {
		String fullDbName = dbName(dataFolder, dbName);

		fullDbName = fullDbName.concat(";AUTO_RECONNECT=TRUE");

		cp = JdbcConnectionPool.create(fullDbName, "sa", "sa");

		createMCUsersTable();
		createLocationsTable();

		log.info("database: {} opened", fullDbName);
	}

	private void createLocationsTable() {
		Connection conn = getConnection();

		Statement stat;
		try {
			stat = conn.createStatement();
			stat.execute("""
					create table if not exists locations
					(id bigint generated always as identity primary key,
					location geometry(pointz) not null,
					mcuser bigint not null,
					moment timestamp default current_timestamp not null,
					foreign key(mcuser) references mcusers(id));""");

			log.info("created locations table");
		} catch (SQLException e) {
			log.error("database create table locations failed: {}", e);
		}
	}

	private void createMCUsersTable() {
		Connection conn = getConnection();

		Statement stat;
		try {
			stat = conn.createStatement();
			stat.execute("""
					create table if not exists mcusers
					(id bigint generated always as identity primary key,
					mcuser varchar(64) not null);""");

			log.info("created mcsers table");
		} catch (SQLException e) {
			log.error("database create table mcusers failed: {}", e);
		}
	}

	public void GetLocations(Player player) {
		Connection conn = getConnection();
	}

	public void shutdown() {
		Connection conn = getConnection();

		try {
			Statement stat = conn.createStatement();
			stat.execute("SHUTDOWN");
			stat.close();
		} catch (SQLException e) {
			log.error("database shutdown failed: {}", e);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			log.error("database connect close failed: {}", e);
		}
	}

	private static Connection getConnection() {
		try {
			return cp.getConnection();
		} catch (SQLException e) {
			log.error("database connect failed: {}", e);

			throw new RuntimeException(e);
		}
	}

	private static String dbName(File dataFolder, String dbName) {
		log.info("data folder name: {}", dataFolder.getName());

		String path;

		try {
			path = dataFolder.getCanonicalPath();
		} catch (IOException e) {
			log.error("error getting data folder canonical path: {}", e);

			throw new RuntimeException(e);
		}

		log.info("data folder path: {}", path);

		if (!dataFolder.exists()) {
			log.info("creating data folder: {}", path);

			if (!dataFolder.mkdir()) {
				log.error("unable to create data folder");
			}
		}

		return db + path + File.separator + dbName;
	}
}
