package de.legaltech.seminar;

import de.legaltech.seminar.entities.ClassificationResult;
import de.legaltech.seminar.entities.LegalFile;
import de.legaltech.seminar.entities.MetaData;
import de.legaltech.seminar.entities.NamedEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

public class DbManager {

    protected static DbManager db;

    protected static String driverName = "oracle.jdbc.driver.OracleDriver";
    protected static String DbName = "legalDB";
    protected static String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=legalDB;Trusted_Connection=True";
    private static String username = "";
    protected static String password = "";

    private Connection conn;

    private DbManager(){
        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(connectionUrl, username, password);
            conn.setAutoCommit(false);
        }catch(SQLException sqlEx){
            sqlEx.printStackTrace();
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

    public void saveClassifiedFile(LegalFile file) {
        int id = saveLegalFile(file);
        if(id != -1){
            file.setId(id);
            file.setSaved(true);
        }
        saveMetaData(file.getMetaData());
        for (IClassifier classifier : file.classificationResultMap.keySet()) {
            ClassificationResult classifiactionResult = file.classificationResultMap.get(classifier);
            int resultId = saveResult(classifiactionResult);
            classifiactionResult.setId(resultId);
            for (NamedEntity nE : classifiactionResult.getNamedEntities()) {
                int nEId = saveNamedEntity(nE);
                nE.setId(nEId);
                insertCRNE(resultId, nEId);
            }
        }
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

    private void insertCRNE(int resultId, int nEId) {
        String sql = "INSERT INTO ClassificationResultEntities (CRID, NEID) VALUES (?, ?)";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, resultId);
            stmt.setInt(2, nEId);
            stmt.execute();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int saveLegalFile(LegalFile file){
        String sql = "INSERT INTO LegalFile (Filename, IsProcessed, Content) VALUES (?, 'true', ?)";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, file.getFilename());
            stmt.setString(2, file.getContent());
            stmt.execute();
            conn.commit();
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.first()){
                return rs.getInt(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int saveResult(ClassificationResult classificationResult) {
        String insertCr = "INSERT INTO ClassificationResult (Filename, [Year], DocumentTags, Classifier) VALUES (?, ?, ?, ?)";
        PreparedStatement prepStmt = null;
        try {
            prepStmt = conn.prepareStatement(insertCr);
            prepStmt.setString(1, classificationResult.getFileName());
            prepStmt.setInt(2, classificationResult.getYear());
            prepStmt.setString(3, classificationResult.getTags());
            prepStmt.setString(4, classificationResult.getClassifier());
            prepStmt.execute();
            conn.commit();
            ResultSet rs = prepStmt.getGeneratedKeys();
            if(rs.first()){
                return rs.getInt(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int saveNamedEntity(NamedEntity nE) {
        String insertNe = "INSERT INTO NamedEntity(Tag, [Value], StartInText, EndInText) VALUES (?, ?, ?, ?)";
        PreparedStatement prepStmt = null;
        try {
            prepStmt = conn.prepareStatement(insertNe);
            prepStmt.setString(1, nE.getTag().toString());
            prepStmt.setString(2, nE.getValue());
            prepStmt.setInt(3, nE.getStartInText());
            prepStmt.setInt(4, nE.getEndInText());
            prepStmt.execute();
            conn.commit();
            ResultSet rs = prepStmt.getGeneratedKeys();
            if(rs.first()){
                return rs.getInt(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void saveMetaData(MetaData meta) {
        String insertMeta = "INSERT INTO MetaData(ClaimerRequest, DefendantRequest, Decision, Title, MatterOfFact) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement prepStmt = null;
        try {
            prepStmt = conn.prepareStatement(insertMeta);
            prepStmt.setString(1, meta.getClaimerRequest());
            prepStmt.setString(2, meta.getDefendantRequest());
            prepStmt.setString(3, meta.getDecision());
            prepStmt.setString(4, meta.getTitle());
            prepStmt.setString(5, meta.getMatterOfFact());
            prepStmt.execute();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
