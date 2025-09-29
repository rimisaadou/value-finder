package org.objects.finder.api;

import java.util.List;

public class ComplexObject {
    private String nameObject;
    private int ageObject;
    private Car carObject;
    private String[] arrayObject;
    private int[][] matrixObject;
    private List<String> listObject;

    public ComplexObject(String nameObject, int ageObject, Car carObject, String[] arrayObject, int[][] matrixObject, List<String> listObject) {
        this.nameObject = nameObject;
        this.ageObject = ageObject;
        this.carObject = carObject;
        this.arrayObject = arrayObject;
        this.matrixObject = matrixObject;
        this.listObject = listObject;
    }

    public String getNameObject() {
        return nameObject;
    }

    public void setNameObject(String nameObject) {
        this.nameObject = nameObject;
    }

    public int getAgeObject() {
        return ageObject;
    }

    public void setAgeObject(int ageObject) {
        this.ageObject = ageObject;
    }

    public Car getCarObject() {
        return carObject;
    }

    public void setCarObject(Car carObject) {
        this.carObject = carObject;
    }

    public String[] getArrayObject() {
        return arrayObject;
    }

    public void setArrayObject(String[] arrayObject) {
        this.arrayObject = arrayObject;
    }

    public int[][] getMatrixObject() {
        return matrixObject;
    }

    public void setMatrixObject(int[][] matrixObject) {
        this.matrixObject = matrixObject;
    }

    public List<String> getListObject() {
        return listObject;
    }

    public void setListObject(List<String> listObject) {
        this.listObject = listObject;
    }
}
