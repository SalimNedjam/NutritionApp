package com.deepside.nutritionapp.Utils;

/**
 * Created by Samy on 27/03/2018.
 */

public class OccurenceRechercheAliment {
    private Long idAliment;
    private Integer nbRecherches;
    
    public OccurenceRechercheAliment(Long idAliment, Integer nbRecherches) {
        this.idAliment = idAliment;
        this.nbRecherches = nbRecherches;
    }
    
    public Long getIdAliment() {
        return idAliment;
    }
    
    public void setIdAliment(Long idAliment) {
        this.idAliment = idAliment;
    }
    
    public Integer getNbRecherches() {
        return nbRecherches;
    }
    
    public void setNbRecherches(Integer nbRecherches) {
        this.nbRecherches = nbRecherches;
    }
    
    @Override
    public String toString() {
        return "OccurenceRechercheAliment{" + "idAliment=" + idAliment + ", nbRecherches=" + nbRecherches + '}';
    }
}