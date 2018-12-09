package de.legaltech.seminar;

import de.legaltech.seminar.entities.MetaData;

public interface IPreprocessor {
    MetaData extractMetaData(String filecontent, int legalFileId);
}
