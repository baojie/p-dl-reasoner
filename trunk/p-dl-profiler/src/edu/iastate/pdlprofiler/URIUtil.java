package edu.iastate.pdlprofiler;


import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

public class URIUtil {
	
	public static URI toURI(String pathOrURI) {
		URI uri = null;
		try {
			uri = new URI(pathOrURI);
		} catch (URISyntaxException e) {
		}
		
		if (uri == null || !uri.isAbsolute()) {
			File file = new File(pathOrURI);
			if (!file.exists()) {
				throw new RuntimeException(new FileNotFoundException(file.getAbsolutePath() + " is not found."));
			}
			
			uri = file.toURI();
		}
		
		return uri;
	}
	
	public static URI filterFragment(URI uri) throws URISyntaxException {
		return new URI(uri.getScheme(), uri.getSchemeSpecificPart(), null);
	}
	
	public static String getLastOfPath(URI uri) {
		String path = uri.getPath();
		int lastSlash = path.lastIndexOf('/');
		return path.substring(lastSlash + 1);
	}

}
