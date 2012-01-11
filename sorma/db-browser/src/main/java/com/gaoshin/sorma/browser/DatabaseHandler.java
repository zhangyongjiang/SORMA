package com.gaoshin.sorma.browser;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import android.database.Cursor;

import com.gaoshin.sorma.SormaContentResolver;
import com.gaoshin.sorma.browser.NanoHTTPD.Response;
import com.gaoshin.sorma.core.DbTable;

public class DatabaseHandler implements HttpHandler {
    private SormaContentResolver contentResolver;
    private String baseUri;

    public DatabaseHandler(SormaContentResolver contentResolver, String baseUri) {
        this.contentResolver = contentResolver;
        if(!baseUri.endsWith("/")) {
            baseUri += "/";
        }
        this.baseUri = baseUri;
    }

    private String getUri(String table) {
        return baseUri + table;
    }

    public Response serve(String uri, String method, Properties header,
            Properties parms, Properties files) {
        try {
            String table = parms.getProperty("from");
            if (table == null) {
                return showTables(uri, method, header, parms, files);
            } else
                return showTableContent(uri, method, header, parms, files);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return new NanoHTTPD.Response(NanoHTTPD.HTTP_INTERNALERROR,
                    NanoHTTPD.MIME_HTML, "<pre>" + sw.toString() + "</pre>");
        }
    }

    private Response showTableContent(String uri, String method,
            Properties header, Properties parms, Properties files) throws IOException {
        String table = parms.getProperty("from");
        String detail = parms.getProperty("detail");
        
        String offset = parms.getProperty("offset");
        int iOffset = 0;
        if(offset != null && offset.length() > 0)
            iOffset = Integer.parseInt(offset);

        String size = parms.getProperty("size");
        int iSize = Integer.MAX_VALUE;
        if(size != null && size.length() > 0)
            iSize = Integer.parseInt(size);

        String order = parms.getProperty("order");
        String selection = parms.getProperty("select");

        String columns = parms.getProperty("where");
        String[] projection = null;
        if (columns != null)
            projection = columns.split(",");

        String filter = parms.getProperty("filter");
        String[] selectionArgs = null;
        if (filter != null) {
            selectionArgs = filter.split(",");
        }

        String contentProviderUri = null;
        if (table.startsWith("content://"))
            contentProviderUri = table;
        else
            contentProviderUri = getUri(table);

        Cursor cursor = contentResolver.query(contentProviderUri, projection,
                selection, selectionArgs, order);

        TableContentCursorInputStream cursorInputStream = new TableContentCursorInputStream(
                cursor, iOffset, iSize, (detail != null && detail.trim().length()>0));
        return new NanoHTTPD.Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_HTML,
                cursorInputStream);
    }

    private Response showTables(String uri, String method, Properties header,
            Properties parms, Properties files) {
        String cpuri = getUri("sqlite_master");
        Cursor cursor = contentResolver.query(cpuri, null, null,
                null, "LOWER(name)");
        SqliteMasterList list = new SqliteMasterList();
        try {
            DbTable<SqliteMaster> table = new DbTable<SqliteMaster>(SqliteMaster.class);
            if(cursor != null) {
                while(cursor.moveToNext()) {
                    SqliteMaster sm = table.getObjectFromRow(cursor);
                    list.getList().add(sm);
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if(cursor != null)
                cursor.close();
        }
        return new NanoHTTPD.Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_HTML,
                JsonUtil.toJsonString(list));
    }

}
