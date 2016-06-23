package aucminer.configuration;

public class Storage {
	public static final int STORAGE_COUNT = 1;
	public static final int NUM_OF_MYSQL_CONFIG = 5;
	
	private boolean storeToFileOrDB;
	private String fileStoragePath;
	private String driver;
	private String host;
	private String port;
	private String user;
	private String password;
	
	public boolean isStoreToFileOrDB() {
		return storeToFileOrDB;
	}
	public void setStoreToFileOrDB(boolean storeToFileOrDB) {
		this.storeToFileOrDB = storeToFileOrDB;
	}
	public String getFileStoragePath() {
		return fileStoragePath;
	}
	public void setFileStoragePath(String fileStoragePath) {
		this.fileStoragePath = fileStoragePath;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
