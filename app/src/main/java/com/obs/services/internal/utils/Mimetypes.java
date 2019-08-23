/**
 * 
 * JetS3t : Java S3 Toolkit
 * Project hosted at http://bitbucket.org/jmurty/jets3t/
 *
 * Copyright 2006-2010 James Murty
 * 
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.obs.services.internal.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Mimetypes
{
    public static final String MIMETYPE_XML = "application/xml";
    
    public static final String MIMETYPE_TEXT_XML = "text/xml";
    
    public static final String MIMETYPE_TEXT_PLAIN = "text/plain";
    
    public static final String MIMETYPE_HTML = "text/html";
    
    public static final String MIMETYPE_OCTET_STREAM = "application/octet-stream";
    
    public static final String MIMETYPE_BINARY_OCTET_STREAM = "binary/octet-stream";
    
    public static final String MIMETYPE_GZIP = "application/x-gzip";
    
    public static final String MIMETYPE_JSON = "application/json";
    
    private final Map<String, String> extensionToMimetypeMap;
    
    private Mimetypes()
    {
        extensionToMimetypeMap = new HashMap<String, String>();
        extensionToMimetypeMap.put("001", "application/x-001");
        extensionToMimetypeMap.put("301", "application/x-301");
        extensionToMimetypeMap.put("323", "text/h323");
        extensionToMimetypeMap.put("7z", "application/x-7z-compressed");
        extensionToMimetypeMap.put("906", "application/x-906");
        extensionToMimetypeMap.put("907", "drawing/907");
        extensionToMimetypeMap.put("IVF", "video/x-ivf");
        extensionToMimetypeMap.put("a11", "application/x-a11");
        extensionToMimetypeMap.put("aac", "audio/x-aac");
        extensionToMimetypeMap.put("acp", "audio/x-mei-aac");
        extensionToMimetypeMap.put("ai", "application/postscript");
        extensionToMimetypeMap.put("aif", "audio/aiff");
        extensionToMimetypeMap.put("aifc", "audio/aiff");
        extensionToMimetypeMap.put("aiff", "audio/aiff");
        extensionToMimetypeMap.put("anv", "application/x-anv");
        extensionToMimetypeMap.put("apk", "application/vnd.android.package-archive");
        extensionToMimetypeMap.put("asa", "text/asa");
        extensionToMimetypeMap.put("asf", "video/x-ms-asf");
        extensionToMimetypeMap.put("asp", "text/asp");
        extensionToMimetypeMap.put("asx", "video/x-ms-asf");
        extensionToMimetypeMap.put("atom", "application/atom+xml");
        extensionToMimetypeMap.put("au", "audio/basic");
        extensionToMimetypeMap.put("avi", "video/avi");
        extensionToMimetypeMap.put("awf", "application/vnd.adobe.workflow");
        extensionToMimetypeMap.put("biz", "text/xml");
        extensionToMimetypeMap.put("bmp", "application/x-bmp");
        extensionToMimetypeMap.put("bot", "application/x-bot");
        extensionToMimetypeMap.put("bz2", "application/x-bzip2");
        extensionToMimetypeMap.put("c4t", "application/x-c4t");
        extensionToMimetypeMap.put("c90", "application/x-c90");
        extensionToMimetypeMap.put("cal", "application/x-cals");
        extensionToMimetypeMap.put("cat", "application/vnd.ms-pki.seccat");
        extensionToMimetypeMap.put("cdf", "application/x-netcdf");
        extensionToMimetypeMap.put("cdr", "application/x-cdr");
        extensionToMimetypeMap.put("cel", "application/x-cel");
        extensionToMimetypeMap.put("cer", "application/x-x509-ca-cert");
        extensionToMimetypeMap.put("cg4", "application/x-g4");
        extensionToMimetypeMap.put("cgm", "application/x-cgm");
        extensionToMimetypeMap.put("cit", "application/x-cit");
        extensionToMimetypeMap.put("class", "java/*");
        extensionToMimetypeMap.put("cml", "text/xml");
        extensionToMimetypeMap.put("cmp", "application/x-cmp");
        extensionToMimetypeMap.put("cmx", "application/x-cmx");
        extensionToMimetypeMap.put("cot", "application/x-cot");
        extensionToMimetypeMap.put("crl", "application/pkix-crl");
        extensionToMimetypeMap.put("crt", "application/x-x509-ca-cert");
        extensionToMimetypeMap.put("csi", "application/x-csi");
        extensionToMimetypeMap.put("css", "text/css");
        extensionToMimetypeMap.put("csv", "text/csv");
        extensionToMimetypeMap.put("cu", "application/cu-seeme");
        extensionToMimetypeMap.put("cut", "application/x-cut");
        extensionToMimetypeMap.put("dbf", "application/x-dbf");
        extensionToMimetypeMap.put("dbm", "application/x-dbm");
        extensionToMimetypeMap.put("dbx", "application/x-dbx");
        extensionToMimetypeMap.put("dcd", "text/xml");
        extensionToMimetypeMap.put("dcx", "application/x-dcx");
        extensionToMimetypeMap.put("deb", "application/x-debian-package");
        extensionToMimetypeMap.put("der", "application/x-x509-ca-cert");
        extensionToMimetypeMap.put("dgn", "application/x-dgn");
        extensionToMimetypeMap.put("dib", "application/x-dib");
        extensionToMimetypeMap.put("dll", "application/x-msdownload");
        extensionToMimetypeMap.put("doc", "application/msword");
        extensionToMimetypeMap.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        extensionToMimetypeMap.put("dot", "application/msword");
        extensionToMimetypeMap.put("drw", "application/x-drw");
        extensionToMimetypeMap.put("dtd", "text/xml");
        extensionToMimetypeMap.put("dvi", "application/x-dvi");
        extensionToMimetypeMap.put("dwf", "application/x-dwf");
        extensionToMimetypeMap.put("dwg", "application/x-dwg");
        extensionToMimetypeMap.put("dxb", "application/x-dxb");
        extensionToMimetypeMap.put("dxf", "application/x-dxf");
        extensionToMimetypeMap.put("edn", "application/vnd.adobe.edn");
        extensionToMimetypeMap.put("emf", "application/x-emf");
        extensionToMimetypeMap.put("eml", "message/rfc822");
        extensionToMimetypeMap.put("ent", "text/xml");
        extensionToMimetypeMap.put("eot", "application/vnd.ms-fontobject");
        extensionToMimetypeMap.put("epi", "application/x-epi");
        extensionToMimetypeMap.put("eps", "application/postscript");
        extensionToMimetypeMap.put("epub", "application/epub+zip");
        extensionToMimetypeMap.put("etd", "application/x-ebx");
        extensionToMimetypeMap.put("etx", "text/x-setext");
        extensionToMimetypeMap.put("exe", "application/x-msdownload");
        extensionToMimetypeMap.put("fax", "image/fax");
        extensionToMimetypeMap.put("fdf", "application/vnd.fdf");
        extensionToMimetypeMap.put("fif", "application/fractals");
        extensionToMimetypeMap.put("flac", "audio/flac");
        extensionToMimetypeMap.put("flv", "video/x-flv");
        extensionToMimetypeMap.put("fo", "text/xml");
        extensionToMimetypeMap.put("frm", "application/x-frm");
        extensionToMimetypeMap.put("g4", "application/x-g4");
        extensionToMimetypeMap.put("gbr", "application/x-gbr");
        extensionToMimetypeMap.put("gif", "image/gif");
        extensionToMimetypeMap.put("gl2", "application/x-gl2");
        extensionToMimetypeMap.put("gp4", "application/x-gp4");
        extensionToMimetypeMap.put("gz", "application/gzip");
        extensionToMimetypeMap.put("hgl", "application/x-hgl");
        extensionToMimetypeMap.put("hmr", "application/x-hmr");
        extensionToMimetypeMap.put("hpg", "application/x-hpgl");
        extensionToMimetypeMap.put("hpl", "application/x-hpl");
        extensionToMimetypeMap.put("hqx", "application/mac-binhex40");
        extensionToMimetypeMap.put("hrf", "application/x-hrf");
        extensionToMimetypeMap.put("hta", "application/hta");
        extensionToMimetypeMap.put("htc", "text/x-component");
        extensionToMimetypeMap.put("htm", "text/html");
        extensionToMimetypeMap.put("html", "text/html");
        extensionToMimetypeMap.put("htt", "text/webviewhtml");
        extensionToMimetypeMap.put("htx", "text/html");
        extensionToMimetypeMap.put("icb", "application/x-icb");
        extensionToMimetypeMap.put("ico", "application/x-ico");
        extensionToMimetypeMap.put("ics", "text/calendar");
        extensionToMimetypeMap.put("iff", "application/x-iff");
        extensionToMimetypeMap.put("ig4", "application/x-g4");
        extensionToMimetypeMap.put("igs", "application/x-igs");
        extensionToMimetypeMap.put("iii", "application/x-iphone");
        extensionToMimetypeMap.put("img", "application/x-img");
        extensionToMimetypeMap.put("ini", "text/plain");
        extensionToMimetypeMap.put("ins", "application/x-internet-signup");
        extensionToMimetypeMap.put("ipa", "application/vnd.iphone");
        extensionToMimetypeMap.put("iso", "application/x-iso9660-image");
        extensionToMimetypeMap.put("isp", "application/x-internet-signup");
        extensionToMimetypeMap.put("jar", "application/java-archive");
        extensionToMimetypeMap.put("java", "java/*");
        extensionToMimetypeMap.put("jfif", "image/jpeg");
        extensionToMimetypeMap.put("jpe", "image/jpeg");
        extensionToMimetypeMap.put("jpeg", "image/jpeg");
        extensionToMimetypeMap.put("jpg", "image/jpeg");
        extensionToMimetypeMap.put("js", "application/x-javascript");
        extensionToMimetypeMap.put("json", "application/json");
        extensionToMimetypeMap.put("jsp", "text/html");
        extensionToMimetypeMap.put("la1", "audio/x-liquid-file");
        extensionToMimetypeMap.put("lar", "application/x-laplayer-reg");
        extensionToMimetypeMap.put("latex", "application/x-latex");
        extensionToMimetypeMap.put("lavs", "audio/x-liquid-secure");
        extensionToMimetypeMap.put("lbm", "application/x-lbm");
        extensionToMimetypeMap.put("lmsff", "audio/x-la-lms");
        extensionToMimetypeMap.put("log", "text/plain");
        extensionToMimetypeMap.put("ls", "application/x-javascript");
        extensionToMimetypeMap.put("ltr", "application/x-ltr");
        extensionToMimetypeMap.put("m1v", "video/x-mpeg");
        extensionToMimetypeMap.put("m2v", "video/x-mpeg");
        extensionToMimetypeMap.put("m3u", "audio/mpegurl");
        extensionToMimetypeMap.put("m4a", "audio/mp4");
        extensionToMimetypeMap.put("m4e", "video/mpeg4");
        extensionToMimetypeMap.put("m4v", "video/mp4");
        extensionToMimetypeMap.put("mac", "application/x-mac");
        extensionToMimetypeMap.put("man", "application/x-troff-man");
        extensionToMimetypeMap.put("math", "text/xml");
        extensionToMimetypeMap.put("mdb", "application/msaccess");
        extensionToMimetypeMap.put("mfp", "application/x-shockwave-flash");
        extensionToMimetypeMap.put("mht", "message/rfc822");
        extensionToMimetypeMap.put("mhtml", "message/rfc822");
        extensionToMimetypeMap.put("mi", "application/x-mi");
        extensionToMimetypeMap.put("mid", "audio/mid");
        extensionToMimetypeMap.put("midi", "audio/mid");
        extensionToMimetypeMap.put("mil", "application/x-mil");
        extensionToMimetypeMap.put("mml", "text/xml");
        extensionToMimetypeMap.put("mnd", "audio/x-musicnet-download");
        extensionToMimetypeMap.put("mns", "audio/x-musicnet-stream");
        extensionToMimetypeMap.put("mocha", "application/x-javascript");
        extensionToMimetypeMap.put("mov", "video/quicktime");
        extensionToMimetypeMap.put("movie", "video/x-sgi-movie");
        extensionToMimetypeMap.put("mp1", "audio/mp1");
        extensionToMimetypeMap.put("mp2", "audio/mp2");
        extensionToMimetypeMap.put("mp2v", "video/mpeg");
        extensionToMimetypeMap.put("mp3", "audio/mp3");
        extensionToMimetypeMap.put("mp4", "video/mp4");
        extensionToMimetypeMap.put("mp4a", "audio/mp4");
        extensionToMimetypeMap.put("mp4v", "video/mp4");
        extensionToMimetypeMap.put("mpa", "video/x-mpg");
        extensionToMimetypeMap.put("mpd", "application/vnd.ms-project");
        extensionToMimetypeMap.put("mpe", "video/x-mpeg");
        extensionToMimetypeMap.put("mpeg", "video/mpg");
        extensionToMimetypeMap.put("mpg", "video/mpg");
        extensionToMimetypeMap.put("mpg4", "video/mp4");
        extensionToMimetypeMap.put("mpga", "audio/rn-mpeg");
        extensionToMimetypeMap.put("mpp", "application/vnd.ms-project");
        extensionToMimetypeMap.put("mps", "video/x-mpeg");
        extensionToMimetypeMap.put("mpt", "application/vnd.ms-project");
        extensionToMimetypeMap.put("mpv", "video/mpg");
        extensionToMimetypeMap.put("mpv2", "video/mpeg");
        extensionToMimetypeMap.put("mpw", "application/vnd.ms-project");
        extensionToMimetypeMap.put("mpx", "application/vnd.ms-project");
        extensionToMimetypeMap.put("mtx", "text/xml");
        extensionToMimetypeMap.put("mxp", "application/x-mmxp");
        extensionToMimetypeMap.put("net", "image/pnetvue");
        extensionToMimetypeMap.put("nrf", "application/x-nrf");
        extensionToMimetypeMap.put("nws", "message/rfc822");
        extensionToMimetypeMap.put("odc", "text/x-ms-odc");
        extensionToMimetypeMap.put("oga", "audio/ogg");
        extensionToMimetypeMap.put("ogg", "audio/ogg");
        extensionToMimetypeMap.put("ogv", "video/ogg");
        extensionToMimetypeMap.put("ogx", "application/ogg");
        extensionToMimetypeMap.put("out", "application/x-out");
        extensionToMimetypeMap.put("p10", "application/pkcs10");
        extensionToMimetypeMap.put("p12", "application/x-pkcs12");
        extensionToMimetypeMap.put("p7b", "application/x-pkcs7-certificates");
        extensionToMimetypeMap.put("p7c", "application/pkcs7-mime");
        extensionToMimetypeMap.put("p7m", "application/pkcs7-mime");
        extensionToMimetypeMap.put("p7r", "application/x-pkcs7-certreqresp");
        extensionToMimetypeMap.put("p7s", "application/pkcs7-signature");
        extensionToMimetypeMap.put("pbm", "image/x-portable-bitmap");
        extensionToMimetypeMap.put("pc5", "application/x-pc5");
        extensionToMimetypeMap.put("pci", "application/x-pci");
        extensionToMimetypeMap.put("pcl", "application/x-pcl");
        extensionToMimetypeMap.put("pcx", "application/x-pcx");
        extensionToMimetypeMap.put("pdf", "application/pdf");
        extensionToMimetypeMap.put("pdx", "application/vnd.adobe.pdx");
        extensionToMimetypeMap.put("pfx", "application/x-pkcs12");
        extensionToMimetypeMap.put("pgl", "application/x-pgl");
        extensionToMimetypeMap.put("pgm", "image/x-portable-graymap");
        extensionToMimetypeMap.put("pic", "application/x-pic");
        extensionToMimetypeMap.put("pko", "application/vnd.ms-pki.pko");
        extensionToMimetypeMap.put("pl", "application/x-perl");
        extensionToMimetypeMap.put("plg", "text/html");
        extensionToMimetypeMap.put("pls", "audio/scpls");
        extensionToMimetypeMap.put("plt", "application/x-plt");
        extensionToMimetypeMap.put("png", "image/png");
        extensionToMimetypeMap.put("pnm", "image/x-portable-anymap");
        extensionToMimetypeMap.put("pot", "application/vnd.ms-powerpoint");
        extensionToMimetypeMap.put("ppa", "application/vnd.ms-powerpoint");
        extensionToMimetypeMap.put("ppm", "application/x-ppm");
        extensionToMimetypeMap.put("pps", "application/vnd.ms-powerpoint");
        extensionToMimetypeMap.put("ppt", "application/vnd.ms-powerpoint");
        extensionToMimetypeMap.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        extensionToMimetypeMap.put("pr", "application/x-pr");
        extensionToMimetypeMap.put("prf", "application/pics-rules");
        extensionToMimetypeMap.put("prn", "application/x-prn");
        extensionToMimetypeMap.put("prt", "application/x-prt");
        extensionToMimetypeMap.put("ps", "application/postscript");
        extensionToMimetypeMap.put("ptn", "application/x-ptn");
        extensionToMimetypeMap.put("pwz", "application/vnd.ms-powerpoint");
        extensionToMimetypeMap.put("qt", "video/quicktime");
        extensionToMimetypeMap.put("r3t", "text/vnd.rn-realtext3d");
        extensionToMimetypeMap.put("ra", "audio/vnd.rn-realaudio");
        extensionToMimetypeMap.put("ram", "audio/x-pn-realaudio");
        extensionToMimetypeMap.put("rar", "application/x-rar-compressed");
        extensionToMimetypeMap.put("ras", "application/x-ras");
        extensionToMimetypeMap.put("rat", "application/rat-file");
        extensionToMimetypeMap.put("rdf", "text/xml");
        extensionToMimetypeMap.put("rec", "application/vnd.rn-recording");
        extensionToMimetypeMap.put("red", "application/x-red");
        extensionToMimetypeMap.put("rgb", "application/x-rgb");
        extensionToMimetypeMap.put("rjs", "application/vnd.rn-realsystem-rjs");
        extensionToMimetypeMap.put("rjt", "application/vnd.rn-realsystem-rjt");
        extensionToMimetypeMap.put("rlc", "application/x-rlc");
        extensionToMimetypeMap.put("rle", "application/x-rle");
        extensionToMimetypeMap.put("rm", "application/vnd.rn-realmedia");
        extensionToMimetypeMap.put("rmf", "application/vnd.adobe.rmf");
        extensionToMimetypeMap.put("rmi", "audio/mid");
        extensionToMimetypeMap.put("rmj", "application/vnd.rn-realsystem-rmj");
        extensionToMimetypeMap.put("rmm", "audio/x-pn-realaudio");
        extensionToMimetypeMap.put("rmp", "application/vnd.rn-rn_music_package");
        extensionToMimetypeMap.put("rms", "application/vnd.rn-realmedia-secure");
        extensionToMimetypeMap.put("rmvb", "application/vnd.rn-realmedia-vbr");
        extensionToMimetypeMap.put("rmx", "application/vnd.rn-realsystem-rmx");
        extensionToMimetypeMap.put("rnx", "application/vnd.rn-realplayer");
        extensionToMimetypeMap.put("rp", "image/vnd.rn-realpix");
        extensionToMimetypeMap.put("rpm", "audio/x-pn-realaudio-plugin");
        extensionToMimetypeMap.put("rsml", "application/vnd.rn-rsml");
        extensionToMimetypeMap.put("rss", "application/rss+xml");
        extensionToMimetypeMap.put("rt", "text/vnd.rn-realtext");
        extensionToMimetypeMap.put("rtf", "application/x-rtf");
        extensionToMimetypeMap.put("rv", "video/vnd.rn-realvideo");
        extensionToMimetypeMap.put("sam", "application/x-sam");
        extensionToMimetypeMap.put("sat", "application/x-sat");
        extensionToMimetypeMap.put("sdp", "application/sdp");
        extensionToMimetypeMap.put("sdw", "application/x-sdw");
        extensionToMimetypeMap.put("sgm", "text/sgml");
        extensionToMimetypeMap.put("sgml", "text/sgml");
        extensionToMimetypeMap.put("sis", "application/vnd.symbian.install");
        extensionToMimetypeMap.put("sisx", "application/vnd.symbian.install");
        extensionToMimetypeMap.put("sit", "application/x-stuffit");
        extensionToMimetypeMap.put("slb", "application/x-slb");
        extensionToMimetypeMap.put("sld", "application/x-sld");
        extensionToMimetypeMap.put("slk", "drawing/x-slk");
        extensionToMimetypeMap.put("smi", "application/smil");
        extensionToMimetypeMap.put("smil", "application/smil");
        extensionToMimetypeMap.put("smk", "application/x-smk");
        extensionToMimetypeMap.put("snd", "audio/basic");
        extensionToMimetypeMap.put("sol", "text/plain");
        extensionToMimetypeMap.put("sor", "text/plain");
        extensionToMimetypeMap.put("spc", "application/x-pkcs7-certificates");
        extensionToMimetypeMap.put("spl", "application/futuresplash");
        extensionToMimetypeMap.put("spp", "text/xml");
        extensionToMimetypeMap.put("ssm", "application/streamingmedia");
        extensionToMimetypeMap.put("sst", "application/vnd.ms-pki.certstore");
        extensionToMimetypeMap.put("stl", "application/vnd.ms-pki.stl");
        extensionToMimetypeMap.put("stm", "text/html");
        extensionToMimetypeMap.put("sty", "application/x-sty");
        extensionToMimetypeMap.put("svg", "image/svg+xml");
        extensionToMimetypeMap.put("swf", "application/x-shockwave-flash");
        extensionToMimetypeMap.put("tar", "application/x-tar");
        extensionToMimetypeMap.put("tdf", "application/x-tdf");
        extensionToMimetypeMap.put("tg4", "application/x-tg4");
        extensionToMimetypeMap.put("tga", "application/x-tga");
        extensionToMimetypeMap.put("tif", "image/tiff");
        extensionToMimetypeMap.put("tiff", "image/tiff");
        extensionToMimetypeMap.put("tld", "text/xml");
        extensionToMimetypeMap.put("top", "drawing/x-top");
        extensionToMimetypeMap.put("torrent", "application/x-bittorrent");
        extensionToMimetypeMap.put("tsd", "text/xml");
        extensionToMimetypeMap.put("ttf", "application/x-font-ttf");
        extensionToMimetypeMap.put("txt", "text/plain");
        extensionToMimetypeMap.put("uin", "application/x-icq");
        extensionToMimetypeMap.put("uls", "text/iuls");
        extensionToMimetypeMap.put("vcf", "text/x-vcard");
        extensionToMimetypeMap.put("vda", "application/x-vda");
        extensionToMimetypeMap.put("vdx", "application/vnd.visio");
        extensionToMimetypeMap.put("vml", "text/xml");
        extensionToMimetypeMap.put("vpg", "application/x-vpeg005");
        extensionToMimetypeMap.put("vsd", "application/vnd.visio");
        extensionToMimetypeMap.put("vss", "application/vnd.visio");
        extensionToMimetypeMap.put("vst", "application/x-vst");
        extensionToMimetypeMap.put("vsw", "application/vnd.visio");
        extensionToMimetypeMap.put("vsx", "application/vnd.visio");
        extensionToMimetypeMap.put("vtx", "application/vnd.visio");
        extensionToMimetypeMap.put("vxml", "text/xml");
        extensionToMimetypeMap.put("wav", "audio/wav");
        extensionToMimetypeMap.put("wax", "audio/x-ms-wax");
        extensionToMimetypeMap.put("wb1", "application/x-wb1");
        extensionToMimetypeMap.put("wb2", "application/x-wb2");
        extensionToMimetypeMap.put("wb3", "application/x-wb3");
        extensionToMimetypeMap.put("wbmp", "image/vnd.wap.wbmp");
        extensionToMimetypeMap.put("webm", "video/webm");
        extensionToMimetypeMap.put("wiz", "application/msword");
        extensionToMimetypeMap.put("wk3", "application/x-wk3");
        extensionToMimetypeMap.put("wk4", "application/x-wk4");
        extensionToMimetypeMap.put("wkq", "application/x-wkq");
        extensionToMimetypeMap.put("wks", "application/x-wks");
        extensionToMimetypeMap.put("wm", "video/x-ms-wm");
        extensionToMimetypeMap.put("wma", "audio/x-ms-wma");
        extensionToMimetypeMap.put("wmd", "application/x-ms-wmd");
        extensionToMimetypeMap.put("wmf", "application/x-wmf");
        extensionToMimetypeMap.put("wml", "text/vnd.wap.wml");
        extensionToMimetypeMap.put("wmv", "video/x-ms-wmv");
        extensionToMimetypeMap.put("wmx", "video/x-ms-wmx");
        extensionToMimetypeMap.put("wmz", "application/x-ms-wmz");
        extensionToMimetypeMap.put("woff", "application/x-font-woff");
        extensionToMimetypeMap.put("wp6", "application/x-wp6");
        extensionToMimetypeMap.put("wpd", "application/x-wpd");
        extensionToMimetypeMap.put("wpg", "application/x-wpg");
        extensionToMimetypeMap.put("wpl", "application/vnd.ms-wpl");
        extensionToMimetypeMap.put("wq1", "application/x-wq1");
        extensionToMimetypeMap.put("wr1", "application/x-wr1");
        extensionToMimetypeMap.put("wri", "application/x-wri");
        extensionToMimetypeMap.put("wrk", "application/x-wrk");
        extensionToMimetypeMap.put("ws", "application/x-ws");
        extensionToMimetypeMap.put("ws2", "application/x-ws");
        extensionToMimetypeMap.put("wsc", "text/scriptlet");
        extensionToMimetypeMap.put("wsdl", "text/xml");
        extensionToMimetypeMap.put("wvx", "video/x-ms-wvx");
        extensionToMimetypeMap.put("x_b", "application/x-x_b");
        extensionToMimetypeMap.put("x_t", "application/x-x_t");
        extensionToMimetypeMap.put("xap", "application/x-silverlight-app");
        extensionToMimetypeMap.put("xbm", "image/x-xbitmap");
        extensionToMimetypeMap.put("xdp", "application/vnd.adobe.xdp");
        extensionToMimetypeMap.put("xdr", "text/xml");
        extensionToMimetypeMap.put("xfd", "application/vnd.adobe.xfd");
        extensionToMimetypeMap.put("xfdf", "application/vnd.adobe.xfdf");
        extensionToMimetypeMap.put("xhtml", "text/html");
        extensionToMimetypeMap.put("xls", "application/vnd.ms-excel");
        extensionToMimetypeMap.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        extensionToMimetypeMap.put("xlw", "application/x-xlw");
        extensionToMimetypeMap.put("xml", "text/xml");
        extensionToMimetypeMap.put("xpl", "audio/scpls");
        extensionToMimetypeMap.put("xpm", "image/x-xpixmap");
        extensionToMimetypeMap.put("xq", "text/xml");
        extensionToMimetypeMap.put("xql", "text/xml");
        extensionToMimetypeMap.put("xquery", "text/xml");
        extensionToMimetypeMap.put("xsd", "text/xml");
        extensionToMimetypeMap.put("xsl", "text/xml");
        extensionToMimetypeMap.put("xslt", "text/xml");
        extensionToMimetypeMap.put("xwd", "application/x-xwd");
        extensionToMimetypeMap.put("yaml", "text/yaml");
        extensionToMimetypeMap.put("yml", "text/yaml");
        extensionToMimetypeMap.put("zip", "application/zip");

    }
    
    private static class MimetypesHolder
    {
        private static Mimetypes mimetypes = new Mimetypes();
    }
    
    public static Mimetypes getInstance()
    {
        return MimetypesHolder.mimetypes;
    }
    
    
    public String getMimetype(String fileName)
    {
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
                return mimetype;
            }
        }
        return mimetype;
    }
    
    public String getMimetype(File file)
    {
        return getMimetype(file.getName());
    }
}
