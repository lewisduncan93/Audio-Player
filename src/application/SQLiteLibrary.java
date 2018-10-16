package application;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A SQLite implementation of the Library interface.
 *
 * @author Lewis Duncan
 *
 */
public class SQLiteLibrary implements Library {

	/**
	 * SQLite database path string.
	 */
	private static final String DB_PATH = "database.db";

	private String dbPath;
	private Connection conn;

	/**
	 * Initialise a SQLite library with the default db path.
	 */
	public SQLiteLibrary() {
		dbPath = DB_PATH;
		initDb();
	}

	/**
	 * Initialise a SQLite library with a specified db path.
	 *
	 * @param dbPath
	 *            the db path.
	 */
	public SQLiteLibrary(String dbPath) {
		this.dbPath = dbPath;
		initDb();
	}

	/**
	 * Initialise the db.
	 *
	 * This first checks if the db exists. If not, the table is created. A
	 * connection to the db is opened and stored in conn.
	 */
	private void initDb() {
		File f = new File(DB_PATH);
		boolean dbExists = f.exists();
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
			conn.setAutoCommit(false);
			System.out.println("Opened database successfully");
			if (!dbExists) {
				// Create table
				String createTableSql = "CREATE TABLE audio_files "
						+ "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ " name TEXT,"
						+ " artist TEXT,"
						+ " album TEXT,"
						+ " size INTEGER,"
						+ " duration INTEGER NOT NULL,"
						+ " path TEXT NOT NULL,"
						+ " play_count INTEGER DEFAULT 0)";
				Statement createTableStmt = conn.createStatement();
				createTableStmt.executeUpdate(createTableSql);
				createTableStmt.close();
				System.out.println("Created table");
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public void add(AudioFile audioFile) {
		try {
			String name = audioFile.getName();
			String artist = audioFile.getArtist();
			String album = audioFile.getAlbum();
			int size = audioFile.getSize();
			int duration = audioFile.getDuration();
			String path = audioFile.getFullPath();
			Statement insertStmt = conn.createStatement();
			String insertSql = "INSERT INTO audio_files "
					+ "(name, artist, album, size, duration, path) VALUES"
					+ "(\"" + name + "\","
					+ " \"" + artist + "\","
					+ " \"" + album + "\","
					+ " " + size + ","
					+ " \"" + duration + "\","
					+ " \"" + path + "\")";
			insertStmt.executeUpdate(insertSql);
			conn.commit();
			insertStmt.close();
			System.out.println("Added success");
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public void remove(AudioFile audioFile) {
		try {
			String path = audioFile.getFullPath();
			Statement deleteStmt = conn.createStatement();
			String deleteSql = "DELETE FROM audio_files WHERE path = \"" + path + "\"";
			deleteStmt.executeUpdate(deleteSql);
			conn.commit();
			deleteStmt.close();
			System.out.println("Delete success");
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public void incrementPlayCount(AudioFile audioFile) {
		try {
			String path = audioFile.getFullPath();
			int newPlayCount = audioFile.getPlayCount() + 1;
			Statement stmt = conn.createStatement();
			String sql = "UPDATE audio_files SET play_count = " + newPlayCount + " WHERE path = \"" + path + "\"";
			stmt.executeUpdate(sql);
			conn.commit();
			stmt.close();
			System.out.println("Increment success");
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public List<AudioFile> getAll() {
		List<AudioFile> list = new ArrayList<>();
		try {
			Statement stmt = conn.createStatement();
			String sql = "SELECT * FROM audio_files";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String name = rs.getString("name");
				String artist = rs.getString("artist");
				String album = rs.getString("album");
				int size = rs.getInt("size");
				int duration = rs.getInt("duration");
				int playCount = rs.getInt("play_count");
				String path = rs.getString("path");
				AudioFile af = new AudioFile(name, artist, album, size, duration, playCount, path);
				list.add(af);
			}
			conn.commit();
			stmt.close();
			System.out.println("Get all success");
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return list;
	}


	}

