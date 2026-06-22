package org.example.model.interfaces;

import org.example.model.Part;

import java.util.List;

public interface IPartDAO {

    List<Part> getAllParts();

    void addPart(String name, String article) throws Exception;

    void updatePart(int partId, String name, String article) throws Exception;

    void deletePart(int partId) throws Exception;
}