package modulos.produto;

import dataFactory.ProdutoDataFactory;
import dataFactory.UsuarioDataFactory;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@DisplayName("Testes de API Rest do módulo de produto")
public class ProdutoTest {
    private String token;

    //Antes de cada teste faça algo
    @BeforeEach
    public void beforeEach() {
        //Configurando os dados da API Rest da Lojinha
        baseURI = "http://165.227.93.41";
        /*Porta onde a aplicação está rodando
        port = 88;*/
        //Caminho inicial da aplicação
        basePath = "/lojinha";



        //Obter o token do usuários admin
        this.token = given()
                .contentType(ContentType.JSON)
                .body(UsuarioDataFactory.criarUsuario())
                .when()
                .post("/v2/login")
                .then()
                .extract()
                .path("data.token");
    }

    @Test
    @DisplayName("Validar que o valor do produto igual a 0.00 não é permitido")
    //Criação de um método
    //Todo método de teste precisa começar com test
    public void testValidarLimitesZeradoProibidoValorProduto() {
        //Aqui é onde se tem os testes em si
        //Tentar inserir um produto com valor 0.00 que a mensagem de erro foi apresentada e o status code retornado foi 422
        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(ProdutoDataFactory.criarProdutoComumComOValorIgualA(0.00))
                .when()
                .post("/v2/produtos")
                .then()
                .assertThat()
                .body("error", equalTo("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00"))
                .statusCode(422);
    }

    @Test
    @DisplayName("Validar que o valor do produto igual a 7000.01 não é permitido")
    public void testValidarLimitesMaiiorSeteMilProibidoValorProduto() {
        //Aqui é onde se tem os testes em si

        //Tentar inserir um produto com valor 0.00 que a mensagem de erro foi apresentada e o status code retornado foi 422
        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body("""
                        {
                          "produtoNome": "Maçarico",
                          "produtoValor": 7000.01,
                          "produtoCores": [
                            "Vermelho"
                          ],
                          "produtoUrlMock": "",
                          "componentes": [
                            {
                              "componenteNome": "Tampa",
                              "componenteQuantidade": 1
                            }
                          ]
                        }""")
                .when()
                .post("/v2/produtos")
                .then()
                .assertThat()
                .body("error", equalTo("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00"))
                .statusCode(422);
    }
}