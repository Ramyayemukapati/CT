import java.io.*;
import java.nio.file.*;
import java.util.stream.Collectors;


public class FileUtility {


// Write text to file (overwrites if exists)
public static void write(String path, String content) throws IOException {
Files.write(Paths.get(path), content.getBytes());
}


// Append text to file
public static void append(String path, String content) throws IOException {
Files.write(Paths.get(path), content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
}


// Read entire file as string
public static String read(String path) throws IOException {
return new String(Files.readAllBytes(Paths.get(path)));
}


// Read file line by line
public static String readLines(String path) throws IOException {
try (BufferedReader br = Files.newBufferedReader(Paths.get(path))) {
return br.lines().collect(Collectors.joining(System.lineSeparator()));
}
}


// Replace text (simple global replace)
public static void replaceAll(String path, String target, String replacement) throws IOException {
Path p = Paths.get(path);
String content = new String(Files.readAllBytes(p));
content = content.replace(target, replacement);
Files.write(p, content.getBytes());
}


// Insert text at a given line number (1-based). If line > existing lines, appends.
public static void insertAtLine(String path, int lineNumber, String textToInsert) throws IOException {
Path p = Paths.get(path);
java.util.List<String> lines = Files.exists(p) ? Files.readAllLines(p) : new java.util.ArrayList<>();
int idx = Math.max(0, Math.min(lineNumber - 1, lines.size()));
lines.add(idx, textToInsert);
Files.write(p, lines);
}


// Demo main
public static void main(String[] args) {
String file = "demo.txt";
try {
write(file, "Hello Codtech!\nThis is a demo file.\n");
append(file, "Appending a second line.\n");
System.out.println("After write+append:\n" + read(file));


insertAtLine(file, 2, "--- Inserted line at position 2 ---");
System.out.println("After insert at line 2:\n" + readLines(file));


replaceAll(file, "demo", "sample");
System.out.println("After replace 'demo'->'sample':\n" + read(file));


} catch (IOException e) {
System.err.println("File operation failed: " + e.getMessage());
}
}
}