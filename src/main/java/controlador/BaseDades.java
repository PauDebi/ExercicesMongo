package controlador;

import com.mongodb.Block;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class BaseDades {
    private MongoClient client;
    private MongoDatabase db;
    private MongoCollection<Document> col;
    private Block<Document> bloc;

    public BaseDades(String host, int port, String bbdd, String colleccio) {
        client = MongoClients.create("mongodb://" + host + ":" + port);
        db = client.getDatabase(bbdd);
        col = db.getCollection(colleccio);

        //    8. Crea un atribut que sigui un Block. Inicia’l al constructor, de manera que mostri per pantalla el nif, el nom i els llinatges.
        bloc = new Block<Document>(){
            @Override
            public void apply(Document document) {
                System.out.println(document.getString("nif") + " " + document.getString("nom") + " " + document.getString("llinatges"));
            }
        };
    }

    public boolean esLlibreValid(HashMap<String, Object> llibre) {
        if (llibre == null) {
            System.out.println("Error: El llibre no pot ser nul.");
            return false;
        }
        if (!llibre.containsKey("isbn") || !llibre.containsKey("titol") || !llibre.containsKey("departament") || !llibre.containsKey("autors")) {
            System.out.println("Error: El llibre ha de contenir els camps isbn, titol, departament i autors.");
            return false;
        }
        if (!(llibre.get("autors") instanceof List) || ((List<?>) llibre.get("autors")).size() < 2) {
            System.out.println("Error: El llibre ha de contenir almenys 2 autors.");
            return false;
        }
        return true;
    }


    public void afegirLlibre(HashMap<String, Object> llibre) {
        if (!esLlibreValid(llibre)) {
            return;
        }

        // Convertir el HashMap en un Document de MongoDB
        Document doc = new Document(llibre);

        // Inserir el document a la col·lecció
        col.insertOne(doc);
        System.out.println("Llibre afegit correctament: " + llibre.get("titol"));
    }

    public List<String> cercarLlibresAmbCamps(List<String> camps) {
        List<String> resultatsJson = new ArrayList<>();

        if (camps == null || camps.isEmpty()) {
            System.out.println("Error: La llista de camps no pot estar buida.");
            return resultatsJson;
        }

        // Construir la projecció excloent `_id` i incloent els camps especificats
        Document projectFields = new Document("_id", 0);
        for (String camp : camps) {
            projectFields.append(camp, 1);
        }

        // Construir l'ordre de classificació basat en el primer camp de la llista
        Document sortCriteria = new Document(camps.getFirst(), 1); // Ordenació ascendent

        // Executar la consulta
        try (MongoCursor<Document> cursor = col.find()
                .projection(projectFields)
                .sort(sortCriteria)
                .iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                resultatsJson.add(doc.toJson(JsonWriterSettings.builder().indent(true).build()));
            }
        }
        return resultatsJson;
    }

    public long comptarDocumentsSenseCamp(String camp) {
        if (camp == null || camp.isEmpty()) {
            System.out.println("Error: El nom del camp no pot estar buit.");
            return -1;
        }

        // Consulta per comptar documents on el camp NO existeix
        long count = col.countDocuments(new Document(camp, new Document("$exists", false)));

        System.out.println("El camp '" + camp + "' NO es troba en " + count + " documents.");
        return count;
    }

    public List<Document> executarAgregacio(List<Document> stages) {
        List<Document> resultats = new ArrayList<>();

        if (stages == null || stages.isEmpty()) {
            System.out.println("Error: La llista de stages no pot estar buida.");
            return resultats;
        }

        // Executar l'agregació
        try (MongoCursor<Document> cursor = col.aggregate(stages).iterator()) {
            while (cursor.hasNext()) {
                resultats.add(cursor.next());
            }
        }

        return resultats;
    }

    public List<String> cercarPerTitol(String paraula) {
        List<String> titols = new ArrayList<>();

        if (paraula == null || paraula.isEmpty()) {
            System.out.println("Error: La paraula no pot estar buida.");
            return titols;
        }

        // Construir la consulta per cercar títols que contenen la paraula
        Document query = new Document("titol", new Document("$regex", paraula).append("$options", "i"));

        // Executar la consulta
        try (MongoCursor<Document> cursor = col.find(query).projection(new Document("titol", 1).append("_id", 0)).iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                titols.add(doc.getString("titol"));
            }
        }

        return titols;
    }

}
