package de.legaltech.seminar;

import de.legaltech.seminar.entities.LegalFile;
import de.legaltech.seminar.entities.NamedEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

public class DbManager {

    protected static DbManager db;

    protected static String driverName = "oracle.jdbc.driver.OracleDriver";
    protected static String DbName = "legalDB";
    protected static String connectionUrl = "Server=localhost\\SQLEXPRESS;Database=master;Trusted_Connection=True";
    private static String username = "";
    protected static String password = "";

    private Connection conn;

    private DbManager(){
        try{
            Class.forName(driverName);
            conn = DriverManager.getConnection(connectionUrl, username, password);
            conn.setAutoCommit(false);
        }catch(SQLException sqlEx){

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static DbManager Instance(){
        if(db == null){
            db = new DbManager();
        }
        return db;
    }

    public void close(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            conn = null;
        }
    }

    public void saveResult(LegalFile legalFile, ClassificationResult classificationResult) throws SQLException, IOException {
        String insertLf = "INSERT INTO LegalFile (Filename, IsProcessed, ContentBlob) VALUES (?, 'true', ?)";
        PreparedStatement lfStmt = conn.prepareStatement(insertLf);
        lfStmt.setString(1, legalFile.getFilename());
        File file = new File(legalFile.getFilename());
        FileInputStream fis = new FileInputStream(file);
        lfStmt.setBinaryStream(2, fis, (int) file.length());
        lfStmt.execute();
        conn.commit();
        fis.close();
        for (NamedEntity p : legalFile.getNamedentities()) {
            saveNamedEntity(p);
        }
        String insertCr = "INSERT INTO ClassificationResult (Filename, Year, DocumentTags) VALUES (?, ?, ?)";

        String insertCrNe = "INSERT INTO ClassificationResultEntities (CRID, NEID) VALUES (?, ?)";
    }

    public int saveNamedEntity(NamedEntity nE) throws SQLException {
        String insertNe = "INSERT INTO NamedEntity(Tag, Value, StartInText, EndInText) VALUES (?, ?, ?, ?)";
        PreparedStatement prepStmt = conn.prepareStatement(insertNe);
        prepStmt.setString(1, nE.getTag().toString());
        prepStmt.setString(2, nE.getValue());
        prepStmt.setInt(3, nE.getStartInText());
        prepStmt.setInt(4, nE.getEndInText());
        ResultSet rs = prepStmt.executeQuery();
        conn.commit();
        if(rs.first()){
            return rs.getInt(0);
        }
        return -1;
    }
}
