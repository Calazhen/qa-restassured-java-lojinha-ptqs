# Testes API lojinha com Cucumber

## Escopo do projeto

Lojinha é um sistema provido pelo mentor [Júlio de lima](https://www.linkedin.com/in/juliodelimas/) para que seus alunos da [PTQS](https://www.juliodelima.com.br/mentoria/) possam praticar e evoluir em testes de Software.

A lojinha API possui uma regra de negócio para cadastro de produto onde o valor deve ser entre R$0,01 e R$7.000,00.  Pensando em um cenário válido sem muitas abstrações ou variações de dados com tabela de exemplos, criei esse código para representar uma evolução simples com a implementação do Cucumber ao que temos na aula de automação de testes de API.

## Tecnologias

[INTELIJ](https://www.jetbrains.com/idea/)

[Java versão 1.8.0_361](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

[Apache Maven 3.9.6](https://maven.apache.org/download.cgi)

1º Passo - Adicionar as dependências do Cucumber ao projeto

```xml
<dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-java</artifactId>
            <version>7.16.1</version>
        </dependency>
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-junit</artifactId>
            <version>7.16.1</version>
            <scope>test</scope>
        </dependency>
```

2º Passo - Baixar o Plugin do Intelij “Java e Cucumber”

![Untitled](https://github.com/Calazhen/qa-restassured-java-lojinha-ptqs/blob/lojinha-api-automacao-cucumber/src/test/resources/images/plugin.png)

3º Passo - Criar uma classe Runner na pasta raiz de teste \src\test\java\RunCucumberClass.java

```java
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.jupiter.api.Tag;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@Tag("@CadastroProdutoValorValido")
@CucumberOptions(plugin = {"pretty", "html:target/cucumber-report.html"})
public class RunCucumberClass {
}
```

4º Passo - Criar a pasta test/resources e um arquivo .feature com o nome da funcionalidade à ser automatizada.

```gherkin
#language: pt

@CadastroProdutoValorValido
Funcionalidade: : Produto com valor válido

  Cenario: Registrar um produto com sucesso
    Dado que tenho um produto com valor válido
    Quando 'POST' um produto
    Então devo ver a mensagem "Produto adicionado com sucesso"
```

Exemplo:  AdicaoProduto.feature

5º Passo - Troque a pasta “componentes” por “stepdefs” (Step Definition)

![Untitled](https://github.com/Calazhen/qa-restassured-java-lojinha-ptqs/blob/lojinha-api-automacao-cucumber/src/test/resources/images/StepDefs.png)

6º Passo - Troque o nome da classe de Teste para “AdicaoProdutoSteps” em \src\test\java\stepdefs\produto\AdicaoProdutoSteps.java

![Untitled](https://github.com/Calazhen/qa-restassured-java-lojinha-ptqs/blob/lojinha-api-automacao-cucumber/src/test/resources/images/AdicaoProdutoStepsClasse.png)

7º Passo - Execute o arquivo .Feature do passo 4, ele irá te dar os métodos equivalentes para implementar os Steps do Cucumber

```java
@Dado("que tenho um produto com valor válido")
    public void que_tenho_um_produto_com_valor_válido() {
// Sua implemetação de testes para executar esse passo
}

@Quando("{string} um produto")
    public void post_um_produto(String httpMethod){
// Sua implemetação de testes para executar esse passo
        
}
    
@Então("devo ver a mensagem {string}")
    public void devo_ver_a_mensagem_produto_adicionado_com_sucesso(String mensagemSucesso){
// Sua implemetação de testes para executar esse passo
}
```

8º Passo -  Criar um método para criar produto sem componentes no DataFactory/ProdutoDataFactory

```java
   public static ProdutoPojo criarProdutoComumSemComponentesEValorIgualA(double valor) {
        ProdutoPojo produto = new ProdutoPojo();
        produto.setProdutoNome("Playstation 5");
        produto.setProdutoValor(valor);

        List<String> cores = new ArrayList<>();
        cores.add("preto");
        cores.add("branco");

        produto.setProdutoCores(cores);
        produto.setProdutoUrlMock("");
        
        return produto;
    }
}
```

9º Passo - Remova as anotações @BeforeEach, utilize os métodos como recurso da classe

```java
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
```

10º Passo - Implementação final

```java
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
    @Quando("{string} um produto")
    public void post_um_produto(String httpMethod) {
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
```

11º Passo - Execute com sucesso ✅

![Untitled](https://github.com/Calazhen/qa-restassured-java-lojinha-ptqs/blob/lojinha-api-automacao-cucumber/src/test/resources/images/execucaoSucesso.png)

12º Passo - Visualize o relatório gerado em \lojinhaAPIAutomacao\target\cucumber-report.html

![Untitled](https://github.com/Calazhen/qa-restassured-java-lojinha-ptqs/blob/lojinha-api-automacao-cucumber/src/test/resources/images/RelatorioCucumber.png)


## Observação

Não utilizei o método já existente no DataFactory pois houve problema para serializar o componente no objeto produto devido a alta abstração.

## Contato

Caso tenha alguma dúvida poderá entrar em contato pelo meu [Linkedin](https://www.linkedin.com/in/henrique-calazans/)

Bora se conectar? =]
