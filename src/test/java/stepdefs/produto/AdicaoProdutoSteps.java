package stepdefs.produto;

import dataFactory.ProdutoDataFactory;
import dataFactory.UsuarioDataFactory;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import dtos.ComponentePojo;
import dtos.ProdutoPojo;

import java.util.ArrayList;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;


@DisplayName("Testes de API Rest do modulo de Produto")
public class AdicaoProdutoSteps {

    ValidatableResponse response;
    ProdutoPojo produtoParaCadastrar;

    String token;


    // Arrange
    @Dado("que tenho um produto com valor válido")
    public void que_tenho_um_produto_com_valor_válido() {
        produtoParaCadastrar = ProdutoDataFactory.criarProdutoComumSemComponentesEValorIgualA(4500.05);
        ComponentePojo componente1 = new ComponentePojo("Controle", 1);
        ComponentePojo componente2 = new ComponentePojo("Cabo de Energia", 1);
        ArrayList<ComponentePojo> componentes = new ArrayList<>();
        componentes.add(componente1);
        componentes.add(componente2);
        produtoParaCadastrar.setComponentes(componentes);
    }

    // Act
    @Quando("o metodo {string} em um produto")
    public void o_metodo_em_um_produto(String httpMethod) {
        response = cadastraProduto(produtoParaCadastrar);
    }


    // Assert
    @Então("devo ver a mensagem {string}")
    public void devo_ver_a_mensagem_produto_adicionado_com_sucesso(String mensagemSucesso) {
        response
                .body("message", equalTo(mensagemSucesso))
                .statusCode(HttpStatus.SC_CREATED);
    }


    private ValidatableResponse cadastraProduto(ProdutoPojo produtoParaCadastrar) {
        baseURI = "http://165.227.93.41";
        capturarToken();

        return given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(produtoParaCadastrar)
                .when().
                post("v2/produtos")
                .then();
    }

    private String capturarToken() {
        baseURI = "http://165.227.93.41";
        basePath = "/lojinha";

        //Obter o token do usuario admin
        return this.token =
                given()
                        .contentType(ContentType.JSON)
                        .body(UsuarioDataFactory.criarUsuarioComLoginSenhaIgualA("Henrique", "12345"))
                        .when()
                        .post("/v2/login")
                        .then()
                        .extract()
                        .path("data.token");


    }
}

