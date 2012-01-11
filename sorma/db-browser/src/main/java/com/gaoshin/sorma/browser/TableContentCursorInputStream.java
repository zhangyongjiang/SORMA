package com.gaoshin.sorma.browser;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import android.database.Cursor;

public class TableContentCursorInputStream extends InputStream {
    private Cursor cursor;
    private boolean init = true;
    private boolean firstRecord = true;
    private boolean done = false;
    private int size;
    private int offset;
    private int currentRow;
    private boolean showDetails = false;
    private LinkedList<Byte> queue = new LinkedList<Byte>();

    public TableContentCursorInputStream(Cursor cursor, int offset, int size, boolean showDetails) throws IOException {
        this.cursor = cursor;
        this.showDetails = showDetails;
        if(offset > 0 && cursor!=null) {
            cursor.move(offset);
        }
        this.size = size;
        this.offset = offset;
        currentRow = -1;
    }
    
    @Override
    public int read() throws IOException {
        if (queue.size() == 0) {
            readMore();
        }
        if (queue.size() == 0) {
            return -1;
        }

        return queue.removeFirst().intValue();
    }

    private void readMore() throws IOException {
        if(done) {
            return;
        }
        
        if (init) {
            init = false;
            String header = "<html><body><table style='border:solid 1px;'>";
            putToQueue(header);
            return;
        }

        StringBuilder sb = new StringBuilder();
        if (cursor.moveToNext() && size>0) {
            currentRow++;
            size--;
            int count = cursor.getColumnCount();

            if (firstRecord) {
                firstRecord = false;
                sb.append("<tr>");
                for (int i = 0; i < count; i++) {
                    sb.append("<th style='border:solid 1px #ccc;padding:1px;'>");
                    sb.append(cursor.getColumnName(i));
                    sb.append("</th>");
                }
                sb.append("</tr>");
            }

            sb.append("<tr onClick='window.location=window.location.href+\"&detail=1&size=1&offset=" + (offset + currentRow) + "\"'>");
            
            for (int i = 0; i < count; i++) {
                String value;
                if (cursor.isNull(i)) {
                    value = "null";
                } else {
                    try {
                        value = cursor.getString(i);
                        if (value.length() == 0)
                            value = "&nbsp;";
                    } catch (Exception e) {
                        value = "---";
                        try {
                            byte[] content = cursor.getBlob(i);
                            if (content != null) {
                                value = "blob " + content.length;
                            }
                            content = null;
                        }
                        catch (Exception e1) {
                        }
                    }
                    int length = value.length();
                    if(!showDetails && length > 32) {
                        value = (value.substring(0, 8)) + ("...") + (value.substring(length-24, length));
                    }
                }
                String columnName = cursor.getColumnName(i);
                sb.append("<td class='" + columnName
                        + "' style='border:solid 1px #ccc;padding:1px;'>");
                sb.append(value);
                sb.append("</td>");
            }
            sb.append("</tr>");
            putToQueue(sb.toString());
        } else {
            putToQueue("</table></body></html>");
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                }
            }
            done = true;
        }
    }

    private void putToQueue(String s) throws IOException {
        for (byte b : s.getBytes()) {
            queue.add(b);
        }
    }
}
