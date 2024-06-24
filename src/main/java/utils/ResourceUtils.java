package utils;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class ResourceUtils {

    /**
     * Obtém o caminho completo de um recurso localizado na pasta "src/main/resources/".
     *
     * @param resource Nome do recurso a ser localizado.
     * @return Caminho completo do recurso.
     * @throws IllegalArgumentException Se o recurso não for encontrado.
     */
    public static String getResourcePath(String resource) {
        String basePath = "src/main/resources/";
        File file = FileUtils.getFile(basePath + resource);

        if (!file.isFile()) {
            throw new IllegalArgumentException("Recurso não encontrado: " + resource);
        }

        System.out.println("O caminho existe: " + file.getPath());
        return file.getPath();
    }
}
