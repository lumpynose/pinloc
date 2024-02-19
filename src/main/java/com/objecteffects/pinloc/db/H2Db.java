package com.objecteffects.pinloc.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.h2.jdbcx.JdbcConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class H2Db {
    private final static Logger log = LoggerFactory.getLogger("PinLoc");

    @SuppressWarnings("unused")
    private final JavaPlugin plugin;

    private JdbcConnectionPool cp;
    private final static String db = "jdbc:h2:";
    private Connection conn;

    public H2Db(JavaPlugin _plugin) {
        this.plugin = _plugin;
    }

    public void dbSetup(File dataFolder, String dbName) {
        String fullDbName = dbName(dataFolder, dbName);

        fullDbName = fullDbName.concat(";AUTO_RECONNECT=TRUE");

        cp = JdbcConnectionPool.create(fullDbName, "sa", "sa");
        conn = getConnection();

        createSchema();
        createMCUsersTable();
        createLocationsTable();

        debugShow();
        
        log.info("database: {} opened", fullDbName);
    }

    private void createSchema() {
        try (Statement stmt = conn.createStatement()) {
            boolean result = stmt.execute("""
                    create schema if not exists mcschema authorization sa;""");
            
            stmt.execute("commit;");
            
            conn.commit();

            log.info("create schema result: {}", result);
        }
        catch (SQLException e) {
            log.error("database create schema failed: {}", e);
        }

    }

    private void createMCUsersTable() {
        try (Statement stmt = conn.createStatement()) {
            boolean result = stmt.execute("""
                    create table if not exists mcschema.mcusers
                    (id bigint generated always as identity primary key,
                    mcuser varchar(64) not null);""");
            
            conn.commit();

            log.info("create mcsers table result: {}", result);
        }
        catch (SQLException e) {
            log.error("database create table mcusers failed: {}", e);
        }
    }

    private void createLocationsTable() {
        try (Statement stmt = conn.createStatement()) {
            boolean result = stmt.execute("""
                    create table if not exists mcschema.locations
                    (id bigint generated always as identity primary key,
                    location geometry(pointz) not null,
                    mcuser bigint not null,
                    moment timestamp default current_timestamp not null,
                    foreign key(mcuser) references mcusers(id));""");
            
            conn.commit();

            log.info("create locations table result: {}", result);
        }
        catch (SQLException e) {
            log.error("database create table locations failed: {}", e);
        }
    }

    public void closeConnection(Connection conn) {
        try {
            log.info("closing connection");
            
            conn.close();
        }
        catch (SQLException e) {
            log.error("database connect close failed: {}", e);
        }
    }

    public void GetLocs(Player player) {
        // add stuff here
    }

    private void debugShow() {
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("""
                    show tables from mcschema""");
            
            while (rs.next()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                int numberOfColumns = rsmd.getColumnCount();
                
                log.info("column count: {}", numberOfColumns);
                
                log.info("column 1 data: {}", rs.getString(1));
                log.info("column 2 data: {}", rs.getString(2));
            }
        }
        catch (SQLException e) {
            log.error("database show failed: {}", e);
        }
    }
    public void shutdown() {
        try (Statement stmt = conn.createStatement()) {
            log.info("executing shutdown");
            
            stmt.execute("shutdown compact;");
//            conn.commit();
        }
        catch (SQLException e) {
            log.error("database shutdown failed:", e);
        }

//        closeConnection(conn);

//        cp.dispose();
    }

    private Connection getConnection() {
        try {
            return cp.getConnection();
        }
        catch (SQLException e) {
            log.error("database connect failed: {}", e);

            throw new RuntimeException(e);
        }
    }

    private String dbName(File dataFolder, String dbName) {
        log.info("data folder name: {}", dataFolder.getName());

        String path;

        try {
            path = dataFolder.getCanonicalPath();
        }
        catch (IOException e) {
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
