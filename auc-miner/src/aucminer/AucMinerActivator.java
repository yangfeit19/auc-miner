package aucminer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import aucminer.configuration.ConfigUtility;
import aucminer.configuration.Configuration;

/**
 * The activator class controls the plug-in life cycle
 */
public class AucMinerActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "auc-miner"; //$NON-NLS-1$

	// The shared instance
	private static AucMinerActivator plugin;
	
	private Configuration configuration;
	
	/**
	 * The constructor
	 */
	public AucMinerActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		try {
			File configXmlFile = new File(FileLocator.resolve(this.getBundle().getEntry("configuration.xml")).toURI());
			configuration = ConfigUtility.parseConfigFromXmlFile(configXmlFile);
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static AucMinerActivator getDefault() {
		return plugin;
	}
	
	public void setConfiguration(Configuration config) {
		this.configuration = config;
	}
	
	public Configuration getConfiguration() {
		return this.configuration;
	}
	
	public URI getConfigFileUri() {
		URI configUri = null;
		try {
			configUri = FileLocator.resolve(this.getBundle().getEntry("configuration.xml")).toURI();
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
		return configUri;
	}
}
