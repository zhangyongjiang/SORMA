package com.gaoshin.sorma.browser;

import java.util.Properties;

public interface HttpHandler {

    NanoHTTPD.Response serve(String uri, String method, Properties header,
            Properties parms, Properties files);
}
