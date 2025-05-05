import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoOrganizer {
    public static void main(String[] args) {
        // CHANGE THIS PATH TO YOUR PHOTOS FOLDER
        String sourceFolderPath = "C:/Users/YourName/Pictures/UnorganizedPhotos";
        
        try {
            organizePhotosByDate(sourceFolderPath);
            System.out.println("Photo organization completed successfully!");
        } catch (IOException e) {
            System.err.println("Error organizing photos: " + e.getMessage());
        }
    }
    
    public static void organizePhotosByDate(String sourceFolderPath) throws IOException {
        File sourceFolder = new File(sourceFolderPath);
        
        if (!sourceFolder.exists() || !sourceFolder.isDirectory()) {
            throw new IOException("Source folder does not exist or is not a directory");
        }
        
        File[] files = sourceFolder.listFiles();
        
        if (files == null || files.length == 0) {
            System.out.println("No files found in the source folder");
            return;
        }
        
        for (File file : files) {
            if (file.isFile()) {
                Date fileDate = getFileCreationDate(file);
                String dateFolderName = new SimpleDateFormat("yyyy-MM-dd").format(fileDate);
                String destFolderPath = sourceFolderPath + File.separator + dateFolderName;
                File destFolder = new File(destFolderPath);
                
                if (!destFolder.exists()) {
                    boolean created = destFolder.mkdir();
                    if (!created) {
                        System.err.println("Failed to create folder: " + destFolderPath);
                        continue;
                    }
                }
                
                Path sourcePath = file.toPath();
                Path destPath = Paths.get(destFolderPath, file.getName());
                
                try {
                    Files.move(sourcePath, destPath);
                    System.out.println("Moved: " + file.getName() + " to " + dateFolderName);
                } catch (IOException e) {
                    System.err.println("Failed to move " + file.getName() + ": " + e.getMessage());
                }
            }
        }
    }
    
    private static Date getFileCreationDate(File file) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        return new Date(attrs.creationTime().toMillis());
    }
}
