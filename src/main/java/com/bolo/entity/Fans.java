package com.bolo.entity;

import java.util.List;
import java.util.Map;

public class Fans {


    double female_occupation;

    public double getFemale_occupation() {
        return female_occupation;
    }

    public void setFemale_occupation(double female_occupation) {
        this.female_occupation = female_occupation;
    }

    public double getMale_occupation() {
        return male_occupation;
    }

    public void setMale_occupation(double male_occupation) {
        this.male_occupation = male_occupation;
    }

    public List<String> getAgeMap() {
        return ageMap;
    }

    public void setAgeMap(List<String> ageMap) {
        this.ageMap = ageMap;
    }

    public Map<String, Double> getAddress() {
        return address;
    }

    public void setAddress(Map<String, Double> address) {
        this.address = address;
    }

    double male_occupation;

    List<String> ageMap;

    Map<String,Double> address;

}
