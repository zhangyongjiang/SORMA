/**
 * For debug purpose. Use a http server to check the sqlite database.
 */
package com.gaoshin.sorma.browser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class HttpServer extends NanoHTTPD {
    private Map<String, HttpHandler> handlers = new Hashtable<String, HttpHandler>();
    private String root;
    
    public HttpServer(int port, String root) throws IOException {
        super(port);
        this.root = root;
    }
    
    public void registerHandler(String uri, HttpHandler handler) {
        handlers.put(uri, handler);
    }

    public Response serve(String uri, String method, Properties header,
            Properties parms, Properties files) {
        System.out.println("HttpServer:\t" + uri);
        
        // exact match
        HttpHandler handler = handlers.get(uri);
        if(handler!=null){
            return handler.serve(uri, method, header, parms, files);
        }
        
        // check static
        String mime = NanoHTTPD.getMimeType(uri);
        InputStream stream = this.getClass().getResourceAsStream(root + uri);
        if(stream != null) {
            return new Response(HTTP_OK, mime, stream);
        }
        
        // check partial match
        List<String> suburis = expand(uri);
        for(int i=suburis.size()-1; i>=0; i--) {
            handler = handlers.get(suburis.get(i));
            if(handler!=null){
                return handler.serve(uri, method, header, parms, files);
            }
        }
        
        return new NanoHTTPD.Response(HTTP_NOTFOUND, MIME_HTML, uri + " not found");
    }

    private List<String> expand(String uri) {
        String[] items = uri.split("/");
        List<String> list = new ArrayList<String>();
        list.add("/");
        String current = "";
        for(String s : items) {
            if(s.length() == 0)
                continue;
            current = current + "/" + s;
            list.add(current);
        }
        return list;
    }
}
