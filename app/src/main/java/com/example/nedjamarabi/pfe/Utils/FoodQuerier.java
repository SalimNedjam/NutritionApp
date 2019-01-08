package com.example.nedjamarabi.pfe.Utils;

import android.content.Context;

import com.example.nedjamarabi.pfe.Suivi.Aliment;
import com.example.nedjamarabi.pfe.Suivi.TypeAliment;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

import java.util.ArrayList;


class FoodQuerier {
    private final String prefixes = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "PREFIX owl: <http://www.w3.org/2002/07/owl#>" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX food: <http://data.lirmm.fr/ontologies/food#>" + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>";
    private final String queryBody = "SELECT ?codebarre ?nom ?calories ?proteines ?lipides ?glucides ?ingredientPrincipal\n" + "WHERE {\n" + "?aliment rdf:type food:FoodProduct.\n" + "?aliment food:code ?codebarre.\n" + "?aliment food:name ?nom.\n" + "?aliment food:energyPer100g ?calories.\n" + "?aliment food:proteinsPer100g ?proteines.\n" + "?aliment food:carbohydratesPer100g ?glucides.\n" + "?aliment food:fatPer100g ?lipides.\n" + "?aliment food:containsIngredient ?x.\n" + "?x food:rank \"1\".\n" + "?x food:food ?ingredientPrincipal";
    private int limit;
    public Model model;
    private String url = "http://localhost:3030/dataset.html?tab=query&ds=/ds";
    
    public FoodQuerier(String url, Context context) {
        limit = 10;
        this.url = url;
        //Remove comment when querying to a raw .rdf file
//        model = ModelFactory.createDefaultModel();
//        AssetManager am = context.getAssets();
//        try {
//            InputStream is = am.open("mini_base.rdf");
//            model.read(is, null);
//            Toast.makeText(context, "Fichier lu avec succès", Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            Toast.makeText(context, "Erreur lors de l'ouverture du fichier", Toast.LENGTH_LONG).show();
//        }
    }
    
    public Aliment searchFoodByCode(long code) throws Exception {
        String stringQuery = prefixes + "\n" + queryBody + ".FILTER (xsd:integer(?codebarre)=" + code + ")" + "} LIMIT 1";
        ArrayList<Aliment> result = searchFood(stringQuery);
        return result.isEmpty() ? null : result.get(0);
    }
    
    public ArrayList<Aliment> searchFoodByName(String name) throws Exception {
        String stringQuery = prefixes + "\n" + queryBody + ".FILTER regex(str(?nom),\"" + name + "\",\"i\")" + "} LIMIT " + limit;
        return searchFood(stringQuery);
    }
    
    private ArrayList<Aliment> searchFood(String stringQuery) {
        ArrayList<Aliment> aliments = new ArrayList<>();
        Query query = QueryFactory.create(stringQuery);
        QueryExecution queryExec = QueryExecutionFactory.sparqlService(url, query);
        //Replace by the following instruction when querying to a raw .rdf file
        //QueryExecution queryExec = QueryExecutionFactory.create(query, model);
        ResultSet rs = queryExec.execSelect();
        while (rs.hasNext()) {
            Aliment a = new Aliment();
            QuerySolution sol = rs.nextSolution();
            a.setIdAliment(Long.parseLong(sol.getLiteral("codebarre").toString()));
            a.setNom(sol.getLiteral("nom").toString());
            a.setNbCalories(Float.parseFloat(sol.getLiteral("calories").toString()));
            a.setQuantiteProteines(Float.parseFloat(sol.getLiteral("proteines").toString()));
            a.setQuantiteGlucides(Float.parseFloat(sol.getLiteral("glucides").toString()));
            a.setQuantiteLipides(Float.parseFloat(sol.getLiteral("lipides").toString()));
            a.setTypeAliment(getTypeAliment(sol.getResource("ingredientPrincipal").toString()));
            aliments.add(a);
        }
        System.out.println(aliments);
        return aliments;
    }
    
    private TypeAliment getTypeAliment(String alimentName) {
        String name = alimentName.toLowerCase();
        return name.contains("huile") || name.contains("eau") || name.contains("lait") || name.contains("oil") || name.contains("water") || name.contains("milk") ? TypeAliment.LIQUIDE : TypeAliment.SOLIDE;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public void setLimit(int limit) {
        this.limit = limit;
    }
    
}
