import controlador.BaseDades;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;

import java.util.HashMap;
import java.util.List;

public class ProvesMain {
    public static void main(String[] args) {
        BaseDades bd = new BaseDades("localhost", 27017, "IFC32C", "llibres");


//1. Definir un mètode que rebi per paràmetre un HashMap del llibre que s'ha d'afegir a la col·lecció. Si el HashMap és nul, ha de mostrar un missatge d'error. Prova´l creant un llibre amb tots els seus camp, o sia, isbn, títol, departament i autors (com a mínim 2).
       // Crear un llibre amb tots els camps necessaris
/*
        HashMap<String, Object> llibre = new HashMap<>();
        llibre.put("isbn", "978-3-16-14810-0");
        llibre.put("titol", "El Gran Llibre de MongoDB Extension");
        llibre.put("departament", "Informàtica");
        llibre.put("autors", List.of(
                new Document("idAut", 1).append("nomAut", "Autor 1"),
                new Document("idAut", 2).append("nomAut", "Autor 2")
        ));
        //Afegir el llibre a la base de dades
        bd.afegirLlibre(llibre);
*/

//2. Definir un mètode que rebi per paràmetre una llista dels noms de camps que s'han d'incloure en el resultat ordenats pel primer camp d'aquesta llista. El camp _id sempre s’ha d’excloure. Ha de tornar la llista de documents trobats en format json.
/*
        //Definir els camps a incloure (el primer s'utilitzarà per ordenar)
        List<String> camps = List.of("titol", "isbn", "departament");

        //Cercar llibres amb aquests camps
        List<String> resultats = bd.cercarLlibresAmbCamps(camps);

        //Mostrar els resultats
        System.out.println("Llibres trobats:");
        resultats.forEach(System.out::println);

 */

//3. Definir un mètode que rebi per paràmetre el nom d'un camp de la col·lecció de llibres i retorni el número total de documents on NO se troba aquest camp (per exemple, el camp departament NO hi és en 88 documents).
/*
        bd.comptarDocumentsSenseCamp("departament");
*/

//4. Definir un mètode que permeti executar agregacions. Ha de rebre la llista de "stages" i retornarà la llista de documents trobats. Prova’l amb un exemple de 3 etapes.

        // Exemple d'ús del mètode executarAgregacio amb 3 etapes
/*
        List<Document> stages = List.of(
                new Document("$match", new Document("departament", "Informàtica")),
                new Document("$group", new Document("_id", "$departament").append("total", new Document("$sum", 1))),
                new Document("$sort", new Document("total", -1))
        );

        List<Document> resultatsAgregacio = bd.executarAgregacio(stages);

        System.out.println("Resultats de l'agregació:");
        resultatsAgregacio.forEach(doc -> System.out.println(doc.toJson(JsonWriterSettings.builder().indent(true).build())));

*/

    //5.Definir un mètode que té un paràmetre de tipus string i ha de retornar una llista dels titols dels libres que contenen aquesta paraula.
        System.out.println(bd.cercarPerTitol("libro"));
    }

}
