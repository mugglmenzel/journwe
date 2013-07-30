package com.journwe.utility;

import java.io.File;
import java.util.Properties;

import net.dharwin.common.tools.cli.api.CLIContext;

public class JournweCLIContext extends CLIContext {
	
	/**
	 * @param app The host application.
	 */
	public JournweCLIContext(Client app) {
		super(app);
	}
	
	
	@Override
	protected String getEmbeddedPropertiesFilename() {
		return "cli.properties";
	}
	
	/**
	 * Specify the property file used by this context to load
	 * initial properties from. This property file should sit outside of
	 * the final jar file.
	 */
	@Override
	protected File getExternalPropertiesFile() {
		return new File("cli.properties");
	}
	
}
