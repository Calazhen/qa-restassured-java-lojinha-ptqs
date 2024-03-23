package modulos.produto;

import dataFactory.ProdutoDataFactory;
import dataFactory.UsuarioDataFactory;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;



@DisplayName("Testes de API Rest do modulo de Produto")
public class ProdutoTest {

    private String token;

    @BeforeEach
    public void beforeEach() {
        baseURI = "http://165.227.93.41";
        //port =8080;
        basePath = "/lojinha";


        //Obter o token do usuario admin
        this.token =
                given()
                        .contentType(ContentType.JSON)
                        .body(UsuarioDataFactory.criarUsuarioComLoginSenhaIgualA("Henrique", "12345"))
                        .when()
                        .post("/v2/login")
                        .then()
                        .extract()
                        .path("data.token");

    }

    @Test
    @DisplayName("Validar os limites proibidos do valor do Produto 0.00 não é permitido")
    public void testValidarLimitesZeradoProibidosValorProduto() {

        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(ProdutoDataFactory.criarProdutoComumComOValorIgualA(0.00))
                .when()
                .post("v2/produtos")
                .then()
                .assertThat()
                //Tentar inserir um produto com valor 00 e validar que a mensagem de erro foi apresentada e o
                .body("error", equalTo("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00"))
                //Status code retornado foi 422
                .statusCode(422);

    }

    @Test
    @DisplayName("Validar os limites proibidos do valor do Produto R$7.000,01 não é permitido")
    public void testValidarLimitesMaiorSeteMilProibidosValorProduto() {

        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(ProdutoDataFactory.criarProdutoComumComOValorIgualA(7000.01))
                .when()
                .post("v2/produtos")
                .then()
                .assertThat()
                .body("error", equalTo("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00"))
                //Status code retornado foi 422
                .statusCode(422);

    }

}

