package de.jawb.jmh.benchmark.example.json.model;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.github.benas.randombeans.api.EnhancedRandom;

public class PersonLoader {

    public enum Mapper {
        Jackson,
        Gson;
    }

    private static String rawData;

    static {
        try {
            InputStream stream = PersonLoader.class.getClassLoader().getResourceAsStream("persons.json");
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = stream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            rawData = result.toString("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }


    public static List<Person> load(Mapper mapper) {

        Reader stream = new StringReader(rawData);

        if (mapper == Mapper.Jackson) {

            ObjectMapper m = new ObjectMapper();
            try {
                CollectionType mappingType = m.getTypeFactory().constructCollectionType(ArrayList.class, Person.class);
                return m.readValue(stream, mappingType);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else if (mapper == Mapper.Gson) {

            try {
                Type listType = new TypeToken<List<Person>>() {}.getType();
                return new Gson().fromJson(stream, listType);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        return null;

    }

    public static void generate() throws Exception {
        List<Person> persons = EnhancedRandom.randomListOf(1000, Person.class);
        ObjectMapper m = new ObjectMapper();
        //        m.enable(SerializationFeature.INDENT_OUTPUT);

        try (PrintWriter out = new PrintWriter("filename.txt")){
            out.println(m.writeValueAsString(persons));
        }
    }

    public static void main(String[] args) throws Exception {
        generate();
    }
}
