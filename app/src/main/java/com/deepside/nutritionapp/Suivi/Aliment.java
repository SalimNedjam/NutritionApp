package com.deepside.nutritionapp.Suivi;

import android.os.Parcel;
import android.os.Parcelable;

public class Aliment implements Parcelable {
    public static final Creator<Aliment> CREATOR = new Creator<Aliment>() {
        
        @Override
        public Aliment createFromParcel(Parcel parcel) {
            return new Aliment(parcel);
        }
        
        @Override
        public Aliment[] newArray(int i) {
            return new Aliment[i];
        }
    };
    private long idAliment;
    private String nom;
    private float nbCalories, quantiteGlucides, quantiteLipides, quantiteProteines;
    private TypeAliment typeAliment;
    
    
    public Aliment(long idAliment, String nom, float nbCalories, float quantiteGlucides, float quantiteLipides, float quantiteProteines, TypeAliment typeAliment) {
        this.idAliment = idAliment;
        this.nom = nom;
        this.nbCalories = nbCalories;
        this.quantiteGlucides = quantiteGlucides;
        this.quantiteLipides = quantiteLipides;
        this.quantiteProteines = quantiteProteines;
        this.typeAliment = typeAliment;
    }
    
    public Aliment() {
    }
    
    private Aliment(Parcel parcel) {
        idAliment = parcel.readLong();
        nom = parcel.readString();
        nbCalories = parcel.readFloat();
        quantiteGlucides = parcel.readFloat();
        quantiteLipides = parcel.readFloat();
        quantiteProteines = parcel.readFloat();
        typeAliment = TypeAliment.get(parcel.readInt());
    }
    
    public long getIdAliment() {
        return idAliment;
    }
    
    public void setIdAliment(long idAliment) {
        this.idAliment = idAliment;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public float getNbCalories() {
        return nbCalories;
    }
    
    public void setNbCalories(float nbCalories) {
        this.nbCalories = nbCalories;
    }
    
    public float getQuantiteGlucides() {
        return quantiteGlucides;
    }
    
    public void setQuantiteGlucides(float quantiteGlucides) {
        this.quantiteGlucides = quantiteGlucides;
    }
    
    public float getQuantiteLipides() {
        return quantiteLipides;
    }
    
    public void setQuantiteLipides(float quantiteLipides) {
        this.quantiteLipides = quantiteLipides;
    }
    
    public float getQuantiteProteines() {
        return quantiteProteines;
    }
    
    public void setQuantiteProteines(float quantiteProteines) {
        this.quantiteProteines = quantiteProteines;
    }
    
    public TypeAliment getTypeAliment() {
        return typeAliment;
    }
    
    public void setTypeAliment(TypeAliment typeAliment) {
        this.typeAliment = typeAliment;
    }
    
    @Override
    public String toString() {
        return "Aliment{" + "idAliment=" + idAliment + ", nom='" + nom + '\'' + ", nbCalories=" + nbCalories + ", quantiteGlucides=" + quantiteGlucides + ", quantiteLipides=" + quantiteLipides + ", quantiteProteines=" + quantiteProteines + ", typeAliment=" + typeAliment + '}';
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(idAliment);
        parcel.writeString(nom);
        parcel.writeFloat(nbCalories);
        parcel.writeFloat(quantiteGlucides);
        parcel.writeFloat(quantiteLipides);
        parcel.writeFloat(quantiteProteines);
        parcel.writeInt(typeAliment.ordinal());
    }
    
    public Aliment clone() {
        Aliment a = new Aliment();
        a.setIdAliment(getIdAliment());
        a.setNom(getNom());
        a.setNbCalories(getNbCalories());
        a.setQuantiteGlucides(getQuantiteGlucides());
        a.setQuantiteLipides(getQuantiteLipides());
        a.setQuantiteProteines(quantiteProteines);
        a.setTypeAliment(getTypeAliment());
        return a;
    }
}
