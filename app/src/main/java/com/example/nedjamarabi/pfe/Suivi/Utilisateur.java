package com.example.nedjamarabi.pfe.Suivi;

import java.io.Serializable;

public class Utilisateur implements Serializable {
    private String idUtilisateur;
    private String username, dateInscription;
    private int age, taille;
    private float poids;
    private float poidsDesire;
    private Sexe sexe;
    private ActivitePhysique activite;
    private Morphotype morphotype;
    private Niveau niveau;
    private Objectif objectif;
    
    public Utilisateur() {
    }
    
    public Utilisateur(String idUtilisateur, String username, String dateInscription, int age, int taille, float poids, float poidsDesire, Sexe sexe, ActivitePhysique activite, Morphotype morphotype, Niveau niveau, Objectif objectif) {
        this.idUtilisateur = idUtilisateur;
        this.username = username;
        this.dateInscription = dateInscription;
        this.age = age;
        this.taille = taille;
        this.poids = poids;
        this.poidsDesire = poidsDesire;
        this.sexe = sexe;
        this.activite = activite;
        this.morphotype = morphotype;
        this.niveau = niveau;
        this.objectif = objectif;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getDateInscription() {
        return dateInscription;
    }
    
    public void setDateInscription(String dateInscription) {
        this.dateInscription = dateInscription;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public int getTaille() {
        return taille;
    }
    
    public void setTaille(int taille) {
        this.taille = taille;
    }
    
    public float getPoids() {
        return poids;
    }
    
    public void setPoids(float poids) {
        this.poids = poids;
    }
    
    public float getPoidsDesire() {
        return poidsDesire;
    }
    
    public void setPoidsDesire(float poidsDesire) {
        this.poidsDesire = poidsDesire;
    }
    
    public Sexe getSexe() {
        return sexe;
    }
    
    public void setSexe(Sexe sexe) {
        this.sexe = sexe;
    }
    
    public ActivitePhysique getActivite() {
        return activite;
    }
    
    public void setActivite(ActivitePhysique activite) {
        this.activite = activite;
    }
    
    public String getIdUtilisateur() {
        return idUtilisateur;
    }
    
    public void setIdUtilisateur(String idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }
    
    public Morphotype getMorphotype() {
        return morphotype;
    }
    
    public void setMorphotype(Morphotype morphotype) {
        this.morphotype = morphotype;
    }
    
    public Niveau getNiveau() {
        return niveau;
    }
    
    public void setNiveau(Niveau niveau) {
        this.niveau = niveau;
    }
    
    public Objectif getObjectif() {
        return objectif;
    }
    
    public void setObjectif(Objectif objectif) {
        this.objectif = objectif;
    }
    
    public float getIMC() {
        return (float) (poids / Math.pow(taille, 2));
    }
    
    public float calculMetabolismeBase() {
        float besoin = 0;
        switch (sexe) {
            case HOMME:
                besoin = 13.707f * poids + 4.923f * taille - 6.673f * age + 77.607f;
                break;
            case FEMME:
                besoin = 9.740f * poids + 1.729f * taille - 4.737f * age + 667.051f;
                break;
        }
        switch (activite) {
            case LEGERE:
                besoin *= 1.56;
                break;
            case MODEREE:
                besoin *= 1.64;
                break;
            case INTENSE:
                besoin *= 1.82;
                break;
        }
        return besoin;
    }
    
    @Override
    public String toString() {
        return "Utilisateur{" + "idUtilisateur='" + idUtilisateur + '\'' + ", username='" + username + '\'' + ", dateInscription='" + dateInscription + '\'' + ", age=" + age + ", taille=" + taille + ", poids=" + poids + ", poidsDesire=" + poidsDesire + ", sexe=" + sexe + ", activite=" + activite + ", morphotype=" + morphotype + ", niveau=" + niveau + ", objectif=" + objectif + '}';
    }
}
