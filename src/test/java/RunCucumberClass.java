import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.jupiter.api.Tag;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@Tag("@CadastroProdutoValorValido")
@CucumberOptions(plugin = {"pretty", "html:target/cucumber-report.html"})
public class RunCucumberClass {

}



