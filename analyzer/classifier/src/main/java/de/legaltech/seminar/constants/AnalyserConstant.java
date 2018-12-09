package de.legaltech.seminar.constants;

public class AnalyserConstant {

    public static final String basePath = System.getenv("LOCALAPPDATA") + "/LegalTech/";
    public static String docRootPath = basePath + "data/files/";
    public static String classifierRootPath = basePath + "data/classifiers/";
    public static String outputRootPath = basePath + "output/";

    public static String fileBlobProcessed = docRootPath + "blob/processed/";
    public static String fileBlobUnprocessed = docRootPath + "blob/unprocessed/";


    public static final String HEURISTIC_TRAINED_VECTORS_FILE = outputRootPath + "/heuristic.txt";
    public static final String CSC_TSV_FILE_MAX = classifierRootPath + "15_Y-300-Z-BECKRS-B-2018-N-19590.tsv";
}
