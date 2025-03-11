import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class WebAppTest {

    @Test
    void sampleTest() {
        String expectedTitle = "Archetype Created Web Application";
        String actualTitle = getWebAppTitle();
        assertEquals(expectedTitle, actualTitle, "Web app title should match");
    }

    // Simulated method to get web app title
    private String getWebAppTitle() {
        return "Archetype Created Web Application";
    }
}
