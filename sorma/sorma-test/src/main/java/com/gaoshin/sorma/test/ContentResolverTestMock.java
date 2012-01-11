package com.gaoshin.sorma.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import android.content.ContentValues;
import android.database.Cursor;

import com.gaoshin.sorma.SormaContentResolver;
import com.gaoshin.sorma.SqlExecutor;
import com.gaoshin.sorma.annotation.AnnotatedSchema;

public class ContentResolverTestMock implements SormaContentResolver {
    private String dbname;
    private AnnotatedSchema ormDefinition;
    
    public ContentResolverTestMock(String dbname, AnnotatedSchema ormDefinition) throws Exception {
        this.dbname = dbname;
        this.ormDefinition = ormDefinition;
        Class.forName("org.sqlite.JDBC");
        createDb();
    }
    
    private void createDb() throws Exception {
        Connection connection = getConnection();
        final Statement statement = connection.createStatement();
        ormDefinition.createDatabase(new SqlExecutor() {
            public void execute(String sql) throws Exception {
                statement.execute(sql);
            }
        });
        statement.close();
        connection.close();
    }

    public Cursor query(String uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        String table = getTableNameFromUri(uri);
        StringBuilder sb = new StringBuilder();
        
        if(table.equals("nativequery")) {
            sb.append(selection);
        }
        else {
            sb.append("select ");
            if(projection == null || projection.length ==0) {
                sb.append(" * ");
            }
            else {
                boolean first = true;
                for(String col : projection) {
                    if(first) {
                        first = false;
                    } else {
                        sb.append(",");
                    }
                    sb.append(" ").append(col);
                }
            }
            
            sb.append(" from ").append(table);
            
            if(selection != null && selection.trim().length() > 0) {
                sb.append(" where ").append(selection);
            }
            
            if(sortOrder != null && sortOrder.trim().length() > 0) {
                sb.append(" order by ").append(sortOrder);
            }
        }
        
        try {
            PreparedStatement statement = getConnection().prepareStatement(sb.toString());
            if(selectionArgs != null && selectionArgs.length > 0) {
                ParameterMetaData metaData = statement.getParameterMetaData();
                for(int i = 0; i<selectionArgs.length; i++) {
                    int columnType = metaData.getParameterType(i+1);
                    if(selectionArgs[i] == null) {
                        statement.setNull(i+1, columnType);
                        continue;
                    }
                    
                    switch(columnType) {
                    case Types.CHAR:
                    case Types.VARCHAR:
                    case Types.NVARCHAR:
                    case Types.LONGVARCHAR:
                    case Types.LONGNVARCHAR:
                        statement.setString(i+1, selectionArgs[i]);
                        break;
                    case Types.SMALLINT:
                        statement.setShort(i+1, Short.parseShort(selectionArgs[i]));
                        break;
                    case Types.INTEGER:
                        statement.setInt(i+1, Integer.parseInt(selectionArgs[i]));
                        break;
                    case Types.BIGINT:
                        statement.setLong(i+1, Integer.parseInt(selectionArgs[i]));
                        break;
                    case Types.FLOAT:
                        statement.setFloat(i+1, Float.parseFloat(selectionArgs[i]));
                        break;
                    case Types.DOUBLE:
                        statement.setDouble(i+1, Double.parseDouble(selectionArgs[i]));
                        break;
                    }
                }
            }
            ResultSet resultSet = statement.executeQuery();
            return new ResultSetCursor(resultSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long insert(String uri, ContentValues values) {
        String table = getTableNameFromUri(uri);
        StringBuilder sb = new StringBuilder();
        
        sb.append("insert into ").append(table);
        
        sb.append("(");
        boolean first = true;
        StringBuilder quesmarks = new StringBuilder();
        String[] keySet = values.getMap().keySet().toArray(new String[0]);
        for(String col : keySet) {
            if(values.get(col)==null)
                continue;
            if(first) {
                first = false;
            } else {
                sb.append(",");
                quesmarks.append(",");
            }
            sb.append(col);
            quesmarks.append("?");
        }
        sb.append(")");
        
        sb.append(" values (");
        sb.append(quesmarks.toString());
        sb.append(" )");
        
        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sb.toString());
            for(int i = 0; i<keySet.length; i++) {
                Object val = values.get(keySet[i]);
                if(val == null) {
                    continue;
                }
                
                if(val instanceof InputStream)
                    statement.setBlob(i+1, (InputStream)val);
                else if(val instanceof byte[])
                    statement.setBlob(i+1, new ByteArrayInputStream((byte[])val));
                else if(val instanceof String)
                    statement.setString(i+1, (String)val);
                else if(val instanceof Integer)
                    statement.setInt(i+1, (Integer)val);
                else if(val instanceof Long)
                    statement.setLong(i+1, (Long)val);
                else if(val instanceof Short)
                    statement.setShort(i+1, (Short)val);
                else if(val instanceof Float)
                    statement.setFloat(i+1, (Float)val);
                else if(val instanceof Double)
                    statement.setDouble(i+1, (Double)val);
                else 
                    throw new RuntimeException("unsupport type " + val.getClass());
            }
            int resultSet = statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            if(keys.next()) {
                return keys.getLong(1);
            } else {
                return resultSet;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int delete(String uri, String selection, String[] selectionArgs) {
        String table = getTableNameFromUri(uri);
        StringBuilder sb = new StringBuilder();
        
        sb.append("delete ");
        sb.append(" from ").append(table);
        
        if(selection != null && selection.trim().length() > 0) {
            sb.append(" where ").append(selection);
        }
        
        try {
            PreparedStatement statement = getConnection().prepareStatement(sb.toString());
            if(selectionArgs != null && selectionArgs.length > 0) {
                ParameterMetaData metaData = statement.getParameterMetaData();
                for(int i = 0; i<selectionArgs.length; i++) {
                    int columnType = metaData.getParameterType(i+1);
                    if(selectionArgs[i] == null) {
                        statement.setNull(i+1, columnType);
                        continue;
                    }
                    
                    switch(columnType) {
                    case Types.CHAR:
                    case Types.VARCHAR:
                    case Types.NVARCHAR:
                    case Types.LONGVARCHAR:
                    case Types.LONGNVARCHAR:
                        statement.setString(i+1, selectionArgs[i]);
                        break;
                    case Types.SMALLINT:
                        statement.setShort(i+1, Short.parseShort(selectionArgs[i]));
                        break;
                    case Types.INTEGER:
                        statement.setInt(i+1, Integer.parseInt(selectionArgs[i]));
                        break;
                    case Types.BIGINT:
                        statement.setLong(i+1, Integer.parseInt(selectionArgs[i]));
                        break;
                    case Types.FLOAT:
                        statement.setFloat(i+1, Float.parseFloat(selectionArgs[i]));
                        break;
                    case Types.DOUBLE:
                        statement.setDouble(i+1, Double.parseDouble(selectionArgs[i]));
                        break;
                    }
                }
            }
            return statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int update(String uri, ContentValues values, String selection,
            String[] selectionArgs) {
        String table = getTableNameFromUri(uri);
        StringBuilder sb = new StringBuilder();
        
        sb.append("update ");
        sb.append(table);
        sb.append(" set ");
        
        boolean first = true;
        String[] keySet = values.getMap().keySet().toArray(new String[0]);
        for(String col : keySet) {
            if(values.get(col)==null)
                continue;
            if(first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(col).append("=?");
        }
        
        if(selection != null && selection.trim().length() > 0) {
            sb.append(" where ").append(selection);
        }
        
        try {
            PreparedStatement statement = getConnection().prepareStatement(sb.toString());
            int i = 0;
            for(i = 0; i<keySet.length; i++) {
                Object val = values.get(keySet[i]);
                if(val == null) {
                    continue;
                }
                
                if(val instanceof InputStream)
                    statement.setBlob(i+1, (InputStream)val);
                else if(val instanceof byte[])
                    statement.setBlob(i+1, new ByteArrayInputStream((byte[])val));
                else if(val instanceof String)
                    statement.setString(i+1, (String)val);
                else if(val instanceof Integer)
                    statement.setInt(i+1, (Integer)val);
                else if(val instanceof Long)
                    statement.setLong(i+1, (Long)val);
                else if(val instanceof Short)
                    statement.setShort(i+1, (Short)val);
                else if(val instanceof Float)
                    statement.setFloat(i+1, (Float)val);
                else if(val instanceof Double)
                    statement.setDouble(i+1, (Double)val);
                else 
                    throw new RuntimeException("unsupport type " + val.getClass());
            }
            
            if(selectionArgs != null && selectionArgs.length > 0) {
                ParameterMetaData metaData = statement.getParameterMetaData();
                for(int j = 0; j<selectionArgs.length; j++, i++) {
                    int columnType = metaData.getParameterType(j+1);
                    if(selectionArgs[j] == null) {
                        statement.setNull(i+1, columnType);
                        continue;
                    }
                    
                    switch(columnType) {
                    case Types.CHAR:
                    case Types.VARCHAR:
                    case Types.NVARCHAR:
                    case Types.LONGVARCHAR:
                    case Types.LONGNVARCHAR:
                        statement.setString(i+1, selectionArgs[j]);
                        break;
                    case Types.SMALLINT:
                        statement.setShort(i+1, Short.parseShort(selectionArgs[j]));
                        break;
                    case Types.INTEGER:
                        statement.setInt(i+1, Integer.parseInt(selectionArgs[j]));
                        break;
                    case Types.BIGINT:
                        statement.setLong(i+1, Integer.parseInt(selectionArgs[j]));
                        break;
                    case Types.FLOAT:
                        statement.setFloat(i+1, Float.parseFloat(selectionArgs[j]));
                        break;
                    case Types.DOUBLE:
                        statement.setDouble(i+1, Double.parseDouble(selectionArgs[j]));
                        break;
                    default:
                    	throw new RuntimeException("unsupported type " + columnType);
                    }
                }
            }

            return statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getTableNameFromUri(String uri) {
        String path[] = uri.split("/");
        if(path.length>3)
            return path[3];
        else
            return path[path.length-1];
    }

    private static String getIdFromUri(String uri) {
        String path[] = uri.split("/");
        return path[4];
    }

    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbname);
        return connection;
    }

    @Override
    public void batchInsert(String uri, 
            ContentValues[] operations) throws Exception {
        for(ContentValues cv : operations) {
            insert(uri, cv);
        }
    }

}
