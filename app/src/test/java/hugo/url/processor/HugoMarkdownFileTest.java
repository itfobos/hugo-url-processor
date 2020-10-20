package hugo.url.processor;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static hugo.url.processor.HugoMarkdownFile.ifUrlToAliasesAndSlug;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HugoMarkdownFileTest {
    @Test
    void notUrlLineIsNotChanged() {
        final String srcStr = "some src non url string";
        assertEquals(srcStr, ifUrlToAliasesAndSlug(srcStr));
    }

    @Test
    void urlIsCorrectlyReplacedWithAlias() {
        final String srcUrl = "/careers/vacancies/executive-assistant/";

        final String result = ifUrlToAliasesAndSlug(urlStrFrom(srcUrl));
        assertThat(result, containsString("aliases: " + srcUrl));
    }

    @Test
    void quotedUrlIsProcessedCorrectly() {
        final String srcUrl = "/careers/working-for-us/";

        final String result = ifUrlToAliasesAndSlug(urlStrFrom("\"%s\"".formatted(srcUrl)));
        assertThat(result, containsString("aliases: " + srcUrl));
    }

    @Test
    void urlWithDotIsProcessedCorrectly() {
        final String srcUrl = "/careers/vacancies/sr.-software-engineer/";

        final String result = ifUrlToAliasesAndSlug(urlStrFrom(srcUrl));
        assertThat(result, containsString("aliases: " + srcUrl));
    }

    @Test
    void charsInSlugAreReplacedCorrectly() {
        final String srcUrl = "/careers/vacancies/1-sr.- softwa.r.e-engineer AAA/";

        final String result = ifUrlToAliasesAndSlug(urlStrFrom(srcUrl));
        assertThat(result, Matchers.endsWith("slug: 1-sr-software-engineer"));
    }

    @Test
    void processedLineContainsOnlyOneNewLineChar() {
        final String result = ifUrlToAliasesAndSlug(urlStrFrom("/careers/vacancies/executive-assistant/"));

        final long newLinesAmount = result.chars().filter(c -> c == (int) '\n').count();
        assertThat(newLinesAmount, is(1L));
    }

    static private String urlStrFrom(String srcUrl) {
        return HugoMarkdownFile.URL_PREFIX + ' ' + srcUrl;
    }
}
