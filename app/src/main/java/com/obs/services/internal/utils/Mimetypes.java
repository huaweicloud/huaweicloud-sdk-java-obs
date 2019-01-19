package com.obs.services.internal.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;

public class Mimetypes
{
    private static final ILogger log = LoggerBuilder.getLogger(Mimetypes.class);
    
    public static final String MIMETYPE_XML = "application/xml";
    
    public static final String MIMETYPE_TEXT_XML = "text/xml";
    
    public static final String MIMETYPE_TEXT_PLAIN = "text/plain";
    
    public static final String MIMETYPE_HTML = "text/html";
    
    public static final String MIMETYPE_OCTET_STREAM = "application/octet-stream";
    
    public static final String MIMETYPE_BINARY_OCTET_STREAM = "binary/octet-stream";
    
    public static final String MIMETYPE_GZIP = "application/x-gzip";
    
    private final Map<String, String> extensionToMimetypeMap;
    
    private Mimetypes()
    {
        extensionToMimetypeMap = new HashMap<String, String>();
        extensionToMimetypeMap.put("txt", MIMETYPE_TEXT_PLAIN);
        extensionToMimetypeMap.put("html", MIMETYPE_HTML);
        extensionToMimetypeMap.put("htm", MIMETYPE_HTML);
        extensionToMimetypeMap.put("7z", "application/x-7z-compressed");
        extensionToMimetypeMap.put("aac", "audio/x-aac");
        extensionToMimetypeMap.put("ai", "application/postscript");
        extensionToMimetypeMap.put("aif", "audio/x-aiff");
        extensionToMimetypeMap.put("asc", "text/plain");
        extensionToMimetypeMap.put("asf", "video/x-ms-asf");
        extensionToMimetypeMap.put("atom", "application/atom+xml");
        extensionToMimetypeMap.put("avi", "video/x-msvideo");
        extensionToMimetypeMap.put("bmp", "image/bmp");
        extensionToMimetypeMap.put("bz2", "application/x-bzip2");
        extensionToMimetypeMap.put("cer", "application/pkix-cert");
        extensionToMimetypeMap.put("crl", "application/pkix-crl");
        extensionToMimetypeMap.put("crt", "application/x-x509-ca-cert");
        extensionToMimetypeMap.put("css", "text/css");
        extensionToMimetypeMap.put("csv", "text/csv");
        extensionToMimetypeMap.put("cu", "application/cu-seeme");
        extensionToMimetypeMap.put("deb", "application/x-debian-package");
        extensionToMimetypeMap.put("doc", "application/msword");
        extensionToMimetypeMap.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        extensionToMimetypeMap.put("dvi", "application/x-dvi");
        extensionToMimetypeMap.put("eot", "application/vnd.ms-fontobject");
        extensionToMimetypeMap.put("eps", "application/postscript");
        extensionToMimetypeMap.put("epub", "application/epub+zip");
        extensionToMimetypeMap.put("etx", "text/x-setext");
        extensionToMimetypeMap.put("flac", "audio/flac");
        extensionToMimetypeMap.put("flv", "video/x-flv");
        extensionToMimetypeMap.put("gif", "image/gif");
        extensionToMimetypeMap.put("gz", "application/gzip");
        extensionToMimetypeMap.put("ico", "image/x-icon");
        extensionToMimetypeMap.put("ics", "text/calendar");
        extensionToMimetypeMap.put("ini", "text/plain");
        extensionToMimetypeMap.put("iso", "application/x-iso9660-image");
        extensionToMimetypeMap.put("jar", "application/java-archive");
        extensionToMimetypeMap.put("jpe", "image/jpeg");
        extensionToMimetypeMap.put("jpeg", "image/jpeg");
        extensionToMimetypeMap.put("jpg", "image/jpeg");
        extensionToMimetypeMap.put("js", "text/javascript");
        extensionToMimetypeMap.put("json", "application/json");
        extensionToMimetypeMap.put("latex", "application/x-latex");
        extensionToMimetypeMap.put("log", "text/plain");
        extensionToMimetypeMap.put("m4a", "audio/mp4");
        extensionToMimetypeMap.put("m4v", "video/mp4");
        extensionToMimetypeMap.put("mid", "audio/midi");
        extensionToMimetypeMap.put("midi", "audio/midi");
        extensionToMimetypeMap.put("mov", "video/quicktime");
        extensionToMimetypeMap.put("mp3", "audio/mpeg");
        extensionToMimetypeMap.put("mp4", "video/mp4");
        extensionToMimetypeMap.put("mp4a", "audio/mp4");
        extensionToMimetypeMap.put("mp4v", "video/mp4");
        extensionToMimetypeMap.put("mpe", "video/mpeg");
        extensionToMimetypeMap.put("mpeg", "video/mpeg");
        extensionToMimetypeMap.put("mpg", "video/mpeg");
        extensionToMimetypeMap.put("mpg4", "video/mp4");
        extensionToMimetypeMap.put("oga", "audio/ogg");
        extensionToMimetypeMap.put("ogg", "audio/ogg");
        extensionToMimetypeMap.put("ogv", "video/ogg");
        extensionToMimetypeMap.put("ogx", "application/ogg");
        extensionToMimetypeMap.put("pbm", "image/x-portable-bitmap");
        extensionToMimetypeMap.put("pdf", "application/pdf");
        extensionToMimetypeMap.put("pgm", "image/x-portable-graymap");
        extensionToMimetypeMap.put("png", "image/png");
        extensionToMimetypeMap.put("pnm", "image/x-portable-anymap");
        extensionToMimetypeMap.put("ppm", "image/x-portable-pixmap");
        extensionToMimetypeMap.put("ppt", "application/vnd.ms-powerpoint");
        extensionToMimetypeMap.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        extensionToMimetypeMap.put("ps", "application/postscript");
        extensionToMimetypeMap.put("qt", "video/quicktime");
        extensionToMimetypeMap.put("rar", "application/x-rar-compressed");
        extensionToMimetypeMap.put("ras", "image/x-cmu-raster");
        extensionToMimetypeMap.put("rss", "application/rss+xml");
        extensionToMimetypeMap.put("rtf", "application/rtf");
        extensionToMimetypeMap.put("sgm", "text/sgml");
        extensionToMimetypeMap.put("sgml", "text/sgml");
        extensionToMimetypeMap.put("svg", "image/svg+xml");
        extensionToMimetypeMap.put("swf", "application/x-shockwave-flash");
        extensionToMimetypeMap.put("tar", "application/x-tar");
        extensionToMimetypeMap.put("tif", "image/tiff");
        extensionToMimetypeMap.put("tiff", "image/tiff");
        extensionToMimetypeMap.put("torrent", "application/x-bittorrent");
        extensionToMimetypeMap.put("ttf", "application/x-font-ttf");
        extensionToMimetypeMap.put("wav", "audio/x-wav");
        extensionToMimetypeMap.put("webm", "video/webm");
        extensionToMimetypeMap.put("wma", "audio/x-ms-wma");
        extensionToMimetypeMap.put("wmv", "video/x-ms-wmv");
        extensionToMimetypeMap.put("woff", "application/x-font-woff");
        extensionToMimetypeMap.put("wsdl", "application/wsdl+xml");
        extensionToMimetypeMap.put("xbm", "image/x-xbitmap");
        extensionToMimetypeMap.put("xls", "application/vnd.ms-excel");
        extensionToMimetypeMap.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        extensionToMimetypeMap.put("xml", "application/xml");
        extensionToMimetypeMap.put("xpm", "image/x-xpixmap");
        extensionToMimetypeMap.put("xwd", "image/x-xwindowdump");
        extensionToMimetypeMap.put("yaml", "text/yaml");
        extensionToMimetypeMap.put("yml", "text/yaml");
        extensionToMimetypeMap.put("zip", "application/zip");
        
    }
    
