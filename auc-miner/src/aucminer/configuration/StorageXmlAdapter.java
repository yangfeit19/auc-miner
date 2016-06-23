package aucminer.configuration;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class StorageXmlAdapter extends XmlAdapter<Object, Storage> {

	@Override
	public Object marshal(Storage storage) throws Exception {
		
		if (storage == null) {
			return null;
		}
		
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = docBuilder.newDocument();
		
		Element rootElement = document.createElement("resultStorage");
		document.appendChild(rootElement);
		
		if (storage.isStoreToFileOrDB()) {
			Element fileStorage = document.createElement("fileStorage");
			fileStorage.setTextContent(storage.getFileStoragePath());
			rootElement.appendChild(fileStorage);
		}
		else {
			Element mysql_config = document.createElement("mysqlConfig");
			
			Element mysql_driver = document.createElement("driver");
			mysql_driver.setTextContent(storage.getDriver());
			mysql_config.appendChild(mysql_driver);
			
			Element mysql_host = document.createElement("host");
			mysql_host.setTextContent(storage.getHost());
			mysql_config.appendChild(mysql_host);
			
			Element mysql_port = document.createElement("port");
			mysql_port.setTextContent(storage.getPort());
			mysql_config.appendChild(mysql_port);
			
			Element mysql_user = document.createElement("user");
			mysql_user.setTextContent(storage.getUser());
			mysql_config.appendChild(mysql_user);
			
			Element mysql_password = document.createElement("password");
			mysql_password.setTextContent(storage.getPassword());
			mysql_config.appendChild(mysql_password);
			
			rootElement.appendChild(mysql_config);
		}
		
		return rootElement;
	}

	@Override
	public Storage unmarshal(Object obj) throws Exception {
		
		if (obj == null) {
			return null;
		}

		Element element = (Element)obj;
		NodeList children = element.getChildNodes();
		if (children == null || children.getLength() != Storage.STORAGE_COUNT + 1) {
			return null;
		}
		
		Node storageNode = children.item(0);
		if (storageNode.getNodeName().equals("fileStorage")) {
			Storage storage = new Storage();
			storage.setStoreToFileOrDB(true);
			storage.setFileStoragePath(storageNode.getTextContent());
			return storage;
		}
		
		if (storageNode.getNodeName().equals("mysqlConfig")) {
			Storage storage = new Storage();
			storage.setStoreToFileOrDB(false);
			
			NodeList mysqlConfigNodes = storageNode.getChildNodes();
			if (mysqlConfigNodes == null || mysqlConfigNodes.getLength() != Storage.NUM_OF_MYSQL_CONFIG + 1) {
				return null;
			}
			
			for (int i = 0; i < Storage.NUM_OF_MYSQL_CONFIG; i++) {
				Node configNode = mysqlConfigNodes.item(i);
				switch (configNode.getNodeName()) {
					case "driver":
						storage.setDriver(configNode.getTextContent());
						break;
					case "host":
						storage.setHost(configNode.getTextContent());
						break;
					case "port":
						storage.setPort(configNode.getTextContent());
						break;
					case "user":
						storage.setUser(configNode.getTextContent());
						break;
					case "password":
						storage.setPassword(configNode.getTextContent());
						break;
					default:
						return null;
				}
			}
			
			return storage;
		}
		
		return null;
	}
}
