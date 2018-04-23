package sample;

import io.indico.Indico;
import io.indico.api.results.BatchIndicoResult;
import io.indico.api.results.IndicoResult;
import io.indico.api.utils.IndicoException;
import javafx.scene.image.Image;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class PhotoOrganiser
{
    private String[] filenames;
    private Indico indico;

    public List<Map<String, Double>> get_results()
    {
        return _results;
    }

    // Variable stores photo recognition results to be operated
    private ArrayList<Map<String, Double>> _results;

    //Variable stores names of photos in dir
    String[] fileNamesInDir;

//    public void organise(String inputPath, String outputPath) throws NoPhotosFoundException, DirectoryCreationFailedException, NoSuchDirectoryException, IndicoException, IOException
//    {
//        File folder = new File(inputPath);
//
//        if (!folder.exists())
//        {
//            throw new NoSuchDirectoryException();
//        }
//
//        File[] arrayOfFiles = folder.listFiles();
//
//        if (arrayOfFiles.length == 0)
//        {
//            throw new NoPhotosFoundException();
//        }
//
//        ArrayList<File> listOfFiles = new ArrayList<File>();
//        Pattern p = Pattern.compile(".*.jpg");
//        Matcher mat;
//
//        for (int i = 0; i < arrayOfFiles.length; i++)
//        {
//            mat = p.matcher(arrayOfFiles[i].getName());
//            if (mat.matches())
//            {
//                listOfFiles.add(arrayOfFiles[i]);
//            }
//        }
//
//        filenames = new String[listOfFiles.size()];
//
//        // Copy file paths to variable
//        for (int i = 0; i < listOfFiles.size(); i++)
//        {
//            filenames[i] = listOfFiles.get(i).getAbsolutePath();
//            System.out.println(filenames[i]);
//        }
//
//        // Image recognition
//        BatchIndicoResult multiple = indico.imageRecognition.predict(filenames);
//        List<Map<String, Double>> results = multiple.getImageRecognition();
//
//        // Vars to store most probable result
//        String maxKey;
//        Double maxVal;
//
//        // iterator to iterate over filenames that will be used in loops
//        int i = 0;
//
//        for (Map<String, Double> r : results)
//        {
//            maxKey = r.entrySet().iterator().next().getKey();
//            maxVal = r.entrySet().iterator().next().getValue();
//
////                currentPhoto = new File(inputPath + filenames[i]);
//
//            for (Map.Entry<String, Double> m : r.entrySet())
//            {
//                // Find biggest element
//                if (maxVal < m.getValue())
//                {
//                    maxKey = m.getKey();
//                    maxVal = m.getValue();
//                }
//            }
//
//            if (new File(outputPath + "/" + maxKey + "/" + listOfFiles.get(i).getName()).exists())
//            {
//                System.out.println("File exists, aborting. . .");
//                continue;
//            }
//
//            // Check if output directory exists, if not create new one
//            if (!new File(outputPath + "/" + maxKey).exists())
//            {
//                if (!new File(outputPath + "/" + maxKey).mkdirs())
//                {
//                    throw new DirectoryCreationFailedException();
//                }
//            }
//
//
//            // Write photo to appropiate loaction
//            System.out.println("[   INFO    ] Writing data:");
//            System.out.println("        Source: " + inputPath + "/" + listOfFiles.get(i).getName());
//            System.out.println("        Target: " + outputPath + "/" + maxKey + "/" + listOfFiles.get(i).getName());
//
//            Files.copy(Paths.get(inputPath + "/" + listOfFiles.get(i).getName()),
//                    Paths.get(outputPath + "/" + maxKey + "/" + listOfFiles.get(i).getName()),
//                    REPLACE_EXISTING);
//
//            i++;
//        }
//
//
//    }

    public void generateImageRecognitionResults(String inputPath) throws IOException, IndicoException
    {
        listDir(inputPath);
        for (String image: fileNamesInDir)
        {
            listPossibilities(new File(inputPath + "/" + image));
        }
    }

    public void writeOrganisedToDir(String inputPath, String outputPath) throws DirectoryCreationFailedException, IOException
    {
        // Vars to store most probable result
        String maxKey;
        Double maxVal;

        // iterator to iterate over filenames that will be used in loops
        int i = 0;

        for (Map<String, Double> r : _results)
        {
            maxKey = r.entrySet().iterator().next().getKey();
            maxVal = r.entrySet().iterator().next().getValue();

            for (Map.Entry<String, Double> m : r.entrySet())
            {
                // Find biggest element
                if (maxVal < m.getValue())
                {
                    maxKey = m.getKey();
                    maxVal = m.getValue();
                }
            }

            if (new File(outputPath + "/" + maxKey + "/" + fileNamesInDir[i]).exists())
            {
                System.out.println("File exists, aborting. . .");
                continue;
            }

            // Check if output directory exists, if not create new one
            if (!new File(outputPath + "/" + maxKey).exists())
            {
                if (!new File(outputPath + "/" + maxKey).mkdirs())
                {
                    throw new DirectoryCreationFailedException();
                }
            }


            // Write photo to appropiate loaction
            System.out.println("[   INFO    ] Writing data:");
            System.out.println("        Source: " + inputPath + "/" + fileNamesInDir[i]);
            System.out.println("        Target: " + outputPath + "/" + maxKey + "/" + fileNamesInDir[i]);

            Files.copy(Paths.get(inputPath + "/" + fileNamesInDir[i]),
                    Paths.get(outputPath + "/" + maxKey + "/" + fileNamesInDir[i]),
                    REPLACE_EXISTING);

            i++;
        }

    }

    public String[] listDir(String dir)
    {
        File folder = new File(dir);
        File[] filesInFolder = folder.listFiles();
        if(filesInFolder == null || filesInFolder.length == 0)
        {
            return null;
        }


        ArrayList<File> listOfFiles = new ArrayList<File>();
        Pattern p = Pattern.compile(".*jpg");
        Matcher mat;

        for (int i = 0; i < filesInFolder.length; i++)
        {
            mat = p.matcher(filesInFolder[i].getName());
            if (mat.matches())
            {
                listOfFiles.add(filesInFolder[i]);
            }
        }

        fileNamesInDir = new String[listOfFiles.size()];

        for (int i = 0; i < listOfFiles.size(); i++)
        {
            fileNamesInDir[i] = listOfFiles.get(i).getName();
        }

        return fileNamesInDir;
    }

    public PhotoOrganiser(String apiKey) throws IndicoException
    {
        indico = new Indico(apiKey);
        _results = new ArrayList<>();
    }

    public Map<String, Double> listPossibilities(File image) throws IOException, IndicoException
    {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("top_n", 10);

        // Get top 10 results in sake of performance
        IndicoResult single = indico.imageRecognition.predict(image, paramMap);
        Map<String, Double> result = single.getImageRecognition();
        _results.add(result);
        return result;
    }

}
