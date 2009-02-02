package edu.iastate.pdlreasoner.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;

public class URIUtil {
	
	private static final Logger LOGGER = Logger.getLogger(URIUtil.class);

	public static URI toURI(String pathOrURI) {
		URI uri = null;
		try {
			uri = new URI(pathOrURI);
		} catch (URISyntaxException e) {
		}
		
		if (uri == null || !uri.isAbsolute()) {
			File file = new File(pathOrURI);
			if (!file.exists()) {
				LOGGER.error("File does not exist: " + file.getAbsolutePath());
				throw new RuntimeException(new FileNotFoundException(file.getAbsolutePath() + " is not found."));
			}
			
			uri = file.toURI();
		}
		
		return uri;
	}

}
