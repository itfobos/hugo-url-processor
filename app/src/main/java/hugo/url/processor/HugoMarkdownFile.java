package hugo.url.processor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HugoMarkdownFile {

    static final String URL_PREFIX = "url:";
    public final Path filePath;

    public HugoMarkdownFile(Path filePath) {
        this.filePath = filePath;
    }

    public void replaceUrlToSlugAndSave() {
        System.out.print("Processing '" + filePath + "' ... ");

        List<String> fileLines;
        try (Stream<String> lines = Files.lines(filePath)) {
            fileLines = lines.map(HugoMarkdownFile::ifUrlToAliasesAndSlug)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error reading file " + filePath.toString(), e);
        }

        try {
            Files.write(filePath, fileLines);
        } catch (IOException e) {
            throw new RuntimeException("Error during writing file " + filePath.toString(), e);
        }

        System.out.println("OK");
    }

    static String ifUrlToAliasesAndSlug(final String src) {
        final String strippedSrc = src.strip();
        if (!strippedSrc.startsWith(URL_PREFIX)) {
            return src;
        }

        final Path urlPath = Path.of(
                strippedSrc.substring(URL_PREFIX.length())
                        .replaceAll("\"", "")
                        .strip()
        );

        final String slug = urlPath
                .getFileName()
                .toString()
                .replaceAll("[^a-z0-9\\\\-]", "");

        return """
                aliases: %s/
                slug: %s"""
                .formatted(urlPath, slug);
    }

    @Override
    public String toString() {
        return "HugoMarkdownFile{" + "filePath=" + filePath + '}';
    }

/*
    TODO: Process aliases.
    They can be not only single line:
    aliases: [solutions/isee-youth.html,/solutions/isee-youth,
        solutions/isee-foster-care.html,/solutions/isee-foster-care,
        solutions/isee-benefits.html,/solutions/isee-benefits,
        solutions/isee-head-start.html,/solutions/isee-head-start]
    */
}
