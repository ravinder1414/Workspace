import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * Created by ruchikarawat on 3/27/15.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        format = {"pretty", "html:target/cucumber","json:target/cucumber.json"},
        features = "src/test/resources",

        tags ={"@Smoke","@regression","@wip"}
)
public class RunCukeTest {
// This class will be empty
}