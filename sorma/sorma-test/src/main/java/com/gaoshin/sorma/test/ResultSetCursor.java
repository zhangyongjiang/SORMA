package com.gaoshin.sorma.test;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

public class ResultSetCursor implements Cursor {
    private ResultSet rs;
    
    public ResultSetCursor(ResultSet rs) {
        this.rs = rs;
    }
    
    @Override
    public int getCount() {
        try {
            int c = 0;
            while (rs.next())
                c++;
            return c;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public boolean move(int offset) {
        try {
            while(offset > 0) {
                boolean next = rs.next();
                if(!next)
                    return false;
                offset--;
            }
            while(offset < 0) {
                boolean previous = rs.previous();
                if(!previous)
                    return false;
                offset++;
            }
            
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean moveToPosition(int position) {
        throw new RuntimeException("not supported");
    }

    @Override
    public boolean moveToFirst() {
        try {
            return rs.first();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean moveToLast() {
        try {
            return rs.last();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean moveToNext() {
        try {
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean moveToPrevious() {
        try {
            return rs.previous();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isFirst() {
        try {
            return rs.isFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isLast() {
        try {
            return rs.isLast();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isBeforeFirst() {
        try {
            return rs.isBeforeFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isAfterLast() {
        try {
            return rs.isAfterLast();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getColumnIndex(String columnName) {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            for(int i=1; i<=metaData.getColumnCount(); i++) {
                String name = metaData.getColumnName(i);
                if(name.equalsIgnoreCase(columnName)) {
                    return i;
                }
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getColumnIndexOrThrow(String columnName)
            throws IllegalArgumentException {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            for(int i=1; i<=metaData.getColumnCount(); i++) {
                String name = metaData.getColumnName(i);
                if(name.equalsIgnoreCase(columnName)) {
                    return i;
                }
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            String name = metaData.getColumnName(columnIndex);
            return name;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] getColumnNames() {
        throw new RuntimeException("not supported");
    }

    @Override
    public int getColumnCount() {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            return metaData.getColumnCount();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] getBlob(int columnIndex) {
        try {
            return rs.getBytes(columnIndex);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getString(int columnIndex) {
        try {
            return rs.getString(columnIndex);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
        throw new RuntimeException("unsupported");
    }

    @Override
    public short getShort(int columnIndex) {
        try {
            return rs.getShort(columnIndex);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getInt(int columnIndex) {
        try {
            return rs.getInt(columnIndex);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getLong(int columnIndex) {
        try {
            return rs.getLong(columnIndex);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public float getFloat(int columnIndex) {
        try {
            return rs.getFloat(columnIndex);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getDouble(int columnIndex) {
        try {
            return rs.getDouble(columnIndex);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isNull(int columnIndex) {
        try {
            return rs.getString(columnIndex) == null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deactivate() {
        throw new RuntimeException("unsupported");
    }

    @Override
    public boolean requery() {
        throw new RuntimeException("unsupported");
    }

    @Override
    public void close() {
        try {
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isClosed() {
        try {
            return rs.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registerContentObserver(ContentObserver observer) {
        throw new RuntimeException("unsupported");
    }

    @Override
    public void unregisterContentObserver(ContentObserver observer) {
        throw new RuntimeException("unsupported");
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        throw new RuntimeException("unsupported");
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        throw new RuntimeException("unsupported");
    }

    @Override
    public void setNotificationUri(ContentResolver cr, Uri uri) {
        throw new RuntimeException("unsupported");
    }

    @Override
    public boolean getWantsAllOnMoveCalls() {
        throw new RuntimeException("unsupported");
    }

    @Override
    public Bundle getExtras() {
        throw new RuntimeException("unsupported");
    }

    @Override
    public Bundle respond(Bundle extras) {
        throw new RuntimeException("unsupported");
    }

}