    private static class MimetypesHolder
    {
        private static Mimetypes mimetypes = new Mimetypes();
        static
        {
            InputStream mimetypesFile = mimetypes.getClass().getResourceAsStream("/mime.types");
            if (mimetypesFile != null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Loading mime types from file in the classpath: mime.types");
                }
                try
                {
                    mimetypes.loadAndReplaceMimetypes(mimetypesFile);
                }
                catch (IOException e)
                {
                    if (log.isErrorEnabled())
                    {
                        log.error("Failed to load mime types from file in the classpath: mime.types", e);
                    }
                }
            }
        }
    }
    
    public static Mimetypes getInstance()
    {
        return MimetypesHolder.mimetypes;
    }
    
    public void loadAndReplaceMimetypes(InputStream is)
        throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        
        while ((line = br.readLine()) != null)
        {
            line = line.trim();
            
            if (line.startsWith("#") || line.length() == 0)
            {
                // Ignore comments and empty lines.
            }
            else
            {
                StringTokenizer st = new StringTokenizer(line, " \t");
                if (st.countTokens() > 1)
                {
                    String mimetype = st.nextToken();
                    while (st.hasMoreTokens())
                    {
                        String extension = st.nextToken();
                        extensionToMimetypeMap.put(extension, mimetype);
                        if (log.isDebugEnabled())
                        {
                            log.debug("Setting mime type for extension '" + extension + "' to '" + mimetype + "'");
                        }
                    }
                }
                else
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Ignoring mimetype with no associated file extensions: '" + line + "'");
                    }
                }
            }
        }
    }
    
    public String getMimetype(String fileName)
    {
        // Look up default mimetype, represented by '*' in the mime.types file or use
        // application/octet-stream as a fallback.
        String mimetype = extensionToMimetypeMap.get("*");
        if (mimetype == null)
        {
            mimetype = MIMETYPE_OCTET_STREAM;
        }
        
        int lastPeriodIndex = fileName.lastIndexOf(".");
        if (lastPeriodIndex > 0 && lastPeriodIndex + 1 < fileName.length())
        {
            String ext = fileName.substring(lastPeriodIndex + 1);
            if (extensionToMimetypeMap.keySet().contains(ext))
            {
                mimetype = extensionToMimetypeMap.get(ext);
                if (log.isDebugEnabled())
                {
                    log.debug("Recognised extension '" + ext + "', mimetype is: '" + mimetype + "'");
                }
                return mimetype;
            }
            else
            {
                if (log.isDebugEnabled())
                {
                    log.debug(
                        "Extension '" + ext + "' is unrecognized in mime type listing" + ", using default mime type: '" + mimetype + "'");
                }
            }
        }
        else
        {
            if (log.isDebugEnabled())
            {
                log.debug("File name has no extension, mime type cannot be recognised for: " + fileName);
            }
        }
        return mimetype;
    }
    
    public String getMimetype(File file)
    {
        return getMimetype(file.getName());
    }
}
