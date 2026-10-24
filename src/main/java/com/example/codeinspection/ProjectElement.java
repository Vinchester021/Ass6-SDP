package com.example.codeinspection;

public interface ProjectElement {

    void accept(ProjectVisitor visitor);
}