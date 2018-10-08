package gov.loc.repository.quartz.plugins.xml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.common.util.IOUtils;
import org.quartz.plugins.xml.XMLSchedulingDataProcessorPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrXMLSchedulingDataProcessorPlugin extends
		XMLSchedulingDataProcessorPlugin {



	/*
	 * Overrides XMLSchedulingDataProcessorPlugin to set the base path of
	 * filenames to Solr config directory.
	 * 
	 * @see org.quartz.plugins.xml.XMLSchedulingDataProcessorPlugin#setFileNames(java.lang.String)
	 */

	private static final Logger logger = LoggerFactory.getLogger(SolrXMLSchedulingDataProcessorPlugin.class);

	@Override
	public void setFileNames(String fileNames) {
		if (fileNames == null) {
			super.setFileNames(fileNames);
			return;
		}

		logger.info("Get Solr's config directory from the SolrResourceLoader");

		//Get Solr's config directory from the SolrResourceLoader
		SolrResourceLoader resourceLoader = new SolrResourceLoader(null);
		String configDir;
		String path = getSolrPathFromProperties();
		try {
			// PATH TO A SERVER SOLR FOLDER
			configDir = SolrResourceLoader.normalizeDir(path);
		} finally {
			IOUtils.closeQuietly(resourceLoader);
		}

		logger.info("Add the config directory to the filenames");
		
		//Add the config directory to the filenames
        List<String> fullFileNameList = new ArrayList<String>();
        for(String fileName : fileNames.split(", *")) {
            fullFileNameList.add(configDir + fileName);        	
        }

		logger.info(" //Re-concatenate");

        //Re-concatenate
        String fullFileNames = StringUtils.join(fullFileNameList, ",");
		super.setFileNames(fullFileNames);

		logger.info("Set new file names.");
	}

	private String getSolrPathFromProperties() {
		Properties prop = new Properties();
		InputStream input = null;
		String path = null;
		try {

			input = new FileInputStream("config.properties");

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			logger.info("Get Solr's config directory : "+ prop.getProperty("solr.home"));
			path = prop.getProperty("solr.home");
		} catch (IOException ex) {
			logger.error(MessageFormat.format("Error shutting down reading solr: {0}",  ex.getMessage(), ex));
		}
		return path;
	}

}
