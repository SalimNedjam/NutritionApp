package com.example.nedjamarabi.pfe.Suivi;

public class Conseil {
    private long idConseil;
    private String description;
    
    
    public Conseil() {
    }
    
    public Conseil(long idConseil, String description) {
        this.idConseil = idConseil;
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public long getIdConseil() {
        return idConseil;
    }
    
    public void setIdConseil(long idConseil) {
        this.idConseil = idConseil;
    }
    
    @Override
    public String toString() {
        return "Conseil{" + "idConseil=" + idConseil + ", description='" + description + '\'' + '}';
    }
}
